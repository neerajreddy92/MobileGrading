package com.mobile.bolt.support;

import com.mobile.bolt.mobilegrading.TakePicture;

/**
 * Created by Neeraj on 4/15/2016.
 */
public class PictureValues {
    private static PictureValues ourInstance = new PictureValues();

    public static PictureValues getInstance() {
        return ourInstance;
    }
    private String ASUAD;
    private String photoPath;

    public String getASUAD() {
        return ASUAD;
    }

    // TODO: 4/15/2016 Might be a security error
    public void setASUAD(String ASUAD) {
            this.ASUAD = ASUAD;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    private PictureValues() {
    }
}
