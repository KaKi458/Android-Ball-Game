package com.example.gra;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class Start extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        //powiązanie java z xml
        Button poziomo = findViewById(R.id.poziomo);
        Button pionowo = findViewById(R.id.pionowo);
        ImageButton ustawienia = findViewById(R.id.ustawienia);
        ImageButton info = findViewById(R.id.info);

        //nasłuchiwacze kliknięcia przycisku z funkcją wykonującą się w razie naciśnięcia

        poziomo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class)); // przeniesienie do mapy poziomej
            }
        });

        pionowo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity2.class)); //przeniesienie do mapy pionowej
            }
        });


        ustawienia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Level.class)); //wyświetlenie wyboru poziomu trudności
            }
        });

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Info.class)); //wyświetlenie informacji o punktach za zderzenia z każdą piłą

            }
        });

    }
}