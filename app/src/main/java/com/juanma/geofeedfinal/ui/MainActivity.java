package com.juanma.geofeedfinal.ui;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.juanma.geofeedfinal.R;
import com.juanma.geofeedfinal.data.Place;
import com.juanma.geofeedfinal.data.PlaceRepository;
import com.juanma.geofeedfinal.utils.NotificationHelper;
import com.juanma.geofeedfinal.utils.PrefsManager;
import com.juanma.geofeedfinal.utils.ReminderReceiver;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_PLACE_ID = "extra_place_id";

    private PlaceAdapter adapter;
    private PrefsManager prefs;
    private SwitchMaterial switchOnlyFavorites;
    private ProgressBar progressLoading;
    private MaterialButton btnScheduleReminder;
    private TextView tvLastUpdate;

    private ActivityResultLauncher<String> notifPermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = new PrefsManager(this);

        switchOnlyFavorites = findViewById(R.id.switchOnlyFavorites);
        progressLoading = findViewById(R.id.progressLoading);
        btnScheduleReminder = findViewById(R.id.btnScheduleReminder);
        tvLastUpdate = findViewById(R.id.tvLastUpdate);

        // 4.8: formato regional del dispositivo
        setLastUpdateNow();

        switchOnlyFavorites.setChecked(prefs.isShowOnlyFavorites());
        switchOnlyFavorites.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.setShowOnlyFavorites(isChecked);
            loadPlaces();
        });

        notifPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                granted -> {
                    if (granted) {
                        scheduleTestReminderIn30s();
                        Toast.makeText(this, getString(R.string.toast_reminder_scheduled), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, getString(R.string.toast_no_notif_permission), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        btnScheduleReminder.setOnClickListener(v -> {
            NotificationHelper.ensureChannel(this);

            if (needsNotifPermission() && !hasNotifPermission()) {
                notifPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
                return;
            }

            scheduleTestReminderIn30s();
            Toast.makeText(this, getString(R.string.toast_reminder_scheduled), Toast.LENGTH_SHORT).show();
        });

        RecyclerView rv = findViewById(R.id.rvPlaces);

        adapter = new PlaceAdapter(place -> {
            Intent i = new Intent(MainActivity.this, DetailActivity.class);
            i.putExtra(EXTRA_PLACE_ID, place.getId());
            startActivity(i);
        });

        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        PlaceRepository.getInstance().ensureSeedData(
                this,
                isLoading -> runOnUiThread(() -> progressLoading.setVisibility(isLoading ? View.VISIBLE : View.GONE)),
                () -> {
                    setLastUpdateNow();
                    loadPlaces();
                }
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPlaces();
    }

    private void loadPlaces() {
        boolean onlyFav = prefs.isShowOnlyFavorites();

        if (onlyFav) {
            PlaceRepository.getInstance().getFavorites(this, this::showPlacesOnUi);
        } else {
            PlaceRepository.getInstance().getAll(this, this::showPlacesOnUi);
        }
    }

    private void showPlacesOnUi(List<Place> list) {
        runOnUiThread(() -> adapter.setItems(list));
    }

    // 4.7
    private void scheduleTestReminderIn30s() {
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (am == null) return;

        Intent intent = new Intent(this, ReminderReceiver.class);

        PendingIntent pi = PendingIntent.getBroadcast(
                this,
                2001,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        long triggerAt = System.currentTimeMillis() + 30_000L;
        am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi);
    }

    private boolean needsNotifPermission() {
        return Build.VERSION.SDK_INT >= 33;
    }

    private boolean hasNotifPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED;
    }

    // 4.8: usa formato regional del dispositivo
    private void setLastUpdateNow() {
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
        String formatted = df.format(new Date());
        tvLastUpdate.setText(getString(R.string.last_update, formatted));
    }
}
