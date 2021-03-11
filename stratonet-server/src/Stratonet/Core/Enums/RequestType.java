package Stratonet.Core.Enums;

public enum RequestType
{
    REQUEST(0), CHALLENGE(1), FAIL(2), SUCCESS(3), CHOICE(4);

    private final int value;

    private RequestType(int value)
    {
        this.value = value;
    }

    public int getValue()
    {
        return value;
    }

    public static RequestType fromInteger(int x)
    {
        switch(x)
        {
            case 0: return REQUEST;
            case 1: return CHALLENGE;
            case 2: return FAIL;
            case 3: return SUCCESS;
            case 4: return CHOICE;
        }
        return null;
    }
}
