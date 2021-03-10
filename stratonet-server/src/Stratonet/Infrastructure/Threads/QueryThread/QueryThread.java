package Stratonet.Infrastructure.Threads.QueryThread;

import Stratonet.Core.Entities.Session;
import Stratonet.Core.Entities.User;
import Stratonet.Core.Enums.RequestPhase;
import Stratonet.Core.Enums.RequestType;
import Stratonet.Core.Helpers.StratonetLogger;
import Stratonet.Core.Models.Message;
import Stratonet.Core.Services.Authentication.IAuthenticationService;
import Stratonet.Core.Services.Message.IMessageService;
import Stratonet.Infrastructure.Services.Authentication.AuthenticationService;
import Stratonet.Infrastructure.Services.Message.MessageService;
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

    private boolean receivedQuery = false;

    private QueryThread(Socket socket)
    {
        logger = StratonetLogger.getInstance();
        this.authenticationService = new AuthenticationService();
        this.socket = socket;
    }

    public void run()
    {
        try
        {
            InitializeIO();

            ReceiveQuery();
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

    private String ReceiveQuery() throws IOException, NullPointerException
    {
        Message message = new Message(RequestPhase.QUERY, RequestType.REQUEST, "Enter your query:");
        messageService.SendMessage(message);
        while(!receivedQuery)
        {
            Message queryMessage = messageService.RetrieveMessage(true);
            System.out.println(message.getToken());
            if (authenticationService.ValidateToken(queryMessage.getToken()))
            {
                // Do sth
                System.out.println("token was valid");
                receivedQuery = true;
            }
            else
            {
                message = new Message(RequestPhase.QUERY, RequestType.FAIL, "Token is not valid");
                messageService.SendMessage(message);
                logger.log(Level.INFO, "Token is not valid, closing the connection");
                break;
            }
        }
        return "DECIDE HERE";
    }
}
