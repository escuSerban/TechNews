package com.example.p8technews;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class QueryUtils {
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl, Context context) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, context.getString(R.string.http_error), e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url, Context context) throws IOException {
        /**
         * Creating String resource ID for JSON response.
         */
        String jsonResponse = "";

        /**
         * If the URL is null, then return early.
         */
        if (url == null) {
            return jsonResponse;
        }

        /**
         * Setting up our connection request.
         */
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod(context.getString(R.string.request_method));
            urlConnection.connect();

            /**
             * If the request was successful (response code 200),
             * then read the input stream and parse the response.
             */
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream, context);
            } else {
                Log.e(LOG_TAG, context.getString(R.string.error_message) + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, context.getString(R.string.jsonResult_exception), e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }

            /**
             * Closing the input stream could throw an IOException, as the method signature specifies it.
             */
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream, Context context) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName(context.getString(R.string.unicode_format)));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();
            while (line != null) {
                output.append(line);
                line = bufferedReader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link TechNews} objects that has been built up
     * from parsing the given JSON response.
     */
    private static List<TechNews> extractArticleFromJson(String techNewsJSON, Context context) {
        /**
         * If JSON string is empty or null, then return early.
         */
        if (TextUtils.isEmpty(techNewsJSON)) {
            return null;
        }

        /**
         * Create an empty ArrayList that we can start adding news to.
         */
        List<TechNews> techNews = new ArrayList<>();

        /**
         * Try to parse the JSON response string. If there's a problem with the way the JSON
         * is formatted, a JSONException exception object will be thrown.
         * Catch the exception so the app doesn't crash, and print the error message to the logs.
         */
        try {
            /**
             * Create a JSONObject from the JSON string.
             */
            JSONObject baseJsonResponse = new JSONObject(techNewsJSON);
            /**
             * Extract the JSONObject associated with the key called "response".
             */
            JSONObject responseJSONObject = baseJsonResponse.getJSONObject(context.getString(R.string.object_response));

            /**
             * Extract the JSONArray associated with the key called "results",
             * which represents a list of news articles.
             */
            JSONArray techNewsArray = responseJSONObject.getJSONArray(context.getString(R.string.array_results));

            /**
             * For each article in the techNewsArray, crate an {@link TechNews} object.
             */
            for (int i = 0; i < techNewsArray.length(); i++) {
                /**
                 * Get a single news article at position i within the list of techNews.
                 */
                JSONObject currentTechNews = techNewsArray.getJSONObject(i);

                /**
                 * Extract the value of the key called "title".
                 */
                String title = currentTechNews.getString(context.getString(R.string.title_string));

                /**
                 * Extract the value of the key called "section".
                 */
                String section = currentTechNews.getString(context.getString(R.string.section_name));

                /**
                 * Extract the value of the key called "webPublicationDate".
                 */
                String publicationDateAndTime = currentTechNews.getString(context.getString(R.string.publication_dateAndTime));

                /**
                 * Extract the value of the key called "webUrl".
                 */
                String webUrl = currentTechNews.getString(context.getString(R.string.url_web));

                /**
                 * Create a JSONObject from the currentTechNews.
                 */
                JSONObject fieldsJSONObject = currentTechNews.getJSONObject(context.getString(R.string.object_fields));

                /**
                 * Extract the value of the key called "thumbnail".
                 */
                String thumbnail = fieldsJSONObject.getString(context.getString(R.string.thumbnail_key));

                /**
                 * Create an ArrayList from the currentTechNews.
                 */
                ArrayList<String> authors = new ArrayList<>();

                /**
                 * Extract the JSONArray associated with the key called "tags",
                 * which represents a list of authors.
                 */
                if (currentTechNews.has(context.getString(R.string.array_tags))) {
                    JSONArray tagsArray = currentTechNews.getJSONArray(context.getString(R.string.array_tags));
                    if (tagsArray == null || tagsArray.length() == 0) {
                        authors = null;
                    } else {
                        for (int j = 0; j < tagsArray.length(); j++) {
                            JSONObject currentObjectInTags = tagsArray.getJSONObject(j);
                            authors.add(currentObjectInTags.getString(context.getString(R.string.title_string)));
                        }
                    }
                } else {
                    authors = null;
                }
                /**
                 * Create and add a new {@link TechNews} object to the list of techNews.
                 */
                techNews.add(new TechNews(title, section, publicationDateAndTime, webUrl, thumbnail, authors));
            }

            /**
             * Handling the JSONException to prevent app from crashing.
             */
        } catch (JSONException e) {
            Log.e(LOG_TAG, context.getString(R.string.parsing_JSON_problem), e);
        }
        return techNews;
    }

    /**
     * Query the Guardian dataset and return a list of {@link TechNews} objects.
     */
    public static List<TechNews> fetchTechNewsData(String requestUrl, Context context) {
        /**
         * Create URL object.
         */
        URL url = createUrl(requestUrl, context);

        /**
         * Perform HTTP request to the UTL and receive JASON response back.
         */
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url, context);
        } catch (IOException e) {
            Log.e(LOG_TAG, context.getString(R.string.request_problem), e);
        }

        /**
         * Extract relevant fields from the JSON response and create a list of {@link TechNews}.
         */
        List<TechNews> techNews = extractArticleFromJson(jsonResponse, context);

        /**
         * Return the list of {@link TechNews}.
         */
        return techNews;
    }
}