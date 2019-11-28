package com.example.p8technews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    private static final String LOG_TAG = Adapter.class.getSimpleName();
    private Context context;
    private List<TechNews> techNewsList;

    /**
     * Constant Strings resource IDs required to setup each item's intent listener.
     */
    public final static String EXTRA_TITLE = String.valueOf(R.string.title_constant);
    public final static String EXTRA_SECTION = String.valueOf(R.string.section_constant);
    public final static String EXTRA_DATEANDTIME = String.valueOf(R.string.dateAndTime_constant);
    public final static String EXTRA_URL = String.valueOf(R.string.url_constant);
    public final static String EXTRA_THUMBNAIL = String.valueOf(R.string.thumbnail_constant);
    public final static String EXTRA_AUTHORS = String.valueOf(R.string.authors_constant);

    /**
     * Constructs a new {@link Adapter} object.
     */
    public Adapter(List<TechNews> techNewsList, Context context) {
        super();

        this.techNewsList = techNewsList;
        this.context = context;
    }

    /**
     * Method required to inflate our content layout for the Adapter.
     */
    @NonNull
    @Override
    public Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_content, parent, false);
        return new Adapter.ViewHolder(view);
    }

    /**
     * Here we bind the data to the View Objects.
     */
    @Override
    public void onBindViewHolder(@NonNull Adapter.ViewHolder holder, final int position) {
        final TechNews techNews = techNewsList.get(position);
        String title = techNews.getTitle();
        String newsSection = techNews.getNewsSection();
        String currentDateAndTime = techNews.getContentDateAndTime();
        String imageUrl = techNews.getThumbnail();

        holder.titleTextView.setText(title);
        holder.sectionTextView.setText(newsSection);
        if (currentDateAndTime != null) {
            try {
                String date = getNewsPublicationDate(currentDateAndTime);
                String time = getNewsPublicationTime(currentDateAndTime);
                holder.dateTextView.setText(date);
                holder.timeTextView.setText(time);
                holder.dateTextView.setVisibility(View.VISIBLE);
                holder.timeTextView.setVisibility(View.VISIBLE);
            } catch (ParseException e) {
                Log.e(LOG_TAG, context.getString(R.string.parsing_dataOrTime_problem), e);
            }
        }
        Picasso.with(context).load(imageUrl).into(holder.articleImageView);
        TextView authorsTextView = holder.authorsTextView.findViewById(R.id.authors_textView);
        ArrayList<String> authorsArray = techNews.getAuthors();
        if (authorsArray == null) {
            authorsTextView.setText(context.getString(R.string.no_author_found));
        } else {
            StringBuilder authorString = new StringBuilder();
            for (int i = 0; i < authorsArray.size(); i++) {
                authorString.append(authorsArray.get(i));
                if ((i + 1) < authorsArray.size()) {
                    authorString.append(", ");
                }
            }
            holder.authorsTextView.setText(authorString.toString());
            holder.authorsTextView.setVisibility(View.VISIBLE);

            /**
             * We handle the click events by setting a click listener, parsing the url
             * and adding extra intents for each content item.
             */
            holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TechNews techNews1 = techNewsList.get(position);
                    Uri techNewsUri = Uri.parse(techNews1.getWebUrl());
                    Intent webIntent = new Intent(Intent.ACTION_VIEW, techNewsUri);

                    webIntent.putExtra(EXTRA_TITLE, techNews1.getTitle());
                    webIntent.putExtra(EXTRA_SECTION, techNews1.getNewsSection());
                    webIntent.putExtra(EXTRA_DATEANDTIME, techNews1.getContentDateAndTime());
                    webIntent.putExtra(EXTRA_URL, techNews1.getWebUrl());
                    webIntent.putExtra(EXTRA_THUMBNAIL, techNews1.getThumbnail());
                    webIntent.putExtra(EXTRA_AUTHORS, techNews1.getAuthors());

                    view.getContext().startActivity(webIntent);
                }
            });
        }
    }

    /**
     * We return the techNewsList size to ensure that the number of items in List equals the one in the RecyclerView.
     */
    @Override
    public int getItemCount() {
        return techNewsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView articleImageView;
        public TextView titleTextView;
        public TextView sectionTextView;
        public TextView timeTextView;
        public TextView dateTextView;
        public TextView authorsTextView;
        public RelativeLayout relativeLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            articleImageView = itemView.findViewById(R.id.thumbnail_imageView);
            titleTextView = itemView.findViewById(R.id.title_textView);
            sectionTextView = itemView.findViewById(R.id.section_textView);
            timeTextView = itemView.findViewById(R.id.time_textView);
            dateTextView = itemView.findViewById(R.id.date_textView);
            authorsTextView = itemView.findViewById(R.id.authors_textView);
            relativeLayout = itemView.findViewById(R.id.relative_layout);
        }
    }

    /**
     * Implementing article's date getter method.
     */
    private String getNewsPublicationDate(String currentDateAndTime) throws ParseException {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat(context.getString(R.string.received_date_and_time_format));
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdfDate = new SimpleDateFormat(context.getString(R.string.displayed_date_format));
        return sdfDate.format(sdf.parse(currentDateAndTime));
    }

    /**
     * Implementing article's time getter method.
     */
    private String getNewsPublicationTime(String currentDateAndTime) throws ParseException {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat(context.getString(R.string.received_date_and_time_format));
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdfTime = new SimpleDateFormat(context.getString(R.string.displayed_time_format));
        sdfTime.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdfTime.format(sdf.parse(currentDateAndTime));
    }

    /**
     * Return the list of TechNews, which will be used into TechActivity's onLoadFinished method.
     */
    public void setTechNewsArrayList(List<TechNews> techNewsList) {
        this.techNewsList = techNewsList;
    }
}