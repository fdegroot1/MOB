package mob.app.networking;

import android.util.Log;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

import mob.sdk.networking.LoggingCallback;
import mob.sdk.networking.SocketClient;
import mob.sdk.networking.Transaction;
import mob.sdk.networking.TransactionType;
import mob.sdk.networking.listeners.ConnectionListener;
import mob.sdk.networking.listeners.DisconnectionListener;
import mob.sdk.networking.payloads.BattleRequest;
import mob.sdk.networking.payloads.BattleRequestInvalid;
import mob.sdk.networking.payloads.BattleResult;
import mob.sdk.networking.payloads.CardRequest;
import mob.sdk.networking.payloads.CardRequestInvalid;
import mob.sdk.networking.payloads.CardResult;

public enum MOBClient implements LoggingCallback {
    INSTANCE;

    private static MOBClient instance;

    private final AtomicBoolean connecting = new AtomicBoolean(false);

    private Socket socket;
    private SocketClient client;
    private ConnectionListener connectionListener;
    private DisconnectionListener disconnectionListener;
    private CardRequestListener cardRequestListener;
    private CardRequestInvalidListener cardRequestInvalidListener;
    private CardResultListener cardResultListener;
    private BattleRequestInvalidListener battleRequestInvalidListener;
    private BattleResultListener battleResultListener;

    /**
     * Start the socket to the server.
     */
    public void start(String host, int port) {
        SocketClient.addLoggingCallback(this);

        if (isRunning()) {
            return;
        }

        connecting.set(true);

        new Thread(() -> {
            while (connecting.get()) {
                try {
                    Log.d(getClass().getSimpleName(), "Connecting to server.");
                    this.socket = new Socket(host, port);
                    Log.d(getClass().getSimpleName(), "Connected to server");
                    this.client = new SocketClient(socket);

                    if (connectionListener != null)
                        connectionListener.onConnection();

                    this.client.addTransactionListener((transaction -> {
                        switch (transaction.getType()) {
                            case BATTLE_REQUEST_INVALID:
                                if (battleRequestInvalidListener != null)
                                    battleRequestInvalidListener.onBattleRequestInvalid((BattleRequestInvalid) transaction.getPayload());
                                break;
                            case BATTLE_RESULT:
                                if (battleResultListener != null)
                                    battleResultListener.onBattleResult((BattleResult) transaction.getPayload());
                                break;
                            case CARD_REQUEST:
                                if (cardRequestListener != null)
                                    cardRequestListener.onCardRequested((CardRequest) transaction.getPayload());
                                break;
                            case CARD_REQUEST_INVALID:
                                if (cardRequestInvalidListener != null) {
                                    cardRequestInvalidListener.onCardRequestInvalid((CardRequestInvalid) transaction.getPayload());
                                }
                                break;
                            case CARD_RESULT:
                                if (cardResultListener != null) {
                                    cardResultListener.onCardResult((CardResult) transaction.getPayload());
                                }
                                break;
                        }
                    }));

                    this.client.addDisconnectionListener(() -> {
                        if (disconnectionListener != null)
                            disconnectionListener.onDisconnection();
                    });

                    connecting.set(false);
                } catch (IOException exception) {
                    Log.d(getClass().getSimpleName(), "Failed to connect to server. Will try again in 1 second.");

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) { }
                }
            }
        }).start();
    }

    /**
     * Stop the socket to the server.
     */
    public void stop() {
        if (!isRunning()) {
            return;
        }

        connecting.set(false);

        if (client != null) {
            client.stop();
        }

        if (socket != null) {
            try {
                socket.close();
            } catch (IOException exception) {
                Log.d(getClass().getSimpleName(), "Failed to close socket.");
            }
        }
    }

    /**
     * Get whether the connection is running.
     */
    public boolean isRunning() {
        return connecting.get() || (socket != null && socket.isConnected());
    }

    /**
     * Send a battle request.
     * @param battleRequest request
     */
    public void sendBattleRequest(BattleRequest battleRequest, SocketClient.SuccessListener listener) {
        if (!canSendTransaction())
            return;

        client.send(new Transaction(TransactionType.BATTLE_REQUEST, battleRequest), listener, null);
    }

    /**
     * On server connection callback.
     * @param listener callback
     */
    public void setOnConnection(ConnectionListener listener) {
        this.connectionListener = listener;
    }

    /**
     * On server disconnection callback.
     * @param listener callback
     */
    public void setOnDisconnection(DisconnectionListener listener) {
        this.disconnectionListener = listener;
    }

    public void setOnBattleRequestInvalid(BattleRequestInvalidListener listener) {
        this.battleRequestInvalidListener = listener;
    }

    public void setOnBattleResult(BattleResultListener listener) {
        this.battleResultListener = listener;
    }

    public void setOnCardRequest(CardRequestListener listener) {
        this.cardRequestListener = listener;
    }

    public void setOnCardRequestInvalid(CardRequestInvalidListener listener) {
        this.cardRequestInvalidListener = listener;
    }

    public void setOnCardResult(CardResultListener listener) {
        this.cardResultListener = listener;
    }

    private boolean canSendTransaction() {
        return isRunning() && client != null;
    }

    @Override
    public void print(String s) {
        Log.d(getClass().getSimpleName(), s);
    }

    @Override
    public void printf(String s, Object... objects) {
        Log.d(getClass().getSimpleName(), String.format(s, objects));
    }

    @FunctionalInterface
    public interface CardRequestListener {
        void onCardRequested(CardRequest cardRequest);
    }

    @FunctionalInterface
    public interface CardRequestInvalidListener {
        void onCardRequestInvalid(CardRequestInvalid cardRequestInvalid);
    }

    @FunctionalInterface
    public interface CardResultListener {
        void onCardResult(CardResult cardResult);
    }

    @FunctionalInterface
    public interface BattleRequestInvalidListener {
        void onBattleRequestInvalid(BattleRequestInvalid battleRequestInvalid);
    }

    @FunctionalInterface
    public interface BattleResultListener {
        void onBattleResult(BattleResult battleResult);
    }
}
