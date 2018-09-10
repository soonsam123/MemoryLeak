package com.example.karat.memoryleak.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.karat.memoryleak.R;

import java.lang.ref.WeakReference;

/**
 * This Activity show examples of memory leaks when using inner class.
 * The common case is that when you use inned class they probably should
 * be static. Otherwise they will hold a reference to the activity even
 * after the activity is destroyed.
 *
 * Meanwhile, static inner class does not hold reference to the activity.
 * However, you can not access the Activity's UI and Themes from inside
 * static inner class. That's why you should construct a
 * {@link WeakReference} that holds the View you want to change as in
 * {@link MyRunnable} or holds the activity, that will come with
 * all the UI and Themes as in {@link MyHandler}.
 *
 * The methods below are not leaked, in order to leak them you should
 * make them non-static inner classes.
 */
public class SecondActivity extends AppCompatActivity {

    /**
     * Instances of static inner classes do not hold on implicit
     * reference to their outer class.
     * Holding a WeakReference to the entire Activity, I'll have
     * access to all the Activity's UI and themes.
     */
    private static class MyHandler extends Handler {
        private final WeakReference<SecondActivity> mActivity;

        public MyHandler(SecondActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            SecondActivity activity = mActivity.get();
            if (activity != null) {
                Toast.makeText(activity, "Done", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private final MyHandler mHandler = new MyHandler(this);

    private static final Runnable sRunnable = new Runnable() {
        @Override
        public void run() {
            /* ... */
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        TextView textView = findViewById(R.id.tv_handler);

        // Holding a reference to the Activity.
        mHandler.postDelayed(sRunnable, 1000 * 10);

        Handler myTextHandler = new Handler();
        myTextHandler.postDelayed(new MyRunnable(textView), 1000 * 10);

        startActivity(new Intent(SecondActivity.this, MainActivity.class));

    }

    /**
     * Holding a WeakReference to the TextView. I'll only
     * have access to the TextView itself.
     */
    private static class MyRunnable implements Runnable{
        WeakReference<TextView> textViewWeakReference;

        public MyRunnable(TextView textView) {
            this.textViewWeakReference = new WeakReference<>(textView);
        }

        @Override
        public void run() {
            TextView myText = textViewWeakReference.get();
            if (myText != null) {
                myText.setText("Done");
            }

        }
    }

    // ---------------------------------------------------------------------
    //                                  Menu
    // ---------------------------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_main:
                startActivity(new Intent(SecondActivity.this, MainActivity.class));
                break;
            case R.id.menu_second:
                startActivity(new Intent(SecondActivity.this, SecondActivity.class));
                break;
            case R.id.menu_third:
                startActivity(new Intent(SecondActivity.this, ThirdActivity.class));
                break;
            case R.id.menu_fourth:
                startActivity(new Intent(SecondActivity.this, FourthActivity.class));
                break;
            case R.id.menu_retrofit:
                startActivity(new Intent(SecondActivity.this, RetrofitActivity.class));
                break;
            case R.id.menu_handle_cc:
                startActivity(new Intent(SecondActivity.this, HandleCCActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
