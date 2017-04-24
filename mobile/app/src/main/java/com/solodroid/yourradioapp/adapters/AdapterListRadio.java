package com.solodroid.yourradioapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.solodroid.yourradioapp.Config;
import com.solodroid.yourradioapp.R;
import com.solodroid.yourradioapp.json.JsonConstant;
import com.solodroid.yourradioapp.models.ItemListRadio;
import com.solodroid.yourradioapp.services.RadiophonyService;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdapterListRadio extends ArrayAdapter<ItemListRadio> {

    ItemListRadio itemListRadio;
    private int row;
    private Activity activity;
    private List<ItemListRadio> arrayItemListRadio;
    private ArrayList<ItemListRadio> arrayListItemListRadio;

    public AdapterListRadio(Activity act, int resource, List<ItemListRadio> arrayList) {
        super(act, resource, arrayList);
        this.activity = act;
        this.row = resource;
        this.arrayItemListRadio = arrayList;
        this.arrayListItemListRadio = new ArrayList<ItemListRadio>();
        this.arrayListItemListRadio.addAll(arrayItemListRadio);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(row, null);
            holder = new ViewHolder();
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        if ((arrayItemListRadio == null) || ((position + 1) > arrayItemListRadio.size()))
            return view;

        itemListRadio = arrayItemListRadio.get(position);

        holder.textView1 = (TextView) view.findViewById(R.id.row_label);
        holder.textView2 = (TextView) view.findViewById(R.id.row_category);
        holder.imageView1 = (ImageView) view.findViewById(R.id.row_logo);
        holder.imageView2 = (ImageView) view.findViewById(R.id.row_play);

        Typeface font = Typeface.createFromAsset(activity.getAssets(), "fonts/NotoSans-Regular.ttf");
        holder.textView1.setTypeface(font);
        holder.textView2.setTypeface(font);

        holder.textView1.setText(itemListRadio.getRadioName());
        holder.textView2.setText(itemListRadio.getRadioCategoryName());

        Glide
                .with(getContext())
                .load(Config.SERVER_URL + "/upload/" + itemListRadio.getRadioImageurl())
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher)
                .crossFade()
                .into(holder.imageView1);

        if (RadiophonyService.getInstance().isPlaying()) {
            String name = RadiophonyService.getInstance().getPlayingRadioStation().getRadioName();
            if (name.equals(itemListRadio.getRadioName())) {
                holder.imageView2.setVisibility(View.VISIBLE);
            }
        } else {
            holder.imageView2.setVisibility(View.GONE);
        }
        if (!JsonConstant.IS_PLAYING.equals("0")) {
            holder.imageView2.setVisibility(View.GONE);
        }

        return view;
    }

    public class ViewHolder {
        public TextView textView1, textView2;
        public ImageView imageView1, imageView2;
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
        notifyDataSetChanged();
    }
}
