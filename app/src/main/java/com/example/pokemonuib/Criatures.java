package com.example.pokemonuib;

public class Criatures {
    String nom;
    int x;
    int y;
    public Criatures(String n, int x, int y){
        this.nom=n;
        this.x=x;
        this.y=y;
    }
    public String getNom(){
        return nom;

    }
    public int getX(){
        return x;
    }
    public int getY(){
        return y;
    }
    public void setX(int x){
        this.x=x;
    }
    public void setY(int y){
        this.y=y;}
    public void setNom(String nom){
        this.nom=nom;
    }
}
