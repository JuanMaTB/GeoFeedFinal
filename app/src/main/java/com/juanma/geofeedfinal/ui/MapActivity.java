package com.juanma.geofeedfinal.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.juanma.geofeedfinal.R;

public class MapActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        int placeId = getIntent().getIntExtra(MainActivity.EXTRA_PLACE_ID, -1);

        if (savedInstanceState == null) {
            MapFragment f = MapFragment.newInstance(placeId);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.mapContainer, f)
                    .commit();
        }
    }
}
