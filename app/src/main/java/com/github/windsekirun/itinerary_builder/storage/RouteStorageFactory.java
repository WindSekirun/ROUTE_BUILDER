package com.github.windsekirun.itinerary_builder.storage;

import android.content.Context;
import android.util.Log;

import com.github.windsekirun.itinerary_builder.model.RouteDB;
import com.github.windsekirun.itinerary_builder.model.RouteModel;
import com.github.windsekirun.itinerary_builder.utils.StorageFileUtils;

import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OptionalDataException;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.util.ArrayList;

/**
 * RouteStorageFactory
 * Created by Pyxis on 2016. 10. 6..
 */
public class RouteStorageFactory implements Serializable {

    static RouteInternal instance;

    public static RouteInternal getInstance(Context c) {
        if (instance == null)
            instance = new RouteInternal(c);

        return instance;
    }

    static class RouteInternal implements RouteStorage {
        RouteDB db;
        Context context;

        public RouteInternal(Context context) {
            this.context = context;

            try {
                FileInputStream fis = StorageFileUtils.openFileInput(context, "routedb.db");
                FSTObjectInput ois = new FSTObjectInput(fis);

                db = (RouteDB) ois.readObject();

                ois.close();
                fis.close();

            } catch (ClassNotFoundException | OptionalDataException | StreamCorruptedException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                Log.d("DamarePreprocessor", "File was not found - will initialize and write file when db change occurs");
                db = null;
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (db == null)
                db = new RouteDB();
        }

        public RouteDB getDB() {
            return db;
        }

        public String getName() {
            return db.getNickname();
        }

        public ArrayList<RouteModel> getRouteModels() {
            return db.getRouteModels();
        }

        public void writeOutChange() {
            try {
                FileOutputStream fos = StorageFileUtils.openFileOutput(context, "routedb.db");
                FSTObjectOutput oos = new FSTObjectOutput(fos);
                oos.writeObject(db);
                oos.flush();
                oos.close();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
