package com.juanma.geofeedfinal.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

// tabla room
// el id viene del seed/mock, luego podra venir de json
@Entity(tableName = "places")
public class PlaceEntity {

    @PrimaryKey
    public int id;

    public String name;
    public String type;
    public String description;

    public double lat;
    public double lng;

    public boolean isFavorite;

    public PlaceEntity(int id, String name, String type, String description, double lat, double lng, boolean isFavorite) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.description = description;
        this.lat = lat;
        this.lng = lng;
        this.isFavorite = isFavorite;
    }
}
