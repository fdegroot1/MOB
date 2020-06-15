package mob.app.networking;

import android.util.Log;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
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
import mob.sdk.networking.payloads.RandomCardResult;

public enum MOBClient implements LoggingCallback {
    INSTANCE;

    private final AtomicBoolean connecting = new AtomicBoolean(false);
    private final Queue<Transaction> transactionQueue = new ConcurrentLinkedQueue<>();
    private final Map<Transaction, SocketClient.SuccessListener> transactionSuccessListenerMap = new ConcurrentHashMap<>();
    private final Map<Transaction, SocketClient.FailureListener> transactionFailureListenerMap = new ConcurrentHashMap<>();

    private Socket socket;
    private SocketClient client;
    private ConnectionListener connectionListener;
    private DisconnectionListener disconnectionListener;
    private CardRequestListener cardRequestListener;
    private CardRequestInvalidListener cardRequestInvalidListener;
    private CardResultListener cardResultListener;
    private BattleRequestInvalidListener battleRequestInvalidListener;
    private BattleResultListener battleResultListener;

    public void start() {
        start("10.0.2.2", 10_000);
    }

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

                    while (transactionQueue.size() > 0) {
                        Transaction transaction = transactionQueue.peek();
                        SocketClient.SuccessListener successListener = transactionSuccessListenerMap.get(transaction);
                        SocketClient.FailureListener failureListener = transactionFailureListenerMap.get(transaction);

                        client.send(transaction, () -> {
                            if (successListener != null)
                                successListener.onSuccess();
                            transactionQueue.remove(transaction);
                            transactionSuccessListenerMap.remove(transaction);
                            transactionFailureListenerMap.remove(transaction);
                        }, () -> {
                            if (failureListener != null)
                                failureListener.onFailure();
                        });

                    }

                    connecting.set(false);
                } catch (IOException exception) {
                    Log.d(getClass().getSimpleName(), "Failed to connect to server. Will try again in 1 second.");

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }
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
        return connecting.get() || (socket != null && !socket.isClosed());
    }

    /**
     * Send a battle request.
     *
     * @param battleRequest request
     */
    public void sendBattleRequest(BattleRequest battleRequest, SocketClient.SuccessListener listener) {
        queueTransaction(new Transaction(TransactionType.BATTLE_REQUEST, battleRequest), listener, null);
    }

    /**
     * Send a card request.
     *
     * @param cardRequest the request to send
     */
    public void sendCardRequest(CardRequest cardRequest, SocketClient.SuccessListener successListener, SocketClient.FailureListener failureListener) {
        queueTransaction(new Transaction(TransactionType.CARD_REQUEST, cardRequest), successListener, failureListener);
    }

    /**
     * On server connection callback.
     *
     * @param listener callback
     */
    public void setOnConnection(ConnectionListener listener) {
        this.connectionListener = listener;
    }

    /**
     * On server disconnection callback.
     *
     * @param listener callback
     */
    public void setOnDisconnection(DisconnectionListener listener) {
        this.disconnectionListener = listener;
    }

    /**
     * On battle request invalid callback.
     *
     * @param listener callback
     */
    public void setOnBattleRequestInvalid(BattleRequestInvalidListener listener) {
        this.battleRequestInvalidListener = listener;
    }

    /**
     * On battle result callback.
     *
     * @param listener callback
     */
    public void setOnBattleResult(BattleResultListener listener) {
        this.battleResultListener = listener;
    }

    /**
     * On card request callback.
     *
     * @param listener callback
     */
    public void setOnCardRequest(CardRequestListener listener) {
        this.cardRequestListener = listener;
    }

    /**
     * On card request invalid callback.
     *
     * @param listener callback
     */
    public void setOnCardRequestInvalid(CardRequestInvalidListener listener) {
        this.cardRequestInvalidListener = listener;
    }

    /**
     * On card result callback.
     *
     * @param listener callback
     */
    public void setOnCardResult(CardResultListener listener) {
        this.cardResultListener = listener;
    }

    @Override
    public void print(String s) {
        Log.d(getClass().getSimpleName(), s);
    }

    @Override
    public void printf(String s, Object... objects) {
        Log.d(getClass().getSimpleName(), String.format(s, objects));
    }

    private boolean canSendTransaction() {
        return isRunning() && client != null;
    }

    private void queueTransaction(Transaction transaction, SocketClient.SuccessListener successListener, SocketClient.FailureListener failureListener) {
        if (canSendTransaction()) {
            new Thread(() -> {
                client.send(transaction, successListener, () -> {
                    if (failureListener != null)
                        failureListener.onFailure();
                    transactionQueue.add(transaction);
                    transactionSuccessListenerMap.put(transaction, successListener);
                    transactionFailureListenerMap.put(transaction, failureListener);
                });
            }).start();
        } else {
            //TODO check if this is right
            transactionQueue.add(transaction);
            if (successListener != null)
                transactionSuccessListenerMap.put(transaction, successListener);
            if (failureListener != null)
                transactionFailureListenerMap.put(transaction, failureListener);

        }
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
