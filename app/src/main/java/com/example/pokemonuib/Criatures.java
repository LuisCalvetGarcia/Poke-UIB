package com.example.pokemonuib;

/**
 * Represents a magical creature in the game.
 * Stores the creature's name and its coordinates on the map.
 */
public class Criatures {
    String nom; // Name of the creature
    int x;      // X-coordinate on the map
    int y;      // Y-coordinate on the map

    /**
     * Constructor to initialize a new creature.
     * * @param n Name of the creature
     * @param x Initial X-coordinate
     * @param y Initial Y-coordinate
     */
    public Criatures(String n, int x, int y){
        this.nom = n;
        this.x = x;
        this.y = y;
    }

    // --- Getters ---

    public String getNom(){
        return nom;
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }

    // --- Setters ---

    public void setX(int x){
        this.x = x;
    }

    public void setY(int y){
        this.y = y;
    }

    public void setNom(String nom){
        this.nom = nom;
    }
}
