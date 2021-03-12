package Stratonet.Infrastructure.Threads.FileThread;

import Stratonet.Core.Enums.APIType;
import Stratonet.Core.Helpers.StratonetLogger;
import Stratonet.Core.Services.Message.IMessageService;
import Stratonet.Infrastructure.Services.Message.MessageService;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;

public class FileThread extends Thread
{
    private StratonetLogger logger;
    private Socket socket;
    private FileInputStream is;
    private FileOutputStream os;
    private IMessageService messageService;

    public FileThread(Socket socket)
    {
        logger = StratonetLogger.getInstance();
        this.socket = socket;
    }

    public void run()
    {
        try
        {
            InitializeIO();
            
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
            is = new FileInputStream(socket.getInputStream());
            os = new FileOutputStream(socket.getOutputStream());
            messageService = new MessageService(socket, is, os);
        }
        catch (IOException ex)
        {
            logger.log(Level.SEVERE, "Exception while opening IO stream: " + ex);
        }
    }
}
