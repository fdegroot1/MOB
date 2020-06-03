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
    private Context context;

    // need to save the identifiers for the cards that have been claimed

    public void saveCard(String cardId) {
        SharedPreferences.Editor editor = context.getSharedPreferences(CARD_IDS,0).edit();
        this.cardIds.add(cardId);
        editor.putStringSet(CARD_IDS, cardIds);
        editor.apply();

        Log.d(getClass().getSimpleName(), String.format("Card: %s saved on device", cardId));
    }

    public void saveCards( String... cardIds) {
        for (String card : cardIds) {
            this.saveCard(card);
        }
    }

    public Set<String> loadCards() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(CARD_IDS,0);
        this.cardIds = sharedPreferences.getStringSet(CARD_IDS, cardIds);
        if (this.cardIds == null || this.cardIds.isEmpty()) {
            // there are no cards yet
            Toast.makeText(context,"You have no cards yet",Toast.LENGTH_SHORT).show();
        }
        Log.d(TAG, "loadCards: " + this.cardIds);
        return this.cardIds;
    }

    public void clear() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(CARD_IDS,0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
