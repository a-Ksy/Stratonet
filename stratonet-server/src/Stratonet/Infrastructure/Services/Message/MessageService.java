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
        os.write(message.requestPhase.getValue());
        os.write(message.requestType.getValue());
        os.writeInt(message.size);
        os.writeUTF(message.payload);
    }

    public Message RetrieveMessage() throws IOException
    {
        RequestPhase requestPhase = RequestPhase.fromInteger(is.read());
        RequestType requestType = RequestType.fromInteger(is.read());
        int size = is.readInt();
        byte[] payloadByte = new byte[size];
        is.read(payloadByte, 0, size);
        String payload = new String(payloadByte);

        Message message = new Message(requestPhase, requestType, size, payload);

        return message;
    }
}
