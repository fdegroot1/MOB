package com.example.android.cardgame;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import mob.sdk.cards.Card;
import mob.sdk.cards.CardRepository;

public class CardDetailActivity extends AppCompatActivity {

    public static final String EXTRA_CARD_ID = "CardDetailActivity.extra.id";

    private Button mFrontButton;
    private Button mBackButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_detail);

        this.mFrontButton = findViewById(R.id.cardFrontButton);
        this.mBackButton = findViewById(R.id.cardBackButton);

        Intent intent = getIntent();
        if (intent != null) {
            String cardId = intent.getStringExtra(EXTRA_CARD_ID);
            setCard(cardId);
        }

        mFrontButton.setOnClickListener(v -> {
            displayFront();
        });

        mBackButton.setOnClickListener(v -> {
            displayBack();
        });
    }

    private void setCard(String cardId) {
        Log.d(getClass().getSimpleName(), cardId);

        if (cardId == null || cardId.length() == 0)
            return;

        Card card = CardRepository.INSTANCE.getCard(cardId);

        if (card == null)
            return;

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(card.getName());

        TextView title = findViewById(R.id.cardTitleDetail);
        TextView description = findViewById(R.id.cardDescription);
        ImageView image = findViewById(R.id.cardImage);

        title.setText(card.getName());
    }

    private void displayFront() {
        findViewById(R.id.cardImage).setVisibility(View.VISIBLE);
        findViewById(R.id.cardDescription).setVisibility(View.INVISIBLE);
    }

    private void displayBack() {
        findViewById(R.id.cardDescription).setVisibility(View.VISIBLE);
        findViewById(R.id.cardImage).setVisibility(View.INVISIBLE);
    }
}