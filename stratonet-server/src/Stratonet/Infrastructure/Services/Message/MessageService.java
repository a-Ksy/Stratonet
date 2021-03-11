package Stratonet.Infrastructure.Services.Message;

import Stratonet.Core.Enums.RequestPhase;
import Stratonet.Core.Enums.RequestType;
import Stratonet.Core.Helpers.StratonetLogger;
import Stratonet.Core.Models.Message;
import Stratonet.Core.Services.Message.IMessageService;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;

public class MessageService implements IMessageService
{
    private StratonetLogger logger;
    private Socket socket;
    private DataInputStream is;
    private DataOutputStream os;

    public MessageService(Socket socket, DataInputStream is, DataOutputStream os)
    {
        this.logger = StratonetLogger.getInstance();
        this.socket = socket;
        this.is = is;
        this.os = os;
    }

    public void SendMessage(Message message) throws IOException
    {
        if (message.getToken() != null)
        {
            os.writeInt(message.getToken().length() + 2);
            os.writeUTF(message.getToken());
        }
        os.write(message.getRequestPhase().getValue());
        os.write(message.getRequestType().getValue());
        os.writeInt(message.getSize());
        os.writeUTF(message.getPayload());
        logger.log(Level.INFO, "Sent message: " + "\"" + message.getPayload() + "\"" + " to the socket: " + socket.getRemoteSocketAddress());
    }


    public Message RetrieveMessage(boolean hasToken) throws IOException
    {
        Message message = new Message();
        if (hasToken)
        {
            int tokenSize = is.readInt();
            byte[] tokenByte = new byte[tokenSize];
            is.readFully(tokenByte, 0, tokenSize);
            String tokenWithExtraChars = new String(tokenByte);
            StringBuilder token = new StringBuilder();
            for (int i=2; i<tokenWithExtraChars.length(); i++)
            {
                token.append(tokenWithExtraChars.charAt(i));
            }
            message.setToken(token.toString());
        }

        message.setRequestPhase(RequestPhase.fromInteger(is.read()));
        message.setRequestType(RequestType.fromInteger(is.read()));
        int size = is.readInt();
        message.setSize(size);
        byte[] payloadByte = new byte[size];
        is.readFully(payloadByte, 0, size);
        String payloadWithExtraChars = new String(payloadByte);
        StringBuilder payload = new StringBuilder();
        for (int i=2; i<payloadWithExtraChars.length(); i++)
        {
            payload.append(payloadWithExtraChars.charAt(i));
        }
        message.setPayload(payload.toString());
        logger.log(Level.INFO, "Received message: " + "\"" + message.getPayload() + "\"" + " from the socket: " + socket.getRemoteSocketAddress());

        return message;
    }
}
