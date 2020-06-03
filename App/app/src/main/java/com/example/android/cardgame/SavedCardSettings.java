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

    private final Set<String> cards = new HashSet<>();
    public final String CARD_IDS = "cardIDs";

    private Context context;

    public void saveCard(String cardId) {
        SharedPreferences.Editor editor = context.getSharedPreferences(CARD_IDS, 0).edit();

        cards.add(cardId);

        editor.putStringSet(CARD_IDS, cards);
        editor.commit();

        Log.d(getClass().getSimpleName(), String.format("Card: %s saved on device", cardId));
    }

    public void saveCards(String... cardIds) {
        for (String card : cardIds) {
            this.saveCard(card);
        }
    }

    public Set<String> loadCards() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(CARD_IDS, 0);
        Set<String> cardIds = sharedPreferences.getStringSet(CARD_IDS, new HashSet<>());

        if (cardIds != null)
            cards.addAll(cardIds);

        if (cards.size() == 0)
            Toast.makeText(context, "You have no cards yet", Toast.LENGTH_SHORT).show();

        Log.d(TAG, "loadCards: " + cards);

        return cards;
    }

    public void clear() {
        Log.d(TAG, "Attempt clear");

        SharedPreferences sharedPreferences = context.getSharedPreferences(CARD_IDS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        cards.clear();
        editor.clear();
        editor.commit();
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
