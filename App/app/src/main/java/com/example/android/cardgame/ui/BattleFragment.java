package com.example.android.cardgame.ui;

import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.android.cardgame.R;
import com.example.android.cardgame.SavedCardSettings;

import java.util.Objects;

import mob.app.networking.MOBClient;
import mob.sdk.networking.payloads.BattleRequest;
import mob.sdk.networking.payloads.BattleRequestInvalid;
import mob.sdk.networking.payloads.BattleResult;

public class BattleFragment extends Fragment implements MOBClient.BattleRequestInvalidListener, MOBClient.BattleResultListener {
    private static final String TAG = "BattleFragment";

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

        ((ImageButton) view.findViewById(R.id.red_team)).setOnClickListener(e -> setColorRed());;
        ((ImageButton) view.findViewById(R.id.blue_team)).setOnClickListener(e -> setColorBlue());;
        ((Button)view.findViewById(R.id.battle_start_button)).setOnClickListener(e -> sendBattleRequest());
        this.mTableIdEditText = (EditText) view.findViewById(R.id.table_input);

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

        if (tableId.length() == 0) {
            Toast.makeText(this.getContext(), R.string.no_id_toast, Toast.LENGTH_SHORT).show();
            return;
        }

        if (mTeamColor == null) {
            Toast.makeText(this.getContext(),R.string.no_color_toast,Toast.LENGTH_SHORT).show();
            return;
        }

        BattleRequest battleRequest = new BattleRequest(tableId, mTeamColor);

        MOBClient.INSTANCE.start();
        MOBClient.INSTANCE.sendBattleRequest(battleRequest, () -> {
            // battle request send!
            Toast.makeText(this.getContext(),R.string.battle_request_sent_toast,Toast.LENGTH_LONG).show();
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