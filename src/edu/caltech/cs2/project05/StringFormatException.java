package edu.caltech.cs2.project05;

public class StringFormatException extends RuntimeException {
    public StringFormatException(String s, int i) {
        super("Illegal Character encountered when parsing " + s + " at index " + i);
    }
}
