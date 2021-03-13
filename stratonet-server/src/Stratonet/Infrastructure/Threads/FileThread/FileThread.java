package Stratonet.Infrastructure.Threads.FileThread;

import Stratonet.Core.Entities.User;
import Stratonet.Core.Enums.RequestPhase;
import Stratonet.Core.Enums.RequestType;
import Stratonet.Core.Helpers.StratonetLogger;
import Stratonet.Core.Models.Message;
import Stratonet.Core.Models.UserQuery;
import Stratonet.Core.Services.Authentication.IAuthenticationService;
import Stratonet.Core.Services.Message.IMessageService;
import Stratonet.Infrastructure.Services.Authentication.AuthenticationService;
import Stratonet.Infrastructure.Services.Message.MessageService;
import Stratonet.Infrastructure.Services.User.UserService;
import Stratonet.Infrastructure.Utils.ObjectToJSONStringConverter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.*;
import java.util.logging.Level;

public class FileThread extends Thread
{
    private BlockingQueue<UserQuery> queue;
    private StratonetLogger logger;
    private Socket socket;
    private DataInputStream is;
    private DataOutputStream os;
    private IMessageService messageService;
    private IAuthenticationService authenticationService;
    private boolean receivedHandshake = false;
    private String clientToken;

    public FileThread(Socket socket, BlockingQueue<UserQuery> queue)
    {
        logger = StratonetLogger.getInstance();
        authenticationService = new AuthenticationService();
        this.queue = queue;
        this.socket = socket;
    }

    public void run()
    {
        try
        {
            InitializeIO();

            clientToken = HandshakeClient();
            if (clientToken == null)
            {
                socket.close();
                return;
            }

            UserQuery userQuery = GetUserQuery(clientToken);
            if (userQuery == null)
            {
                socket.close();
                return;
            }

            switch(userQuery.getApiType())
            {
                case APOD:
                    SendAPODMessage(userQuery.getObject());
                    break;

                case Insight:
                    SendInsightMessage(userQuery.getObject());
                    break;
            }

            AskForRestart();

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

    private String HandshakeClient() throws IOException, NullPointerException
    {
        Message message = new Message(RequestPhase.FILE, RequestType.REQUEST, "Identification Request");
        messageService.SendMessage(message);
        while (!receivedHandshake)
        {
            Message handshakeMessage = messageService.RetrieveMessage(true);
            if (authenticationService.ValidateToken(handshakeMessage.getToken()))
            {
                receivedHandshake = true;
                return handshakeMessage.getToken();
            }
        }
        return null;
    }

    private UserQuery GetUserQuery(String clientToken)
    {
        for (UserQuery uq : queue)
        {
            if (uq.getToken().equals(clientToken))
            {
                queue.remove(uq);
                return uq;
            }
        }

        return null;
    }

    private void SendInsightMessage(Object object) throws IOException, NullPointerException
    {
        String json = ObjectToJSONStringConverter.Convert(object);
        Message insightMessage = new Message(RequestPhase.FILE, RequestType.SUCCESS, json);
        messageService.SendMessage(insightMessage);
    }

    private void SendAPODMessage(Object object) throws IOException, NullPointerException
    {
        byte[] imageAsByteArray = (byte[]) object;
        Message apodMessage = new Message(RequestPhase.FILE, RequestType.SUCCESS, imageAsByteArray);
        messageService.SendMessage(apodMessage);
    }

    private void AskForRestart() throws IOException, NullPointerException
    {
        Message requestMessage = new Message(RequestPhase.FILE, RequestType.REQUEST, "Would you like to start another query? (Y or N)");
        messageService.SendMessage(requestMessage);

        while (true)
        {
            Message restartMessage = RetrieveMessageAndValidateTimeout();
            if (restartMessage == null)
            {
                User user =  UserService.getInstance().GetUserByToken(clientToken);
                UserService.getInstance().ResetUserSession(user);
                break;
            }

            if (restartMessage.getPayload() != null)
            {
                if (restartMessage.getPayload().equals("N"))
                {
                    User user =  UserService.getInstance().GetUserByToken(clientToken);
                    UserService.getInstance().ResetUserSession(user);
                }
                break;
            }
        }
    }

    private Message RetrieveMessageAndValidateTimeout() throws IOException
    {
        ExecutorService executor = Executors.newCachedThreadPool();
        Callable<Message> task = new Callable<Message>() {
            public Message call() throws IOException {
                return messageService.RetrieveMessage(true);
            }
        };
        Future<Message> future = executor.submit(task);
        try {
            Message result = future.get(10, TimeUnit.SECONDS);
            return result;
        } catch (Exception ex) {
            logger.log(Level.INFO, "User failed to request a restart on time");
            future.cancel(true);
        }

        return null;
    }
}
