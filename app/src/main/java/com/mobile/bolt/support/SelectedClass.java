package com.mobile.bolt.support;

/**
 * Created by Neeraj on 4/16/2016.
 * Singleton class to store the current selected class.
 * prevents the class being changed when the page is refreshed or when the state is restored.
 */
public class SelectedClass {
    private String currentClass;

    public String getCurrentClass() {
        return currentClass;
    }

    public void setCurrentClass(String currentClass) {
        this.currentClass = currentClass;
    }

    private static SelectedClass ourInstance = new SelectedClass();

    public static SelectedClass getInstance() {
        return ourInstance;
    }

    private SelectedClass() {
    }
}
