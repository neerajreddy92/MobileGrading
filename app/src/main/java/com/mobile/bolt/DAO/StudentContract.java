package com.mobile.bolt.DAO;

import android.provider.BaseColumns;

/**
 * Created by Neeraj on 2/22/2016.
 */
public final class StudentContract {
    public StudentContract(){ }
    public static abstract class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "Student";
        public static final String COLUMN_NAME_ENTRY_ID = "ASUAD";
        public static final String COLUMN_NAME_FIRST_NAME = "FirstName";
        public static final String COLUMN_NAME_LAST_NAME = "LastName";
    }
}
