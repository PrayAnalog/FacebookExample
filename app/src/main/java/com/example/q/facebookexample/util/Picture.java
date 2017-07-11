package com.example.q.facebookexample.util;

/**
 * Created by q on 2017-07-09.
 */


public class Picture {
    private String photoID;
    private String photoName;
    private String photoDir;
    private String thumbnailDir;
    private String photoCachedDir;

    public String getPhotoID() { return this.photoID ; }
    public String getPhotoName() { return this.photoName; }
    public String getPhotoDir() { return this.photoDir; }
    public String getThumbnailDir() { return this.thumbnailDir; }
    public String getPhotoCachedDir() { return this.photoCachedDir; }

    public void setPhotoID(String photoID) { this.photoID = photoID; }
    public void setPhotoName(String photoName) { this.photoName = photoName; }
    public void setPhotoDir(String photoDir) { this.photoDir = photoDir; }
    public void setThumbnailDir(String thumbnailDir) { this.thumbnailDir = thumbnailDir; }
    public void setPhotoCachedDir(String photoCachedDir) { this.photoCachedDir = photoCachedDir; }
}
