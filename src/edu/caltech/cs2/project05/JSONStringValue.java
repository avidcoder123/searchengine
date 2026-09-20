package edu.caltech.cs2.project05;

public class JSONStringValue extends JSONValue<String> {

    public JSONStringValue(String value) {
        super(value);
    }

    /**
     * Note that the parameter numIndents goes unused for JSONValues.
     */
    public String dump(int numIndents) {
        return JSONHelpers.toFormattedString(this.get());
    }
}
