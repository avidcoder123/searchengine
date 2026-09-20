package edu.caltech.cs2.project05;

import java.util.List;
import java.util.ArrayList;
import java.util.NoSuchElementException;

public class Queue<E> {
    private List<E> backing;

    public Queue() {
        this.backing = new ArrayList<>();
    }

    public void enqueue(E elt) {
        this.backing.add(elt);
    }

    public E peek() {
        if (this.isEmpty()) {
            throw new NoSuchElementException();
        }
        return this.backing.get(0);
    }

    public E dequeue() {
        E result = this.peek();
        this.backing.remove(0);
        return result;
    }

    public int size() {
        return this.backing.size();
    }

    public boolean isEmpty() {
        return this.backing.isEmpty();
    }
}
