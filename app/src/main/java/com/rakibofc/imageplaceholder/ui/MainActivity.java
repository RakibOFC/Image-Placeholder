package com.rakibofc.imageplaceholder.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.rakibofc.imageplaceholder.databinding.ActivityMainBinding;
import com.rakibofc.imageplaceholder.receiver.ConnectionReceiver;
import com.rakibofc.imageplaceholder.viewmodel.MainViewModel;

public class MainActivity extends AppCompatActivity {

    public final static String CONNECTIVITY_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
    public final static String CACHE_IMAGE_KEY = "cacheImage";
    IntentFilter intentFilter;
    ConnectionReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflates the layout XML file for the main activity and creates an instance of the binding class
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());

        // Initializes the ViewModel for the main activity using the ViewModelProvider
        MainViewModel viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        // Set content view
        setContentView(binding.getRoot());

        // Initialize connection status receiver
        intentFilter = new IntentFilter();
        receiver = new ConnectionReceiver();

        // Add action in intent filter
        intentFilter.addAction(CONNECTIVITY_ACTION);

        // Get and set image from ViewModel
        viewModel.getImage().observe(this, binding.imageView::setImageBitmap);

        viewModel.getImage().observe(this, binding.imageView::setImageBitmap);

        binding.imageView.setImageBitmap(viewModel.getBitmapFromMemCache());

        binding.btnFetchImage.setOnClickListener(v -> {

            // Click listener to load data from ViewModel
            viewModel.loadData();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("Info", "App killed!");
    }
}