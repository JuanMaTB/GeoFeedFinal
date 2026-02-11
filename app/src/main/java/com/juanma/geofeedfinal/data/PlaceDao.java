package com.juanma.geofeedfinal.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PlaceDao {

    @Query("select * from places order by name asc")
    List<PlaceEntity> getAll();

    @Query("select * from places where isFavorite = 1 order by name asc")
    List<PlaceEntity> getFavorites();

    @Query("select * from places where id = :id limit 1")
    PlaceEntity getById(int id);

    @Query("select count(*) from places")
    int count();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<PlaceEntity> items);

    @Query("update places set isFavorite = :fav where id = :id")
    void setFavorite(int id, boolean fav);
}
