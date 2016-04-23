package com.mobile.bolt.support;

/**
 * Created by Neeraj on 4/23/2016.
 */
public class SearchFilterVal {
    private static SearchFilterVal ourInstance = new SearchFilterVal();
    private String searchVal = "";
    public static SearchFilterVal getInstance() {
        return ourInstance;
    }

    public String getSearchVal() {
        return searchVal;
    }

    public void setSearchVal(String searchVal) {
        this.searchVal = searchVal;
    }

    private SearchFilterVal() {
    }
}
