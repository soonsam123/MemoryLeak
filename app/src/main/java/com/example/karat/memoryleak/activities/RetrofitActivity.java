package com.example.karat.memoryleak.activities;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.karat.memoryleak.R;
import com.example.karat.memoryleak.models.Show;
import com.example.karat.memoryleak.service.TvMazeService;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitActivity extends AppCompatActivity {

    private static final String TAG = "RetrofitActivity";

    private AsyncTask mRetrofitCall;
    private TextView mNumberOfShows;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrofit);

        mNumberOfShows = findViewById(R.id.text_view);
        mProgressBar = findViewById(R.id.progress_bar);
        mNumberOfShows.setVisibility(View.GONE);

        mRetrofitCall = new RetrofitCall(this);

        TextView retrofit = findViewById(R.id.text_view_retrofit);
        retrofit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(RetrofitActivity.this, "began", Toast.LENGTH_SHORT).show();
                /*callRetrofitCausingMemoryLeak();*/
                mRetrofitCall = new RetrofitCall(RetrofitActivity.this).execute();
            }
        });

        // Option 1. Call retrofit asynchronous in the UI thread. (cause memory leak)
        /*callRetrofitCausingMemoryLeak();*/

        // Option 2. Call retrofit synchronous in a background thread. (do NOT cause memory leak)
        /*mRetrofitCall = new RetrofitCall(this).execute();*/

        // Option 3. Call retrofit asynchronous in a ViewModel. (do NOT cause memory leak)
        /*ShowsAsyncViewModel model = ViewModelProviders.of(this).get(ShowsAsyncViewModel.class);
        model.getShows().observe(this, new Observer<List<Show>>() {
            @Override
            public void onChanged(@Nullable List<Show> shows) {
                Log.i("RetrofitCall", "onChanged: Information was changed");
                if (shows != null) {
                    mNumberOfShows.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.GONE);

                    String number = String.valueOf(shows.size());
                    mNumberOfShows.setText(number);
                }
            }
        });*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /*if (mRetrofitCall != null) {
            mRetrofitCall.cancel(true);
            Log.i(TAG, "onDestroy: Cancelling call: " + mRetrofitCall);
        }*/
    }

    // ---------------------------------------------------------------------------------
    //                                      Option 1
    // ---------------------------------------------------------------------------------

    /**
     * 1. Calling retrofit in the main thread doing an asynchronous call
     * holds a reference to the activity that will be leaked when the
     * activity is destroyed by rotating the device or something else.
     * </p>
     * If the user rotate the screen before the first call finish, it
     * will call retrofit again without the last one being finished.
     * And the {@link Call#enqueue(Callback)} method which runs in
     * the background thread will keep alive. Therefore, if the user
     * rotates the screen multiple times, there will be multiple
     * calls to retrofit and then when he stops there will be
     * multiple "same" responses and all of them holding reference
     * to the {@link Context}.
     */
    private void callRetrofitCausingMemoryLeak() {
        Log.i(TAG, "callRetrofitCausingMemoryLeak: Starting to call retrofit");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TvMazeService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        TvMazeService service = retrofit.create(TvMazeService.class);
        service.getShows().enqueue(new Callback<List<Show>>() {
            @Override
            public void onResponse(Call<List<Show>> call, Response<List<Show>> response) {
                Log.i(TAG, "onResponse: Got a response");
                if (response.isSuccessful()) {
                    Log.i(TAG, "onResponse: It was successfully");
                    mNumberOfShows.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.GONE);
                    List<Show> shows = response.body();
                    if (shows != null) {
                        Log.i(TAG, "onResponse: Displaying the text");
                        String number = String.valueOf(shows.size());
                        mNumberOfShows.setText(number);
                    }
                } else {
                    Log.i(TAG, "onResponse: Error code: " + response.code() + " - error message: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Show>> call, Throwable t) {
                t.printStackTrace();
                Log.i(TAG, "onFailure: Failed to connect: " + t.getMessage());
            }
        });
    }

    // ---------------------------------------------------------------------------------
    //                                      Option 2
    // ---------------------------------------------------------------------------------

    /**
     * 2. Inner static classes does not hold reference to the activity, meaning
     * that it'll not cause a memory leak.
     * The difference is that this method starts to run in the background, if
     * the user rotate the screen the call is cancelled and
     * {@link AsyncTask#doInBackground(Object[])} method is not called again.
     * It waits to finish the last call to call again.
     * </p>
     * If the user rotates the screen multiple times it will end up an {@link IOException}
     * with the message thread interrupted.
     */
    private static class RetrofitCall extends AsyncTask<Void, Void, List<Show>> {
        private WeakReference<RetrofitActivity> activityWeakReference;

        private RetrofitCall(RetrofitActivity context) {
            Log.i(TAG, "RetrofitCall: Creating retrofit instance");
            this.activityWeakReference = new WeakReference<>(context);
        }

        @Override
        protected List<Show> doInBackground(Void... voids) {
            Log.i(TAG, "doInBackground: Starting to call in the background");
            List<Show> showList = new ArrayList<>();

            if (!isCancelled()) {
                Log.i(TAG, "doInBackground: Call was not canceled");
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(TvMazeService.BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                TvMazeService service = retrofit.create(TvMazeService.class);
                Log.i(TAG, "doInBackground: Service created");
                try {
                    Response<List<Show>> response = service.getShows().execute();
                    if (response.isSuccessful()) {
                        Log.i(TAG, "doInBackground: Got a response successfully");
                        showList = response.body();
                    } else {
                        Log.i(TAG, "doInBackground: Error code: " + response.code() + " - error message: " + response.message());
                    }
                    return showList;
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.i(TAG, "doInBackground: Got a exception: " + e.getMessage());
                }
            } else {
                Log.i(TAG, "doInBackground: Retrofit call was canceled");
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<Show> shows) {
            super.onPostExecute(shows);
            Log.i(TAG, "onPostExecute: Call is done, starting to prepare for text display");
            RetrofitActivity context = activityWeakReference.get();
            if (shows != null) {
                Log.i(TAG, "onPostExecute: Number of shows: " + shows.size());
            } else { Log.i(TAG, "onPostExecute: Show is also NULL"); }
            if (context != null && shows != null) {
                Log.i(TAG, "onPostExecute: Context is non-null, display text");
                TextView textView = context.findViewById(R.id.text_view);
                textView.setVisibility(View.VISIBLE);
                ProgressBar progressBar = context.findViewById(R.id.progress_bar);
                progressBar.setVisibility(View.GONE);

                String number = String.valueOf(shows.size());
                textView.setText(number);
            } else {
                Log.i(TAG, "onPostExecute: Context is NULL");
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
                startActivity(new Intent(RetrofitActivity.this, MainActivity.class));
                break;
            case R.id.menu_second:
                startActivity(new Intent(RetrofitActivity.this, SecondActivity.class));
                break;
            case R.id.menu_third:
                startActivity(new Intent(RetrofitActivity.this, ThirdActivity.class));
                break;
            case R.id.menu_fourth:
                startActivity(new Intent(RetrofitActivity.this, FourthActivity.class));
                break;
            case R.id.menu_retrofit:
                startActivity(new Intent(RetrofitActivity.this, RetrofitActivity.class));
                break;
            case R.id.menu_handle_cc:
                startActivity(new Intent(RetrofitActivity.this, HandleCCActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
