package com.solodroid.yourradioapp.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.solodroid.yourradioapp.Config;
import com.solodroid.yourradioapp.R;
import com.solodroid.yourradioapp.adapters.AdapterListRadio;
import com.solodroid.yourradioapp.firebase.Analytics;
import com.solodroid.yourradioapp.json.JsonConstant;
import com.solodroid.yourradioapp.json.JsonUtils;
import com.solodroid.yourradioapp.models.ItemListRadio;
import com.solodroid.yourradioapp.services.RadiophonyService;
import com.solodroid.yourradioapp.utilities.DatabaseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ActivityRadioByCategory extends AppCompatActivity implements OnClickListener {

    ListView listView;
    TextView textView;
    ImageView imageView1, imageView2;
    AdapterListRadio adapterListRadio;
    RelativeLayout relativeLayout;
    ItemListRadio itemListRadio;
    CharSequence charSequence = null;
    ProgressBar progressBar;
    DatabaseHandler databaseHandler;
    SwipeRefreshLayout swipeRefreshLayout = null;
    List<ItemListRadio> arrayItemListRadio;
    String StrName, strCategoryName, StrImage, StrUrl, StrId;
    private AdView mAdView;
    private InterstitialAd interstitial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radio_by_category);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //Firebase LogEvent
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, getResources().getString(R.string.analytics_item_id_2));
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, getResources().getString(R.string.analytics_item_name_2));
        //bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Activity");

        //Logs an app event.
        Analytics.getFirebaseAnalytics().logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        //Sets whether analytics collection is enabled for this app on this device.
        Analytics.getFirebaseAnalytics().setAnalyticsCollectionEnabled(true);

        //Sets the minimum engagement time required before starting a session. The default value is 10000 (10 seconds). Let's make it 5 seconds
        Analytics.getFirebaseAnalytics().setMinimumSessionDuration(5000);

        //Sets the duration of inactivity that terminates the current session. The default value is 1800000 (30 minutes). Let’s make it 10.
        Analytics.getFirebaseAnalytics().setSessionTimeoutDuration(1000000);

        loadInterstitialAd();

        mAdView = (AdView) findViewById(R.id.adView);
        mAdView.loadAd(new AdRequest.Builder().build());
        mAdView.setAdListener(new AdListener() {

            @Override
            public void onAdClosed() {
            }

            @Override
            public void onAdFailedToLoad(int error) {
                mAdView.setVisibility(View.GONE);
            }

            @Override
            public void onAdLeftApplication() {
            }

            @Override
            public void onAdOpened() {
            }

            @Override
            public void onAdLoaded() {
                mAdView.setVisibility(View.VISIBLE);
            }
        });

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.orange, R.color.green, R.color.blue);

        listView = (ListView) findViewById(R.id.list_radio);

        arrayItemListRadio = new ArrayList<ItemListRadio>();
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        databaseHandler = new DatabaseHandler(ActivityRadioByCategory.this);

        registerForContextMenu(listView);
        if (JsonUtils.isNetworkAvailable(ActivityRadioByCategory.this)) {
            new MyTask().execute(Config.SERVER_URL + "/api.php?cat_id=" + JsonConstant.CATEGORY_ID);
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
        }

        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                itemListRadio = arrayItemListRadio.get(position);

                String radioName = itemListRadio.getRadioName();
                if (RadiophonyService.getInstance().isPlaying()) {
                    ItemListRadio playingRadio = RadiophonyService.getInstance().getPlayingRadioStation();
                    String playingRadioName = playingRadio.getRadioName();

                    if (radioName.equals(playingRadioName)) {
                        play(false);
                        JsonConstant.IS_PLAYING = "1";

                    } else {
                        play(false);
                        ItemListRadio radio = find(position);
                        RadiophonyService.initialize(ActivityRadioByCategory.this, radio, 2);
                        play(true);
                        JsonConstant.IS_PLAYING = "0";
                    }
                } else {
                    ItemListRadio radio = find(position);
                    RadiophonyService.initialize(ActivityRadioByCategory.this, radio, 2);
                    play(true);
                    JsonConstant.IS_PLAYING = "0";
                }
            }
        });

        // Using to refresh webpage when user swipes the screen
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        adapterListRadio.clear();
                        new MyTask().execute(Config.SERVER_URL + "/api.php?cat_id=" + JsonConstant.CATEGORY_ID);
                    }
                }, 3000);
            }
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                boolean enable = false;
                if (listView != null && listView.getChildCount() > 0) {
                    boolean firstItemVisible = listView.getFirstVisiblePosition() == 0;
                    boolean topOfFirstItemVisible = listView.getChildAt(0).getTop() == 0;
                    enable = firstItemVisible && topOfFirstItemVisible;
                }
                swipeRefreshLayout.setEnabled(enable);
            }
        });


    }

    public ItemListRadio find(int position) {
        ItemListRadio object = new ItemListRadio();
        itemListRadio = arrayItemListRadio.get(position);
        object.setRadioName(itemListRadio.getRadioName());
        object.setRadioCategoryName(itemListRadio.getRadioCategoryName());
        object.setRadioId(itemListRadio.getRadioId());
        object.setRadioImageurl(itemListRadio.getRadioImageurl());
        object.setRadiourl(itemListRadio.getRadiourl());
        return object;
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {

        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
        int index = info.position;

        itemListRadio = arrayItemListRadio.get(index);
        StrName = itemListRadio.getRadioName();
        StrId = itemListRadio.getRadioId();

        menu.setHeaderTitle(String.valueOf(StrName));
        getMenuInflater().inflate(R.menu.context, menu);

        List<ItemListRadio> item = databaseHandler.getFavRow(StrId);
        if (item.size() == 0) {
            menu.findItem(R.id.menu_context_favorite).setTitle(R.string.option_set_favorite);
            charSequence = menu.findItem(R.id.menu_context_favorite).getTitle();
        } else {
            if (item.get(0).getRadioId().equals(StrId)) {
                menu.findItem(R.id.menu_context_favorite).setTitle(R.string.option_unset_favorite);
                charSequence = menu.findItem(R.id.menu_context_favorite).getTitle();
            }

        }
    }

    public boolean onContextItemSelected(MenuItem item) {

        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        int index = info.position;
        itemListRadio = arrayItemListRadio.get(index);
        StrName = itemListRadio.getRadioName();
        strCategoryName = itemListRadio.getRadioCategoryName();
        StrId = itemListRadio.getRadioId();
        StrImage = itemListRadio.getRadioImageurl();
        StrUrl = itemListRadio.getRadiourl();

        switch (item.getItemId()) {
            case R.id.menu_context_favorite:
                if (charSequence.equals(getString(R.string.option_set_favorite))) {
                    databaseHandler.AddtoFavorite(new ItemListRadio(StrId, StrName, strCategoryName, StrUrl, StrImage));
                    Toast.makeText(getApplicationContext(), getString(R.string.favorite_added), Toast.LENGTH_SHORT).show();
                    showInterstitialAd();

                } else if (charSequence.equals(getString(R.string.option_unset_favorite))) {
                    databaseHandler.RemoveFav(new ItemListRadio(StrId));
                    Toast.makeText(getApplicationContext(), getString(R.string.favorite_removed), Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.menu_context_share:

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.share_title) + StrName + "\n" + getResources().getString(R.string.share_content) + "https://play.google.com/store/apps/details?id=" + getPackageName());
                sendIntent.setType("text/plain");
                startActivity(sendIntent);

                break;

            default:
                break;
        }
        return true;
    }

    private class MyTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            return JsonUtils.getJSONString(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressBar.setVisibility(View.INVISIBLE);

            if (null == result || result.length() == 0) {
                Toast.makeText(getApplicationContext(), getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();

            } else {

                try {
                    JSONObject json = new JSONObject(result);
                    JSONArray jsonArray = json.getJSONArray(JsonConstant.RADIO_ARRAY_NAME);
                    JSONObject object = null;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        object = jsonArray.getJSONObject(i);

                        ItemListRadio item = new ItemListRadio();
                        item.setRadioName(object.getString(JsonConstant.RADIO_NAME));
                        item.setRadioCategoryName(object.getString(JsonConstant.RADIO_CATEGORY_NAME));
                        item.setRadioId(object.getString(JsonConstant.RADIO_CID));
                        item.setRadioImageurl(object.getString(JsonConstant.RADIO_IMAGE));
                        item.setRadiourl(object.getString(JsonConstant.RADIO_URL));
                        arrayItemListRadio.add(item);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                setAdapterToListView();
            }
        }
    }

    public void setAdapterToListView() {
        adapterListRadio = new AdapterListRadio(ActivityRadioByCategory.this, R.layout.lsv_item_list_radio, arrayItemListRadio);
        listView.setAdapter(adapterListRadio);
    }

    public void notifyShowBar() {
        ItemListRadio radioStation = RadiophonyService.getInstance().getPlayingRadioStation();
        relativeLayout = (RelativeLayout) findViewById(R.id.main_bar);
        imageView1 = (ImageView) findViewById(R.id.main_bar_logo);
        textView = (TextView) findViewById(R.id.main_bar_station);
        imageView2 = (ImageView) findViewById(R.id.main_pause);
        imageView2.setOnClickListener(this);

        Glide
                .with(this)
                .load(Config.SERVER_URL + "/upload/" + radioStation.getRadioImageurl())
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher)
                .crossFade()
                .into(imageView1);

        textView.setText(radioStation.getRadioName());
        relativeLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.main_pause:
                play(false);
                break;

            default:
                break;
        }
    }

    public void play(boolean play) {
        if (!play) {
            stopService(new Intent(ActivityRadioByCategory.this, RadiophonyService.class));
            relativeLayout.setVisibility(View.GONE);
        } else {
            startService(new Intent(ActivityRadioByCategory.this, RadiophonyService.class));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search, menu);

        final android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView)
                MenuItemCompat.getActionView(menu.findItem(R.id.search));

        final MenuItem searchMenuItem = menu.findItem(R.id.search);
        searchView.setQueryHint(getResources().getString(R.string.search));

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (!hasFocus) {
                    searchMenuItem.collapseActionView();
                    searchView.setQuery("", false);
                }
            }
        });

        searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                String text = newText.toLowerCase(Locale.getDefault());
                adapterListRadio.filter(text);
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            default:
                return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        mAdView.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mAdView.resume();
        super.onResume();
        if (RadiophonyService.getInstance().isPlaying()) {
            notifyShowBar();
        }
    }

    @Override
    protected void onDestroy() {
        mAdView.destroy();
        super.onDestroy();
    }

    private void loadInterstitialAd() {
        Log.d("TAG", "showAd");
        interstitial = new InterstitialAd(ActivityRadioByCategory.this);
        interstitial.setAdUnitId(getResources().getString(R.string.admob_interstitial_id));
        interstitial.loadAd(new AdRequest.Builder().build());
    }

    private void showInterstitialAd() {
        if (interstitial.isLoaded()) {
            interstitial.show();
        }
    }

}
