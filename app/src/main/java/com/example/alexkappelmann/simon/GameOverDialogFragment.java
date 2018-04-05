package com.example.alexkappelmann.simon;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by alexkappelmann on 2/26/18.
 *
 * A Dialog Box that is displayed when the user loses.
 * Displays data from the recent game.
 * Asks the user to play again
 **/
public class GameOverDialogFragment extends DialogFragment {

    private Button yesButton;
    private Button noButton;
    private TextView timeoutLoseText;
    private TextView scoreText;
    private int score;

    interface PlayAgainListener {
        void restartGame();
    }
    public PlayAgainListener playAgainListener = null;
    public GameOverDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }
    public static GameOverDialogFragment newInstance(boolean isTimeoutLose, int score) {
        GameOverDialogFragment frag = new GameOverDialogFragment();
        Bundle args = new Bundle();
        args.putBoolean("ISTIMEOUTLOSE", isTimeoutLose);
        args.putInt("SCORE", score);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.game_over, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get fields from view
        yesButton = view.findViewById(R.id.button_gameoverYes);
        noButton = view.findViewById(R.id.button_gameoverNo);
        timeoutLoseText = view.findViewById(R.id.textView_timeoutGameover);
        scoreText = view.findViewById(R.id.textView_gameoverScore);

        score = getArguments().getInt("SCORE", 0);

        if (getArguments().getBoolean("ISTIMEOUTLOSE", false))
            timeoutLoseText.setAlpha(1);

        scoreText.setText(String.valueOf(score));

        yesButton.setOnClickListener( (View v) -> {
            playAgainListener.restartGame();
            dismiss();
        });

        noButton.setOnClickListener( (View v) -> {
            //Go back to MainActivity/Entry Screen
            Intent intent = new Intent(getActivity().getApplicationContext(), MainEntryActivity.class);
            startActivity(intent);
        });
    }
}
