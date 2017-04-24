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
import com.solodroid.yourradioapp.models.ItemCategory;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdapterCategory extends ArrayAdapter<ItemCategory> {

    ItemCategory itemCategory;
    private int row;
    private Activity activity;
    private List<ItemCategory> arrayItemCategory;
    private ArrayList<ItemCategory> arrayListItemCategory;

    public AdapterCategory(Activity act, int resource, List<ItemCategory> arrayList) {
        super(act, resource, arrayList);
        this.activity = act;
        this.row = resource;
        this.arrayItemCategory = arrayList;
        this.arrayListItemCategory = new ArrayList<ItemCategory>();
        this.arrayListItemCategory.addAll(arrayItemCategory);

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

        if ((arrayItemCategory == null) || ((position + 1) > arrayItemCategory.size()))
            return view;

        itemCategory = arrayItemCategory.get(position);

        holder.textView = (TextView) view.findViewById(R.id.row_label);
        holder.imageView1 = (ImageView) view.findViewById(R.id.row_logo);
        holder.imageView2 = (ImageView) view.findViewById(R.id.row_play);
        holder.imageView2.setVisibility(View.INVISIBLE);

        Typeface font = Typeface.createFromAsset(activity.getAssets(), "fonts/NotoSans-Regular.ttf");
        holder.textView.setTypeface(font);

        holder.textView.setText(itemCategory.getCategoryName());

        Glide
                .with(getContext())
                .load(Config.SERVER_URL + "/upload/category/" + itemCategory.getCategoryImageurl())
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher)
                .crossFade()
                .into(holder.imageView1);

        return view;

    }

    public class ViewHolder {
        public TextView textView;
        public ImageView imageView1, imageView2;
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
        notifyDataSetChanged();
    }
}
