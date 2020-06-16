package com.example.android.cardgame;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public enum SavedCardSettings {
    INSTANCE;

    private static final String TAG = "SavedCardSettings";

    private final List<String> cards = new ArrayList<>();
    public final String CARD_IDS = "cardIDs";

    private Context context;

    public void saveCard(String cardId) {
        if (cards.contains(cardId)) {
            return;
        }

        SharedPreferences.Editor editor = context.getSharedPreferences(CARD_IDS, 0).edit();

        cards.add(cardId);

        editor.putString(CARD_IDS, cards.toString());
        editor.apply();

        Log.d(getClass().getSimpleName(), String.format("Card: %s saved on device", cardId));
    }

    public void saveCards(String... cardIds) {
        for (String card : cardIds) {
            this.saveCard(card);
        }
    }

    public List<String> loadCards() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(CARD_IDS, 0);
        String cardIds = sharedPreferences.getString(CARD_IDS, "[]");

        if (!cardIds.equals("[]")) {
            cardIds = cardIds.substring(1, cardIds.length() - 1);
            for (String cardId : cardIds.split(",")) {
                cardId = cardId.trim();

                if (!cards.contains(cardId))
                    cards.add(cardId);
            }
        }

        Log.d(TAG, "loadCards: " + cards);

        return cards;
    }

    public void clear() {
        Log.d(TAG, "Attempt clear");

        SharedPreferences sharedPreferences = context.getSharedPreferences(CARD_IDS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        cards.clear();
        editor.clear();
        editor.apply();
    }

    public void setContext(Context context) {
        this.context = context;
    }


}
