package com.paicheya.pimagepicker.core;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by cly on 17/2/22.
 */

public class OutputUri implements Parcelable {
    private Uri uri;
    private int width;
    private int height;
    private int size;
    private String path;


    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }


    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }


    public OutputUri() {
    }
    public OutputUri(Uri uri,String path,int width,int height,int size) {
        this.uri = uri;
        this.path = path;
        this.width = width;
        this.height = height;
        this.size = size;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.uri, flags);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
        dest.writeInt(this.size);
        dest.writeString(this.path);
    }

    protected OutputUri(Parcel in) {
        this.uri = in.readParcelable(Uri.class.getClassLoader());
        this.width = in.readInt();
        this.height = in.readInt();
        this.size = in.readInt();
        this.path = in.readString();
    }

    public static final Creator<OutputUri> CREATOR = new Creator<OutputUri>() {
        @Override
        public OutputUri createFromParcel(Parcel source) {
            return new OutputUri(source);
        }

        @Override
        public OutputUri[] newArray(int size) {
            return new OutputUri[size];
        }
    };
}
