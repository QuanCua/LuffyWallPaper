package com.skyfree.coolluffywallpaperhd.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.skyfree.coolluffywallpaperhd.object.Picture;
import com.skyfree.coolluffywallpaperhd.R;

import java.util.List;

/**
 * Created by Asus on 3/21/2018.
 */

public class GridViewAdapter extends BaseAdapter {
    private List<Picture> pictureList;
    private Context context;

    public GridViewAdapter() {
     }

    public GridViewAdapter(List<Picture> pictureList, Context context) {
        this.pictureList = pictureList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return pictureList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.line_picture, null);

        ImageView imgView = (ImageView)view.findViewById(R.id.imageViewPicture);
        Glide.with(context)
                .load(pictureList.get(i).getUrl())
                .centerCrop()
                .placeholder(R.drawable.luffy_icon)
                .error(R.drawable.error)
                .into(imgView);

        return view;
    }
}
