package com.skyfree.coolluffywallpaperhd.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.skyfree.coolluffywallpaperhd.object.Picture;
import com.skyfree.coolluffywallpaperhd.R;

import java.util.List;

public class ViewPagerAdapter extends PagerAdapter {
    private Context context;
    private List<Picture> arrayListWallpaper;

    public ViewPagerAdapter(Context context, List<Picture> arrayListWallpaper){
        this.context = context;
        this.arrayListWallpaper = arrayListWallpaper;
    }


    @Override
    public int getCount() {
        return arrayListWallpaper.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.line_picture, container, false);

        ImageView imageView = (ImageView) itemView.findViewById(R.id.imageViewPicture);
        Glide.with(context)
                .load(arrayListWallpaper.get(position).getUrl())
                .centerCrop()
                .placeholder(R.drawable.luffy_icon)
                .error(R.drawable.error)
                .into(imageView);

        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
