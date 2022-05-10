package com.example.gra;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class GameOver extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        //powiązanie obiektów java z widgetami android
        TextView wynikLabel = findViewById(R.id.wynikLabel);
        TextView najlepszyWynikLabel = findViewById(R.id.najlepszyWynikLabel);
        Button jeszczeRaz = findViewById(R.id.jeszczeRaz);

        //odbiór wyniku przesłanego z MainActivity(2)
        int wynik = getIntent().getIntExtra("WYNIK", 0);
        wynikLabel.setText("Wynik: "+ wynik);//wyświetlenie wyniku na ekranie

        //odczytanie dotychczasowego najwyższego wyniku
        SharedPreferences sharedPreferences = getSharedPreferences("NAJLEPSZY", Context.MODE_PRIVATE);
        int najlepszyWynik = sharedPreferences.getInt("NAJLEPSZY", 0);

        //porównanie najwyższego wyniku z obecnym i ewentualne zaktualizowanie go
        if (wynik > najlepszyWynik)
        {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("NAJLEPSZY", wynik);
            editor.apply();
            najlepszyWynikLabel.setText("Najlepszy wynik: " + wynik);

        }
        else
            najlepszyWynikLabel.setText("Najlepszy wynik: " + najlepszyWynik);

        //powrót do ekranu startowego po wciśnięciu przycisku
        jeszczeRaz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Start.class));
            }
        });

    }
}
