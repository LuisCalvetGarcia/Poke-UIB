package com.example.pokemonuib;

import java.util.Iterator;

/**
 * Implementation of an unsorted set using a fixed-size array.
 * * @param <E> The type of elements maintained by this set
 */
public class UnsortedArraySet<E> {
    
    private E[] array; // Array to store the elements of the set
    private int tam;   // Current number of elements in the set (size)

    /**
     * Constructor to initialize the array set with a specific capacity.
     * * @param tamañoTotal Maximum capacity of the set
     */
    public UnsortedArraySet(int tamañoTotal) {
        // Instantiate the array of elements (empty initially)
        this.array = (E[]) new Object[tamañoTotal]; 
        this.tam = 0;
    }

    /**
     * Adds an element to the set if there is available space.
     * * @param element The element to be added
     */
    public void add(E element) {
        // Add the element only if the array is not full
        if (tam < array.length) {
            array[tam++] = element;
        }
    }

    /**
     * Returns an iterator to traverse the elements in the set.
     * * @return Iterator for the set
     */
    public Iterator<E> iterator() {
        return new IteratorUnsortedArraySet();
    }

    /**
     * Private inner class that implements the Iterator interface.
     */
    private class IteratorUnsortedArraySet implements Iterator<E> {
        private int indice; // Current index for iteration

        /**
         * Constructor for the iterator.
         */
        private IteratorUnsortedArraySet() {
            this.indice = 0;
        }

        /**
         * Checks if there are more elements to iterate over.
         * * @return true if there is a next element, false otherwise
         */
        @Override
        public boolean hasNext() {
            return indice < tam;
        }

        /**
         * Returns the next element in the array and advances the iterator.
         * * @return The next element of type E
         */
        @Override
        public E next() {
            return array[indice++];
        }
    }
}
