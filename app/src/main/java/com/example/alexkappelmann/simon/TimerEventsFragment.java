package com.example.alexkappelmann.simon;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;


/**
 * Created by alexkappelmann on 2/15/18.
 */

public class TimerEventsFragment extends Fragment {

    private static String TAG = "TimerEventsFragment";
    private Handler sequenceHandler = null;
    private Handler timeoutHandler = null;
    private static int timeout_delay = 0;
    private static int seq_delay = 0;

    public TimerEventDelegate eventDelegate = null;

    interface TimerEventDelegate{
        void updateSequenceEvent();
        void timeExpired();
    }

    @Override
    public void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        seq_delay = getArguments().getInt("SEQ_DELAY");
        timeout_delay = getArguments().getInt("TIMEOUT_DELAY");
        Log.e(TAG, "FRAGMENT was Created!!!");
    }
    @Override
    public void onDestroy () {
        super.onDestroy();
        Log.e(TAG, "FRAGMENT was Destroyed!!!");
    }

    public void startSequence() {
        Log.e("FRAGMENT", "startSequenced() called");
        if (sequenceHandler == null) {
            sequenceHandler = new Handler();
        } else {
            Log.e(TAG, "Handler already created!");
        }
        sequenceHandler.postDelayed(runnableSeq, 1000);
    }

    public void stopSequence() {
        Log.e(TAG, "stopSequence() called");
        sequenceHandler.removeCallbacks(runnableSeq);
        sequenceHandler = null;
    }

    public void startTimeoutTime() {
        Log.e(TAG, "startTimeoutTime() called");
        if (timeoutHandler == null) {
            timeoutHandler = new Handler();
        }
        timeoutHandler.postDelayed(runnableTimeout, timeout_delay);
    }

    public void stopTimeoutTime() {
        Log.e(TAG, "stopTimeoutTime() called");
        timeoutHandler.removeCallbacks(runnableTimeout);
    }

    public void resetTimeoutTime() {
        timeoutHandler.removeCallbacks(runnableTimeout);
        timeoutHandler.postDelayed(runnableTimeout, timeout_delay);
    }

    private Runnable runnableSeq = ()-> {
        Log.e(TAG, "Running in the runnable with the runnings.");

        if (eventDelegate != null)
            eventDelegate.updateSequenceEvent();

        if (sequenceHandler != null)
            sequenceHandler.postDelayed(this.runnableSeq, seq_delay);

        else
            Log.e(TAG, "handler is null");
    };

    private Runnable runnableTimeout = ()-> {
        Log.e(TAG, "Running in the runnable with the runnings.");
        if (eventDelegate != null)
            eventDelegate.timeExpired();

    };

}
