package com.mobile.bolt.support;

import android.util.Log;

/**
 * Created by Neeraj on 4/17/2016.
 */
public class SimilarityMethod {
    private static String TAG = "MobileGrading";
    private static SimilarityMethod ourInstance = new SimilarityMethod();
    private String method;
    public static SimilarityMethod getInstance() {
        return ourInstance;
    }
    private Integer methodValue=1;

    public Integer getMethodValue() {
        return methodValue;
    }

    public String getMethod() {
        return method;

    }

    public void setMethod(String method) {
        this.method = method;
        Log.d(TAG, "Similarity Method: setMethod: similarity set to "+method);
        if(method.contains("cosine")) methodValue=1;
        if(method.contains("jaccard")) methodValue=2;
    }

    private SimilarityMethod() {
    }
}
