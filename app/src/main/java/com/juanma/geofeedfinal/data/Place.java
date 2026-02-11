package com.juanma.geofeedfinal.data;

// modelo simple para la lista
// de momento es en memoria (mock). luego esto lo mapeamos a room.

public class Place {

    private final int id;
    private final String name;
    private final String type;
    private final String description;
    private final double lat;
    private final double lng;

    private boolean isFavorite;

    public Place(int id, String name, String type, String description, double lat, double lng, boolean isFavorite) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.description = description;
        this.lat = lat;
        this.lng = lng;
        this.isFavorite = isFavorite;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
}
