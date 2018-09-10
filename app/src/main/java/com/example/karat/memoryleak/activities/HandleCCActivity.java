package com.example.karat.memoryleak.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.karat.memoryleak.R;
import com.example.karat.memoryleak.fragments.TaskFragment;

/**
 * This Activity displays the screen's UI, creates a TaskFragment
 * to manage the task, and receives progress updates and results
 * from the TaskFragment when they occur.
 */
public class HandleCCActivity extends AppCompatActivity implements TaskFragment.TaskCallbacks{

    private static final String TAG = "HandleCCActivity";
    private static final String TAG_TASK_FRAGMENT = "task_fragment";

    private TextView mPercent;

    private TaskFragment mTaskFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handle_cc);

        FragmentManager fm = getSupportFragmentManager();
        mTaskFragment = (TaskFragment) fm.findFragmentByTag(TAG_TASK_FRAGMENT);

        // If the fragment is non null, then it is currently being
        // retained across a configuration change.
        if (mTaskFragment == null) {
            mTaskFragment = new TaskFragment();
            fm.beginTransaction().add(mTaskFragment, TAG_TASK_FRAGMENT).commit();
        }

        // TODO: initialize views, restore saved state, etc.
        mPercent = findViewById(R.id.text_percent);
    }


    // The four methods below are called by the TaskFragment when new
    // progress updates or results are available. The HandleCCActivity
    // should respond by updating its UI to indicate the change.

    @Override
    public void onPreExecute() {
        Log.i(TAG, "onPreExecute: onPreExecute in Activity");
    }

    @Override
    public void onProgressUpdate(int percent) {
        Log.i(TAG, "onProgressUpdate: onProgressUpdate in Activity");
        mPercent.setText(String.valueOf(percent));
        Log.i(TAG, "onProgressUpdate: Percent: " + String.valueOf(percent));
    }

    @Override
    public void onCancelled() {
        Log.i(TAG, "onCancelled: onCancelled in Activity");
    }

    @Override
    public void onPostExecute() {
        Log.i(TAG, "onPostExecute: onPostExecute in Activity");
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
                startActivity(new Intent(HandleCCActivity.this, MainActivity.class));
                break;
            case R.id.menu_second:
                startActivity(new Intent(HandleCCActivity.this, SecondActivity.class));
                break;
            case R.id.menu_third:
                startActivity(new Intent(HandleCCActivity.this, ThirdActivity.class));
                break;
            case R.id.menu_fourth:
                startActivity(new Intent(HandleCCActivity.this, FourthActivity.class));
                break;
            case R.id.menu_retrofit:
                startActivity(new Intent(HandleCCActivity.this, RetrofitActivity.class));
                break;
            case R.id.menu_handle_cc:
                startActivity(new Intent(HandleCCActivity.this, HandleCCActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
