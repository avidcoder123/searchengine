package edu.caltech.cs2.project05;

public abstract class JSONValue<E> implements JSON {
    private final E value;

    protected JSONValue(E value) {
        super();
        this.value = value;
    }

    public E get() {
        return value;
    }

    public boolean equals(Object other) {
        return other instanceof JSONValue && ((JSONValue<?>) other).value.equals(value);
    }

    public String toString() {
        return this.dump();
    }
}
