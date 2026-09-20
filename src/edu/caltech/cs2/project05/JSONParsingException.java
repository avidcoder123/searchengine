package edu.caltech.cs2.project05;

public class JSONParsingException extends RuntimeException {
    int idx;

    /**
     * Gives a runtime exception at the index where the error occurs.
     */
    public JSONParsingException(int idx) {
        super("JSONParsingException: Could not parse string. Error at character number " + idx);
        this.idx = idx;
    }

    public int getIndex() {
        return idx;
    }
}
