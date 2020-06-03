package com.example.android.cardgame;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Set;


public enum SavedCardSettings {
    INSTANCE;
    private static final String TAG = "SavedCardSettings";

    private Set<String> cardIds = new HashSet<>();
    public final String CARD_IDS = "cardIDs";

    // need to save the identifiers for the cards that have been claimed

    public void saveCard(Context context, String cardId) {
        SharedPreferences.Editor editor = context.getSharedPreferences(CARD_IDS,0).edit();
        this.cardIds.add(cardId);
        editor.putStringSet(CARD_IDS, cardIds);
        editor.apply();

    }

    public void saveCards(Context context, String... cardIds) {
        for (String card : cardIds) {
            this.saveCard(context,card);
        }
    }

    public Set<String> loadCards(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(CARD_IDS,0);
        this.cardIds = sharedPreferences.getStringSet(CARD_IDS, cardIds);
        if (this.cardIds == null || this.cardIds.isEmpty()) {
            // there are no cards yet
            Toast.makeText(context,"You have no cards yet",Toast.LENGTH_SHORT).show();
        }
        Log.d(TAG, "loadCards: " + this.cardIds);
        return this.cardIds;
    }

    public void clear(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(CARD_IDS,0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
