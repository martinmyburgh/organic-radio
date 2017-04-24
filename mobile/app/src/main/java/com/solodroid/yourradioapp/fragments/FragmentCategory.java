package com.solodroid.yourradioapp.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.solodroid.yourradioapp.Config;
import com.solodroid.yourradioapp.R;
import com.solodroid.yourradioapp.activities.ActivityRadioByCategory;
import com.solodroid.yourradioapp.adapters.AdapterCategory;
import com.solodroid.yourradioapp.json.JsonConstant;
import com.solodroid.yourradioapp.json.JsonUtils;
import com.solodroid.yourradioapp.models.ItemCategory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FragmentCategory extends Fragment {

    GridView gridView;
    AdapterCategory adapterCategory;
    ItemCategory itemCategory;
    ProgressBar progressBar;
    List<ItemCategory> arrayItemCategory;
    private ArrayList<ItemCategory> arrayListItemCategory;
    SwipeRefreshLayout swipeRefreshLayout = null;
    private InterstitialAd interstitial;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_category, container, false);
        setHasOptionsMenu(true);

        loadInterstitialAd();

        gridView = (GridView) rootView.findViewById(R.id.grid_radio);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.orange, R.color.green, R.color.blue);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);

        arrayItemCategory = new ArrayList<ItemCategory>();
        this.arrayListItemCategory = new ArrayList<ItemCategory>();

        if (JsonUtils.isNetworkAvailable(getActivity())) {
            new GetTask().execute(Config.SERVER_URL + "/api.php");
        } else {
            Toast.makeText(getActivity(), getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
        }

        gridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                itemCategory = arrayItemCategory.get(position);
                JsonConstant.CATEGORY_ID = itemCategory.getCategoryId();
                JsonConstant.CATEGORY_ID_NAME = itemCategory.getCategoryName();
                Intent intent = new Intent(getActivity(), ActivityRadioByCategory.class);
                startActivity(intent);

                showInterstitialAd();

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
                        adapterCategory.clear();
                        new GetTask().execute(Config.SERVER_URL + "/api.php");
                    }
                }, 3000);
            }
        });

        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                boolean enable = false;
                if (gridView != null && gridView.getChildCount() > 0) {
                    boolean firstItemVisible = gridView.getFirstVisiblePosition() == 0;
                    boolean topOfFirstItemVisible = gridView.getChildAt(0).getTop() == 0;
                    enable = firstItemVisible && topOfFirstItemVisible;
                }
                swipeRefreshLayout.setEnabled(enable);
            }
        });
        return rootView;
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
                    JSONObject objJson = null;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        objJson = jsonArray.getJSONObject(i);

                        ItemCategory objItem = new ItemCategory();
                        objItem.setCategoryName(objJson.getString(JsonConstant.CATEGORY_NAME));
                        objItem.setCategoryId(objJson.getInt(JsonConstant.CATEGORY_CID));
                        objItem.setCategoryImageurl(objJson.getString(JsonConstant.CATEGORY_IMAGE));
                        arrayItemCategory.add(objItem);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                arrayListItemCategory.addAll(arrayItemCategory);
                setAdapterToListView();
            }
        }
    }

    public void setAdapterToListView() {
        adapterCategory = new AdapterCategory(getActivity(), R.layout.lsv_item_category, arrayItemCategory);
        gridView.setAdapter(adapterCategory);
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

                return false;
            }
        });
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        arrayItemCategory.clear();
        if (charText.length() == 0) {
            arrayItemCategory.addAll(arrayListItemCategory);
        } else {
            for (ItemCategory filter : arrayListItemCategory) {
                if (filter.getCategoryName().toLowerCase(Locale.getDefault()).contains(charText)) {
                    arrayItemCategory.add(filter);
                }
            }
        }
        setAdapterToListView();
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
