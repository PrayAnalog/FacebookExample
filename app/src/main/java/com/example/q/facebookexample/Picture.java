package com.example.q.facebookexample;

/**
 * Created by q on 2017-07-09.
 */


public class Picture {
    private String photoName;
    private String photoDir;
    private String thumbnailDir;
    private String photoCachedDir;

    public String getPhotoName() { return this.photoName; }
    public String getPhotoDir() { return this.photoDir; }
    public String getThumbnailDir() { return this.thumbnailDir; }
    public String getPhotoCachedDir() { return this.photoCachedDir; }

    public void setPhotoName(String photoName) { this.photoName = photoName; }
    public void setPhotoDir(String photoDir) { this.photoDir = photoDir; }
    public void setThumbnailDir(String thumbnailDir) { this.thumbnailDir = thumbnailDir; }
    public void setPhotoCachedDir(String photoCachedDir) { this.photoCachedDir = photoCachedDir; }
}
