package com.mobile.bolt.support;

/**
 * Created by Neeraj on 4/17/2016.
 */
public class SimilarityMethod {
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
        if(method.contains("cosine")) methodValue=1;
    }

    private SimilarityMethod() {
    }
}
