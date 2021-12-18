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

public class SourcesDownloadRunnable implements Runnable {

    private final MainActivity mainActivity;

    private static final String APIKey = "0e97639027e348db9ffc273127c573b6";

    private static final String sourcesURL =
            "https://newsapi.org/v2/sources";

    SourcesDownloadRunnable(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void run() {
        Uri.Builder dataUri = Uri.parse(sourcesURL).buildUpon();
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
        final ArrayList<Sources> s = parseSourceJSON(jsonString);
        mainActivity.runOnUiThread(() -> mainActivity.updateData(s));
        }

    private ArrayList<Sources> parseSourceJSON(String s) {
        ArrayList<Sources> SourceList = new ArrayList<>();
        try {

            JSONObject jObjMain = new JSONObject(s);

            JSONArray sources = jObjMain.getJSONArray("sources");
            for (int i = 0; i < sources.length(); ++i) {
                JSONObject jSources = (JSONObject) sources.get(i);
                String id = jSources.getString("id");
                String name = jSources.getString("name");
                String topic = jSources.getString("category");
                String language = jSources.getString("language");
                String country = jSources.getString("country");
                SourceList.add(new Sources(id, name, topic, language, country));
            }

            return SourceList;
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
