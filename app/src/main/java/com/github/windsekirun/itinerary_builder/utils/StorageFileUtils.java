package com.github.windsekirun.itinerary_builder.utils;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class StorageFileUtils {

    public static FileOutputStream openFileOutput(Context c, String fileName) throws FileNotFoundException {
        File outputFile = c.getDir("storage", Context.MODE_PRIVATE);
        File myFile = new File(outputFile, fileName);
        return new FileOutputStream(myFile);
    }

    public static FileInputStream openFileInput(Context c, String fileName) throws FileNotFoundException {
        File outputFile = c.getDir("storage", Context.MODE_PRIVATE);
        File myFile = new File(outputFile, fileName);
        return new FileInputStream(myFile);
    }

}