package com.example.android.cardgame.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.example.android.cardgame.R;
import com.example.android.cardgame.SavedCardSettings;

import mob.app.networking.MOBClient;
import mob.sdk.networking.payloads.BattleRequest;
import mob.sdk.networking.payloads.BattleRequestInvalid;
import mob.sdk.networking.payloads.BattleResult;

public class BattleFragment extends Fragment implements MOBClient.BattleRequestInvalidListener, MOBClient.BattleResultListener {
    private EditText mTableIdEditText;
    private BattleRequest.Color mTeamColor;
    // TODO set card that will be claimed
    private String mCardId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MOBClient.INSTANCE.setOnBattleRequestInvalid(this);
        MOBClient.INSTANCE.setOnBattleResult(this);
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

        MOBClient.INSTANCE.start();
        MOBClient.INSTANCE.sendBattleRequest(battleRequest, () -> {
            // battle request send!
            // @todo update UI
        });
    }

    /**
     * Claim a card that is won
     */
    public void claimCard() {
        if (mCardId == null)
            return;

        // @todo start card claimed activity
    }

    @Override
    public void onBattleRequestInvalid(BattleRequestInvalid battleRequestInvalid) {
        switch (battleRequestInvalid.getReason()) {
            case ALREADY_PLAYING:
                // table is already playing
                // @todo update UI
                break;
            case DEVICE_ID_WRONG:
                // device id is wrong
                // @todo update UI
                break;
            case TEAM_ALREADY_TAKEN:
                // chosen team is already taken
                // @TODO: update ui
        }
    }

    @Override
    public void onBattleResult(BattleResult battleResult) {
        // battle has finished

        // @todo update UI

        if (battleResult.hasWon()) {
            // @todo update UI
        } else if (battleResult.hasLost()) {
            // @todo update UI
        } else {
            // @todo update UI
        }

        MOBClient.INSTANCE.stop();
    }
}