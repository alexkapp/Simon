package com.example.alexkappelmann.simon;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ToggleButton;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by alexkappelmann on 3/21/18.
 *
 * UI Fragment for Game Buttons
 */

public class GameButtonFragment extends Fragment {

    private ToggleButton buttons[] = null;
    private Map<Integer, Integer> buttonColorMap;

    public GameControllerFragmentDelegate delegate= null;

    interface GameControllerFragmentDelegate {
        void colorButtonClicked(int buttonIndex);
        void buttonAnimComplete();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.buttons_layout, container, false);

        buttons = new ToggleButton[]{view.findViewById(R.id.buttonRed), view.findViewById(R.id.buttonBlue),
                view.findViewById(R.id.buttonGreen), view.findViewById(R.id.buttonYellow)};

        bindButtons();

        buttonColorMap = new LinkedHashMap<>();
        buttonColorMap.put(getResources().getColor(R.color.red_off), getResources().getColor(R.color.red_on));
        buttonColorMap.put(getResources().getColor(R.color.blue_off), getResources().getColor(R.color.blue_on));
        buttonColorMap.put(getResources().getColor(R.color.green_off), getResources().getColor(R.color.green_on));
        buttonColorMap.put(getResources().getColor(R.color.yellow_off), getResources().getColor(R.color.yellow_on));
        return view;
    }

    private void bindButtons() {
        buttons[0].setOnClickListener( (View v) -> {
            buttons[0].setChecked(false);
            delegate.colorButtonClicked(0);
        });
        buttons[1].setOnClickListener( (View v) -> {
            buttons[1].setChecked(false);
            delegate.colorButtonClicked(1);
        });
        buttons[2].setOnClickListener( (View v) -> {
            buttons[2].setChecked(false);
            delegate.colorButtonClicked(2);
        });
        buttons[3].setOnClickListener( (View v) -> {
            buttons[3].setChecked(false);
            delegate.colorButtonClicked(3);
        });
    }

    public boolean isButtonChecked(int btnIndex) {
        return buttons[btnIndex].isChecked();
    }

    public void turnButtonOn(int btnIndex) {
        buttons[btnIndex].setChecked(true);
    }

    public void turnButtonOff(int btnIndex) {
        buttons[btnIndex].setChecked(false);
    }

    public synchronized void disableGameButtonsClicks() {
        if (buttons == null) return;
        for (Button b: buttons) b.setClickable(false);
    }

    public synchronized void enableGameButtonsClicks() {
        if (buttons == null) return;
        for (Button b: buttons) b.setClickable(true);
    }

    public void runGameOverButtonAnim(int missedBtnIndex) {
        final ToggleButton missedButton = buttons[missedBtnIndex];
        final Drawable backgroundRes = missedButton.getBackground(); //used to reset background after animation
        final int missedOffColor = (Integer) buttonColorMap.keySet().toArray()[missedBtnIndex];
        final int missedOnColor = (Integer) buttonColorMap.values().toArray()[missedBtnIndex];

        ObjectAnimator anim = ObjectAnimator.ofInt(missedButton, "backgroundColor", missedOffColor, missedOnColor);
        anim.setDuration(300);
        anim.setEvaluator(new ArgbEvaluator());
        anim.setRepeatMode(ObjectAnimator.REVERSE);
        anim.setRepeatCount(5);

        anim.addListener(new Animator.AnimatorListener() {
            @Override public void onAnimationStart(Animator animation)  {}
            @Override public void onAnimationCancel(Animator animation) {}
            @Override public void onAnimationRepeat(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {
                missedButton.setBackgroundDrawable(backgroundRes);
                delegate.buttonAnimComplete();
            }
        });
        anim.start();
    }
}
