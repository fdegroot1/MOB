package com.example.android.cardgame.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.example.android.cardgame.R;

import mob.app.networking.MOBClient;
import mob.sdk.networking.payloads.BattleRequest;

public class BattleFragment extends Fragment {
    private EditText mTableIdEditText;
    private BattleRequest.Color mTeamColor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MOBClient.INSTANCE.setOnBattleRequestInvalid(battleRequestInvalid -> {
            switch (battleRequestInvalid.getField()) {
                case TABLE_ID:
                    // table id is wrong
                    // @todo update UI
                    break;
                case TABLE_COLOR:
                    // team color is already taken
                    // @todo update UI
                    break;
            }
        });

        MOBClient.INSTANCE.setOnBattleResult(battleResult -> {
            // battle has finished

            // @todo update UI

            if (battleResult.hasWon()) {
                // @todo update UI
            } else if (battleResult.hasLost()) {
                // @todo update UI
            } else {
                // @todo update UI
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_battle, container, false);

        // @todo set mTableIdEditText

        return view;
    }

    /**
     * Set team color blue.
     */
    public void setColorBlue() {
        this.mTeamColor = BattleRequest.Color.BLUE;
    }

    /**
     * Set team color red
     */
    public void setColorRed() {
        this.mTeamColor = BattleRequest.Color.RED;
    }

    /**
     * Send and start battle request
     */
    public void sendBattleRequest() {
        String tableId = mTableIdEditText.getText().toString();

        if (tableId.length() == 0 || mTeamColor == null)
            return;

        BattleRequest battleRequest = new BattleRequest(tableId, mTeamColor);

        MOBClient.INSTANCE.sendBattleRequest(battleRequest, () -> {
            // battle request send!
            // @todo update UI
        });
    }
}