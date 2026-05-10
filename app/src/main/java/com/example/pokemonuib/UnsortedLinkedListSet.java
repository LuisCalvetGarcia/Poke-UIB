package com.example.pokemonuib;

import java.util.Iterator;

/**
 * Implementation of an unsorted set using a singly linked list.
 * * @param <E> The type of elements maintained by this set
 */
public class UnsortedLinkedListSet<E> {

    /**
     * Private inner class representing a node in the linked list.
     */
    private class Node {
        private E elem;     // The data element
        private Node next;  // Reference to the next node
    }
    
    private Node first; // Reference to the first node (head) of the set

    /**
     * Constructor to initialize an empty linked list set.
     */
    public UnsortedLinkedListSet() {
        first = null;
    }

    /**
     * Checks if the set contains the specified element.
     * * @param elem The element to search for
     * @return true if the element is found, false otherwise
     */
    public boolean contains(E elem) {
        Node nouNode = first;
        boolean trobat = false;
        
        // Traverse the list until the element is found or the end is reached
        while(nouNode != null && !trobat) { 
            if(nouNode.elem.equals(elem)){
                trobat = true;
            } else {
                nouNode = nouNode.next;
            }
        }
        return trobat;
    }

    /**
     * Adds a new element to the set.
     * * @param elem The element to add
     * @return true if added successfully, false if it was already in the set
     */
    public boolean add(E elem) {
        // Do not add if the element already exists (Set property)
        if(contains(elem)){
            return false;
        }
        
        // Create a new node and insert it at the beginning of the list
        Node nouNode = new Node();
        nouNode.elem = elem;
        nouNode.next = first;
        first = nouNode;
        
        return true;
    }

    /**
     * Removes a specific element from the set.
     * * @param elem The element to remove
     * @return true if successfully removed, false if not found
     */
    public boolean remove(E elem) {
        Node p = first;
        Node pp = null; // Previous node
        boolean trobat = false;
        
        // Traverse the list to find the element
        while (!trobat && p != null) {
            if(p.elem.equals(elem)){
                trobat = true;
            } else {
                pp = p;
                p = p.next;
            }
        }
        
        // If found, unlink the node from the list
        if(trobat){
            if(pp == null){
                // The element to remove is the first node
                first = p.next;
            } else {
                // The element is in the middle or end
                pp.next = p.next;
            }
        }
        return trobat;
    }

    /**
     * Checks if the set is empty.
     * * @return true if empty, false otherwise
     */
    public boolean isEmpty() {
        return first == null;
    }

    /**
     * Returns an iterator to traverse the elements in the linked list set.
     * * @return Iterator for the set
     */
    public Iterator<E> iterator() {
        return new IteratorUnsortedLinkedListSet();
    }

    /**
     * Private inner class that implements the Iterator interface.
     */
    private class IteratorUnsortedLinkedListSet implements Iterator<E> {
        private Node current = first; // Starts at the head of the list

        /**
         * Checks if there are more nodes to iterate over.
         * * @return true if there is a next element, false otherwise
         */
        @Override
        public boolean hasNext() {
            return current != null;
        }

        /**
         * Returns the next element and advances the iterator.
         * * @return The next element of type E
         */
        @Override
        public E next() {
            E elem = current.elem;
            current = current.next;
            return elem;
        }
    }
}
