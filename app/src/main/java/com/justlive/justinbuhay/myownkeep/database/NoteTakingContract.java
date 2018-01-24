package com.justlive.justinbuhay.myownkeep.database;

import android.provider.BaseColumns;

/**
 * Created by justinbuhay on 11/23/17.
 */

public class NoteTakingContract {



    private NoteTakingContract(){

    }

    public static class NoteTakingEntry implements BaseColumns{
        public static final String TABLE_NAME = "notes";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_NOTE_TITLE = "notetitle";
        public static final String COLUMN_ACTUAL_NOTE = "actualnote";
        public static final String COLUMN_UNIQUE_ID = "uniqueid";
        public static final String COLUMN_IMAGE_PATH = "imagepath";
        public static final String COLUMN_IMAGE_UUID = "imageuuid";

    }

}
