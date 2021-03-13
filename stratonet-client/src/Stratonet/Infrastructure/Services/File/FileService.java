package Stratonet.Infrastructure.Services.File;

import Stratonet.Core.Enums.APIType;
import Stratonet.Core.Enums.RequestPhase;
import Stratonet.Core.Enums.RequestType;
import Stratonet.Core.Helpers.StratonetLogger;
import Stratonet.Core.Models.Message;
import Stratonet.Core.Models.PRE;
import Stratonet.Core.Services.File.IFileService;
import Stratonet.Core.Services.Message.IMessageService;
import Stratonet.Core.Services.Save.ISaveService;
import Stratonet.Infrastructure.Helpers.FileNameGenerator;
import Stratonet.Infrastructure.Helpers.HashValidator;
import Stratonet.Infrastructure.Helpers.StringToPREConverter;
import Stratonet.Infrastructure.Services.Authentication.AuthenticationService;
import Stratonet.Infrastructure.Services.Message.MessageService;
import Stratonet.Infrastructure.Services.Query.QueryService;
import Stratonet.Infrastructure.Services.Save.SaveService;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;

public class FileService implements IFileService
{
    private StratonetLogger logger;
    private DataInputStream is;
    private DataOutputStream os;
    private Socket socket;
    private IMessageService messageService;
    private ISaveService saveService;
    private String token;

    public FileService(Socket socket)
    {
        logger = StratonetLogger.getInstance();
        this.socket = socket;
        this.token = AuthenticationService.GetToken();
        saveService = new SaveService();
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

    public void RunFile()
    {
        Message message;
        switch (QueryService.apiType)
        {
            case Insight:
                while ((message = messageService.RetrieveMessage()).getRequestPhase() != null)
                {
                    if (message.getRequestPhase().equals(RequestPhase.FILE))
                    {
                        if (message.getRequestType().equals(RequestType.REQUEST))
                        {
                            Message queryMessage = new Message(RequestPhase.FILE, RequestType.CHALLENGE, "Identification Response");
                            queryMessage.setToken(token);
                            messageService.SendMessage(queryMessage);
                        }
                        else if (message.getRequestType().equals(RequestType.FAIL))
                        {
                            logger.log(Level.INFO, "File transmission failed, closing connection");
                            break;
                        }
                        else if (message.getRequestType().equals(RequestType.SUCCESS))
                        {
                            logger.log(Level.INFO, "Successfully received the file from the server.");
                            PRE pre = StringToPREConverter.Convert(message.getPayload());

                            if(HashValidator.ValidateJSONHash(QueryService.hashValue, pre))
                            {
                                saveService.SaveObjectAsJSON(pre, FileNameGenerator.Generate("json"));
                            }
                        }
                    }
                }
                break;
            case APOD:
                break;
        }

    }
}
