package com.example.karat.memoryleak.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.karat.memoryleak.R;

import java.lang.ref.WeakReference;

public class FourthActivity extends AppCompatActivity {

    private AsyncTask mLongRunningTask;
    private TextView textView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fourth);

        textView = findViewById(R.id.text_view);

        mLongRunningTask = new LongRunningTask(textView).execute();
        /*new LongRunningTask().execute();*/

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLongRunningTask.cancel(true);
    }

    /**
     * This class does not cause a memory leak because it is static and so it
     * does not hold a reference to the activity when this is destroyed.
     * But I lose the free access to the activity's resources, therefore
     * I need to pass a TextView to the task that will be a WeakReference
     * and then I'll have access to this Ui's TextView.
     */
    private static class LongRunningTask extends AsyncTask<Void, Void, String> {

        private WeakReference<TextView> textViewWeakReference;

        public LongRunningTask(TextView textView) {
            textViewWeakReference = new WeakReference<>(textView);
        }

        @Override
        protected String doInBackground(Void... voids) {
            String message = null;
            if (!isCancelled()) {
                message = "Am I finally done";
            }
            return message;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            TextView textView = textViewWeakReference.get();
            if (textView != null) {
                textView.setText(s);
            }
        }
    }

    // Using the AsyncTask in this will will cause memory leak.
    /*private class LongRunningTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            return "Am I Finally done";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            textView.setText(s);
        }
    }*/


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
                startActivity(new Intent(FourthActivity.this, MainActivity.class));
                break;
            case R.id.menu_second:
                startActivity(new Intent(FourthActivity.this, SecondActivity.class));
                break;
            case R.id.menu_third:
                startActivity(new Intent(FourthActivity.this, ThirdActivity.class));
                break;
            case R.id.menu_fourth:
                startActivity(new Intent(FourthActivity.this, FourthActivity.class));
                break;
            case R.id.menu_retrofit:
                startActivity(new Intent(FourthActivity.this, RetrofitActivity.class));
                break;
            case R.id.menu_handle_cc:
                startActivity(new Intent(FourthActivity.this, HandleCCActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
