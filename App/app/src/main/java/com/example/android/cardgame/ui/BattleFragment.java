package com.example.android.cardgame.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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

import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

import mob.app.networking.MOBClient;
import mob.sdk.cards.Card;
import mob.sdk.cards.CardRepository;
import mob.sdk.networking.payloads.BattleRequest;
import mob.sdk.networking.payloads.BattleRequestInvalid;
import mob.sdk.networking.payloads.BattleResult;

public class BattleFragment extends Fragment implements MOBClient.BattleRequestInvalidListener, MOBClient.BattleResultListener {
    private static final String TAG = "BattleFragment";

    private EditText mTableIdEditText;
    private BattleRequest.Color mTeamColor;
    // TODO set card that will be claimed
    private Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MOBClient.INSTANCE.setOnBattleRequestInvalid(this);
        MOBClient.INSTANCE.setOnBattleResult(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_battle, container, false);
        ImageButton red = (ImageButton) view.findViewById(R.id.red_team);
        ImageButton blue = (ImageButton) view.findViewById(R.id.blue_team);


        red.setOnClickListener(e -> {
            red.setImageDrawable(getResources().getDrawable(R.drawable.red_placeholder_selected, null));
            blue.setImageDrawable(getResources().getDrawable(R.drawable.blue_placeholder, null));
            setColorRed();
        });
        blue.setOnClickListener(e -> {
            blue.setImageDrawable(getResources().getDrawable(R.drawable.blue_placeholder_selected, null));
            red.setImageDrawable(getResources().getDrawable(R.drawable.red_placeholder, null));
            setColorBlue();
        });
        ((Button) view.findViewById(R.id.battle_start_button)).setOnClickListener(e -> sendBattleRequest());
        this.mTableIdEditText = (EditText) view.findViewById(R.id.table_input);
        this.mContext = view.getContext();

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
            Toast.makeText(this.getContext(), R.string.no_color_toast, Toast.LENGTH_SHORT).show();
            return;
        }

        BattleRequest battleRequest = new BattleRequest(tableId, mTeamColor);

        MOBClient.INSTANCE.start();
        MOBClient.INSTANCE.sendBattleRequest(battleRequest, () -> {
            // battle request send!
            showToast(R.string.battle_request_sent_toast, Toast.LENGTH_SHORT);
        });
    }

    @Override
    public void onBattleRequestInvalid(BattleRequestInvalid battleRequestInvalid) {
        switch (battleRequestInvalid.getReason()) {
            case ALREADY_PLAYING:
                // table is already playing
//                showToast(R.string.battle_already_playing_toast,Toast.LENGTH_SHORT);
                showDialog(R.string.battle_already_playing_toast);
                break;
            case DEVICE_ID_WRONG:
                // device id is wrong
//                showToast(R.string.battle_device_id_wrong_toast,Toast.LENGTH_SHORT);
                showDialog(R.string.battle_device_id_wrong_toast);


                break;
            case TEAM_ALREADY_TAKEN:
                // chosen team is already taken
//                showToast("This team is already taken!",Toast.LENGTH_SHORT);
                showDialog(R.string.battle_team_taken);
        }
    }

    @Override
    public void onBattleResult(BattleResult battleResult) {
        //TODO add card to win

        if (battleResult.hasWon()) {
            showDialog(getResources().getString(R.string.battle_result_won) + " " + getResources().getString(R.string.won) + ": " + battleResult.getAmountWon()
                    + ", " + getResources().getString(R.string.lost) + ": " + battleResult.getAmountLost());
            addNewCard();
        } else if (battleResult.hasLost()) {
            showDialog(getResources().getString(R.string.battle_result_lost) + " " + getResources().getString(R.string.won) + ": " + battleResult.getAmountWon()
                    + ", " + getResources().getString(R.string.lost) + ": " + battleResult.getAmountLost());
        } else {
            showDialog(R.string.draw);
        }

        MOBClient.INSTANCE.stop();
    }

    private void addNewCard() {

        //if the user does not already have all the cards
        if (SavedCardSettings.INSTANCE.loadCards().size() < CardRepository.INSTANCE.getCardIds().size()) {

            // get the available cards (the cards that the user does not have yet)
            List<String> availableCards = SavedCardSettings.INSTANCE.loadCards().size() > 0 ? CardRepository.INSTANCE.getCardIds().stream()
                    .filter(cardId -> !SavedCardSettings.INSTANCE.loadCards().contains(cardId))
                    .collect(Collectors.toList()) : CardRepository.INSTANCE.getCardIds();

            // give the card to the user
            String randomCardId = availableCards.get(new Random().nextInt(availableCards.size() - 1));
            SavedCardSettings.INSTANCE.saveCard(randomCardId);
            showDialog(getResources().getString(R.string.new_card_received) + " " + CardRepository.INSTANCE.getCard(randomCardId).getName() + "!");
        } else {
            showDialog(getResources().getString(R.string.all_cards));
        }
    }

    private void showToast(String message, int duration) {
        getActivity().runOnUiThread(() -> {
            Toast.makeText(this.getContext(), message, duration).show();
        });

    }

    private void showToast(int message, int duration) {
        getActivity().runOnUiThread(() -> {
            Toast.makeText(this.getContext(), message, duration).show();
        });
    }

    private void showDialog(String message) {
        getActivity().runOnUiThread(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
            builder.setTitle(message);
            builder.setCancelable(true);
            builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        });

    }

    private void showDialog(int message) {
        getActivity().runOnUiThread(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
            builder.setTitle(message);
            builder.setCancelable(true);
            builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        });
    }

}