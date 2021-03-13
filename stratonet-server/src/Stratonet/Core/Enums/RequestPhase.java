package Stratonet.Core.Enums;

public enum RequestPhase {
    AUTH(0), QUERY(1), FILE(2);

    private final int value;

    private RequestPhase(int value) {
        this.value = value;
    }

    public static RequestPhase fromInteger(int x) {
        switch (x) {
            case 0:
                return AUTH;
            case 1:
                return QUERY;
            case 2:
                return FILE;
        }
        return null;
    }

    public int getValue() {
        return value;
    }
}
