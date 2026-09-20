package edu.caltech.cs2.project05;

import java.util.List;
import java.util.ArrayList;
import java.util.NoSuchElementException;

public class Stack<E> {
    private List<E> backing;

    public Stack() {
        this.backing = new ArrayList<>();
    }

    public void push(E elt) {
        this.backing.add(elt);
    }

    public E peek() {
        if (this.isEmpty()) {
            throw new NoSuchElementException();
        }
        return this.backing.get(this.backing.size() - 1);
    }

    public E pop() {
        E result = this.peek();
        this.backing.remove(this.backing.size() - 1);
        return result;
    }

    public int size() {
        return this.backing.size();
    }

    public boolean isEmpty() {
        return this.backing.isEmpty();
    }
}
