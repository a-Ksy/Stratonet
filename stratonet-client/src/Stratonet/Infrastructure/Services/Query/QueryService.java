package Stratonet.Infrastructure.Services.Query;

import Stratonet.Core.Enums.RequestPhase;
import Stratonet.Core.Enums.RequestType;
import Stratonet.Core.Helpers.StratonetLogger;
import Stratonet.Core.Models.Message;
import Stratonet.Core.Services.Message.IMessageService;
import Stratonet.Core.Services.Query.IQueryService;
import Stratonet.Infrastructure.Services.Authentication.AuthenticationService;
import Stratonet.Infrastructure.Services.Message.MessageService;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;

public class QueryService implements IQueryService
{
    private StratonetLogger logger;
    private DataInputStream is;
    private DataOutputStream os;
    private Socket socket;
    private IMessageService messageService;
    private String token;

    public QueryService(Socket socket)
    {
        logger = StratonetLogger.getInstance();
        this.socket = socket;
        this.token = AuthenticationService.GetToken();
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

    public void RunQuery()
    {
        Message message;
        while ((message = messageService.RetrieveMessage()).getRequestPhase() != null)
        {
            if (message.getRequestPhase().equals(RequestPhase.QUERY))
            {
                if (message.getRequestType().equals(RequestType.REQUEST))
                {
                    System.out.println(message.getPayload());
                    Scanner scanner = new Scanner(System.in);
                    String input = scanner.nextLine().trim();
                    Message queryMessage = new Message(RequestPhase.QUERY, RequestType.CHALLENGE, input);
                    queryMessage.setToken(token);
                    messageService.SendMessage(queryMessage);
                }
                else if (message.getRequestType().equals(RequestType.FAIL))
                {
                    logger.log(Level.INFO, "Authentication failed, closing connection");
                    break;
                }
                else if (message.getRequestType().equals(RequestType.SUCCESS))
                {
                    logger.log(Level.INFO, "Successfully authenticated with the server.");
                    System.out.println(token);
                }
            }
        }
    }
}
