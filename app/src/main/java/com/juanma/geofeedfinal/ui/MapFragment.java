package com.juanma.geofeedfinal.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.juanma.geofeedfinal.R;
import com.juanma.geofeedfinal.data.Place;
import com.juanma.geofeedfinal.data.PlaceRepository;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private static final String ARG_PLACE_ID = "arg_place_id";

    private GoogleMap googleMap;
    private int placeId = -1;

    private ActivityResultLauncher<String> requestLocationPermission;

    public static MapFragment newInstance(int placeId) {
        MapFragment f = new MapFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_PLACE_ID, placeId);
        f.setArguments(b);
        return f;
    }

    public MapFragment() {
        super(R.layout.fragment_map);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            placeId = getArguments().getInt(ARG_PLACE_ID, -1);
        }

        requestLocationPermission = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                granted -> {
                    if (granted) enableMyLocationIfPossible();
                }
        );
    }

    @Override
    public void onViewCreated(@NonNull android.view.View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mapFrag =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapView);

        if (mapFrag != null) {
            mapFrag.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;

        if (hasFineLocationPermission()) {
            enableMyLocationIfPossible();
        } else {
            requestLocationPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        PlaceRepository.getInstance().ensureSeedData(requireContext(), null, () -> {
            PlaceRepository.getInstance().getById(requireContext(), placeId, place ->
                    requireActivity().runOnUiThread(() -> showPlaceOnMap(place))
            );
        });
    }

    private void showPlaceOnMap(Place place) {
        if (googleMap == null) return;

        LatLng pos;
        String title;

        if (place == null) {
            pos = new LatLng(40.416775, -3.703790);
            title = "place not found";
        } else {
            pos = new LatLng(place.getLat(), place.getLng());
            title = place.getName();
        }

        googleMap.addMarker(new MarkerOptions().position(pos).title(title));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 15f));
    }

    private boolean hasFineLocationPermission() {
        return ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED;
    }

    private void enableMyLocationIfPossible() {
        if (googleMap == null) return;
        if (!hasFineLocationPermission()) return;

        try {
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        } catch (SecurityException ignored) {
        }
    }
}
