package com.example.p8technews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class TechActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<TechNews>> {
    private RecyclerView recyclerView;
    private Adapter adapter;
    private ArrayList<TechNews> techNewsArrayList;
    private View loadingBar;
    private TextView emptyStateTextView;
    private RecyclerView.ItemDecoration dividerItemDecoration;

    private static final int TECHNEWS_LOADER_ID = 1;
    private static final String GUARDIAN_REQUEST_URL =
            "https://content.guardianapis.com/search?api-key=test";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_technology);

        emptyStateTextView = findViewById(R.id.empty_view);
        techNewsArrayList = new ArrayList<>();
        recyclerView = findViewById(R.id.recycler_grid);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        /**
         * Create a new adapter that takes an empty list of news as input
         * and set the click listener created within TechnologyAdapter.
         */
        adapter = new Adapter(techNewsArrayList, this);
        recyclerView.setAdapter(adapter);

        /**
         * Get the reference to the ConnectivityManager to check state of network.
         */
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        /**
         * If there is a network connection, fetch data and get a reference to the LoaderManager.
         */
        if (networkInfo != null && networkInfo.isConnected()) {
            LoaderManager loaderManager = getSupportLoaderManager();
            loaderManager.initLoader(TECHNEWS_LOADER_ID, null, this);
        } else {
            loadingBar = findViewById(R.id.loading_bar);
            loadingBar.setVisibility(View.GONE);
            emptyStateTextView.setText(R.string.no_internet_connection);
        }
    }

    @NonNull
    @Override
    public Loader<List<TechNews>> onCreateLoader(int id, @Nullable Bundle args) {
        /**
         * parse breaks apart the URI string that's passed into its parameter.
         */
        Uri baseUri = Uri.parse(GUARDIAN_REQUEST_URL);
        /**
         * buildUpon prepares the baseUri that we just parsed so we can add query parameters to it.
         */
        Uri.Builder builder = baseUri.buildUpon();
        /**
         * Append query parameter and its value.
         */
        builder.appendQueryParameter("section", "technology");
        builder.appendQueryParameter("show-tags", "contributor");
        builder.appendQueryParameter("format", "json");
        builder.appendQueryParameter("lang", "en");
        builder.appendQueryParameter("order-by", "newest");
        builder.appendQueryParameter("show-fields", "thumbnail");
        builder.appendQueryParameter("page-size", "50");

        /**
         * Return the completed uri http://content.guardianapis.com/search?section=technology&show-tags=contributor&format=json&lang=en&order-by=newest&show-fields=thumbnail&page-size=50&api-key=test
         */
        return new NewsLoader(this, builder.toString());
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<TechNews>> loader, List<TechNews> techNewsList) {

        /**
         *After the loader finished its load, we stop the visibility of our loading bar and
         * either show our emptyView message or display our list of news.
         */
        loadingBar = findViewById(R.id.loading_bar);
        loadingBar.setVisibility(View.GONE);
        emptyStateTextView.setText(R.string.no_news_found);
        adapter.notifyDataSetChanged();
        if (techNewsList != null && !techNewsList.isEmpty()) {
            emptyStateTextView.setVisibility(View.GONE);
            adapter.setTechNewsArrayList(techNewsList);
        }
    }

    /**
     * Called when a previously created loader is being reset, at this point
     * any View reflecting the data set should refresh itself.
     */
    @Override
    public void onLoaderReset(@NonNull Loader<List<TechNews>> loader) {
        adapter.notifyDataSetChanged();
    }
}