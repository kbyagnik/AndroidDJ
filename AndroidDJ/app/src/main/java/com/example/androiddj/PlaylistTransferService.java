package com.example.androiddj;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by Savyasachi on 13-03-2015.
 */
public class PlaylistTransferService extends IntentService {


    private static final int SOCKET_TIMEOUT = 5000;
    public static final String EXTRAS_PLAYLIST = "json";
    public static final String ACTION_SEND_FILE = "com.example.android.wifidirect.SEND_FILE";
    public static final String EXTRAS_CLIENT_ADDRESS = "go_host";
    public static final String EXTRAS_CLIENT_PORT = "go_port";

    public PlaylistTransferService(String name) {
        super(name);
    }

    public PlaylistTransferService() {
        super("FileTransferService");
    }

    /*
     * (non-Javadoc)
     * @see android.app.IntentService#onHandleIntent(android.content.Intent)
     */
    @Override
    protected void onHandleIntent(Intent intent) {

        Context context = getApplicationContext();
        if (intent.getAction().equals(ACTION_SEND_FILE)) {
            String Playlist = intent.getExtras().getString(EXTRAS_PLAYLIST);
            String host = intent.getExtras().getString(EXTRAS_CLIENT_ADDRESS);
            Socket clientSocket = new Socket();
            int port = intent.getExtras().getInt(EXTRAS_CLIENT_PORT);

            try {
                Log.d(WiFiDirectActivity.TAG, "Opening socket for playlist - ");
                clientSocket.bind(null);
                clientSocket.connect((new InetSocketAddress(host, port)), SOCKET_TIMEOUT);
                Log.d(WiFiDirectActivity.TAG, "playlist socket - " + clientSocket.isConnected());
                OutputStream outstream = clientSocket.getOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(outstream,"UTF-8");
                osw.write(EXTRAS_PLAYLIST,0, EXTRAS_PLAYLIST.length());
                Log.d(WiFiDirectActivity.TAG, "Client: Playlist written");
            } catch (IOException e) {
                Log.e(WiFiDirectActivity.TAG, e.getMessage());
            } finally {
                if (clientSocket != null) {
                    if (clientSocket.isConnected()) {
                        try {
                            clientSocket.close();
                        } catch (IOException e) {
                            // Give up
                            e.printStackTrace();
                        }
                    }
                }
            }

        }
    }

}
