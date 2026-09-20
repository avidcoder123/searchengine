package edu.caltech.cs2.project05;

import java.math.BigDecimal;

public class JSONDoubleValue extends JSONValue<Double> {

    public JSONDoubleValue(Double value) {
        super(value);
    }

    /**
     * Note that the parameter numIndents goes unused for JSONValues.
     */
    public String dump(int numIndents) {
        BigDecimal val = BigDecimal.valueOf(this.get());

        return val.toPlainString();
    }
}
