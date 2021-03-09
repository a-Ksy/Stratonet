package Stratonet.Core.Enums;

public enum RequestPhase
{
    AUTH(0), QUERY(1);

    private final int value;

    private RequestPhase(int value)
    {
        this.value = value;
    }

    public int getValue()
    {
        return value;
    }

    public static RequestPhase fromInteger(int x)
    {
        switch(x)
        {
            case 0: return AUTH;
            case 1: return QUERY;
        }
        return null;
    }
}
