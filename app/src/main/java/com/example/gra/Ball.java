package com.example.gra;

import android.widget.ImageView;

class Ball
{
    Ball(int predkosc, int czestotliwosc, int punkty)
    {
        this.predkosc = predkosc;
        this.czestotliwosc = czestotliwosc;
        this.punkty = punkty;
    }

    public ImageView imageView;   //obrazek przypisany do danej piłki
    private float x, y;           //położenie x i y piłki
    private int predkosc;
    private final int czestotliwosc,punkty;  //częstotliwość umożliwia zróżnicowanie czasu pojawiania się piłek na ekranie

    void setX(float x)
    {
        this.x = x;
        this.imageView.setX(this.x);  //oprócz zmiany pozycji należy jeszcze zmienić położenie samego obrazka
    }
    void setY(float y)
    {
        this.y = y;
        this.imageView.setY(this.y);  //tak samo jak wyżej
    }
    void losujX(int szerokoscFrame)  //metoda umożliwia wylosowanie położenia x, tak by obrazek pojawiał się za każdym razem w innym miejscu na ekranie
    {
        this.x = (float)Math.floor(Math.random() * (szerokoscFrame - this.imageView.getWidth()));
        this.imageView.setX(this.x); //po wylosowaniu pozycji zmieniamy też położenie samego obrazka
    }
    void losujY(int wysokoscFrame) //tak samo jak wyżej, tylko losowana jest pozycja y
    {
        this.y = (float)Math.floor(Math.random() * (wysokoscFrame - this.imageView.getHeight()));
        this.imageView.setY(this.y);
    }
    void setPredkosc(int predkosc)
    {
        this.predkosc = predkosc;
    }
    float getX()
    {
        return this.x;
    }
    float getY()
    {
        return this.y;
    }
    float getSrodekX()//zwraca składową x środka obrazka
    {
        return this.x + this.imageView.getWidth()/2.0f;
    }
    float getSrodekY() //zwraca składową y środka obrazka
    {
        return this.y + this.imageView.getHeight()/2.0f;
    }
    int getPredkosc()
    {
        return this.predkosc;
    }
    int getPunkty()
    {
        return this.punkty;
    }
    int getCzestotliwosc()
    {
        return this.czestotliwosc;
    }
}

