package com.example.justinbuhay.myownkeep.asynctasks;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.AsyncTaskLoader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static com.example.justinbuhay.myownkeep.HelperMethods.modifyOrientation;


/**
 * Created by jbuha on 1/5/2018.
 */

public class LoadingImageAyncTaskLoader extends AsyncTaskLoader<Bitmap> {

    private Uri uriForBitmap;
    private String pathforbitmap;
    private Context context;
    private String LOG_TAG;

    public LoadingImageAyncTaskLoader(Context context, Uri uriForBitmap, String pathforbitmap, String LOG_TAG) {
        super(context);
        this.context = context;
        this.uriForBitmap = uriForBitmap;
        this.pathforbitmap = pathforbitmap;
        this.LOG_TAG = LOG_TAG;
    }


    @Override
    public Bitmap loadInBackground() {
        Bitmap bitmap = null;

        try {
            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uriForBitmap);

            Bitmap orientedBitmap = null;

            orientedBitmap = modifyOrientation(LOG_TAG, bitmap, pathforbitmap.toString());

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            orientedBitmap.compress(Bitmap.CompressFormat.JPEG, 10, baos);

            bitmap = orientedBitmap;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }
}
