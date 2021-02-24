package ru.krus.bashNews;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.text.LineBreaker;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

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

public class AfishaDetailed extends AppCompatActivity {

    TextView title_afisha;
    TextView date_afisha;
    TextView time_afisha;
    TextView place_afisha;
    TextView price_afisha;
    ImageView ivAfisha;
    String time_temp;
    SharedPreferences sp;
    public Elements content;
    public Elements contentTime;
    public ArrayList<String> link = new ArrayList<String>();
    String timeAfisha;
    String tvAfishaDetailed;
    String imageLink;
    String imageText;
    int count_element;
    final String LOG_TAG = "myLogs";
    TextView tvAfishaText;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_afisha_detailed);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getIntent().getStringExtra("title"));
        }
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshAfishaDetailed);
        mSwipeRefreshLayout.setRefreshing(true);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                NewThreadDetailed newThread = new NewThreadDetailed();
                newThread.execute();
            }
        });
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        title_afisha = findViewById(R.id.titleAfisha);
        title_afisha.setText(getIntent().getStringExtra("title"));
        date_afisha = findViewById(R.id.tvAfishaDateNum);
        date_afisha.setText(getIntent().getStringExtra("date"));
        place_afisha = findViewById(R.id.tvAfishaPlaceLoc);
        place_afisha.setText(getIntent().getStringExtra("place"));
        price_afisha = findViewById(R.id.tvAfishaPriceNum);
        price_afisha.setText(getIntent().getStringExtra("price").substring(6));
        tvAfishaText = findViewById(R.id.tvAfishaText);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean bigText = sp.getBoolean("bigText",false);
        if (bigText){
            tvAfishaText.setTextSize(getResources().getDimension(R.dimen.big_text));
        }else{
            tvAfishaText.setTextSize(getResources().getDimension(R.dimen.normal_text));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            tvAfishaText.setJustificationMode(LineBreaker.JUSTIFICATION_MODE_INTER_WORD);
        }
        NewThreadDetailed newThreadAfisha = new NewThreadDetailed();
        newThreadAfisha.execute();
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
                // Toast.makeText(getApplicationContext(), "Нет соединения!", Toast.LENGTH_SHORT).show();
            }catch (IOException e){
                e.printStackTrace();
                //Toast.makeText(getApplicationContext(), "Нет соединения!", Toast.LENGTH_SHORT).show();
            }catch (NullPointerException e){
                e.printStackTrace();
            }

            if (doc != null)
            {
                ivAfisha = findViewById(R.id.ivAfisha);
                imageLink = doc.select("div[class=c-shedule-affiche__icon]").select("span").attr("style");
                time_afisha = findViewById(R.id.tvAfishaTimeNum);
                contentTime = doc.select("p[class=c-shedule-affiche__desc-item]");
                int count = 0;
                for (Element contents: contentTime){
                    if (count == 1)
                    {
                        time_temp = contents.text().substring(6);
                    }
                    count = count + 1;
                }
                content = doc.select("div[class=c-shedule-affiche__desc c-shedule-affiche__desc-full]").select("p");
                tvAfishaDetailed = "";
                for (Element contents: content){
                    if (contents.text().contains("Дорогие читатели!" )!= true)
                    {
                            tvAfishaDetailed = tvAfishaDetailed + contents.text() + "\n" + "\n";
                    }
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
                tvAfishaText.setText(tvAfishaDetailed);
                time_afisha.setText(time_temp);
                mSwipeRefreshLayout.setRefreshing(false);
                Picasso.get().load(Uri.parse(imageLink.substring(23,imageLink.length()-3))).into(ivAfisha);
            /*}
            else
            {
                Toast.makeText(getApplicationContext(), "Нет соединения!", Toast.LENGTH_LONG).show();
            }
            mSwipeRefreshLayout.setRefreshing(false);*/
        }

    }
}
