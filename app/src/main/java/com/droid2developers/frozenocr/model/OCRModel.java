package com.droid2developers.frozenocr.model;

public class OCRModel {

    private long timeStamp;
    private String imageUri;
    private String extaText;


    public OCRModel() {
    }

    public OCRModel(long timeStamp, String imageUri, String extaText) {
        this.timeStamp = timeStamp;
        this.imageUri = imageUri;
        this.extaText = extaText;
    }


    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getExtaText() {
        return extaText;
    }

    public void setExtaText(String extaText) {
        this.extaText = extaText;
    }


    @Override
    public String toString() {
        return "OCRModel{" +
                "timeStamp=" + timeStamp +
                ", imageUri='" + imageUri + '\'' +
                ", extaText='" + extaText + '\'' +
                '}';
    }
}
