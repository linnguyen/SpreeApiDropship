package com.example.ryne.myapplication.Java;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**
 * Created by ryne on 21/12/2018.
 */

public class Utils {
    public static boolean indexExists(final String [] list, final int index) {
        return index >= 0 && index < list.length;
    }

    public static void glideUrl(Context context, String url, ImageView imv){
        Glide.with(context)
                .load(url)
                .into(imv);
    }

    public static boolean productUrlExist(String url){
        return url!= null && !url.equals("");
    }
}
