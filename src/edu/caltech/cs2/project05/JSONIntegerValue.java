package edu.caltech.cs2.project05;

public class JSONIntegerValue extends JSONValue<Integer> {
    public JSONIntegerValue(Integer value) {
        super(value);
    }

    /**
     * Note that the parameter numIndents goes unused for JSONValues.
     */
    public String dump(int numIndents) {
        return this.get().toString();
    }
}
