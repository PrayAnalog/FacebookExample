package com.example.q.facebookexample;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by q on 2017-07-09.
 */

public class CustomGalleryAdapter extends BaseAdapter {

    private ArrayList<Picture> galleryViewItemList = new ArrayList<Picture>();

    public CustomGalleryAdapter() {

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

        Picture gridViewItem = galleryViewItemList.get(position);

        ImageView galleryImageView = (ImageView) convertView.findViewById(R.id.galleryImageView);
//        Picasso.with(context).load(gridViewItem.getThumbnailDir()).into(galleryImageView);
//        Picasso.with(context).load(gridViewItem.getPhotoDir()).into(galleryImageView);
        Picasso.with(context).load("http://13.124.41.33:1234/images/dummy.jpg").into(galleryImageView);


//        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.valueOf(gridViewItem.getPhotoCachedDir()));
//        InputStream photo_stream = ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(), contactUri, true);
//        galleryImageView.setImageBitmap(BitmapFactory.decodeStream(photo_stream));


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

    public void addItem(String photoName, String photoDir, String photoThumbnailDir) {
        Picture item = new Picture();
        item.setPhotoName(photoName);
        item.setPhotoDir(photoDir);
        item.setThumbnailDir(photoThumbnailDir);
        galleryViewItemList.add(item);
    }

}
