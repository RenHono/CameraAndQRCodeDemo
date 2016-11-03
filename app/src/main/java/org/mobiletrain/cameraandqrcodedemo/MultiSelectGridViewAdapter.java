package org.mobiletrain.cameraandqrcodedemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lihailong on 2016/11/3.
 */

public class MultiSelectGridViewAdapter extends BaseAdapter{
    private Context context;
    private List<String> imageCollection;
    private boolean[] selectedTag;

    public MultiSelectGridViewAdapter(Context context, List<String> imageCollection) {
        this.context = context;
        this.imageCollection = imageCollection;

        selectedTag = new boolean[imageCollection.size()];
    }

    @Override
    public int getCount() {
        return imageCollection.size();
    }

    @Override
    public Object getItem(int i) {
        return imageCollection.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_gridview_picture_multiselect, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }
        else {
            holder = (ViewHolder)view.getTag();
        }

        String fileName = imageCollection.get(i);
        Picasso.with(context).load(new File(fileName)).resize(330,440).into(holder.itemImageView);

        if (selectedTag[i]) {
            holder.itemImageViewChecked.setImageResource(R.drawable.autopay_checkbox_checked);
        }
        else {
            holder.itemImageViewChecked.setImageResource(R.drawable.autopay_checkbox_normal);
        }

        return view;
    }

    public void changeState(int position) {
        selectedTag[position] = !selectedTag[position];
        notifyDataSetChanged();
    }

    public List<Integer> getSelectedIndexes() {
        List<Integer> result = new ArrayList<>();
        for (int i=0; i<selectedTag.length; i++) {
            if (selectedTag[i]) {
                result.add(i);
            }
        }
        return result;
    }

    private class ViewHolder {
        ViewHolder(View view) {
            x.view().inject(this, view);
        }

        @ViewInject(R.id.itemImageView)
        ImageView itemImageView;

        @ViewInject(R.id.itemImageViewChecked)
        ImageView itemImageViewChecked;
    }
}
