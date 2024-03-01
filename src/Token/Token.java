package Token;

public class Token {
    private String type = "";
    private String value = "";
    private int position = 0;

    public Token(String type, String value, int position) {
        setPosition(position);
        setType(type);
        setValue(value);
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void print() {
        System.out.println(this.type + " " + this.value + " " + position);
    }

    public String getValue() {
        return value;
    }

    public String getType() {
        return type;
    }

    public int getPosition() {
        return position;
    }

    public String toString() {
        return this.type + " " + this.value;
    }
}