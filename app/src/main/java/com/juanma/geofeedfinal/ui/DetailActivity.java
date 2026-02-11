package com.juanma.geofeedfinal.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.juanma.geofeedfinal.R;
import com.juanma.geofeedfinal.data.Place;
import com.juanma.geofeedfinal.data.PlaceRepository;

public class DetailActivity extends AppCompatActivity {

    private int placeId;

    private TextView tvName;
    private TextView tvDesc;
    private Button btnFav;
    private Button btnMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        tvName = findViewById(R.id.tvPlaceName);
        tvDesc = findViewById(R.id.tvPlaceDesc);
        btnFav = findViewById(R.id.btnToggleFavorite);
        btnMap = findViewById(R.id.btnViewMap);

        placeId = getIntent().getIntExtra(MainActivity.EXTRA_PLACE_ID, -1);

        PlaceRepository.getInstance().ensureSeedData(this, null, this::loadPlace);

        btnFav.setOnClickListener(v -> {
            PlaceRepository.getInstance().toggleFavorite(this, placeId, nowFav ->
                    runOnUiThread(() -> refreshFavButtonText(nowFav))
            );
        });

        btnMap.setOnClickListener(v -> {
            Intent i = new Intent(DetailActivity.this, MapActivity.class);
            i.putExtra(MainActivity.EXTRA_PLACE_ID, placeId);
            startActivity(i);
        });
    }

    private void loadPlace() {
        PlaceRepository.getInstance().getById(this, placeId, place -> runOnUiThread(() -> bind(place)));
    }

    private void bind(Place place) {
        if (place == null) {
            tvName.setText(getString(R.string.detail_not_found_title));
            tvDesc.setText(getString(R.string.detail_not_found_desc));
            btnFav.setEnabled(false);
            btnMap.setEnabled(false);
            return;
        }

        tvName.setText(place.getName());
        tvDesc.setText(place.getDescription());
        refreshFavButtonText(place.isFavorite());
    }

    private void refreshFavButtonText(boolean isFav) {
        if (isFav) btnFav.setText(getString(R.string.btn_unfavorite));
        else btnFav.setText(getString(R.string.btn_favorite));
    }
}
