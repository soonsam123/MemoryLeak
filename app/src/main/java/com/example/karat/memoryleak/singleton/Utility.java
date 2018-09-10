package com.example.karat.memoryleak.singleton;

import android.util.Log;

public class Utility {
    private static Utility instance = null;
    private UpdateListener listener;

    // Make it a Singleton class
    private Utility(){}

    public static Utility getInstance() {
        if (instance == null) {
            instance = new Utility();
        }
        return instance;
    }

    public void setListener(UpdateListener listener) {
        this.listener = listener;
    }

    // Long running background thread
    public void startNewThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000 * 10);
                    if (listener != null)
                        listener.onUpdate();
                } catch (InterruptedException e) {
                    Log.i("Utility", "run: " + e.getMessage());
                }
            }
        }).start();
    }

    // Listener interface
    public interface UpdateListener{
        public void onUpdate();
    }
}
