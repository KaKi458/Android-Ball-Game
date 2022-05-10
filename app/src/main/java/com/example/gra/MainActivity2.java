package com.example.gra;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Button;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity2 extends AppCompatActivity implements View.OnTouchListener {

    //deklaracja zmiennych, zmiennych sterujących(boolowskich), timera i handlerów, obiektów związanych z widgetami
    private TextView startLabel, wynikLabel;
    int wynik, level;

    Button lewoButton;
    Button prawoButton;

    private FrameLayout frameLayout;//ramka w której odbywa się gra
    int wysokoscFrame, szerokoscFrame;//wysokość i szerokość pola gry, niezbędne do losowania i aktualizowania pozycji piłek i gracza

    private Ball football, basketball, tennis, baseball, blackBall;//deklaracja piłek
    private Gracz gracz;//deklaracja postaci, którą sterujemy

    private Timer timer = new Timer();
    private Handler handler = new Handler();
    private Handler handler2 = new Handler();

    private boolean lewoFlaga = false; //czy naciśnięto przycisk Lewo
    private boolean prawoFlaga = false;//czy naciśnięto przycisk Prawo
    private boolean startFlaga = false;//czy gra się zaczęła (czy pierwszy raz naciśnięto ekran)


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        //odczytanie poziomu trudności i przypisanie do zmiennj level
        SharedPreferences levelPreferences = getSharedPreferences("LEVEL", MODE_PRIVATE);
        level = levelPreferences.getInt("LEVEL", 0);

        wynikLabel = findViewById(R.id.wynikP);
        startLabel = findViewById(R.id.startP);

        lewoButton = findViewById(R.id.lewo);
        prawoButton = findViewById(R.id.prawo);

        //konstruktory piłek
        football = new Ball(10, 70, 10);
        basketball = new Ball(10, 10, 30);
        tennis = new Ball(10, 30, -5);
        baseball = new Ball(15, 20, -10);
        blackBall = new Ball(20, 80, 0);

        //konstruktor gracza
        gracz = new Gracz(20);
        gracz.imageView = findViewById(R.id.androidP);

        //przypisanie piłkom odpowiedniego obrazka i ustawienie pozycji początkowej, niewidocznej i umożliwającej wylowoanie nowej pozycji po uruchomieniu timera
        inicjuj(football, R.id.footballP);
        inicjuj(basketball, R.id.koszykowkaP);
        inicjuj(tennis, R.id.tennisP);
        inicjuj(baseball, R.id.baseballP);
        inicjuj(blackBall, R.id.blackP);

        //przypisanie listenera do przycisków
        findViewById(R.id.lewo).setOnTouchListener(this);
        findViewById(R.id.prawo).setOnTouchListener(this);

    }

    //funkcja przypisująca piłce obrazek i ustawiająca początkowe położenie
    public void inicjuj(Ball ball, int id) {
        ball.imageView = findViewById(id);
        ball.setX(-100.0f);
        ball.setY(-100.0f);
    }

    //funkcja sprawdzająca, czy nastąiło zderzenie piłki z postacią
    public void zderzenie(Ball ball, Gracz gracz)
    {
        //jeśli środek piłki znajdzie się w obrębie obrazka gracza, to znaczy, że nastąpiło zderzenie
        if (gracz.getX() <= ball.getSrodekX() && ball.getSrodekX() <= gracz.getX() + gracz.getSzerokosc() &&
                gracz.getY() <= ball.getSrodekY() && ball.getSrodekY() <= gracz.getY() + gracz.getWysokosc())
        {
            wynik += ball.getPunkty();//zaktualizowanie wyniku
            if(wynik < 0) wynik = 0;//wynik nie może być ujemny

            ball.setY(wysokoscFrame + 100.0f);//piłka zostaje przesunięta poza widoczny ekran, w taką pozycję, aby mogła zostać wylosowana jej nowa pozycja

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

        ball.setY(ball.getY() + ball.getPredkosc()); //przesuwanie piłki w zależności od prędkości
        //jeśli piłka wyszła poza ekran - wylosowanie nowej pozycji, by znów pojawiła się na ekrane i poruszałą w stronę gracza
        if(ball.getY() > wysokoscFrame)
        {
            ball.setY(100.0f - ball.getCzestotliwosc());
            ball.losujX(szerokoscFrame);
        }

        wynikLabel.setText("Wynik : " + wynik);//zaktualizowanie wyświetlanego wyniku

    }

    //odświeżanie pozycji sterowanej postaci
    public void aktualizacjaGracza(Gracz gracz) {
        //ruch w lewo lub w prawo w zależności od naciśniętego przycisku
        if (lewoFlaga)
            gracz.setX(gracz.getX() - gracz.getPredkosc());
        else if (prawoFlaga)
            gracz.setX(gracz.getX() + gracz.getPredkosc());

        //ograniczenia ruchu, by gracz pozostawał widoczny na ekranie
        if (gracz.getX() < 0) gracz.setX(0);
        if (gracz.getX() > szerokoscFrame - gracz.getSzerokosc()) gracz.setX(szerokoscFrame - gracz.getSzerokosc());

    }

    //sprawdzanie, czy ekran został dotknięty
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (!startFlaga) //po pierwszym dotknięciu ekranu timer rozpoczyna pracę i następuje właściwa rozgrywka, kolejne dotknięcia ekranu nie powodują żadnych akcji
        {
            startFlaga = true;

            //pobranie wysokości i szerokości ramki, na której odbywa się gra
            frameLayout = findViewById(R.id.frameP);
            wysokoscFrame = frameLayout.getHeight();
            szerokoscFrame = frameLayout.getWidth();

            //przypisanie graczowi pozycji początkowej
            gracz.setY(wysokoscFrame - gracz.getWysokosc());
            gracz.setX(szerokoscFrame / 2.0f);

            gracz.imageView.setVisibility(View.VISIBLE); //wyświetlenie gracza na ekranie
            startLabel.setVisibility(View.GONE);//znika napisa "ZACZYNAMY"

            //zadania timera
            //aktualizacja pozycji piłek i gracza i sprawdzanie zderzenia co 20ms
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            aktualizacjaGracza(gracz);
                            aktualizacjaPozycji(football, gracz);
                            aktualizacjaPozycji(basketball, gracz);
                            aktualizacjaPozycji(tennis, gracz);
                            aktualizacjaPozycji(baseball, gracz);
                            aktualizacjaPozycji(blackBall, gracz);
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

        }
        return super.onTouchEvent(event);
    }

    //czy dotknięto przycisk
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent)
    {
        if(motionEvent.getAction() == MotionEvent.ACTION_DOWN)
        {
            if (view.getId() == lewoButton.getId()) //jeśli wciśnięto lewy przycisk
                lewoFlaga = true;
            else if (view.getId() == prawoButton.getId()) //jeśłi wciśnięto prawy przycisk
                prawoFlaga = true;
        }
        else //jeśli żaden przycisk nie jest wciśnięty, flagi są ustawione na false - ruch nie nastęuje11
        {
            lewoFlaga = false;
            prawoFlaga = false;
        }

    return true;
    }
}