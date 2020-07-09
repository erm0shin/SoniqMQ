package ru.innotechnum.testlistener.dto;

public class Message {
    private int a;
    private int b;

    public Message() {
    }

    public Message(int a, int b) {
        this.a = a;
        this.b = b;
    }

    public int getA() {
        return a;
    }

    public void setA(int a) {
        this.a = a;
    }

    public int getB() {
        return b;
    }

    public void setB(int b) {
        this.b = b;
    }

    @Override
    public String toString() {
        return "Message{" +
                "a=" + a +
                ", b=" + b +
                '}';
    }
}
