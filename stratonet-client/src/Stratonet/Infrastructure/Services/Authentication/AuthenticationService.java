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

public class AuthenticationService implements IAuthenticationService {
    private static String token = null;
    private StratonetLogger logger;
    private DataInputStream is;
    private DataOutputStream os;
    private Socket socket;
    private boolean isAuthenticated = false;
    private IMessageService messageService;

    public AuthenticationService(Socket socket) {
        logger = StratonetLogger.getInstance();
        this.socket = socket;
        InitializeIO();
    }

    public static String GetToken() {
        return token;
    }

    private static void setToken(String token) {
        AuthenticationService.token = token;
    }

    private void InitializeIO() {
        try {
            is = new DataInputStream(socket.getInputStream());
            os = new DataOutputStream(socket.getOutputStream());
            messageService = new MessageService(socket, is, os);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Exception while initializing IO: " + ex);
        }
    }

    public void RunAuthentication() {
        Message message;
        while ((message = messageService.RetrieveMessage(false)).getRequestPhase() != null) {
            if (message.getRequestPhase().equals(RequestPhase.AUTH)) {
                if (message.getRequestType().equals(RequestType.REQUEST)) {
                    System.out.println(message.getPayload());
                    Scanner scanner = new Scanner(System.in);
                    String input = scanner.nextLine().trim();
                    Message authMessage = new Message(RequestPhase.AUTH, RequestType.CHALLENGE, input);
                    messageService.SendMessage(authMessage);
                } else if (message.getRequestType().equals(RequestType.FAIL)) {
                    logger.log(Level.INFO, "Authentication failed, closing connection");
                    break;
                } else if (message.getRequestType().equals(RequestType.SUCCESS)) {
                    logger.log(Level.INFO, "Successfully authenticated with the server.");
                    setToken(message.getPayload());
                    System.out.println("Received token= " + token);
                    break;
                }
            }
        }
    }
}
