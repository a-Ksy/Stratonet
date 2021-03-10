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
import Stratonet.Infrastructure.Services.User.UserService;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.*;
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
            InitializeIO();

            User user = ReceiveUsernameAndFindUser();
            if (!receivedUsername)
            {
                return;
            }

            ReceivePassword(user);
            if (!receivedPassword)
            {
                return;
            }

            SendToken(user);

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

    private User ReceiveUsernameAndFindUser() throws IOException, NullPointerException
    {
        Message message = new Message(RequestPhase.AUTH, RequestType.REQUEST, "Enter your username:");
        messageService.SendMessage(message);
        User user = null;
        while(!receivedUsername)
        {
            Message usernameMessage = messageService.RetrieveMessage();

            if (authenticationService.ValidateUsername(usernameMessage.payload))
            {
                user = UserService.getInstance().GetUser(usernameMessage.payload);
                user.setSession(new Session(null, socket.getRemoteSocketAddress()));
                UserService.getInstance().ModifyUser(user);

                receivedUsername = true;
            }
            else
            {
                message = new Message(RequestPhase.AUTH, RequestType.FAIL, "User does not exist");
                messageService.SendMessage(message);
                logger.log(Level.INFO, "User does not exist, closing the connection");
                break;
            }
        }
        return user;
    }

    private void ReceivePassword(User user) throws IOException, NullPointerException
    {
        Message message = new Message(RequestPhase.AUTH, RequestType.REQUEST, "Enter your password:" );
        messageService.SendMessage(message);

        while(!receivedPassword)
        {
            Message passwordMessage = RetrieveMessageAndValidateTimeout(user);
            if (passwordMessage == null)
            {
                return;
            }
            if (passwordMessage != null && passwordMessage.requestType.equals(RequestType.CHALLENGE))
            {
                if (passwordMessage.payload == null || !authenticationService.ValidatePassword(user, passwordMessage.payload))
                {
                    tryLeft -= 1;
                    if (tryLeft == 0)
                    {
                        message = new Message(RequestPhase.AUTH, RequestType.FAIL, "No attempts left!");
                        messageService.SendMessage(message);
                        return;
                    }
                    message = new Message(RequestPhase.AUTH, RequestType.REQUEST, "Enter your password (" + tryLeft + " guesses left):");
                    messageService.SendMessage(message);
                }
                else
                {
                    receivedPassword = true;
                    logger.log(Level.INFO, "User " + user.getUsername() + " has successfully authenticated.");
                }
            }
        }
    }

    private Message RetrieveMessageAndValidateTimeout(User user) throws IOException
    {
        ExecutorService executor = Executors.newCachedThreadPool();
        Callable<Message> task = new Callable<Message>() {
            public Message call() throws IOException {
                return messageService.RetrieveMessage();
            }
        };
        Future<Message> future = executor.submit(task);
        try {
            Message result = future.get(10, TimeUnit.SECONDS);
            System.out.println(result.payload);
            return result;
        } catch (TimeoutException ex)
        {
            logger.log(Level.INFO, "User " + user.getUsername() + " failed to authenticate on time.");
            Message message = new Message(RequestPhase.AUTH, RequestType.FAIL, "Disconnected from the server for no respond.");
            messageService.SendMessage(message);
            future.cancel(true);
        } catch (InterruptedException e)
        {
            logger.log(Level.INFO, "User " + user.getUsername() + " failed to authenticate on time.");
            Message message = new Message(RequestPhase.AUTH, RequestType.FAIL, "Disconnected from the server for no respond.");
            messageService.SendMessage(message);
            future.cancel(true);
        }
        catch (ExecutionException e)
        {
            logger.log(Level.INFO, "User " + user.getUsername() + " failed to authenticate on time.");
            Message message = new Message(RequestPhase.AUTH, RequestType.FAIL, "Disconnected from the server for no respond.");
            messageService.SendMessage(message);
            future.cancel(true);
        }

        return null;
    }

    private void SendToken(User user) throws IOException, NullPointerException
    {
        String token = authenticationService.GenerateToken(user);
        user.getSession().setToken(token);
        UserService.getInstance().ModifyUser(user);
        Message message = new Message(RequestPhase.AUTH, RequestType.SUCCESS, token);
        messageService.SendMessage(message);
    }
}
