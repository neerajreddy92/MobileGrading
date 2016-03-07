package com.mobile.bolt.Model;

/**
 * Created by Neeraj on 2/24/2016.
 */

public class Image {
    // TODO: 2/24/2016 create a system where all the uploaded items will be automatically deleated.
    // TODO: 2/25/2016 add a coloumn to save retreived qr code and change the dao to reflect that
    private int id;
    private String ASU_ID;
    private int graded;
    private int uploaded;
    private String QrCodeSolution;
    private String QrCodeValues; //shameless hack

    // TODO: 3/7/2016 update qr code values

    public String getQrCodeValues() {
        return QrCodeValues;
    }

    public void setQrCodeValues(String qrCodeValues) {
        QrCodeValues = qrCodeValues;
    }

    public Image(){
    }

    public String getQrCodeSolution() {
        return QrCodeSolution;
    }

    public void setQrCodeSolution(String qrCode) {
        QrCodeSolution = qrCode;
    }

    public int getGraded() {
        return graded;
    }

    public void setGraded(int graded) {
        this.graded = graded;
    }

    public int getUploaded() {
        return uploaded;
    }

    public void setUploaded(int uploaded) {
        this.uploaded = uploaded;
    }

    public void setId(int id) {
        this.id = id;
    }

    private String Location;

    public int getId() {
        return id;
    }

    public String getASU_ID() {
        return ASU_ID;
    }

    public void setASU_ID(String ASU_ID) {
        this.ASU_ID = ASU_ID;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    @Override
    public String toString(){
        return id+" "+ASU_ID+" "+Location+" "+graded+" "+uploaded+" "+QrCodeSolution+" "+QrCodeValues;
    }
}
