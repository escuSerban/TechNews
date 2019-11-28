package com.example.p8technews;

import java.util.ArrayList;

public class TechNews {
    private String mTitle;
    private String mNewsSection;
    private String mContentDateAndTime;
    private String mWebUrl;
    private String mThumbnail;
    private ArrayList<String> mAuthors;

    /**
     * Create a new TechNews object.
     *
     * @param title              is the string resource Id for each news article
     * @param newsSection        is the string resource Id for the news article section
     * @param contentDateAndTime is the string resource ID for the date&time of each news article
     * @param webUrl             is the string resource Id for web address of news article
     * @param thumbnail          is the string resource Id for thumbnail of news article
     * @param authors            of news articles contained into an ArrayList of Strings
     */
    public TechNews(String title, String newsSection, String contentDateAndTime, String webUrl, String thumbnail, ArrayList<String> authors) {
        mTitle = title;
        mNewsSection = newsSection;
        mContentDateAndTime = contentDateAndTime;
        mWebUrl = webUrl;
        mThumbnail = thumbnail;
        mAuthors = authors;
    }

    /**
     * Get the string resource ID for the title of the news article.
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * Get the string resource ID for the section of the article.
     */
    public String getNewsSection() {
        return mNewsSection;
    }

    /**
     * Get the string resource ID for the date&time of the news article
     */
    public String getContentDateAndTime() {
        return mContentDateAndTime;
    }

    /**
     * Get the string resource ID for the web address of the news article.
     */
    public String getWebUrl() {
        return mWebUrl;
    }

    /**
     * Get the string resource ID for the thumbnail of the news article.
     */
    public String getThumbnail() {
        return mThumbnail;
    }

    /**
     * Get the list of strings with authors of the news article.
     */
    public ArrayList<String> getAuthors() {
        return mAuthors;
    }
}