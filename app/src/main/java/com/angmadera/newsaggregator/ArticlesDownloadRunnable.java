package com.angmadera.newsaggregator;

import android.net.Uri;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import javax.net.ssl.HttpsURLConnection;

public class ArticlesDownloadRunnable implements Runnable {

    private final MainActivity mainActivity;

    private static final String APIKey = "0e97639027e348db9ffc273127c573b6";
    private final String source;

    private static final String sourcesURL =
            "https://newsapi.org/v2/top-headlines";

    ArticlesDownloadRunnable(MainActivity mainActivity, String sourceId) {
        this.mainActivity = mainActivity;
        this.source = sourceId;
    }

    @Override
    public void run() {
        Uri.Builder dataUri = Uri.parse(sourcesURL).buildUpon();
        dataUri.appendQueryParameter("sources", source);
        dataUri.appendQueryParameter("apiKey", APIKey);
        String urlToUse = dataUri.build().toString();

        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(urlToUse);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "");
            conn.connect();

            if (conn.getResponseCode() != HttpsURLConnection.HTTP_OK) {
                handleResults(null);
                return;
            }

            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }

        } catch (Exception e) {
            handleResults(null);
            return;
        }
        handleResults(sb.toString());
    }

    public void handleResults(final String jsonString) {
        final ArrayList<Article> s = parseArticleJSON(jsonString);
        mainActivity.runOnUiThread(() -> mainActivity.updateDataTwo(s));
    }

    private String parseAuthor(String s) {
        String sub = s.substring(27);
        String[] split = sub.split("\"");
        return split[0];
    }

    private ArrayList<Article> parseArticleJSON(String s) {
        ArrayList<Article> articleList = new ArrayList<>();
        try {
            String author;
            String description;
            String publishedAt;
            JSONObject jObjMain = new JSONObject(s);

            JSONArray articles = jObjMain.getJSONArray("articles");
            for (int i = 0; i < articles.length(); ++i) {
                JSONObject jArticle = (JSONObject) articles.get(i);
                if(jArticle.isNull("author") || jArticle.getString("author").equals("")) {
                    author = null;
                } else {
                    if(this.source.equals("buzzfeed")) {
                        author = parseAuthor(jArticle.getString("author"));
                    } else {
                        author = jArticle.getString("author");
                    }
                }
                String title = jArticle.getString("title");
                if(title.contains("<em>") || title.contains("</em>")){
                    title = title.replaceAll("<em>", "");
                    title = title.replaceAll("</em>", "");
                }
                if(jArticle.isNull("description") || jArticle.getString("description").equals("")) {
                    description = null;
                } else {
                    description = jArticle.getString("description");
                    if(description.contains("<em>") || description.contains("</em>")) {
                        description = description.replaceAll("<em>", "");
                        description = description.replaceAll("</em>", "");
                    }
                }
                String url = jArticle.getString("url");
                String urlToImage = jArticle.getString("urlToImage");
                if(jArticle.isNull("publishedAt") || jArticle.getString("publishedAt").equals("")) {
                    publishedAt = null;
                } else {
                    publishedAt = jArticle.getString("publishedAt");
                }
                articleList.add(new Article(author, title, description, url, urlToImage, publishedAt, i+1));
            }
            return articleList;
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
}