package com.WebApplcation.MyDietPlan.Model;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class Image {
    private int imageID;
    private String imageName;
    private String imageType;
    private byte[] blob;

    //This is used to show the image on the website. It is used by thymeleaf to render the image.
    private String base64Image;

    
    public int getImageID() {
        return imageID;
    }
    public void setImageID(int imageID) {
        this.imageID = imageID;
    }
    public String getImageName() {
        return imageName;
    }
    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
    public String getImageType() {
        return imageType;
    }
    public void setImageType(String imageType) {
        this.imageType = imageType;
    }
    public byte[] getBlob() {
        return blob;
    }
    public void setBlob(byte[] blob) {
        this.blob = blob;
    }

    public String getBase64Image() {
        return base64Image;
    }

    public void setBase64Image(String base64Image) {
        this.base64Image = base64Image;
    }

    @Override
    public String toString() {
        return "Image{" +
                "imageID=" + imageID +
                ", imageName='" + imageName + '\'' +
                ", imageType='" + imageType + '\'' +
                ", blob=" + (blob != null) +
                '}';
    }
}
