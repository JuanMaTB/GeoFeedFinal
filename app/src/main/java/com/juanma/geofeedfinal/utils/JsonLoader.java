package com.juanma.geofeedfinal.utils;

import android.content.Context;

import com.juanma.geofeedfinal.data.PlaceEntity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

// carga simple de json desde assets
// no usamos librerias externas para que sea temario puro
public class JsonLoader {

    public static List<PlaceEntity> loadPlacesFromAssets(Context context, String fileName) {
        try {
            String json = readAssetFile(context, fileName);
            JSONArray arr = new JSONArray(json);

            List<PlaceEntity> out = new ArrayList<>();

            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);

                int id = o.getInt("id");
                String name = o.getString("name");
                String type = o.getString("type");
                String description = o.getString("description");
                double lat = o.getDouble("lat");
                double lng = o.getDouble("lng");
                boolean isFavorite = o.optBoolean("isFavorite", false);

                out.add(new PlaceEntity(id, name, type, description, lat, lng, isFavorite));
            }

            return out;

        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private static String readAssetFile(Context context, String fileName) throws Exception {
        InputStream is = context.getAssets().open(fileName);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        StringBuilder sb = new StringBuilder();
        String line;

        while ((line = br.readLine()) != null) {
            sb.append(line);
        }

        br.close();
        return sb.toString();
    }
}
