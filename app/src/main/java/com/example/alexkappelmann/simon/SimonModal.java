package com.example.alexkappelmann.simon;

import java.util.ArrayList;
import java.util.Random;
import android.util.Log;
/**
 * Created by alexkappelmann on 2/12/18.
 */

public class SimonModal {

    private ArrayList<Integer> colorList;
    private final static String Tag = "SimonModal";
    private int mPosition = 0;
    private final int initSize;
    private Random rand;

    public SimonModal(int numColors) {
        initSize = numColors;
        colorList = new ArrayList<>();
        rand = new Random(System.currentTimeMillis());

        for (int i = 0; i < initSize; i++)
            addRandColor();
    }

    public void addRandColor() {
        colorList.add(rand.nextInt(9000) % 4);
    }

    public boolean onLastColor() {
        return (mPosition == colorList.size() - 1);
    }

    public void advanceColorSeq() {
        mPosition = mPosition + 1;
    }

    public void resetListPosition() {
        mPosition = 0;
    }

    public boolean isInputCorrect(int color) {
        return (getCurrentColor().intValue() == color);
    }

    public Integer getCurrentColor(){
        return colorList.get(mPosition);
    }

    /*
    private void printList() {
        for (int c: colorList) {
            switch (c) {
                case 0:  Log.e(Tag, "RED");
                    break;
                case 1:Log.e(Tag, "BLUE");
                    break;
                case 2:Log.e(Tag, "GREEN");
                    break;
                case 3: Log.e(Tag, "YELLOW");
                    break;
                default:Log.e(Tag, "UNKNOWN");
                    break;
            }
        }
    }*/
}
