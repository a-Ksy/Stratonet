package Stratonet.Infrastructure.Threads.AuthenticationThread;

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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;

public class AuthenticationThread extends Thread
{
    private StratonetLogger logger;
    private Socket socket;
    private DataInputStream is;
    private DataOutputStream os;
    private IMessageService messageService;
    private IAuthenticationService authenticationService;

    private boolean receivedUsername = false;
    private boolean receivedPassword = false;
    private int tryLeft = 3;

    public AuthenticationThread(Socket socket)
    {
        logger = StratonetLogger.getInstance();
        authenticationService = new AuthenticationService();
        this.socket = socket;
    }

    public void run()
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

        try
        {
            // Receiving username from client
            Message message = new Message(RequestPhase.AUTH, RequestType.REQUEST, "Enter your username:");
            messageService.SendMessage(message);
            while(!receivedUsername)
            {
                Message usernameMessage = messageService.RetrieveMessage();

                if (authenticationService.ValidateUsername(usernameMessage.payload))
                {
                    User user = authenticationService.GetUser(usernameMessage.payload);
                    String generatedToken = authenticationService.GenerateToken(user);
                    user.setSession(new Session(generatedToken, socket.getRemoteSocketAddress()));
                    authenticationService.ModifyUser(user);

                    receivedUsername = true;
                }
                else
                {
                    message = new Message(RequestPhase.AUTH, RequestType.FAIL, "User does not exist");
                    messageService.SendMessage(message);
                    logger.log(Level.INFO, "User does not exist, closing the connection");
                    return;
                }
            }

            // Receiving password from client
            message = new Message(RequestPhase.AUTH, RequestType.REQUEST, "Enter your password:" );
            messageService.SendMessage(message);
            while(!receivedPassword && tryLeft > 0)
            {
                Message passwordMessage = messageService.RetrieveMessage();
                System.out.println(passwordMessage.payload);
                break;
            }

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

    private void ReceiveUsername()
    {

    }
}
