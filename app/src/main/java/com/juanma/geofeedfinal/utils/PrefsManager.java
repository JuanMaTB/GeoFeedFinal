package com.juanma.geofeedfinal.utils;

import android.content.Context;
import android.content.SharedPreferences;

// prefs simples para el feedback
// - show_only_favorites: filtra la lista
// - last_type_filter: ultimo tipo elegido (de momento solo guardamos, ui vendra luego)
// - accept_notifications: lo usaremos en 4.7
public class PrefsManager {

    private static final String PREFS_NAME = "geofeedfinal_prefs";

    private static final String KEY_SHOW_ONLY_FAVORITES = "show_only_favorites";
    private static final String KEY_LAST_TYPE_FILTER = "last_type_filter";
    private static final String KEY_ACCEPT_NOTIFICATIONS = "accept_notifications";

    private final SharedPreferences sp;

    public PrefsManager(Context context) {
        sp = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public boolean isShowOnlyFavorites() {
        return sp.getBoolean(KEY_SHOW_ONLY_FAVORITES, false);
    }

    public void setShowOnlyFavorites(boolean value) {
        sp.edit().putBoolean(KEY_SHOW_ONLY_FAVORITES, value).apply();
    }

    public String getLastTypeFilter() {
        return sp.getString(KEY_LAST_TYPE_FILTER, "");
    }

    public void setLastTypeFilter(String type) {
        sp.edit().putString(KEY_LAST_TYPE_FILTER, type == null ? "" : type).apply();
    }

    public boolean isAcceptNotifications() {
        return sp.getBoolean(KEY_ACCEPT_NOTIFICATIONS, true);
    }

    public void setAcceptNotifications(boolean value) {
        sp.edit().putBoolean(KEY_ACCEPT_NOTIFICATIONS, value).apply();
    }
}
