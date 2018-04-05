package com.example.alexkappelmann.simon;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

public class MainEntryActivity extends AppCompatActivity {

    private final static String TAG = "MainEntryActivity";
    private final static String MY_PREFS="mypref";
    private final static String HIGHSCORE_KEY = "HighScore";
    private Button startButton;
    private Button clearHighButton;
    private RadioGroup difficultyGroup;
    private int highscore;
    private TextView highscoreText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_entry);

        startButton = findViewById(R.id.start_button_main);
        clearHighButton = findViewById(R.id.clear_highscore);
        difficultyGroup = findViewById(R.id.difficultyRadioGroup);
        highscoreText = findViewById(R.id.textView_mainHighscore);

        SharedPreferences sharedPrefs = this.getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
        highscore = sharedPrefs.getInt(HIGHSCORE_KEY, 0);
        highscoreText.setText(String.valueOf(highscore));

        clearHighButton.setOnClickListener( (View v) -> {
            SharedPreferences.Editor editor = sharedPrefs.edit();
            highscore = 0;
            editor.putInt(HIGHSCORE_KEY, highscore).commit();
            highscoreText.setText(String.valueOf(highscore));
        });

        startButton.setOnClickListener( (View v) -> {
            Log.e(TAG, "Start Button Clicked");

            int level;
            switch (difficultyGroup.getCheckedRadioButtonId()) {
                case R.id.radioEasy:  //SEQ_DELAY=0.75s, TIMEOUT_DELAY=9s, INIT_SIZE=1
                    level = 0;
                    break;
                case R.id.radioNormal://SEQ_DELAY=0.5s, TIMEOUT_DELAY=6s, INIT_SIZE=3
                default:
                    level = 1;
                    break;
                case R.id.radioHard: //SEQ_DELAY=0.25s, TIMEOUT_DELAY=3s, INIT_SIZE=5
                    level = 2;
                    break;
            }

            Intent intent = new Intent(getApplicationContext(), GameControllerActivity.class);

            //Controls how fast the sequence is displayed
            intent.putExtra("DIFFICULTY_SEQ_DELAY",750 - (level * 250));

            //Time given to select next color in sequence
            intent.putExtra("DIFFICULTY_TIMEOUT_DELAY", 9000 - (level * 3000));

            //Initial Number of colors in sequence at start of game
            intent.putExtra("DIFFICULTY_INIT_SEQ_SIZE", (level * 2) + 1);

            startActivity(intent);
        });
    }
}

