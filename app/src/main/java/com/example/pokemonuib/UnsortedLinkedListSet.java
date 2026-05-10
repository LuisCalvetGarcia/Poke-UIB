package com.example.pokemonuib;

import java.util.Iterator;

public class UnsortedLinkedListSet<E> {

    private class Node {
        private E elem;
        private Node next;
    }
    private Node first;

    public UnsortedLinkedListSet() {
        first=null;
    }

    public boolean contains(E elem) {
        Node nouNode=first;
        boolean trobat=false;
        while(nouNode!=null||trobat){
            if(nouNode.equals(elem)){
                trobat=true;
            }
            nouNode=nouNode.next;
        }
        return trobat;

    }

    public boolean add(E elem) {
        if(contains(elem)){
            return false;
        }
        Node nouNode=(Node)elem;
        nouNode.next=first;
        first=nouNode;
        return true;
    }

    public boolean remove(E elem) {
        Node p = first;
        Node pp= null;
        boolean trobat=false;
        if (!trobat&&p!=null){
            if(p.elem.equals(elem)){
                trobat=true;
            }
            if(trobat){
                pp=p;
                p=p.next;
            }
        }
        if(trobat){
            if(pp==null){
                first=p.next;
            }
            pp.next=p.next;
        }
        return trobat;
    }

    public boolean isEmpty() {
        return first==null;
    }


    public Iterator<E> iterator() {
        return new IteratorUnsortedLinkedListSet();
    }

    private class IteratorUnsortedLinkedListSet implements Iterator<E> {
        private Node current = first;

        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
        public E next() {
            E elem = current.elem;
            current = current.next;
            return elem;
        }
    }
}
