package com.rakibofc.imageplaceholder.viewmodel;

import static com.rakibofc.imageplaceholder.ui.MainActivity.CACHE_IMAGE_KEY;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

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

    public MainViewModel(@NonNull Application application) {
        super(application);

        // Initialize liveImage and sharedPreferences
        liveImage = new MutableLiveData<>();
        sharedPreferences = getApplication().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        // loadData();
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

        // Convert the Bitmap to a byte array and compress it as PNG format
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);

        // Get the byte array from the ByteArrayOutputStream
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        // Encode the byte array as a Base64 string
        String encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);

        // Get the SharedPreferences editor to make changes
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Store the Base64-encoded image string in SharedPreferences
        editor.putString(CACHE_IMAGE_KEY, encodedImage).apply();
    }

    public Bitmap getBitmapFromMemCache() {

        // Retrieve the Base64-encoded image string from SharedPreferences
        String encodedImage = sharedPreferences.getString(CACHE_IMAGE_KEY, "");

        // Decode the Base64 string back into a byte array
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);

        // Convert the byte array into a Bitmap object and return bitmap image
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }
}