package com.example.q.facebookexample;

import android.content.Context;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.example.q.facebookexample.util.Picture;

import java.util.ArrayList;

/**
 * Created by q on 2017-07-09.
 */

public class CustomGalleryAdapter extends BaseAdapter {
    public Integer imageSize = 0;
    public Integer column = 3;

    private ArrayList<Picture> galleryViewItemList = new ArrayList<Picture>();

    public CustomGalleryAdapter(Integer column) {
        this.column = column;
    }

    public ArrayList<Picture> getPictureViewItemList() {
        return this.galleryViewItemList;
    }
    public void setPictureViewItemList(ArrayList<Picture> newList) {
        this.galleryViewItemList = newList;
    }

    @Override
    public int getCount() {
        return galleryViewItemList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Context context = parent.getContext();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.gallery_grid_item, parent, false);
        }

        convertView.setPadding(1, 1, 1, 1);

        Picture gridViewItem = galleryViewItemList.get(position);

        if (imageSize == 0) {
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            imageSize = display.getWidth()/this.column;
        }

        ImageView galleryImageView = (ImageView) convertView.findViewById(R.id.galleryImageView);
        galleryImageView.setLayoutParams(new LinearLayout.LayoutParams(imageSize, imageSize));
        galleryImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        Glide.with(context).load(gridViewItem.getPhotoDir()).into(galleryImageView);

        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return position ;
    }

    @Override
    public Object getItem(int position) {
        return galleryViewItemList.get(position) ;
    }

    public void addItem(String photoID, String photoName, String photoDir, String photoThumbnailDir) {
        Picture item = new Picture();
        item.setPhotoID(photoID);
        item.setPhotoName(photoName);
        item.setPhotoDir(photoDir);
        item.setThumbnailDir(photoThumbnailDir);
        galleryViewItemList.add(item);
    }

}
