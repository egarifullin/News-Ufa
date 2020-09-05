package ru.krus.ufanews;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Layout;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Objects;

public class NewsDetailed extends AppCompatActivity {
    TextView title_news;
    TextView text_news;
    TextView text_add;
    ImageView ivNews;
    public Elements content;
    public ArrayList<String> link = new ArrayList<String>();
    String tvNewsDetailed;
    String imageLink;
    String imageText;
    int count_element;
    final String LOG_TAG = "myLogs";
    TextView tvImageText;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private AdView mAdView;
    SharedPreferences sp;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detailed);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getIntent().getStringExtra("title"));
        }
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshNews);
        mSwipeRefreshLayout.setRefreshing(true);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                NewThreadDetailed newThread = new NewThreadDetailed();
                newThread.execute();
            }
        });

        title_news = findViewById(R.id.titleNews);
        title_news.setText(getIntent().getStringExtra("title"));

        NewThreadDetailed newThreadNews = new NewThreadDetailed();
        newThreadNews.execute();

        text_add = findViewById(R.id.tvAdd);
        text_add.setText(getIntent().getStringExtra("add"));

        text_news = findViewById(R.id.tvNewsText);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean bigText = sp.getBoolean("bigText",false);
        if (bigText){
            text_news.setTextSize(getResources().getDimension(R.dimen.big_text));
        }else{
            text_news.setTextSize(getResources().getDimension(R.dimen.normal_text));
        }

        tvImageText = findViewById(R.id.tvImageText);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            text_news.setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mymenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            case R.id.menu_browser:
                Intent browserIntent = new
                        Intent(Intent.ACTION_VIEW, Uri.parse(getIntent().getStringExtra("link")));
                startActivity(browserIntent);
            case R.id.share:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, getIntent().getStringExtra("link"));
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class  NewThreadDetailed extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... arg) {
            Document doc = null;
            try {
                doc = Jsoup.connect(getIntent().getStringExtra("link")).timeout(5 * 1000).get();

            } catch (SocketTimeoutException e){
                e.printStackTrace();
                //Toast.makeText(getApplicationContext(), "Ошибка соединения!", Toast.LENGTH_SHORT).show();
            }catch (IOException e){
                e.printStackTrace();
                //Toast.makeText(getApplicationContext(), "Ошибка соединения!", Toast.LENGTH_SHORT).show();
            }catch (NullPointerException e){
                e.printStackTrace();
                //Toast.makeText(getApplicationContext(), "Ошибка соединения!", Toast.LENGTH_SHORT).show();
            }

            if (doc != null)
            {
                ivNews = findViewById(R.id.ivNews);
                content = doc.select("div[class=c-page-content__content]").select("p");
                imageLink = "https://gorobzor.ru" + doc.select("div[class=c-page-content__content]").select("img").attr("src");
                imageText = doc.select("div[class=c-page-content__content]").select("figure").select("figcaption").text();
                tvNewsDetailed = "";
                count_element = 0;
                for (Element contents: content){
                    if (contents.text().contains("Дорогие читатели!" )!= true)
                    {
                        if (count_element != 0 ) {
                            tvNewsDetailed = tvNewsDetailed + contents.text() + "\n" + "\n";
                        }
                    }
                    count_element = count_element + 1;
                }
            }
            else {
               // Toast.makeText(getApplicationContext(), "Нет соединения!", Toast.LENGTH_LONG).show();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            /*if (doc!=null) {*/
                text_news.setText(tvNewsDetailed);
                tvImageText.setText(imageText);
                mSwipeRefreshLayout.setRefreshing(false);
                if (imageLink != null) {
                    Picasso.get().load(Uri.parse(imageLink)).into(ivNews);
                }
            /*}
            else
            {
                Toast.makeText(getApplicationContext(), "Нет соединения!", Toast.LENGTH_LONG).show();
            }
            mSwipeRefreshLayout.setRefreshing(false);*/
        }
    }
}
