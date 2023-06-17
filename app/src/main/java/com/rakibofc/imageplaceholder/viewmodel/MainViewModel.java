package com.rakibofc.imageplaceholder.viewmodel;

import static com.rakibofc.imageplaceholder.ui.MainActivity.CACHE_IMAGE_KEY;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.util.LruCache;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.IOException;
import java.net.URL;

public class MainViewModel extends AndroidViewModel {

    MutableLiveData<Bitmap> liveImage;
    private LruCache<String, Bitmap> memoryCache;

    public MainViewModel(@NonNull Application application) {
        super(application);

        liveImage = new MutableLiveData<>();
        // loadData();
        initCacheImage();
    }

    private void initCacheImage() {

        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        final int cacheSize = maxMemory / 8;

        memoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {

                // The cache size will be measured in kilobytes rather than number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
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
                addBitmapToMemoryCache(CACHE_IMAGE_KEY, bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            memoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return memoryCache.get(key);
    }
}