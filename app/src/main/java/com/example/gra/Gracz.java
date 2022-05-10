package com.example.gra;

import android.widget.ImageView;

class Gracz
{
    Gracz(int predkosc)
    {
        this.predkosc = predkosc;
    }
    public ImageView imageView;  //obrazek sterowanej postaci
    private float x, y;          //położenie
    private final int predkosc;

    void setX(float x)
    {
        this.x = x;
        this.imageView.setX(this.x);
    }
    void setY(float y)
    {
        this.y = y;
        this.imageView.setY(this.y);
    }
    float getX() { return  this.x; }
    float getY()
    {
        return this.y;
    }
    int getWysokosc()
    {
        return this.imageView.getHeight();
    }
    int getSzerokosc()
    {
        return this.imageView.getWidth();
    }
    int getPredkosc()
    {
        return this.predkosc;
    }
}
