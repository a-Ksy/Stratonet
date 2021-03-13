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

public class MessageService implements IMessageService {
    private StratonetLogger logger;
    private Socket socket;
    private DataInputStream is;
    private DataOutputStream os;

    public MessageService(Socket socket, DataInputStream is, DataOutputStream os) {
        this.logger = StratonetLogger.getInstance();
        this.socket = socket;
        this.is = is;
        this.os = os;
    }

    public void SendMessage(Message message) {
        try {
            if (message.getToken() != null) {
                os.writeInt(message.getToken().length() + 2);
                os.writeUTF(message.getToken());
            }
            os.write(message.getRequestPhase().getValue());
            os.write(message.getRequestType().getValue());
            os.writeInt(message.getSize());
            os.writeUTF(message.getPayload());
            logger.log(Level.INFO, "Sent message: " + "\"" + message.getPayload() + "\"");
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Exception while sending message");
        }
    }

    public Message RetrieveMessage(boolean payloadIsByteArray) {
        Message message = new Message();
        try {
            message.setRequestPhase(RequestPhase.fromInteger(is.read()));
            message.setRequestType(RequestType.fromInteger(is.read()));
            int size = is.readInt();
            message.setSize(size);
            byte[] payloadByte = new byte[size];
            is.readFully(payloadByte, 0, size);
            if (!payloadIsByteArray) {
                String payloadWithExtraChars = new String(payloadByte);
                StringBuilder payload = new StringBuilder();
                for (int i = 2; i < payloadWithExtraChars.length(); i++) {
                    payload.append(payloadWithExtraChars.charAt(i));
                }
                message.setPayload(payload.toString());
                logger.log(Level.INFO, "Received message: " + "\"" + message.getPayload() + "\"");
            } else {
                message.setPayloadAsByteArray(payloadByte);
                logger.log(Level.INFO, "Received message as a byte array");
            }

        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Exception while retrieving message");
        }

        return message;
    }
}
