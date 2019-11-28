package com.example.p8technews;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import java.util.List;

public class NewsLoader extends AsyncTaskLoader<List<TechNews>> {
    private String Url;

    /**
     * Constructs a new {@link NewsLoader}.
     *
     * @param context of the activity
     * @param url     to load data from
     */
    public NewsLoader(@NonNull Context context, String url) {
        super(context);
        Url = url;
    }

    /**
     * This will take care of loading the data.
     */
    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /**
     * This is on a background thread.
     */
    @Nullable
    @Override
    public List<TechNews> loadInBackground() {
        if (Url == null) {
            return null;
        }

        /**
         * Perform the network request, parse the response, and extract a list of news articles.
         */
        List<TechNews> techNews = QueryUtils.fetchTechNewsData(Url, getContext());
        return techNews;
    }
}