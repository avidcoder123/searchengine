package edu.caltech.cs2.project05;

import java.util.HashMap;
import java.util.Set;

public class Graph<V, E> implements IGraph<V, E> {
    HashMap<V, HashMap<V, E>> adjacencyList;

    public Graph() {
        adjacencyList = new HashMap<V, HashMap<V, E>>();
    }

    public Graph(HashMap<V, HashMap<V, E>> adjList) {
        adjacencyList = adjList;
    }

    @Override
    public boolean addVertex(V vertex) {
        if (adjacencyList.containsKey(vertex)) return false;
        adjacencyList.put(vertex, new HashMap<V, E>());
        return true;
    }

    @Override
    public boolean addEdge(V src, V dest, E e) {
        if (!adjacencyList.containsKey(src) || !adjacencyList.containsKey(dest)) throw new IllegalArgumentException();
        if (adjacencyList.get(src).containsKey(dest)) {
            adjacencyList.get(src).put(dest, e);
            return false;
        }
        adjacencyList.get(src).put(dest, e);
        return true;
    }

    @Override
    public boolean addUndirectedEdge(V n1, V n2, E e) {
        boolean b1 = addEdge(n1, n2, e);
        boolean b2 = addEdge(n2, n1, e);
        return b1 && b2;
    }

    @Override
    public boolean removeEdge(V src, V dest) {
        if (!adjacencyList.containsKey(src) || !adjacencyList.containsKey(dest)) throw new IllegalArgumentException();
        if (!adjacencyList.get(src).containsKey(dest)) return false;
        adjacencyList.get(src).remove(dest);
        return true;
    }

    @Override
    public Set<V> vertices() {
        return adjacencyList.keySet();
    }

    @Override
    public E adjacent(V i, V j) {
        if (!adjacencyList.containsKey(i) || !adjacencyList.containsKey(j)) throw new IllegalArgumentException();
        if (!adjacencyList.get(i).containsKey(j)) return null;
        return adjacencyList.get(i).get(j);
    }

    @Override
    public Set<V> neighbors(V vertex) {
        if (!adjacencyList.containsKey(vertex)) throw new IllegalArgumentException();
        return adjacencyList.get(vertex).keySet();
    }
}