package domain;

public enum Type {
    BUY('B'),
    SELL('S');

    private final char type;

    Type(char type) {
        this.type = type;
    }

    public char Type() {
        return type;
    }
}
