package com.mobile.bolt.support;

/**
 * Created by Neeraj on 4/23/2016.
 * Singleton class to store the current filter value.
 * Prevents the search form *refreshing* when the page is refreshed or the state is restored.
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
