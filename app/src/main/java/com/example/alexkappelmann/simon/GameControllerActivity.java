package com.example.alexkappelmann.simon;

import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

/*
 * Game Controller Activity-game logic
 */
public class GameControllerActivity extends AppCompatActivity
                                    implements TimerEventsFragment.TimerEventDelegate,
                                               GameOverDialogFragment.PlayAgainListener,
                                               GameButtonFragment.GameControllerFragmentDelegate {

    private final static String TIME_FRAG_TAG = "TimerEventsFragment";
    private final static String G_OVER_FRAG_TAG = "GameOverFragment";
    private final static String TAG = "GameControllerActivity";
    private final static String MY_PREFS="mypref";
    private final static String HIGHSCORE_KEY = "HighScore";

    private SharedPreferences sharedPrefs;

    private GameButtonFragment gameButtonFragment;
    private TimerEventsFragment timerEventsFragment;

    private TextView statusText;
    private TextView scoreText;
    private TextView highscoreText;

    private SimonModal mSimonModal;

    private int seq_delay;
    private int init_seq_size;
    private int score;
    private int highscore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity_container);
        FragmentManager fragManager = getFragmentManager();

        int timeout_delay = getIntent().getIntExtra("DIFFICULTY_TIMEOUT_DELAY", 6000);
        init_seq_size = getIntent().getIntExtra("DIFFICULTY_INIT_SEQ_SIZE", 3);
        seq_delay = getIntent().getIntExtra("DIFFICULTY_SEQ_DELAY", 500);

        gameButtonFragment = (GameButtonFragment) fragManager.findFragmentById(R.id.game_view_container_layout);
        if (gameButtonFragment == null) {
            gameButtonFragment = new GameButtonFragment();
            fragManager.beginTransaction()
                    .add(R.id.game_view_container_layout, gameButtonFragment)
                    .commit();
        }
        gameButtonFragment.delegate = this;

        //Set up time callbacks for running the sequence animation and user input timeout
        Bundle timeFragBundle = new Bundle();
        timeFragBundle.putInt("TIMEOUT_DELAY",timeout_delay);
        timeFragBundle.putInt("SEQ_DELAY",seq_delay);
        timerEventsFragment = (TimerEventsFragment) fragManager.findFragmentByTag(TIME_FRAG_TAG);
        if (timerEventsFragment == null) {
            timerEventsFragment = new TimerEventsFragment();
            fragManager.beginTransaction().add(timerEventsFragment, TIME_FRAG_TAG).commit();
        }
        timerEventsFragment.setArguments(timeFragBundle);
        timerEventsFragment.eventDelegate = this;

        scoreText = findViewById(R.id.scoreNumber);
        statusText = findViewById(R.id.statusText);
        highscoreText = findViewById(R.id.bestScoreNumber);

        sharedPrefs = this.getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
        highscore = sharedPrefs.getInt(HIGHSCORE_KEY, 0);
        highscoreText.setText(String.valueOf(highscore));

        mSimonModal = new SimonModal(init_seq_size);
    }

    @Override
    protected void onStart() {
        startGame();
        super.onStart();
    }

    public void startGame() {
        score = 0;
        highscoreText.setText(String.valueOf(highscore));
        scoreText.setText(String.valueOf(score));
        statusText.setText("WATCH");
        mSimonModal.resetListPosition();
        gameButtonFragment.disableGameButtonsClicks();
        timerEventsFragment.startSequence();
    }

    /* Advances the sequence position after the user
     * selects the correct Button in the sequence */
    private void updateSequencePos() {

        if (mSimonModal.onLastColor()) {
            score+=1;
            timerEventsFragment.stopTimeoutTime();
            mSimonModal.addRandColor();
            mSimonModal.resetListPosition();

            statusText.setText("WATCH");
            scoreText.setText(String.valueOf(score));
            if (score > highscore)
                highscoreText.setText(String.valueOf(score));

            timerEventsFragment.startSequence();
            gameButtonFragment.disableGameButtonsClicks();
        }
        else {
            if (score < init_seq_size)
                score+=1;
            mSimonModal.advanceColorSeq();
            timerEventsFragment.resetTimeoutTime();
        }
    }

    private void gameOver(boolean isTimeoutLose) {
        timerEventsFragment.stopTimeoutTime();
        gameButtonFragment.disableGameButtonsClicks();

        if (score > highscore) {
            highscore = score;
            Log.e(TAG, "NEW HIGH SCORE");
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putInt(HIGHSCORE_KEY, highscore);
            editor.commit();
        }
        if (!isTimeoutLose)
            gameButtonFragment.runGameOverButtonAnim(mSimonModal.getCurrentColor());
        else
            showGameOverDialogueFrag(true);
    }

    private void showGameOverDialogueFrag(boolean isTimeout) {
        FragmentManager fm = getFragmentManager();
        GameOverDialogFragment gameover_frag = GameOverDialogFragment.newInstance(isTimeout, score);
        gameover_frag.playAgainListener = this;
        gameover_frag.show(fm, "fragment_alert");
    }

    // checks if the user selected button is correct
    @Override
    public void colorButtonClicked(int c) {
        if (mSimonModal.isInputCorrect(c)) {
            gameButtonFragment.turnButtonOff(c);
            this.updateSequencePos();
        } else gameOver(false);
    }

    /* callback function for displaying the sequence animation.
     * Every color in the sequence remains current for two callbacks.
     * The current button is turned-on on the first call
     * and then turned-off on the second
     */
    @Override
    public synchronized void updateSequenceEvent() {

        int curColor = mSimonModal.getCurrentColor();
        if (!gameButtonFragment.isButtonChecked(curColor)) {
            gameButtonFragment.turnButtonOn(curColor);
        }
        else {
            gameButtonFragment.turnButtonOff(curColor);
            if (mSimonModal.onLastColor()) {
                timerEventsFragment.stopSequence();
                gameButtonFragment.enableGameButtonsClicks();
                mSimonModal.resetListPosition();
                statusText.setText("REPEAT");
                timerEventsFragment.startTimeoutTime();
                return;
            }
            mSimonModal.advanceColorSeq();
        }

    }

    /* Called when the user runs out of the allotted time to select a color. */
    @Override
    public synchronized void timeExpired() {
        statusText.setText("Time Expired");
        gameOver(true);
    }

    /* Called from "GameOverDialogueFragment" when user selects to play again*/
    @Override
    public void restartGame() {
        mSimonModal = new SimonModal(init_seq_size);
        startGame();
    }

    @Override
    public void buttonAnimComplete() {
        showGameOverDialogueFrag(false);
    }
}


