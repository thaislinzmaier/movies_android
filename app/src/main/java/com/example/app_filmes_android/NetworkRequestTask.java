package com.example.app_filmes_android;

import static com.example.app_filmes_android.ApiConfig.API_KEY;

import android.os.AsyncTask;
import android.util.Log;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class NetworkRequestTask extends AsyncTask<String, Void, JSONObject> {
    private MainActivity activity;

    public NetworkRequestTask(MainActivity activity) {
        this.activity = activity;
    }
    @Override
    protected JSONObject doInBackground(String... params) {
        OkHttpClient client = new OkHttpClient();
        String movieId = params[0];
        String baseUrl = "https://api.themoviedb.org/3/movie/" + movieId + "?api_key=" + API_KEY + "&language=en-US"; // Base da URL da API

        Request request = new Request.Builder()
                .url(baseUrl)
                .build();
        try {
            okhttp3.Response response_api = client.newCall(request).execute();
            String jsonData = response_api.body().string();
            return new JSONObject(jsonData);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        if (jsonObject != null) {
            try{
                String id = jsonObject.getString("id");
                String posterPath = jsonObject.getString("poster_path");
                String title = jsonObject.getString("title");
                String overview = jsonObject.getString("overview");

                posterPath = posterPath.replace("\"", "");
                String baseUrlPoster = "https://image.tmdb.org/t/p/w500";
                String posterUrl = baseUrlPoster + posterPath;

                activity.showDetailsLayout(activity.bindingDetails, posterUrl, title, overview, id);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
    }
    }
}