package com.example.android.cardgame;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;


public enum SavedCardSettings {
    INSTANCE;

    private Set<String> cardCodes = new HashSet<>();
    private final String CARD_CODES = "cardCodes";

    // need to save the identifiers for the cards that have been claimed

    public void saveCard(Context context, String cardCode) {
        SharedPreferences.Editor editor = context.getSharedPreferences(CARD_CODES,0).edit();
        this.cardCodes.add(cardCode);
        editor.putStringSet(CARD_CODES,cardCodes);
        editor.apply();

    }

    public Set<String> loadCards(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(CARD_CODES,0);
        this.cardCodes = sharedPreferences.getStringSet(CARD_CODES,cardCodes);
        if (this.cardCodes == null || this.cardCodes.isEmpty()) {
            // there are no cards yet
        }
        return this.cardCodes;
    }
}
