package com.justlive.justinbuhay.myownkeep.asynctasks;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.AsyncTaskLoader;

import static com.justlive.justinbuhay.myownkeep.HelperMethods.getImagePathFromInputStreamUri;

/**
 * Created by jbuha on 1/5/2018.
 */

public class GettingImagePathAsyncTaskLoader extends AsyncTaskLoader {

    private Uri uriFromData;
    private Context context;


    public GettingImagePathAsyncTaskLoader(Context context, Uri uriFromData) {
        super(context);
        this.uriFromData = uriFromData;
        this.context = context;
    }

    @Override
    public Object loadInBackground() {
        String pathforbitmap;
        pathforbitmap = getImagePathFromInputStreamUri(context, uriFromData);

        return pathforbitmap;
    }
}
