package com.example.crystalfiles.model;

import android.graphics.drawable.Drawable;

public class imageFolder {
    private String path;
    private String FolderName;
    private int numberOfPics = 0;
    private String firstPic;
    private Boolean state = false;
    private Drawable drawable = null;
    public imageFolder(){ }
    public imageFolder(String path, String folderName) {
        this.path = path; FolderName = folderName;
    }
    public Drawable getDrawable(){ return drawable;};
    public Boolean getState(){
        return state;
    }
    public void setState(Boolean states){
        this.state = states;
    }
    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public String getFolderName() {
        return FolderName;
    }
    public void setFolderName(String folderName) {
        FolderName = folderName;
    }
    public int getNumberOfPics() {
        return numberOfPics;
    }
    public void setNumberOfPics(int numberOfPics) {
        this.numberOfPics = numberOfPics;
    }
    //this method increments the numberOfPics varaible, it is used to get
//the total count of images in the given folder
    public void addpics(){
        this.numberOfPics++;
    }
    public String getFirstPic() {
        return firstPic;
    }
    //this method gets the path to the first picture in the folder
//the picture is the used in the recyclerview adapter to represent
//the whole folder
    public void setFirstPic(String firstPic) {
        this.firstPic = firstPic;
    }
}