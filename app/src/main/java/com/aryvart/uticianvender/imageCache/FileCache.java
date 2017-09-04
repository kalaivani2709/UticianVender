package com.aryvart.uticianvender.imageCache;

import android.content.Context;

import java.io.File;

/**
 * Created by ${Rajaji} on 29-04-2016.
 */
public class FileCache {

    private File cacheDir;

    public FileCache(Context context) {

        try {
            //Find the dir to save cached images

            if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))

                cacheDir = new File(android.os.Environment.getExternalStorageDirectory(), "TTImage_cache");

            else

                cacheDir = context.getCacheDir();

            if (!cacheDir.exists())

                cacheDir.mkdirs();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public File getFile(String url) {
        File f = null;

        try {
            //I identify images by hashcode. Not a perfect solution, good for the demo.

            String filename = String.valueOf(url.hashCode());

            //Another possible solution (thanks to grantland)

            //String filename = URLEncoder.encode(url);

            f = new File(cacheDir, filename);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return f;

    }

    public void clear() {

        File[] files = cacheDir.listFiles();

        if (files == null)

            return;

        for (File f : files)

            f.delete();

    }

}

