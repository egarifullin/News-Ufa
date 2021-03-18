package ru.krus.bashNews;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import android.preference.PreferenceManager;
import android.view.View;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public Elements content;
    public ArrayList<News> news = new ArrayList<ru.krus.bashNews.News>();
    ru.krus.bashNews.NewsAdapter adapter;
    public ArrayList<Afisha> afishas = new ArrayList<Afisha>();
    ru.krus.bashNews.AfishaAdapter adapter_afisha;
    private ListView lv;
    private TextView tvNocomm;
    Document doc = null;
    public String stringURL;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    final String LOG_TAG = "myLogs";

    FloatingActionMenu materialDesignFAM;
    FloatingActionButton floatingActionButton1, floatingActionButton2, floatingActionButton3, floatingActionButton4;
    FloatingActionButton floatingActionButton5, floatingActionButton6, floatingActionButton7, floatingActionButton8;
    FloatingActionButton floatingActionButton9;

    SharedPreferences sp;

    @SuppressLint({"ResourceAsColor", "WrongConstant"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        String listTheme = sp.getString("listTheme", "1");
            if (listTheme.equals("")) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                }else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
            } else if (listTheme.equals("1")) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                    View view = getWindow().getDecorView();
                    view.setSystemUiVisibility(view.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                }
            } else if (listTheme.equals("2")) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                    View view = getWindow().getDecorView();
                    view.setSystemUiVisibility(view.getSystemUiVisibility() & ~(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR));
                }
            }else if (listTheme.equals("3")) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                }
            }
        String listValue = sp.getString("list", "1");
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (listValue.equals("")){
            stringURL = "https://gorobzor.ru/novosti";
            toolbar.setTitle("Новости | Все");
            NewsParse();
        } else if (listValue.equals("1")){
            stringURL = "https://gorobzor.ru/novosti";
            toolbar.setTitle("Новости | Все");
            NewsParse();
        } else if (listValue.equals("2")){
            stringURL = "https://gorobzor.ru/novosti/proisshestviya";
            toolbar.setTitle("Новости | Происшествия");
            NewsParse();
        }else if (listValue.equals("3")) {
            stringURL = "https://gorobzor.ru/novosti/sport";
            toolbar.setTitle("Новости | Спорт");
            NewsParse();

        }else if (listValue.equals("4 ")) {
            stringURL = "https://gorobzor.ru/novosti/obschestvo";
            toolbar.setTitle("Новости | Общество");
            NewsParse();

        }else if (listValue.equals("5")) {
            stringURL = "https://gorobzor.ru/novosti/ekonomika";
            toolbar.setTitle("Новости | Экономика");
            NewsParse();

        }else if (listValue.equals("6")) {
            stringURL = "https://gorobzor.ru/novosti/pogoda-v-ufe-i-bashkirii";
            toolbar.setTitle("Новости | Погода");
            NewsParse();

        }else if (listValue.equals("7")) {
            stringURL = "https://gorobzor.ru/novosti/politika";
            toolbar.setTitle("Новости | Политика");
            NewsParse();

            // Handle the camera action
        } else if (listValue.equals("8")) {
            toolbar.setTitle("Афиша");
            AfishaParse();

        } else if (listValue.equals("9")) {
            stringURL = "https://gorobzor.ru/novosti/goroskop-na-segodnya";
            toolbar.setTitle("Гороскоп");
            NewsParse();
        } else
        {
            stringURL = "https://gorobzor.ru/novosti";
            toolbar.setTitle("Новости | Все");
            NewsParse();
        }
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        final NavigationView navigationView = findViewById(R.id.nav_view);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean bigText = sp.getBoolean("bigText",false);
        if (bigText) {
            navigationView.setItemTextAppearance(R.style.TextAppearance_AppCompat_Large);
        }else{
            navigationView.setItemTextAppearance(R.style.TextAppearance_AppCompat_Small);
        }
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        if (listValue .equals("")){
            navigationView.setCheckedItem(R.id.nav_home);
        } else if (listValue.equals("1")){
            navigationView.setCheckedItem(R.id.nav_home);
        } else if (listValue.equals("2")){
            navigationView.setCheckedItem(R.id.nav_proish);
        }else if (listValue.equals("3")) {
            navigationView.setCheckedItem(R.id.nav_sport);

        }else if (listValue.equals("4")) {
            navigationView.setCheckedItem(R.id.nav_people);

        }else if (listValue.equals("5")) {
            navigationView.setCheckedItem(R.id.nav_economy);

        }else if (listValue.equals("6")) {
            navigationView.setCheckedItem(R.id.nav_weather);

        }else if (listValue.equals("7")) {
            navigationView.setCheckedItem(R.id.nav_politics);
            // Handle the camera action
        } else if (listValue.equals("8")) {
            navigationView.setCheckedItem(R.id.nav_gallery);

        } else if (listValue.equals("9")) {
            navigationView.setCheckedItem(R.id.nav_goroscope);
        }else {
            navigationView.setCheckedItem(R.id.nav_home);
        }

        materialDesignFAM = findViewById(R.id.fab_menu);
        floatingActionButton1 = findViewById(R.id.fab_all);
        floatingActionButton2 = findViewById(R.id.fab_proish);
        floatingActionButton3 = findViewById(R.id.fab_sport);
        floatingActionButton4 = findViewById(R.id.fab_people);
        floatingActionButton5 = findViewById(R.id.fab_economy);
        floatingActionButton6 = findViewById(R.id.fab_weather);
        floatingActionButton7 = findViewById(R.id.fab_politics);
        floatingActionButton8 = findViewById(R.id.fab_afisha);
        floatingActionButton9 = findViewById(R.id.fab_goroscope);
        boolean buttonHide = sp.getBoolean("fabHide", false);
        if (buttonHide) {
            materialDesignFAM.setVisibility(4);
        }else {
            materialDesignFAM.setVisibility(0);
        }

        floatingActionButton1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                stringURL = "https://gorobzor.ru/novosti";
                NewsParse();
                Toolbar toolbar = findViewById(R.id.toolbar);
                toolbar.setTitle("Новости | Все");
                navigationView.setCheckedItem(R.id.nav_home);
                materialDesignFAM.close(true);
            }
        });
        floatingActionButton2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                stringURL = "https://gorobzor.ru/novosti/proisshestviya";
                NewsParse();
                Toolbar toolbar = findViewById(R.id.toolbar);
                toolbar.setTitle("Новости | Происшествия");
                navigationView.setCheckedItem(R.id.nav_proish);
                materialDesignFAM.close(true);
            }
        });
        floatingActionButton3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                stringURL = "https://gorobzor.ru/novosti/sport";
                NewsParse();
                Toolbar toolbar = findViewById(R.id.toolbar);
                toolbar.setTitle("Новости | Спорт");
                navigationView.setCheckedItem(R.id.nav_sport);
                materialDesignFAM.close(true);
            }
        });
        floatingActionButton4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                stringURL = "https://gorobzor.ru/novosti/obschestvo";
                NewsParse();
                Toolbar toolbar = findViewById(R.id.toolbar);
                toolbar.setTitle("Новости | Общество");
                navigationView.setCheckedItem(R.id.nav_people);
                materialDesignFAM.close(true);
            }
        });
        floatingActionButton5.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                stringURL = "https://gorobzor.ru/novosti/ekonomika";
                NewsParse();
                Toolbar toolbar = findViewById(R.id.toolbar);
                toolbar.setTitle("Новости | Экономика");
                navigationView.setCheckedItem(R.id.nav_economy);
                materialDesignFAM.close(true);
            }
        });
        floatingActionButton6.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                stringURL = "https://gorobzor.ru/novosti/pogoda-v-ufe-i-bashkirii";
                NewsParse();
                Toolbar toolbar = findViewById(R.id.toolbar);
                toolbar.setTitle("Новости | Погода");
                navigationView.setCheckedItem(R.id.nav_weather);
                materialDesignFAM.close(true);
            }
        });
        floatingActionButton7.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                stringURL = "https://gorobzor.ru/novosti/politika";
                NewsParse();
                Toolbar toolbar = findViewById(R.id.toolbar);
                toolbar.setTitle("Новости | Политика");
                navigationView.setCheckedItem(R.id.nav_politics);
                materialDesignFAM.close(true);
            }
        });
        floatingActionButton8.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AfishaParse();
                Toolbar toolbar = findViewById(R.id.toolbar);
                toolbar.setTitle("Афиша");
                navigationView.setCheckedItem(R.id.nav_gallery);
                materialDesignFAM.close(true);
            }
        });
        floatingActionButton9.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                stringURL = "https://gorobzor.ru/novosti/goroskop-na-segodnya";
                NewsParse();
                Toolbar toolbar = findViewById(R.id.toolbar);
                toolbar.setTitle("Гороскоп");
                navigationView.setCheckedItem(R.id.nav_goroscope);
                materialDesignFAM.close(true);
            }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            stringURL = "https://gorobzor.ru/novosti";
            NewsParse();
            Toolbar toolbar = findViewById(R.id.toolbar);
            toolbar.setTitle("Новости | Все");

        }else if (id == R.id.nav_proish) {
            stringURL = "https://gorobzor.ru/novosti/proisshestviya";
            NewsParse();
            Toolbar toolbar = findViewById(R.id.toolbar);
            toolbar.setTitle("Новости | Происшествия");

        }else if (id == R.id.nav_sport) {
            stringURL = "https://gorobzor.ru/novosti/sport";
            NewsParse();
            Toolbar toolbar = findViewById(R.id.toolbar);
            toolbar.setTitle("Новости | Спорт");

        }else if (id == R.id.nav_people) {
            stringURL = "https://gorobzor.ru/novosti/obschestvo";
            NewsParse();
            Toolbar toolbar = findViewById(R.id.toolbar);
            toolbar.setTitle("Новости | Общество");

        }else if (id == R.id.nav_economy) {
            stringURL = "https://gorobzor.ru/novosti/ekonomika";
            NewsParse();
            Toolbar toolbar = findViewById(R.id.toolbar);
            toolbar.setTitle("Новости | Экономика");

        }else if (id == R.id.nav_weather) {
            stringURL = "https://gorobzor.ru/novosti/pogoda-v-ufe-i-bashkirii";
            NewsParse();
            Toolbar toolbar = findViewById(R.id.toolbar);
            toolbar.setTitle("Новости | Погода");

        }else if (id == R.id.nav_politics) {
            stringURL = "https://gorobzor.ru/novosti/politika";
            NewsParse();
            Toolbar toolbar = findViewById(R.id.toolbar);
            toolbar.setTitle("Новости | Политика");

            // Handle the camera action
        } else if (id == R.id.nav_gallery) {
            AfishaParse();
            Toolbar toolbar = findViewById(R.id.toolbar);
            toolbar.setTitle("Афиша");

        } else if (id == R.id.nav_goroscope) {
            stringURL = "https://gorobzor.ru/novosti/goroskop-na-segodnya";
            NewsParse();
            Toolbar toolbar = findViewById(R.id.toolbar);
            toolbar.setTitle("Гороскоп");

        } else if (id == R.id.nav_send) {
            Intent intent = new Intent(this, About.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_settings) {
            Intent intent = new Intent(this, PrefActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public class  NewThread extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... arg) {

            try {
                doc = Jsoup.connect(stringURL).timeout(5 * 1000).get();


            } catch (SocketTimeoutException e){
                e.printStackTrace();
                //Toast.makeText(getApplicationContext(), "Нет соединения!", Toast.LENGTH_SHORT).show();
            } catch (IOException e){
                e.printStackTrace();
                //Toast.makeText(getApplicationContext(), "Нет соединения!", Toast.LENGTH_SHORT).show();
            }

            if (doc != null)
            {
                content = doc.select("article[class=c-news-n-cards__c-news-n-card c-news-n-card]");
                news.clear();
                for (Element contents: content){
                    if (contents.select("h3[class=c-news-n-card__h]").text() != "") {
                        news.add(new ru.krus.bashNews.News(contents.select("h3[class=c-news-n-card__h]").text(),
                                contents.select("h3[class=c-news-n-card__h]").text(),
                                contents.select("div[class=c-news-n-card__cat]").text() + " | " + contents.select("span[class=c-news-n-card__date-time]").text(),
                                "https://gorobzor.ru" + contents.select("div[class=c-news-n-card__image]").select("div[class=c-news-n-card__image-inner]").select("img").attr("src"),
                                "https://gorobzor.ru" + contents.select("a").attr("href")
                        ));
                    }
                }
            }
            return null;
        }
        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            if (doc!=null) {
                lv.setAdapter(adapter);
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Нет соединения!", Toast.LENGTH_LONG).show();
            }
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    public class  NewThreadAfisha extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... arg) {
            Document doc = null;
            try {
                doc = Jsoup.connect("https://gorobzor.ru/afisha/concerty").timeout(5 * 1000).get();


            } catch (SocketTimeoutException e){
                e.printStackTrace();
                //Toast.makeText(getApplicationContext(), "Нет соединения!", Toast.LENGTH_SHORT).show();
            } catch (IOException e){
                e.printStackTrace();
                //Toast.makeText(getApplicationContext(), "Нет соединения!", Toast.LENGTH_SHORT).show();
            }

            if (doc != null)
            {
                content = doc.select("div[class=js-grid-news-content]");
                afishas.clear();
                for (Element contents: content){
                    if (contents.select("div[class=js-grid-news-content]").text() != "") {
                        String img = contents.select("div[class=c-news-card__img-inner]").attr("style");
                        //Log.d(LOG_TAG,img.substring(23,img.length()-3));
                        afishas.add(new ru.krus.bashNews.Afisha(contents.select("h3[class=c-news-card__header]").text(),
                                contents.select("time[class=c-news-card__concert-info-p]").text(),
                                contents.select("p[class=c-news-card__concert-info-p c-news-card__concert-info-location]").text(),
                                contents.select("p[class=c-news-card__concert-info-p]").text(),
                                img.substring(23,img.length()-3),
                                "https://gorobzor.ru" + contents.select("a").attr("href")
                        ));

                    }

                }
            }
            doc = null;
            try {
                doc = Jsoup.connect("https://gorobzor.ru/afisha/teatry").timeout(5 * 1000).get();


            } catch (SocketTimeoutException e){
                e.printStackTrace();
                //Toast.makeText(getApplicationContext(), "Нет соединения!", Toast.LENGTH_SHORT).show();
            } catch (IOException e){
                e.printStackTrace();
                //Toast.makeText(getApplicationContext(), "Нет соединения!", Toast.LENGTH_SHORT).show();
            }

            if (doc != null)
            {
                content = doc.select("div[class=js-grid-news-content]");
                //afishas.clear();
                for (Element contents: content){
                    if (contents.select("div[class=js-grid-news-content]").text() != "") {
                        String img = contents.select("div[class=c-news-card__img-inner]").attr("style");
                        //Log.d(LOG_TAG,img.substring(23,img.length()-3));
                        afishas.add(new ru.krus.bashNews.Afisha(contents.select("h3[class=c-news-card__header]").text(),
                                contents.select("time[class=c-news-card__concert-info-p]").text(),
                                contents.select("p[class=c-news-card__concert-info-p c-news-card__concert-info-location]").text(),
                                contents.select("p[class=c-news-card__concert-info-p]").text(),
                                img.substring(23,img.length()-3),
                                "https://gorobzor.ru" + contents.select("a").attr("href")
                        ));

                    }

                }
            }
            doc = null;
            try {
                doc = Jsoup.connect("https://gorobzor.ru/afisha/vistavky").timeout(5 * 1000).get();


            } catch (SocketTimeoutException e){
                e.printStackTrace();
                //Toast.makeText(getApplicationContext(), "Нет соединения!", Toast.LENGTH_SHORT).show();
            } catch (IOException e){
                e.printStackTrace();
                //Toast.makeText(getApplicationContext(), "Нет соединения!", Toast.LENGTH_SHORT).show();
            }

            if (doc != null)
            {
                content = doc.select("div[class=js-grid-news-content]");
                //afishas.clear();
                for (Element contents: content){
                    if (contents.select("div[class=js-grid-news-content]").text() != "") {
                        String img = contents.select("div[class=c-news-card__img-inner]").attr("style");
                        //Log.d(LOG_TAG,img.substring(23,img.length()-3));
                        afishas.add(new ru.krus.bashNews.Afisha(contents.select("h3[class=c-news-card__header]").text(),
                                contents.select("time[class=c-news-card__concert-info-p]").text(),
                                contents.select("p[class=c-news-card__concert-info-p c-news-card__concert-info-location]").text(),
                                contents.select("p[class=c-news-card__concert-info-p]").text(),
                                img.substring(23,img.length()-3),
                                "https://gorobzor.ru" + contents.select("a").attr("href")
                        ));

                    }

                }
            }
            doc = null;
            try {
                doc = Jsoup.connect("https://gorobzor.ru/afisha/vecherinky").timeout(5 * 1000).get();


            } catch (SocketTimeoutException e){
                e.printStackTrace();
                //Toast.makeText(getApplicationContext(), "Нет соединения!", Toast.LENGTH_SHORT).show();
            } catch (IOException e){
                e.printStackTrace();
                //Toast.makeText(getApplicationContext(), "Нет соединения!", Toast.LENGTH_SHORT).show();
            }

            if (doc != null)
            {
                content = doc.select("div[class=js-grid-news-content]");
                //afishas.clear();
                for (Element contents: content){
                    if (contents.select("div[class=js-grid-news-content]").text() != "") {
                        String img = contents.select("div[class=c-news-card__img-inner]").attr("style");
                        //Log.d(LOG_TAG,img.substring(23,img.length()-3));
                        afishas.add(new ru.krus.bashNews.Afisha(contents.select("h3[class=c-news-card__header]").text(),
                                contents.select("time[class=c-news-card__concert-info-p]").text(),
                                contents.select("p[class=c-news-card__concert-info-p c-news-card__concert-info-location]").text(),
                                contents.select("p[class=c-news-card__concert-info-p]").text(),
                                img.substring(23,img.length()-3),
                                "https://gorobzor.ru" + contents.select("a").attr("href")
                        ));

                    }

                }
            }
            doc = null;
            try {
                doc = Jsoup.connect("https://gorobzor.ru/afisha/drugie-sobitia").timeout(5 * 1000).get();


            } catch (SocketTimeoutException e){
                e.printStackTrace();
                //Toast.makeText(getApplicationContext(), "Нет соединения!", Toast.LENGTH_SHORT).show();
            } catch (IOException e){
                e.printStackTrace();
                //Toast.makeText(getApplicationContext(), "Нет соединения!", Toast.LENGTH_SHORT).show();
            }

            if (doc != null)
            {
                content = doc.select("div[class=js-grid-news-content]");
                //afishas.clear();
                for (Element contents: content){
                    if (contents.select("div[class=js-grid-news-content]").text() != "") {
                        String img = contents.select("div[class=c-news-card__img-inner]").attr("style");
                        //Log.d(LOG_TAG,img.substring(23,img.length()-3));
                        afishas.add(new ru.krus.bashNews.Afisha(contents.select("h3[class=c-news-card__header]").text(),
                                contents.select("time[class=c-news-card__concert-info-p]").text(),
                                contents.select("p[class=c-news-card__concert-info-p c-news-card__concert-info-location]").text(),
                                contents.select("p[class=c-news-card__concert-info-p]").text(),
                                img.substring(23,img.length()-3),
                                "https://gorobzor.ru" + contents.select("a").attr("href")
                        ));

                    }

                }
            }
            return null;
        }
        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            lv.setAdapter(adapter_afisha);
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    public void NewsParse(){
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeToRefresh);
        mSwipeRefreshLayout.setRefreshing(true);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                lv = (ListView) findViewById(R.id.lvNews);
                NewThread newThread = new NewThread();
                newThread.execute();
                adapter = new ru.krus.bashNews.NewsAdapter(MainActivity.this, news);
            }
        });

        lv = (ListView) findViewById(R.id.lvNews);

        NewThread newThread = new NewThread();
        newThread.execute();
        adapter = new ru.krus.bashNews.NewsAdapter(this, news);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                      @Override
                                      public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                                          Intent intent = new Intent(MainActivity.this, ru.krus.bashNews.NewsDetailed.class);
                                          intent.putExtra("title", news.get(position).title);
                                          intent.putExtra("link", news.get(position).link);
                                          intent.putExtra("add", news.get(position).additional);

                                          startActivity(intent);
                                      }
                                  }
        );
    }
    public void AfishaParse(){
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeToRefresh);
        mSwipeRefreshLayout.setRefreshing(true);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                lv = (ListView) findViewById(R.id.lvNews);
                NewThreadAfisha newThreadA = new NewThreadAfisha();
                newThreadA.execute();
                adapter_afisha = new ru.krus.bashNews.AfishaAdapter(MainActivity.this, afishas);
            }
        });

        lv = (ListView) findViewById(R.id.lvNews);

        NewThreadAfisha newThreadA = new NewThreadAfisha();
        newThreadA.execute();
        adapter_afisha = new ru.krus.bashNews.AfishaAdapter(MainActivity.this, afishas);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                      @Override
                                      public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                                          Intent intent = new Intent(MainActivity.this, ru.krus.bashNews.AfishaDetailed.class);
                                          intent.putExtra("title", afishas.get(position).artist);
                                          intent.putExtra("link", afishas.get(position).link);
                                          intent.putExtra("date", afishas.get(position).date);
                                          intent.putExtra("place", afishas.get(position).place);
                                          intent.putExtra("price", afishas.get(position).price);

                                          startActivity(intent);
                                      }
                                  }
        );
    }
    @SuppressLint("WrongConstant")
    @Override
    protected void onResume() {
        super.onResume();
        final NavigationView navigationView = findViewById(R.id.nav_view);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean bigText = sp.getBoolean("bigText",false);
        if (bigText) {
            navigationView.setItemTextAppearance(R.style.TextAppearance_AppCompat_Large);
        }else{
            navigationView.setItemTextAppearance(R.style.TextAppearance_AppCompat_Small);
        }
        materialDesignFAM = (FloatingActionMenu) findViewById(R.id.fab_menu);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean buttonHide = sp.getBoolean("fabHide", false);
        if (buttonHide) {
            materialDesignFAM.setVisibility(4);
        }else {
            materialDesignFAM.setVisibility(0);
        }
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        String listTheme = sp.getString("listTheme", "1");
        if (listTheme.equals("")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            }else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        } else if (listTheme.equals("1")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                View view = getWindow().getDecorView();
                view.setSystemUiVisibility(view.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        } else if (listTheme.equals("2")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                View view = getWindow().getDecorView();
                view.setSystemUiVisibility(view.getSystemUiVisibility() & ~(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR));
            }
        }else if (listTheme.equals("3")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            }
        }
    }
}
