package com.solodroid.yourradioapp.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.solodroid.yourradioapp.Config;
import com.solodroid.yourradioapp.R;
import com.solodroid.yourradioapp.activities.MainActivity;
import com.solodroid.yourradioapp.adapters.AdapterListRadio;
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

public class FragmentListRadio extends Fragment {

    ListView listView;
    Intent intent;
    CharSequence charSequence = null;
    ItemListRadio itemListRadio;
    AdapterListRadio adapterListRadio;
    DatabaseHandler databaseHandler;
    ProgressBar progressBar;
    SwipeRefreshLayout swipeRefreshLayout = null;
    String StrName, StrCategoryName, StrImage, StrUrl, StrId;
    List<ItemListRadio> arrayItemListRadio;
    private ArrayList<ItemListRadio> arrayListItemListRadio;
    private InterstitialAd interstitial;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.intent = new Intent(getActivity(), RadiophonyService.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_list_radio, container, false);
        setHasOptionsMenu(true);
        loadInterstitialAd();

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.orange, R.color.green, R.color.blue);
        listView = (ListView) rootView.findViewById(R.id.list_radio);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);

        arrayItemListRadio = new ArrayList<ItemListRadio>();
        databaseHandler = new DatabaseHandler(getActivity());

        this.arrayListItemListRadio = new ArrayList<ItemListRadio>();

        if (JsonUtils.isNetworkAvailable(getActivity())) {
            new GetTask().execute(Config.SERVER_URL + "/api.php?latest=20");
        } else {
            Toast.makeText(getActivity(), getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
        }

        // Using to refresh webpage when user swipes the screen
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        adapterListRadio.clear();
                        new GetTask().execute(Config.SERVER_URL + "/api.php?latest=20");
                    }
                }, 3000);
            }
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                boolean enable = false;
                if (listView != null && listView.getChildCount() > 0) {
                    boolean firstItemVisible = listView.getFirstVisiblePosition() == 0;
                    boolean topOfFirstItemVisible = listView.getChildAt(0).getTop() == 0;
                    enable = firstItemVisible && topOfFirstItemVisible;
                }
                swipeRefreshLayout.setEnabled(enable);
            }
        });


        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                itemListRadio = arrayItemListRadio.get(position);

                String radioName = itemListRadio.getRadioName();
                if (RadiophonyService.getInstance().isPlaying()) {
                    ItemListRadio play = RadiophonyService.getInstance().getPlayingRadioStation();
                    String playingRadioName = play.getRadioName();

                    if (radioName.equals(playingRadioName)) {
                        ((MainActivity) getActivity()).play(false);
                        JsonConstant.IS_PLAYING = "1";

                    } else {
                        ((MainActivity) getActivity()).play(false);
                        ItemListRadio radio = find(position);
                        RadiophonyService.initialize(getActivity(), radio, 1);
                        ((MainActivity) getActivity()).play(true);
                        JsonConstant.IS_PLAYING = "0";
                    }
                } else {
                    ItemListRadio radio = find(position);
                    RadiophonyService.initialize(getActivity(), radio, 1);
                    ((MainActivity) getActivity()).play(true);
                    JsonConstant.IS_PLAYING = "0";
                }
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        registerForContextMenu(listView);
    }


    private class GetTask extends AsyncTask<String, Void, String> {

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
                Toast.makeText(getActivity(), getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();

            } else {

                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(JsonConstant.RADIO_ARRAY_NAME);
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
                arrayListItemListRadio.addAll(arrayItemListRadio);
                setAdapterToListview();
            }
        }
    }

    public ItemListRadio find(int position) {

        ItemListRadio objItem = new ItemListRadio();
        itemListRadio = arrayItemListRadio.get(position);
        objItem.setRadioName(itemListRadio.getRadioName());
        objItem.setRadioCategoryName(itemListRadio.getRadioCategoryName());
        objItem.setRadioId(itemListRadio.getRadioId());
        objItem.setRadioImageurl(itemListRadio.getRadioImageurl());
        objItem.setRadiourl(itemListRadio.getRadiourl());

        return objItem;
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
        getActivity().getMenuInflater().inflate(R.menu.context, menu);

        List<ItemListRadio> itemList = databaseHandler.getFavRow(StrId);
        if (itemList.size() == 0) {
            menu.findItem(R.id.menu_context_favorite).setTitle(R.string.option_set_favorite);
            charSequence = menu.findItem(R.id.menu_context_favorite).getTitle();
        } else {
            if (itemList.get(0).getRadioId().equals(StrId)) {
                menu.findItem(R.id.menu_context_favorite).setTitle(R.string.option_unset_favorite);
                charSequence = menu.findItem(R.id.menu_context_favorite).getTitle();
            }

        }
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

                if (charSequence.equals(getString(R.string.option_set_favorite))) {
                    databaseHandler.AddtoFavorite(new ItemListRadio(StrId, StrName, StrCategoryName, StrUrl, StrImage));
                    Toast.makeText(getActivity(), getString(R.string.favorite_added), Toast.LENGTH_SHORT).show();
                    showInterstitialAd();

                } else if (charSequence.equals(getString(R.string.option_unset_favorite))) {
                    databaseHandler.RemoveFav(new ItemListRadio(StrId));
                    Toast.makeText(getActivity(), getString(R.string.favorite_removed), Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.menu_context_share:

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "I'm Listening " +StrName+"\n" +" I Would like to share this with you. Here You Can Download This Application from PlayStore "+"https://play.google.com/store/apps/details?id="+ getActivity().getPackageName());
                sendIntent.setType("text/plain");
                startActivity(sendIntent);

                break;

            default:
                break;
        }
        return true;
    }

    public void setAdapterToListview() {
        adapterListRadio = new AdapterListRadio(getActivity(), R.layout.lsv_item_list_radio, arrayItemListRadio);
        listView.setAdapter(adapterListRadio);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
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
                filter(text);
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {

                return true;
            }
        });
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        arrayItemListRadio.clear();
        if (charText.length() == 0) {
            arrayItemListRadio.addAll(arrayListItemListRadio);
        } else {
            for (ItemListRadio filter : arrayListItemListRadio) {
                if (filter.getRadioName().toLowerCase(Locale.getDefault()).contains(charText)) {
                    arrayItemListRadio.add(filter);
                }
            }
        }
        setAdapterToListview();
    }

    private void loadInterstitialAd() {
        Log.d("TAG", "showAd");
        interstitial = new InterstitialAd(getActivity());
        interstitial.setAdUnitId(getResources().getString(R.string.admob_interstitial_id));
        interstitial.loadAd(new AdRequest.Builder().build());
    }

    private void showInterstitialAd() {
        if (interstitial.isLoaded()) {
            interstitial.show();
        }
    }
}
