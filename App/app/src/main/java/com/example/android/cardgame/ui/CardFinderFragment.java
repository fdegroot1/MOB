package com.example.android.cardgame.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.android.cardgame.R;

import mob.app.networking.MOBClient;
import mob.sdk.cards.Card;
import mob.sdk.networking.payloads.CardRequest;
import mob.sdk.networking.payloads.CardRequestInvalid;
import mob.sdk.networking.payloads.CardResult;

public class CardFinderFragment extends Fragment implements MOBClient.CardRequestListener, MOBClient.CardResultListener, MOBClient.CardRequestInvalidListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MOBClient.INSTANCE.setOnCardRequest(this);
        MOBClient.INSTANCE.setOnCardRequestInvalid(this);
        MOBClient.INSTANCE.setOnCardResult(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cardfinder, container, false);
    }

    @Override
    public void onCardRequested(CardRequest cardRequest) {
        // a card was requested for claiming

        String enteredCode = cardRequest.getCode();
        //TODO update ui

        // Toast.makeText(this.getContext(),String.format("Validating code %s...",enteredCode),Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onCardRequestInvalid(CardRequestInvalid cardRequestInvalid) {
        // a requested cards code was invalid
        String wrongCode = cardRequestInvalid.getCode();
        // TODO update ui

    }

    @Override
    public void onCardResult(CardResult cardResult) {
        // a card was returned from the server
        Card claimedCard = cardResult.getCard();
        //TODO update ui


    }

    public void sendCardRequest() {
        // TODO connect ui elements to this
        // TODO get card code from ui and replace "test" with
        MOBClient.INSTANCE.sendCardRequest(new CardRequest("test"),
                () -> {
                    // Card request sent successfully!
                    //TODO update ui
                },
                () -> {
                    // Card request failed!
                    //TODO update ui
                });
    }
}
