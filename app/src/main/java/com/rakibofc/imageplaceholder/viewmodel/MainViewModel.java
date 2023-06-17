package com.rakibofc.imageplaceholder.viewmodel;

import static com.rakibofc.imageplaceholder.ui.MainActivity.CACHE_IMAGE_KEY;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.util.LruCache;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

public class MainViewModel extends AndroidViewModel {

    public MutableLiveData<Bitmap> liveImage;
    public SharedPreferences sharedPreferences;
    private LruCache<String, Bitmap> memoryCache;

    public MainViewModel(@NonNull Application application) {
        super(application);

        liveImage = new MutableLiveData<>();
        sharedPreferences = getApplication().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        loadData();
    }

    public LiveData<Bitmap> getImage() {
        return liveImage;
    }

    public void loadData() {

        String imageUrl = "https://picsum.photos/200";

        new Thread(() -> {

            try {

                // Initialize the random image placeholder
                URL url = new URL(imageUrl);

                // Get the bitmap image
                Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                // Set the bitmap in live image data
                liveImage.postValue(bitmap);

                // Add the image in cache
                addBitmapToMemoryCache(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void addBitmapToMemoryCache(Bitmap bitmap) {


        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);

        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);


        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(CACHE_IMAGE_KEY, encodedImage).apply();
    }

    public Bitmap getBitmapFromMemCache() {

        String encodedImage = sharedPreferences.getString(CACHE_IMAGE_KEY, "");
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);

        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }
}