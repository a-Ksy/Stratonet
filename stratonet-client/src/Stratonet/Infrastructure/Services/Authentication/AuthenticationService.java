package Stratonet.Infrastructure.Services.Authentication;

import Stratonet.Core.Enums.RequestPhase;
import Stratonet.Core.Enums.RequestType;
import Stratonet.Core.Helpers.StratonetLogger;
import Stratonet.Core.Models.Message;
import Stratonet.Core.Services.Authentication.IAuthenticationService;
import Stratonet.Core.Services.Message.IMessageService;
import Stratonet.Infrastructure.Services.Message.MessageService;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;

public class AuthenticationService implements IAuthenticationService
{
    private StratonetLogger logger;
    private DataInputStream is;
    private DataOutputStream os;
    private Socket socket;
    private boolean isAuthenticated = false;
    private IMessageService messageService;

    public AuthenticationService(Socket socket)
    {
        logger = StratonetLogger.getInstance();
        this.socket = socket;
        InitializeIO();
    }

    private void InitializeIO()
    {
        try
        {
            is =  new DataInputStream(socket.getInputStream());
            os = new DataOutputStream(socket.getOutputStream());
            messageService = new MessageService(socket, is, os);
        }
        catch (IOException ex)
        {
            logger.log(Level.SEVERE, "Exception while initializing IO: " + ex);
        }
    }

    public void RunAuthentication()
    {
            Message message;
            while ((message = messageService.RetrieveMessage()).requestPhase != null)
            {
                if (message.requestPhase.equals(RequestPhase.AUTH))
                {
                    if (message.requestType.equals(RequestType.REQUEST))
                    {
                        System.out.println(message.payload);
                        Scanner scanner = new Scanner(System.in);
                        String input = scanner.nextLine().trim();
                        Message authMessage = new Message(RequestPhase.AUTH, RequestType.CHALLENGE, input);
                        messageService.SendMessage(authMessage);
                    }
                    else if (message.requestType.equals(RequestType.FAIL))
                    {
                        logger.log(Level.INFO, "Authentication failed, closing connection");
                        break;
                    }

                }
            }
    }
}
