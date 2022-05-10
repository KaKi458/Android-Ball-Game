package com.example.gra;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Button;
import android.widget.TextView;

public class Level extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level);

        //Zapisywanie wybranego poziomu trudności do plików programu, by móc z niego korzystać w innych aktywnociach
        SharedPreferences levelPreferences = getSharedPreferences("LEVEL", Context.MODE_PRIVATE);

        final int[] level = new int[1]; // zmienna przechowujaca wybrany poziom trudności

        //odczytanie aktualnego poziomu trudności i ustawienie odpowiedniego textu i położenia na SeekBarze
        level[0] = levelPreferences.getInt("LEVEL", 0);
        TextView levelText = findViewById(R.id.levelText);
        levelText.setText("Poziom trudności: " + level[0]);
        SeekBar seekBar = findViewById(R.id.seekBar);
        seekBar.setProgress(level[0]);

        //nasłuchiwacz zmian na SeekBarze
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            //zmiana położenia paska na SeekBarze = zmiana poziomu trudności
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                level[0] = progress; //przypisanie do zmiennej level aktualnego położenia na SeekBarze
                levelText.setText("Poziom trudności: " + progress);// zmiana textu nad SeekBarem na akutualny poziom trudności

            }
            //metody nieużywane
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //przycisk powodujący zatwierdzenie nowego poziomu trudności i zapisane go do plików programu
        Button okButton = findViewById(R.id.okButton);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                SharedPreferences.Editor editor = levelPreferences.edit();
                editor.putInt("LEVEL", level[0]);
                editor.apply();

                startActivity(new Intent(getApplicationContext(), Start.class));//powrót do ekranu startowego
            }
        });

    }
}