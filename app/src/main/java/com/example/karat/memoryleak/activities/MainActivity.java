package com.example.karat.memoryleak.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.karat.memoryleak.R;
import com.example.karat.memoryleak.singleton.SampleClass;
import com.example.karat.memoryleak.singleton.Utility;

import java.lang.ref.WeakReference;
import java.util.Date;

/**
 * This Activity shows some examples of memory leaks and
 * how to avoid them.
 * 1. Building a {@link AsyncTask} non-static causes a memory leak
 * because when the activity is destroyed the background
 * task will hold a reference to the activity and will prevent
 * the garbage collector to take the activity instance.
 * Therefore, you need to declare {@link AsyncTask} static
 * and if you need to access the activity's resources you
 * should make a {@link WeakReference} of the activity
 * in the {@link AsyncTask}.
 *
 * 2. Building a singleton and parsing this to get the instance
 * will cause a memory leak, you should instead pass the
 * getApplicationContext().
 *
 * 3. Building a listeners and not nullifying it in {@link #onDestroy()}
 * causes a memory leak. You should nullify the listeners when the
 * activity is destroyed.
 */
public class MainActivity extends AppCompatActivity {

    private TextView myTextBox;
    SampleClass sampleClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // AsyncTask memory leak
        // Making SampleTask non-static causes a memory leak.
        myTextBox = findViewById(R.id.tv_handler);
        new SampleTask(this).execute();

        // SampleClass memory leak
        // Replacing getApplicationContext() for this causes a memory leak.
        sampleClass = SampleClass.getSampleClassInstance(getApplicationContext());

        // Listener memory leak
        // Not nullifying the listener when the activity is finished causes a memory leak.
        Utility.getInstance().setListener(new Utility.UpdateListener() {
            @Override
            public void onUpdate() {
                Log.i("MainActivity", "onUpdate: Something is updated");
                Toast.makeText(MainActivity.this, "Something is updated", Toast.LENGTH_SHORT).show();
            }
        });

        Utility.getInstance().startNewThread();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Listeners must be nullified when the activity is destroyed.
        Utility.getInstance().setListener(null);
    }

    /**
     * Background work should be static, otherwise it will hold in to
     * the activity and will prevent the Activity from being garbage
     * collected.
     *
     * If you still need to access the activity' resources inside the
     * background you can make a {@link WeakReference} to the activity.
     */
    private static class SampleTask extends AsyncTask<Void, Void, Void> {
        // Declaring static and getting Activity weak reference
        // prevent memory leaks.
        private WeakReference<MainActivity> activityReference;

        SampleTask(MainActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Thread.sleep(1000 * 10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            MainActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;

            TextView myTextBox = activity.findViewById(R.id.tv_handler);
            myTextBox.setText("Done " + new Date().getTime());
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
                startActivity(new Intent(MainActivity.this, MainActivity.class));
                break;
            case R.id.menu_second:
                startActivity(new Intent(MainActivity.this, SecondActivity.class));
                break;
            case R.id.menu_third:
                startActivity(new Intent(MainActivity.this, ThirdActivity.class));
                break;
            case R.id.menu_fourth:
                startActivity(new Intent(MainActivity.this, FourthActivity.class));
                break;
            case R.id.menu_retrofit:
                startActivity(new Intent(MainActivity.this, RetrofitActivity.class));
                break;
            case R.id.menu_handle_cc:
                startActivity(new Intent(MainActivity.this, HandleCCActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
