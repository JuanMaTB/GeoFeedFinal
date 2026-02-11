package com.juanma.geofeedfinal.data;

import android.content.Context;

import com.juanma.geofeedfinal.utils.JsonLoader;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// repo con room
// 4.6:
// - simular carga en background (delay)
// - leer json desde assets
// - si bd esta vacia, insertar y luego notificar
public class PlaceRepository {

    public interface Callback<T> {
        void onResult(T data);
    }

    private static PlaceRepository instance;

    private final ExecutorService io = Executors.newSingleThreadExecutor();

    private PlaceRepository() {
    }

    public static synchronized PlaceRepository getInstance() {
        if (instance == null) instance = new PlaceRepository();
        return instance;
    }

    // onLoading(true) al empezar, onLoading(false) al terminar
    public void ensureSeedData(Context context, Callback<Boolean> onLoading, Runnable onDone) {
        io.execute(() -> {
            if (onLoading != null) onLoading.onResult(true);

            try {
                // delay para que se vea el progreso (simula descarga)
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            }

            AppDatabase db = AppDatabase.getInstance(context);
            PlaceDao dao = db.placeDao();

            if (dao.count() == 0) {
                List<PlaceEntity> items = JsonLoader.loadPlacesFromAssets(context, "places.json");
                if (!items.isEmpty()) {
                    dao.insertAll(items);
                }
            }

            if (onLoading != null) onLoading.onResult(false);
            if (onDone != null) onDone.run();
        });
    }

    public void getAll(Context context, Callback<List<Place>> cb) {
        io.execute(() -> {
            List<PlaceEntity> entities = AppDatabase.getInstance(context).placeDao().getAll();
            if (cb != null) cb.onResult(mapList(entities));
        });
    }

    public void getFavorites(Context context, Callback<List<Place>> cb) {
        io.execute(() -> {
            List<PlaceEntity> entities = AppDatabase.getInstance(context).placeDao().getFavorites();
            if (cb != null) cb.onResult(mapList(entities));
        });
    }

    public void getById(Context context, int id, Callback<Place> cb) {
        io.execute(() -> {
            PlaceEntity e = AppDatabase.getInstance(context).placeDao().getById(id);
            if (cb != null) cb.onResult(mapOne(e));
        });
    }

    public void toggleFavorite(Context context, int id, Callback<Boolean> cb) {
        io.execute(() -> {
            PlaceDao dao = AppDatabase.getInstance(context).placeDao();
            PlaceEntity e = dao.getById(id);
            if (e == null) {
                if (cb != null) cb.onResult(false);
                return;
            }
            boolean nowFav = !e.isFavorite;
            dao.setFavorite(id, nowFav);
            if (cb != null) cb.onResult(nowFav);
        });
    }

    private java.util.List<Place> mapList(java.util.List<PlaceEntity> list) {
        java.util.List<Place> out = new java.util.ArrayList<>();
        if (list == null) return out;
        for (PlaceEntity e : list) out.add(mapOne(e));
        return out;
    }

    private Place mapOne(PlaceEntity e) {
        if (e == null) return null;
        return new Place(
                e.id,
                e.name,
                e.type,
                e.description,
                e.lat,
                e.lng,
                e.isFavorite
        );
    }
}
