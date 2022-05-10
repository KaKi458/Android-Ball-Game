package com.example.gra;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity
{
    //deklaracja zmiennych, zmiennych sterujących(boolowskich), timera i handlerów, obiektów związanych z widgetami
    private TextView startLabel, wynikLabel;
    int wynik, level;

    private FrameLayout frameLayout; //ramka w której odbywa się gra
    int wysokoscFrame, szerokoscFrame; //wysokość i szerokość pola gry, niezbędne do losowania i aktualizowania pozycji piłek i gracza

    private Ball football, basketball, tennis,baseball, blackBall; //deklaracja piłek
    private Gracz gracz; //deklaracja postaci, którą sterujemy

    private Timer timer = new Timer();
    private Handler handler = new Handler();
    private Handler handler2 = new Handler();

    private boolean dotykFlaga = false; //czy naciśnięto ekran
    private boolean startFlaga = false; //czy gra się zaczęła (czy pierwszy raz naciśnięto ekran)

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //odczytanie poziomu trudności i przypisanie do zmiennj level
        SharedPreferences levelPreferences = getSharedPreferences("LEVEL", MODE_PRIVATE);
        level = levelPreferences.getInt("LEVEL", 0);

        startLabel = findViewById(R.id.start);
        wynikLabel = findViewById(R.id.wynik);

        //konstruktory piłek
        football = new Ball(20, 70, 10);
        basketball = new Ball(15, 10, 30);
        tennis = new Ball(20, 30, -5);
        baseball = new Ball(25, 20, -10);
        blackBall = new Ball(30, 80, 0);

        //konstruktor gracza
        gracz = new Gracz(20);
        gracz.imageView = findViewById(R.id.android);//przypisanie graczowi odpowiedniego obrazka z xml

        //przypisanie piłkom odpowiedniego obrazka i ustawienie pozycji początkowej, niewidocznej i umożliwającej wylowoanie nowej pozycji po uruchomieniu timera
        inicjuj(football, R.id.football);
        inicjuj(basketball, R.id.koszykowka);
        inicjuj(tennis, R.id.tennis);
        inicjuj(baseball, R.id.baseball);
        inicjuj(blackBall, R.id.black);
    }


    //funkcja przypisująca piłce obrazek i ustawiająca początkowe położenie
    public  void inicjuj(Ball ball, int id)
    {
        ball.imageView = findViewById(id);
        ball.setX(-100.0f);
        ball.setY(-100.0f);
    }

    //funkcja sprawdzająca, czy nastąiło zderzenie piłki z postacią
    public void zderzenie(Ball ball, Gracz gracz)
    {
        //jeśli środek piłki znajdzie się w obrębie obrazka gracza, to znaczy, że nastąpiło zderzenie
        if (0 <= ball.getSrodekX() && ball.getSrodekX() <= gracz.getSzerokosc() &&
                 gracz.getY() <= ball.getSrodekY() && ball.getSrodekY() <= gracz.getY() + gracz.getWysokosc())
        {
            wynik += ball.getPunkty(); //zaktualizowanie wyniku
            if(wynik < 0) wynik = 0; //wynik nie może być ujemny
            ball.setX(-100.0f); //piłka zostaje przesunięta poza widoczny ekran, w taką pozycję, aby mogła zostać wylosowana jej nowa pozycja

            //jeśli nastąpiło zderzenie z blackBall, to następuje przegrana, timer zostaje zatrzymany
            if(ball == blackBall)
            {
                if (timer != null)
                {
                    timer.cancel();
                    timer = null;
                }

                //przejście do ekranu GameOver i wysłanie tam uzyskanego wyniku
                Intent intent = new Intent(getApplicationContext(), GameOver.class);
                intent.putExtra("WYNIK", wynik);
                startActivity(intent);
            }
        }
    }


    //odświeżanie pozycji piłki na ekranie
    public void aktualizacjaPozycji(Ball ball, Gracz gracz)
    {
        zderzenie(ball, gracz);//sprawdzanie, czy nastąpiło zderzenie

        ball.setX(ball.getX() - ball.getPredkosc()); //przesuwanie piłki w zależności od prędkości
        //jeśli piłka wyszła poza ekran - wylosowanie nowej pozycji, by znów pojawiła się na ekrane i poruszałą w stronę gracza
        if(ball.getX() < 0)
        {
            ball.setX(szerokoscFrame + 100.0f - ball.getCzestotliwosc());
            ball.losujY(wysokoscFrame);
        }
        wynikLabel.setText("Wynik : " + wynik); //zaktualizowanie wyświetlanego wyniku
    }

    //odświeżanie pozycji sterowanej postaci
    public void aktualizacjaGracza(Gracz gracz)
    {
        //ruch w górę, jeśli ekran dotknięty, w dół jeśli nie
        if (dotykFlaga)
            gracz.setY(gracz.getY() - gracz.getPredkosc());
        else
            gracz.setY(gracz.getY() + gracz.getPredkosc());

        //ograniczenia ruchu, by gracz pozostawał widoczny na ekranie
        if (gracz.getY() < 0) gracz.setY(0);
        if (gracz.getY() > wysokoscFrame - gracz.getWysokosc()) gracz.setY(wysokoscFrame - gracz.getWysokosc());
    }

    //sprawdzanie, czy ekran jest dotknięty
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (!startFlaga) //pierwsze dotknięcie ekranu powoduje start gry i uruchomienie timera
        {
            startFlaga = true;

            //pobranie wysokości i szerokości ramki, na której odbywa się gra
            frameLayout = findViewById(R.id.frame);
            wysokoscFrame = frameLayout.getHeight();
            szerokoscFrame = frameLayout.getWidth();

            gracz.setY(wysokoscFrame/2.0f); //przypisanie graczowi pozycji początkowej

            startLabel.setVisibility(View.GONE); //znika napisa "ZACZYNAMY"

            //zadania timera
            //aktualizacja pozycji piłek i gracza i sprawdzanie zderzenia co 20ms
            timer.schedule(new TimerTask()
            {
                @Override
                public void run()
                {
                    handler.post(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            aktualizacjaGracza(gracz);
                            aktualizacjaPozycji(football,gracz);
                            aktualizacjaPozycji(basketball,gracz);
                            aktualizacjaPozycji(tennis,gracz);
                            aktualizacjaPozycji(baseball,gracz);
                            aktualizacjaPozycji(blackBall,gracz);
                        }
                    });
                }
            }, 0, 20);

            //zwiększanie prędkości piłek w zależności od poziomu trudności co 2500ms
            timer.schedule(new TimerTask()
            {
                @Override
                public void run()
                {
                    handler2.post(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            football.setPredkosc(football.getPredkosc() + level);
                            basketball.setPredkosc(basketball.getPredkosc() + level);
                            tennis.setPredkosc(tennis.getPredkosc() + level);
                            baseball.setPredkosc(baseball.getPredkosc() + level);
                            blackBall.setPredkosc(blackBall.getPredkosc() + level);
                        }
                    });
                }
            }, 2500, 2500);


        } else //gdy tmer juz ruszł, funkcja sprawdza jedynie czy ekran jest nacisnięty czy nie i zmienia w zależności od tego wartość flagi
        {
            if (event.getAction() == MotionEvent.ACTION_DOWN)
                dotykFlaga = true;

            else if (event.getAction() == MotionEvent.ACTION_UP) {
                dotykFlaga = false;
            }
        }
        return super.onTouchEvent(event);
    }
}