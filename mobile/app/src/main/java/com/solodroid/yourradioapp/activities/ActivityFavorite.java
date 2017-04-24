package com.solodroid.yourradioapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
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
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.solodroid.yourradioapp.Config;
import com.solodroid.yourradioapp.R;
import com.solodroid.yourradioapp.adapters.AdapterListRadio;
import com.solodroid.yourradioapp.json.JsonConstant;
import com.solodroid.yourradioapp.models.ItemListRadio;
import com.solodroid.yourradioapp.services.RadiophonyService;
import com.solodroid.yourradioapp.utilities.DatabaseHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ActivityFavorite extends AppCompatActivity implements View.OnClickListener {

    ListView listView;
    AdapterListRadio adapterListRadio;
    ItemListRadio itemListRadio;
    DatabaseHandler databaseHandler;
    TextView textView1, textView2;
    ImageView imageView1, imageView2;
    RelativeLayout relativeLayout;
    String StrName, StrCategoryName, StrImage, StrUrl, StrId;
    List<ItemListRadio> arrayItemListRadio;
    ArrayList<ItemListRadio> arrayListItemListRadio;
    private AdView mAdView;
    private InterstitialAd interstitial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.favorite);
        }

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

        loadInterstitialAd();

        listView = (ListView) findViewById(R.id.list_radio_fav);
        textView1 = (TextView) findViewById(R.id.textView1);

        databaseHandler = new DatabaseHandler(getApplicationContext());
        this.arrayListItemListRadio = new ArrayList<ItemListRadio>();
        arrayItemListRadio = databaseHandler.getAllData();
        arrayListItemListRadio.addAll(arrayItemListRadio);
        setAdapterToListview();

        registerForContextMenu(listView);

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
                        RadiophonyService.initialize(ActivityFavorite.this, radio, 3);
                        play(true);
                        JsonConstant.IS_PLAYING = "0";
                    }
                } else {
                    ItemListRadio radio = find(position);
                    RadiophonyService.initialize(ActivityFavorite.this, radio, 3);
                    play(true);
                    JsonConstant.IS_PLAYING = "0";
                }
            }
        });
    }

    public void notifyShowBar() {
        ItemListRadio radioStation = RadiophonyService.getInstance().getPlayingRadioStation();
        relativeLayout = (RelativeLayout) findViewById(R.id.main_bar);
        imageView1 = (ImageView) findViewById(R.id.main_bar_logo);
        textView2 = (TextView) findViewById(R.id.main_bar_station);
        imageView2 = (ImageView) findViewById(R.id.main_pause);
        imageView2.setOnClickListener(this);

        Glide
                .with(this)
                .load(Config.SERVER_URL + "/upload/" + radioStation.getRadioImageurl())
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher)
                .crossFade()
                .into(imageView1);

        textView2.setText(radioStation.getRadioName());
        relativeLayout.setVisibility(View.VISIBLE);
    }

    public void setAdapterToListview() {

        adapterListRadio = new AdapterListRadio(ActivityFavorite.this, R.layout.lsv_item_list_radio, arrayItemListRadio);
        listView.setAdapter(adapterListRadio);

        if (arrayItemListRadio.size() == 0) {
            textView1.setVisibility(View.VISIBLE);
        } else {
            textView1.setVisibility(View.INVISIBLE);
        }
    }

    public ItemListRadio find(int postion) {
        ItemListRadio object = new ItemListRadio();
        itemListRadio = arrayItemListRadio.get(postion);
        object.setRadioName(itemListRadio.getRadioName());
        object.setRadioCategoryName(itemListRadio.getRadioCategoryName());
        object.setRadioId(itemListRadio.getRadioId());
        object.setRadioImageurl(itemListRadio.getRadioImageurl());
        object.setRadiourl(itemListRadio.getRadiourl());
        return object;
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
            stopService(new Intent(ActivityFavorite.this, RadiophonyService.class));
            relativeLayout.setVisibility(View.GONE);
        } else {
            startService(new Intent(ActivityFavorite.this, RadiophonyService.class));
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {

        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
        int index = info.position;

        itemListRadio = arrayItemListRadio.get(index);
        StrName = itemListRadio.getRadioName();

        menu.setHeaderTitle(String.valueOf(StrName));
        getMenuInflater().inflate(R.menu.context, menu);

        menu.findItem(R.id.menu_context_favorite).setTitle(R.string.option_unset_favorite);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        int index = info.position;

        itemListRadio = arrayItemListRadio.get(index);

        StrName = itemListRadio.getRadioName();
        StrCategoryName = itemListRadio.getRadioCategoryName();
        StrId = itemListRadio.getRadioId();
        StrImage = itemListRadio.getRadioImageurl();
        StrUrl = itemListRadio.getRadiourl();

        switch (item.getItemId()) {
            case R.id.menu_context_favorite:

                databaseHandler.RemoveFav(new ItemListRadio(StrId));
                arrayItemListRadio = databaseHandler.getAllData();
                setAdapterToListview();
                Toast.makeText(getApplicationContext(), getString(R.string.favorite_removed), Toast.LENGTH_SHORT).show();

                showInterstitialAd();

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search, menu);

        final android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView)
                MenuItemCompat.getActionView(menu.findItem(R.id.search));

        final MenuItem searchMenuItem = menu.findItem(R.id.search);

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
                filter(text);
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {

                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        arrayItemListRadio.clear();
        if (charText.length() == 0) {
            arrayItemListRadio.addAll(arrayListItemListRadio);
        } else {
            for (ItemListRadio wp : arrayListItemListRadio) {
                if (wp.getRadioName().toLowerCase(Locale.getDefault()).contains(charText)) {
                    arrayItemListRadio.add(wp);
                }
            }
        }

        setAdapterToListview();
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
    protected void onPause() {
        mAdView.pause();
        super.onPause();
    }

    @Override
    public void onResume() {
        mAdView.resume();

        super.onResume();
        if (RadiophonyService.getInstance().isPlaying()) {
            notifyShowBar();
        }

        arrayItemListRadio = databaseHandler.getAllData();
        adapterListRadio = new AdapterListRadio(ActivityFavorite.this, R.layout.lsv_item_list_radio, arrayItemListRadio);
        listView.setAdapter(adapterListRadio);

        if (arrayItemListRadio.size() == 0) {
            textView1.setVisibility(View.VISIBLE);
        } else {
            textView1.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        mAdView.destroy();
        super.onDestroy();
    }

    private void loadInterstitialAd() {
        Log.d("TAG", "showAd");
        interstitial = new InterstitialAd(ActivityFavorite.this);
        interstitial.setAdUnitId(getResources().getString(R.string.admob_interstitial_id));
        interstitial.loadAd(new AdRequest.Builder().build());
    }

    private void showInterstitialAd() {
        if (interstitial.isLoaded()) {
            interstitial.show();
        }
    }

}
