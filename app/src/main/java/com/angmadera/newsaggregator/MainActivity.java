package com.angmadera.newsaggregator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private final MainActivity main = this;

    private final ArrayList<String> countriesArray = new ArrayList<>();
    private final ArrayList<String> languagesArray = new ArrayList<>();
    private final ArrayList<String> topicsArray = new ArrayList<>();
    private final ArrayList<Sources> query = new ArrayList<>();
    private final ArrayList<String> namesArray = new ArrayList<>();

    private final int[] colorsArr = {Color.rgb(170, 0, 0), Color.rgb(204, 102, 0), Color.rgb(190, 150, 0), Color.rgb(119, 179, 0), Color.rgb(0, 128, 120), Color.rgb(77,166,255), Color.rgb(51,85,255), Color.rgb(170,128,255), Color.rgb(230,160,250), Color.rgb(128,85,0), Color.rgb(127,0,153)};

    SubMenu topics;
    SubMenu languages;
    SubMenu countries;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    ArrayList<String> arrayTopic = new ArrayList<>();
    ArrayList<String> arrayCountry = new ArrayList<>();
    ArrayList<String> arrayLanguage = new ArrayList<>();
    ArrayList<SpannableString> arrayListSpannable = new ArrayList<>();
    ArrayList<String> arrayListString = new ArrayList<>();

    int count;
    private ArticleAdapter articleAdapter;
    boolean onInstance = false;

    String articleTitle;
    ArrayList<Article> articles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerList = findViewById(R.id.left_drawer);
        mDrawerList.setOnItemClickListener(
                (parent, view, position, id) -> selectItem(position)
        );
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        doDownload();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    private void selectItem(int position) {
        articleTitle = arrayListString.get(position);
        setTitle(articleTitle);
        mDrawerLayout.closeDrawer(mDrawerList);
        for(int i = 0; query.size() > i; i++) {
            Sources a = query.get(i);
            String name = a.getName();
            String id = a.getId();
            if(arrayListString.get(position).equals(name)) {
                ArticlesDownloadRunnable loaderTaskRunnable = new ArticlesDownloadRunnable(main, id);
                new Thread(loaderTaskRunnable).start();
                break;
            }
        }
    }

    private void doDownload() {
        SourcesDownloadRunnable loaderTaskRunnable = new SourcesDownloadRunnable(main);
        new Thread(loaderTaskRunnable).start();
    }

    public void updateData(ArrayList<Sources> sources) {
        if (sources == null) {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            return;
        }

        addSubMenuItems(sources);

        if(!onInstance) {
            addDrawerItems(sources);
            queryTranspose(sources);
            count = sources.size();
            arrayTopic.addAll(namesArray);
            arrayCountry.addAll(namesArray);
            arrayLanguage.addAll(namesArray);
            arrayListString.addAll(namesArray);
        }
    }

    private void queryTranspose(ArrayList<Sources> sources) {
        for (int i = 0; i < sources.size(); i++) {
            query.add(i, sources.get(i));
        }
    }

    private void addDrawerItems(ArrayList<Sources> sources) {
        ArrayList<SpannableString> span = new ArrayList<>();
        for (int i = 0; i < sources.size(); i++) {
            Sources a = sources.get(i);
            String name = a.getName();
            String topics = a.getTopic();

            for (int j = 0; j < topicsArray.size(); j++) {
                if (topicsArray.get(j).equals(topics)) {
                    namesArray.add(name);
                    span.add(textColorTopics(name, j));
                }
            }
            mDrawerList.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_list_item, span));
            setTitle(String.format(Locale.getDefault(), "News Gateway (%d)", span.size()));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        topics = menu.addSubMenu("Topics");
        countries = menu.addSubMenu("Countries");
        languages = menu.addSubMenu("Languages");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        int parentSubmenu = item.getGroupId();
        if (parentSubmenu == 0) {
            if (item.getTitle().equals("all")) {
                arrayTopic.clear();
                arrayListSpannable.clear();
                arrayListString.clear();
                arrayTopic.addAll(namesArray);
                for (int i = 0; arrayTopic.size() > i; i++) {
                    Sources a = query.get(i);
                    String top = a.getTopic();

                    if (arrayCountry.contains(arrayTopic.get(i)) && arrayLanguage.contains(arrayTopic.get(i))) {
                        for (int j = 0; topicsArray.size() > j; j++) {
                            if (top.equals(topicsArray.get(j))) {
                                arrayListSpannable.add(textColorTopics(arrayTopic.get(i), j));
                                arrayListString.add(arrayTopic.get(i));
                            }
                        }
                    }
                }
                setTitle(String.format(Locale.getDefault(), "News Gateway (%d)", arrayListSpannable.size()));
                mDrawerList.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_list_item, arrayListSpannable));
                return true;
            } else if (topicsArray.contains(item.getTitle().toString())) {
                arrayTopic.clear();
                arrayListString.clear();
                arrayListSpannable.clear();
                String topTwo = "";
                for (int i = 0; query.size() > i; i++) {
                    Sources sr = query.get(i);
                    String top = sr.getTopic();
                    String name = sr.getName();
                    if (top.equals(item.getTitle().toString())) {
                        arrayTopic.add(name);
                        topTwo = top;
                    }
                }
                for (int i = 0; arrayTopic.size() > i; i++) {
                    if (arrayCountry.contains(arrayTopic.get(i)) && arrayLanguage.contains(arrayTopic.get(i))) {
                        for (int j = 0; topicsArray.size() > j; j++) {
                            if (topTwo.equals(topicsArray.get(j))) {
                                arrayListSpannable.add(textColorTopics(arrayTopic.get(i), j));
                                arrayListString.add(arrayTopic.get(i));
                            }
                        }
                    }
                }
                setTitle(String.format(Locale.getDefault(), "News Gateway (%d)", arrayListSpannable.size()));
                mDrawerList.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_list_item, arrayListSpannable));
                if(arrayListSpannable.isEmpty()) {
                    Toast.makeText(this, "no sources match specified topic", Toast.LENGTH_LONG).show();
                }
                return true;
            }
        } else if (parentSubmenu == 1) {
            if (item.getTitle().equals("all")) {
                arrayCountry.clear();
                arrayListString.clear();
                arrayListSpannable.clear();
                arrayCountry.addAll(namesArray);
                for (int i = 0; arrayCountry.size() > i; i++) {
                    Sources a = query.get(i);
                    String top = a.getTopic();

                    if (arrayTopic.contains(arrayCountry.get(i)) && arrayLanguage.contains(arrayCountry.get(i))) {
                        for (int j = 0; topicsArray.size() > j; j++) {
                            if (top.equals(topicsArray.get(j))) {
                                arrayListSpannable.add(textColorTopics(arrayCountry.get(i), j));
                                arrayListString.add(arrayCountry.get(i));
                                break;
                            }
                        }
                    }
                }
                count = arrayListSpannable.size();
                setTitle(String.format(Locale.getDefault(), "News Gateway (%d)", count));
                mDrawerList.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_list_item, arrayListSpannable));
                return true;
            } else if (countriesArray.contains(item.getTitle().toString())) {
                ArrayList<String> topics = new ArrayList<>();
                arrayListSpannable.clear();
                arrayListString.clear();
                arrayCountry.clear();
                for (int i = 0; query.size() > i; i++) {
                    Sources sr = query.get(i);
                    String count = getCountryName(sr.getCountry());
                    String name = sr.getName();
                    String top = sr.getTopic();

                    if (count.equals(item.getTitle().toString())) {
                        arrayCountry.add(name);
                        topics.add(top);
                    }
                }
                for (int i = 0; arrayCountry.size() > i; i++) {
                    for (int j = 0; topicsArray.size() > j; j++) {
                        if (topics.get(i).equals(topicsArray.get(j))) {
                            arrayListSpannable.add(textColorTopics(arrayCountry.get(i), j));
                            arrayListString.add(arrayCountry.get(i));
                        }
                    }
                }
                count = arrayListSpannable.size();
                setTitle(String.format(Locale.getDefault(), "News Gateway (%d)", count));
                mDrawerList.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_list_item, arrayListSpannable));
                if(arrayListSpannable.isEmpty()) {
                    Toast.makeText(this, "no sources match specified country", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        } else if (parentSubmenu == 2) {
            if (item.getTitle().equals("all")) {
                arrayListSpannable.clear();
                arrayListString.clear();
                arrayLanguage.clear();
                arrayLanguage.addAll(namesArray);

                for (int i = 0; arrayLanguage.size() > i; i++) {
                    Sources a = query.get(i);
                    String top = a.getTopic();

                    if (arrayTopic.contains(arrayLanguage.get(i)) && arrayCountry.contains(arrayLanguage.get(i))) {
                        for (int j = 0; topicsArray.size() > j; j++) {
                            if (top.equals(topicsArray.get(j))) {
                                arrayListSpannable.add(textColorTopics(arrayLanguage.get(i), j));
                                arrayListString.add(arrayLanguage.get(i));
                            }
                        }
                    }
                }
                count = arrayListSpannable.size();
                setTitle(String.format(Locale.getDefault(), "News Gateway (%d)", count));
                mDrawerList.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_list_item, arrayListSpannable));
                return true;
            } else if (languagesArray.contains(item.getTitle().toString())) {
                ArrayList<String> topics = new ArrayList<>();
                arrayListString.clear();
                arrayListSpannable.clear();
                arrayLanguage.clear();
                for (int i = 0; query.size() > i; i++) {
                    Sources sr = query.get(i);
                    String lang = getLanguageName(sr.getLanguage());
                    String name = sr.getName();
                    String top = sr.getTopic();

                    if (lang.equals(item.getTitle().toString())) {
                        arrayLanguage.add(name);
                        topics.add(top);
                    }
                }
                for (int i = 0; arrayLanguage.size() > i; i++) {
                    if(arrayCountry.contains(arrayLanguage.get(i)) && arrayTopic.contains(arrayLanguage.get(i))) {
                        for (int j = 0; topicsArray.size() > j; j++) {
                            if (topics.get(i).equals(topicsArray.get(j))) {
                                arrayListSpannable.add(textColorTopics(arrayLanguage.get(i), j));
                                arrayListString.add(arrayLanguage.get(i));
                            }
                        }
                    }
                }
                count = arrayListSpannable.size();
                setTitle(String.format(Locale.getDefault(), "News Gateway (%d)", count));
                mDrawerList.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_list_item, arrayListSpannable));
                if(arrayListSpannable.isEmpty()) {
                    Toast.makeText(this, "no sources match specified language", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void addSubMenuItems(ArrayList<Sources> sources) {
        if(!topicsArray.isEmpty()) {
            topicsArray.clear();
        }
        topics.add(0, 0, 0, "all");
        countries.add(1, 0, 0, "all");
        languages.add(2, 0, 0, "all");

        for (int i = 0; i < sources.size(); i++) {
            Sources a = sources.get(i);
            String topic = a.getTopic();
            String country = getCountryName(a.getCountry());
            String language = getLanguageName(a.getLanguage());

            if (topicsArray.isEmpty() || !topicsArray.contains(topic)) {
                topicsArray.add(topic);
            }

            if (countriesArray.isEmpty() || !countriesArray.contains(country)) {
                countriesArray.add(country);
            }

            if (languagesArray.isEmpty() || !languagesArray.contains(language)) {
                languagesArray.add(language);
            }
        }

        Collections.sort(topicsArray);
        Collections.sort(countriesArray);
        Collections.sort(languagesArray);

        for (int j = 0; j < topicsArray.size(); j++) {
            topics.add(0, j + 1, j + 1, textColorTopics(topicsArray.get(j), j));
        }

        for (int m = 0; m < countriesArray.size(); m++) {
            countries.add(1, m + 1, m + 1, countriesArray.get(m));
        }

        for (int m = 0; m < languagesArray.size(); m++) {
            languages.add(2, m + 1, m + 1, languagesArray.get(m));
        }
    }

    public String getCountryName(String s) {
        String json;
        String country = null;
        String sUpper = s.toUpperCase();
        try {
            InputStream is = getAssets().open("country_codes.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            json = new String(buffer, StandardCharsets.UTF_8);

            JSONObject jObjMain = new JSONObject(json);

            JSONArray count = jObjMain.getJSONArray("countries");
            for (int i = 0; i < count.length(); i++) {
                JSONObject jObj = count.getJSONObject(i);
                if (jObj.getString("code").equals(sUpper)) {
                    country = jObj.getString("name");
                    break;
                }
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return country;
    }

    public String getLanguageName(String s) {
        String json;
        String language = null;
        String sUpper = s.toUpperCase();
        try {
            InputStream is = getAssets().open("language_codes.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            json = new String(buffer, StandardCharsets.UTF_8);

            JSONObject jObjMain = new JSONObject(json);

            JSONArray count = jObjMain.getJSONArray("languages");
            for (int i = 0; i < count.length(); i++) {
                JSONObject jObj = count.getJSONObject(i);
                if (jObj.getString("code").equals(sUpper)) {
                    language = jObj.getString("name");
                    break;
                }
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return language;
    }

    SpannableString textColorTopics(String s, int i) {
        SpannableString spannableString = new SpannableString(s);
        ForegroundColorSpan color = new ForegroundColorSpan(colorsArr[i]);
        spannableString.setSpan(color, 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    public void updateDataTwo(ArrayList<Article> article) {
        articles.clear();
        if (article == null) {
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_LONG).show();
        } else {
            articles.addAll(article);
            ViewPager2 viewPager2 = findViewById(R.id.view_pager);

            articleAdapter = new ArticleAdapter(this, articles);
            viewPager2.setAdapter(articleAdapter);
            viewPager2.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
            articleAdapter.notifyItemRangeChanged(0, article.size());
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        count = arrayListString.size();
        outState.putParcelableArrayList("ARTICLES", articles);
        outState.putStringArrayList("COUNTRY_ARRAY", arrayCountry);
        outState.putStringArrayList("LANGUAGE_ARRAY", arrayLanguage);
        outState.putStringArrayList("TOPIC_ARRAY", arrayTopic);
        outState.putStringArrayList("STRING_ARRAY", arrayListString);
        outState.putParcelableArrayList("QUERY_ARRAY", query);
        outState.putStringArrayList("NAME_ARRAY", namesArray);
        outState.putStringArrayList("TOPICS", topicsArray);
        outState.putString("TITLE", articleTitle);
        outState.putInt("COUNT", count);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        onInstance = true;
        super.onRestoreInstanceState(savedInstanceState);
        if (articles != null) {
            updateDataTwo(savedInstanceState.getParcelableArrayList("ARTICLES"));
        }
        arrayTopic.addAll(savedInstanceState.getStringArrayList("TOPIC_ARRAY"));
        arrayLanguage.addAll(savedInstanceState.getStringArrayList("LANGUAGE_ARRAY"));
        arrayCountry.addAll(savedInstanceState.getStringArrayList("COUNTRY_ARRAY"));
        arrayListString.addAll(savedInstanceState.getStringArrayList("STRING_ARRAY"));
        namesArray.addAll(savedInstanceState.getStringArrayList("NAME_ARRAY"));
        query.addAll(savedInstanceState.getParcelableArrayList("QUERY_ARRAY"));
        topicsArray.addAll(savedInstanceState.getStringArrayList("TOPICS"));
        articleTitle = savedInstanceState.getString("TITLE");
        if (articleTitle != null) {
            setTitle(savedInstanceState.getString("TITLE"));
        } else {
            setTitle(String.format(Locale.getDefault(), "News Gateway (%d)", savedInstanceState.getInt("COUNT")));
        }
        for(int i = 0; query.size() > i; i++) {
            Sources a = query.get(i);
            String name = a.getName();
            String topic = a.getTopic();
            if(arrayListString.contains(name)) {
                int c = arrayListString.indexOf(name);
                int b = topicsArray.indexOf(topic);
                arrayListSpannable.add(textColorTopics(arrayListString.get(c), b));
            }
        }
        mDrawerList.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_list_item, arrayListSpannable));
    }
}
