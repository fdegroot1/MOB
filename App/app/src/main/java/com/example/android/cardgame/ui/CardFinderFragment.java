package com.example.android.cardgame.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.android.cardgame.MainActivity;
import com.example.android.cardgame.R;
import com.example.android.cardgame.SavedCardSettings;

import mob.app.networking.MOBClient;
import mob.sdk.cards.CardRepository;
import mob.sdk.networking.payloads.CardRequest;
import mob.sdk.networking.payloads.CardRequestInvalid;
import mob.sdk.networking.payloads.CardResult;

public class CardFinderFragment extends Fragment implements MOBClient.CardRequestListener, MOBClient.CardResultListener, MOBClient.CardRequestInvalidListener {
    private EditText mTestCardField;
    private Button mTestCardButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MOBClient.INSTANCE.setOnCardRequest(this);
        MOBClient.INSTANCE.setOnCardRequestInvalid(this);
        MOBClient.INSTANCE.setOnCardResult(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cardfinder, container, false);

        this.mTestCardField = view.findViewById(R.id.testCardField);
        this.mTestCardButton = view.findViewById(R.id.testCardButton);

        mTestCardButton.setOnClickListener(v -> sendCardRequest());

        return view;
    }

    @Override
    public void onCardRequested(CardRequest cardRequest) {
        // a card was requested for claiming

        String enteredCode = cardRequest.getCardCode();
        //TODO update ui

        // Toast.makeText(this.getContext(),String.format("Validating code %s...",enteredCode),Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onCardRequestInvalid(CardRequestInvalid cardRequestInvalid) {
        // a requested cards code was invalid
        String wrongCode = cardRequestInvalid.getCardCode();

        showDialog("Code " + wrongCode + " " + getResources().getString(R.string.wrong));
    }

    @Override
    public void onCardResult(CardResult cardResult) {
        // a card was returned from the server
        String cardId = cardResult.getCardId();
        String name = CardRepository.INSTANCE.getCard(cardId).getName();

        getActivity().runOnUiThread(() -> {
            if (SavedCardSettings.INSTANCE.loadCards().contains(cardId)) {
                showDialog(getResources().getString(R.string.card_already_got) + " " + name + "!");
            } else {
                SavedCardSettings.INSTANCE.saveCard(cardId);
                showDialog(getResources().getString(R.string.new_card_received) + " " + name + "!");
            }
            mTestCardField.getText().clear();
            hideKeyboardFrom(this.getContext(), mTestCardField);
        });

        MOBClient.INSTANCE.stop();
    }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void sendCardRequest() {

        String cardCode = mTestCardField.getText().toString();
        if (cardCode.length() == 0) {
            showToast(R.string.card_no_code, Toast.LENGTH_SHORT);
            return;
        }

        MOBClient.INSTANCE.start();
        MOBClient.INSTANCE.sendCardRequest(new CardRequest(cardCode),
                () -> {
                    Log.d(getClass().getSimpleName(), "Success");
                    // Card request sent successfully!
                    showToast(R.string.card_request_sent, Toast.LENGTH_SHORT);
                },
                () -> {
                    Log.d(getClass().getSimpleName(), "Failure");
                    // Card request failed!
                    showToast(R.string.card_request_failed, Toast.LENGTH_SHORT);
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
}
