package com.example.android.cardgame.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.android.cardgame.R;

import mob.app.networking.MOBClient;
import mob.sdk.networking.payloads.CardRequest;

public class CardFinderFragment extends Fragment implements MOBClient.CardRequestListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MOBClient.INSTANCE.setOnCardRequestListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cardfinder, container, false);
    }

    @Override
    public void onCardRequested(CardRequest cardRequest) {

    }
}
