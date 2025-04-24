package com.example.android.droidcafeinput;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class FavoritesManager {
    private static final String PREFS_NAME = "favorites_prefs";
    private static final String KEY_SET = "favorites_set";

    public static void addToFavorites(Context context, String productName) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Set<String> favorites = new HashSet<>(prefs.getStringSet(KEY_SET, new HashSet<>()));
        favorites.add(productName);
        prefs.edit().putStringSet(KEY_SET, favorites).apply();
    }

    public static void removeFromFavorites(Context context, String productName) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Set<String> favorites = new HashSet<>(prefs.getStringSet(KEY_SET, new HashSet<>()));
        favorites.remove(productName);
        prefs.edit().putStringSet(KEY_SET, favorites).apply();
    }

    public static Set<String> getFavorites(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getStringSet(KEY_SET, new HashSet<>());
    }

    public static boolean isFavorite(Context context, String productName) {
        return getFavorites(context).contains(productName);
    }
}
