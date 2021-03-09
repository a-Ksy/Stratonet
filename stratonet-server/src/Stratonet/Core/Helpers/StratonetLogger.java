package Stratonet.Core.Helpers;

import java.util.logging.*;

public class StratonetLogger
{
    private Logger logger;
    private static StratonetLogger stratonetLogger;

    private StratonetLogger()
    {
        logger = Logger.getLogger(StratonetLogger.class.getName());
        logger.setUseParentHandlers(false);
        StratonetFormatter formatter = new StratonetFormatter();
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(formatter);
        logger.addHandler(handler);
    }

    public static StratonetLogger getInstance()
    {
        if (stratonetLogger == null)
        {
            stratonetLogger = new StratonetLogger();
        }

        return stratonetLogger;
    }

    public void log(Level level, String message)
    {
        logger.log(level, message);
    }

}

class StratonetFormatter extends Formatter
{
    public String format(LogRecord record)
    {
        StringBuilder builder = new StringBuilder(1000);
        builder.append("[").append(record.getLevel()).append("] - ");
        builder.append(formatMessage(record));
        builder.append("\n");
        return builder.toString();
    }

    public String getHead(Handler h) {
        return super.getHead(h);
    }

    public String getTail(Handler h) {
        return super.getTail(h);
    }
}
