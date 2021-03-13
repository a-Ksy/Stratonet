package Stratonet.Infrastructure.Threads.QueryThread;

import Stratonet.Core.Entities.User;
import Stratonet.Core.Enums.APIType;
import Stratonet.Core.Enums.RequestPhase;
import Stratonet.Core.Enums.RequestType;
import Stratonet.Core.Helpers.StratonetLogger;
import Stratonet.Core.Models.APODResponse;
import Stratonet.Core.Models.Message;
import Stratonet.Core.Models.PRE;
import Stratonet.Core.Models.UserQuery;
import Stratonet.Core.Services.APOD.IAPODService;
import Stratonet.Core.Services.Authentication.IAuthenticationService;
import Stratonet.Core.Services.Insight.IInsightService;
import Stratonet.Core.Services.Message.IMessageService;
import Stratonet.Core.Services.Query.IQueryService;
import Stratonet.Infrastructure.Utils.ImageToByteArrayConverter;
import Stratonet.Infrastructure.Utils.ObjectToJSONStringConverter;
import Stratonet.Infrastructure.Services.APOD.APODService;
import Stratonet.Infrastructure.Services.Authentication.AuthenticationService;
import Stratonet.Infrastructure.Services.Insight.InsightService;
import Stratonet.Infrastructure.Services.Message.MessageService;
import Stratonet.Infrastructure.Services.Query.QueryService;
import Stratonet.Infrastructure.Services.User.UserService;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.zip.Adler32;
import java.util.zip.Checksum;

public class QueryThread extends Thread
{
    private StratonetLogger logger;
    private BlockingQueue<UserQuery> queue;
    private Socket socket;
    private DataInputStream is;
    private DataOutputStream os;
    private IMessageService messageService;
    private IAuthenticationService authenticationService;
    private IQueryService queryService;
    private IInsightService insightService;
    private IAPODService apodService;
    private String clientToken;
    private APIType apiType;

    private boolean receivedAPIChoice = false;
    private boolean receivedDate = false;

    public QueryThread(Socket socket, BlockingQueue<UserQuery> queue)
    {
        logger = StratonetLogger.getInstance();
        this.queue = queue;
        this.authenticationService = new AuthenticationService();
        this.queryService = new QueryService();
        this.insightService = new InsightService();
        this.apodService = new APODService();
        this.socket = socket;
    }

    public void run()
    {
        try
        {
            InitializeIO();

            apiType = ReceiveAPIChoice();
            if (!receivedAPIChoice)
            {
                socket.close();
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
            // DisconnectClient();
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
        // PRE pre = insightService.GetRandomPRE();
        // For debug purposes
        PRE pre = new PRE();
        pre.av = 1;
        pre.ct = 1;
        pre.mn = 1;
        pre.mx = 1;
        if (pre == null)
        {
            Message message = new Message(RequestPhase.QUERY, RequestType.FAIL, "Couldn't fetched a PRE");
            messageService.SendMessage(message);
            logger.log(Level.INFO, "Couldn't fetched a PRE, restarting the query");
            this.run();
            return;
        }
        AddToQueue(pre);
        String hashedPRE = String.valueOf(ObjectToJSONStringConverter.Convert(pre).hashCode());
        logger.log(Level.INFO, "Sent hash = " +  hashedPRE);
        Message hashMessage = new Message(RequestPhase.QUERY, RequestType.SUCCESS, hashedPRE);
        messageService.SendMessage(hashMessage);
    }

    private void SendAPODMessage() throws IOException, NullPointerException
    {
        String date = null;
        Message message = new Message(RequestPhase.QUERY, RequestType.REQUEST, "Provide a date in yyyy-MM-dd format:");
        messageService.SendMessage(message);
        while (!receivedDate)
        {
            Message dateMessage = messageService.RetrieveMessage(true);
            if (authenticationService.ValidateToken(dateMessage.getToken()))
            {
                receivedDate = true;
                date = dateMessage.getPayload();
            }
        }
        APODResponse apodResponse = apodService.getAPODImage(date);
        byte[] imageAsByteArray = ImageToByteArrayConverter.Convert(apodResponse.url);
        AddToQueue(imageAsByteArray);
        // ToDo: Hash the image and send the hash to the client
        Checksum checksum = new Adler32();
        checksum.update(imageAsByteArray, 0, imageAsByteArray.length);
        long hashedImage = checksum.getValue();
        logger.log(Level.INFO, "Sent hash = " +  hashedImage);
        Message hashMessage = new Message(RequestPhase.QUERY, RequestType.SUCCESS, String.valueOf(hashedImage));
        messageService.SendMessage(hashMessage);
    }

    private void AddToQueue(Object object)
    {
        queue.add(new UserQuery(clientToken, object, apiType));
    }
}
