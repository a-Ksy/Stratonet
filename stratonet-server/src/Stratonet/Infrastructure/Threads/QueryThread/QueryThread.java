package Stratonet.Infrastructure.Threads.QueryThread;

import Stratonet.Core.Entities.Session;
import Stratonet.Core.Entities.User;
import Stratonet.Core.Enums.APIType;
import Stratonet.Core.Enums.RequestPhase;
import Stratonet.Core.Enums.RequestType;
import Stratonet.Core.Helpers.StratonetLogger;
import Stratonet.Core.Models.Message;
import Stratonet.Core.Models.PRE;
import Stratonet.Core.Services.Authentication.IAuthenticationService;
import Stratonet.Core.Services.Insight.IInsightService;
import Stratonet.Core.Services.Message.IMessageService;
import Stratonet.Core.Services.Query.IQueryService;
import Stratonet.Core.Services.User.IUserService;
import Stratonet.Infrastructure.Helpers.ObjectToJSONStringConverter;
import Stratonet.Infrastructure.Services.Authentication.AuthenticationService;
import Stratonet.Infrastructure.Services.Insight.InsightService;
import Stratonet.Infrastructure.Services.Message.MessageService;
import Stratonet.Infrastructure.Services.Query.QueryService;
import Stratonet.Infrastructure.Services.User.UserService;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;

public class QueryThread extends Thread
{
    private StratonetLogger logger;
    private Socket socket;
    private DataInputStream is;
    private DataOutputStream os;
    private IMessageService messageService;
    private IAuthenticationService authenticationService;
    private IQueryService queryService;
    private IInsightService insightService;
    private String clientToken;

    private boolean receivedAPIChoice = false;

    public QueryThread(Socket socket)
    {
        logger = StratonetLogger.getInstance();
        this.authenticationService = new AuthenticationService();
        this.queryService = new QueryService();
        this.insightService = new InsightService();
        this.socket = socket;
    }

    public void run()
    {
        try
        {
            InitializeIO();

            APIType apiType = ReceiveAPIChoice();
            if (!receivedAPIChoice)
            {
                return;
            }

            switch (apiType)
            {
                case Insight:
                    SendInsightMessage();
                    break;
                case APOD:
                    SendAPODMessage();
                    break;
            }

            // Have a timeout before disconnecting the client
            DisconnectClient();
        }
        catch (IOException ex)
        {
            logger.log(Level.SEVERE, "Exception while IO operation: " + ex);
        }
        catch (NullPointerException ex)
        {
            logger.log(Level.SEVERE, "Exception while IO operation, thread closed: " + ex);
        }
        finally
        {
            try
            {
                logger.log(Level.INFO, "Closing the connection with the socket: " + socket.getRemoteSocketAddress());
                if (is != null)
                {
                    is.close();
                    logger.log(Level.WARNING, "Socket Input closed");
                }

                if (os != null)
                {
                    os.close();
                    logger.log(Level.WARNING, "Socket Output closed");
                }
                if (socket != null)
                {
                    socket.close();
                    logger.log(Level.WARNING, "Socket closed");
                }
            }
            catch (IOException ex)
            {
                logger.log(Level.SEVERE, "Exception while closing the socket connection: " + ex);
            }
        }
    }

    private void InitializeIO() throws NullPointerException
    {
        try
        {
            is = new DataInputStream(socket.getInputStream());
            os = new DataOutputStream(socket.getOutputStream());
            messageService = new MessageService(socket, is, os);
        }
        catch (IOException ex)
        {
            logger.log(Level.SEVERE, "Exception while opening IO stream: " + ex);
        }
    }

    private APIType ReceiveAPIChoice() throws IOException, NullPointerException
    {
        Message message = new Message(RequestPhase.QUERY, RequestType.CHOICE, "Enter your API Choice (APOD or Insight):");
        messageService.SendMessage(message);
        while(!receivedAPIChoice)
        {
            Message queryMessage = messageService.RetrieveMessage(true);
            if (authenticationService.ValidateToken(queryMessage.getToken()))
            {
                logger.log(Level.INFO, "Token is valid for the socket: " + socket.getRemoteSocketAddress());
                clientToken = queryMessage.getToken();

                if (queryService.validateAPIType(queryMessage.getPayload()))
                {
                    receivedAPIChoice = true;
                    return APIType.valueOf(queryMessage.getPayload());
                }
                else
                {
                    logger.log(Level.INFO, "Provided API type is not valid");
                    User user = UserService.getInstance().GetUserByToken(queryMessage.getToken());
                    UserService.getInstance().ResetUserSession(user);                    
                    break;
                }
            }
            else
            {
                message = new Message(RequestPhase.QUERY, RequestType.FAIL, "Token is not valid");
                messageService.SendMessage(message);
                logger.log(Level.INFO, "Token is not valid, closing the connection");
                break;
            }
        }
        return null;
    }

    private void DisconnectClient()
    {
        User user = UserService.getInstance().GetUserByToken(clientToken);
        if (user != null)
        {
            UserService.getInstance().ResetUserSession(user);
        }
        logger.log(Level.INFO, "Query is finished, disconnecting the user");
    }

    private void SendInsightMessage() throws IOException, NullPointerException
    {
        PRE pre = insightService.GetRandomPRE();
        if (pre == null)
        {
            Message message = new Message(RequestPhase.QUERY, RequestType.FAIL, "Couldn't fetched a PRE");
            messageService.SendMessage(message);
            logger.log(Level.INFO, "Couldn't fetched a PRE, restarting the query");
            this.run();
            return;
        }
        String hashedPRE = String.valueOf(ObjectToJSONStringConverter.Convert(pre).hashCode());
        Message message = new Message(RequestPhase.QUERY, RequestType.SUCCESS, hashedPRE);
        messageService.SendMessage(message);
    }

    private void SendAPODMessage() throws IOException, NullPointerException
    {
        System.out.println("Not yed implemented");
        return;
    }
}
