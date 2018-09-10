package com.example.karat.memoryleak.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.os.AsyncTask;
import android.util.Log;

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

public class ShowsAsyncViewModel extends ViewModel {

    private static final String TAG = "ShowsAsyncViewModel";

    private MutableLiveData<List<Show>> shows;

    public LiveData<List<Show>> getShows() {
        if (shows == null) {
            shows = new MutableLiveData<>();
            // Calling using enqueue method.
            loadShows();

            // Calling using static inner class
            /*new RetrofitCall(shows).execute();*/
        }
        return shows;
    }

    private void loadShows() {
        // Do an asynchronous operation to fetch users.
        Log.i(TAG, "loadShows: Starting to load the shows");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TvMazeService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        TvMazeService service = retrofit.create(TvMazeService.class);
        Log.i(TAG, "loadShows: Service created");
        service.getShows().enqueue(new Callback<List<Show>>() {
            @Override
            public void onResponse(Call<List<Show>> call, Response<List<Show>> response) {
                Log.i(TAG, "onResponse: Got a response");
                if (response.isSuccessful()) {
                    Log.i(TAG, "onResponse: Response was successfully");
                    List<Show> showList = response.body();
                    if (showList != null) {
                        shows.setValue(showList);
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

    /*private static class RetrofitCall extends AsyncTask<Void, Void, List<Show>> {
        private static final String TAG = "RetrofitCall";
        private WeakReference<MutableLiveData<List<Show>>> showsWeakReference;

        private RetrofitCall(MutableLiveData<List<Show>> shows) {
            this.showsWeakReference = new WeakReference<>(shows);
        }

        @Override
        protected List<Show> doInBackground(Void... voids) {
            Log.i(TAG, "doInBackground: Starting to call the background");

            List<Show> showList = new ArrayList<>();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(TvMazeService.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            TvMazeService service = retrofit.create(TvMazeService.class);
            try {
                Log.i(TAG, "doInBackground: Starting to execute");
                Response<List<Show>> response = service.getShows().execute();
                if (response.isSuccessful()) {
                    Log.i(TAG, "doInBackground: Response was successfully");
                    showList = response.body();
                } else {
                    Log.i(TAG, "doInBackground: Error code: " + response.code() + " error message: " + response.message());
                }
                return showList;
            } catch (IOException e) {
                e.printStackTrace();
                Log.i(TAG, "doInBackground: Got an exception: " + e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<Show> showsList) {
            super.onPostExecute(showsList);
            Log.i(TAG, "onPostExecute: Finished the task");
            MutableLiveData<List<Show>> listMutableLiveData = showsWeakReference.get();
            if (listMutableLiveData != null) {
                listMutableLiveData.setValue(showsList);
            }
        }
    }
*/
}
