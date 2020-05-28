package mob.app.networking;

import android.util.Log;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

import mob.sdk.networking.LoggingCallback;
import mob.sdk.networking.SocketClient;
import mob.sdk.networking.listeners.ConnectionListener;
import mob.sdk.networking.listeners.DisconnectionListener;

public enum MOBClient implements LoggingCallback {
    INSTANCE;

    private static MOBClient instance;

    private final AtomicBoolean connecting = new AtomicBoolean(false);

    private Socket socket;
    private SocketClient client;
    private ConnectionListener connectionListener;
    private DisconnectionListener disconnectionListener;

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

    private boolean canSendTransaction() {
        return isRunning() && client != null;
    }

    @FunctionalInterface
    public interface onCardRequested {
        void onCardRequested();
    }

    @Override
    public void print(String s) {
        Log.d(getClass().getSimpleName(), s);
    }

    @Override
    public void printf(String s, Object... objects) {
        Log.d(getClass().getSimpleName(), String.format(s, objects));
    }
}
