package com.example.android.cardgame.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.android.cardgame.R;
import com.example.android.cardgame.SavedCardSettings;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;

import mob.sdk.cards.Card;
import mob.sdk.cards.CardRepository;

public class CatalogueFragment extends Fragment {
    private Set<String> cardCodes;
    private ArrayList<Card> cards;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.cards = new ArrayList<>();
        this.cardCodes = SavedCardSettings.INSTANCE.loadCards(Objects.requireNonNull(this.getContext()));

        for (String code : this.cardCodes) {
            Card card = CardRepository.INSTANCE.getCard(code);
            this.cards.add(card);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_catalogus, container, false);
    }
}