    package com.example.pokemonuib;

    import java.util.Iterator;

    public class UnsortedArraySet<E> {
        //Este codigo hace refereenica a un conjunto no ordenado
        private E[] array;
        private int tam;

        //CONSTRUCTOR
        public UnsortedArraySet(int tamañoTotal) {
            this.array = (E[]) new Object[tamañoTotal]; //Instanciar el array de elementos del conjunto (vacio)
            this.tam = 0;
        }

        //Sirve para añadir un elmento al CONJUNTO
        public void add(E element) {
            //Añadir elemento si hay espacio en el conjunto
            if (tam < array.length) {
                array[tam++] = element;
            }
        }

        //Metodo para obtener un iterador porque es privada la clase
        public Iterator<E> iterator() {
            Iterator<E> it = new IteratorUnsortedArraySet();
            return it;
        }

        // Clase interna que implementa Iterator
        private class IteratorUnsortedArraySet implements Iterator<E> {
            private int indice;

            //CONSTRUCTOR
            private IteratorUnsortedArraySet() {

                this.indice = 0;
            }

            //Metodo que verifica si hay mas elementos
            @Override
            public boolean hasNext() {
                return indice < tam;
            }

            //Retorna el siguiente elemento del array del conjunto una vez comprobado que hay elemntos
            @Override
            public E next() {
                return array[indice++];
            }
        }
    }
