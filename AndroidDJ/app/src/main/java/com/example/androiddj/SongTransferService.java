package com.example.androiddj;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;


public class SongTransferService extends IntentService {
    private static final int SOCKET_TIMEOUT = 5000;
    public static final String EXTRAS_SONG_NAME = "filename";
    public static final String EXTRAS_SONG_ID = "file_id";
    public static final String ACTION_SEND_FILE = "com.example.android.wifidirect.SEND_FILE";
//    public static final String EXTRAS_FILE_PATH = "file_url";
    public static final String EXTRAS_GROUP_OWNER_ADDRESS = "go_host";
    public static final String EXTRAS_GROUP_OWNER_PORT = "go_port";

    public SongTransferService(String name) {
        super(name);
    }

    public SongTransferService() {
        super("SongTransferService");
    }

    /*
     * (non-Javadoc)
     * @see android.app.IntentService#onHandleIntent(android.content.Intent)
     */
    @Override
    protected void onHandleIntent(Intent intent) {

        Context context = getApplicationContext();
        if (intent.getAction().equals(ACTION_SEND_FILE)) {
  //         String fileUri = intent.getExtras().getString(EXTRAS_FILE_PATH);
            int songID = intent.getExtras().getInt(EXTRAS_SONG_ID);
            String songName = intent.getExtras().getString(EXTRAS_SONG_NAME);
            String host = intent.getExtras().getString(EXTRAS_GROUP_OWNER_ADDRESS);
            Socket clientSocket = new Socket();
            int port = intent.getExtras().getInt(EXTRAS_GROUP_OWNER_PORT);

            try {
                Log.d(WiFiDirectActivity.TAG, "Opening client socket - ");
                clientSocket.bind(null);
                clientSocket.connect((new InetSocketAddress(host, port)), SOCKET_TIMEOUT);
                Log.d(WiFiDirectActivity.TAG, "Client socket - " + clientSocket.isConnected());
                OutputStream outstream = clientSocket.getOutputStream();
                PrintWriter pw = new PrintWriter(outstream);
                Log.d(WiFiDirectActivity.TAG, "File name: " + " " + songName);
                pw.println("SEND_SONG");
                pw.flush();

                pw.println(songID);
                pw.flush();
                final File f = new File(ClientView.folder + songName);
                f.createNewFile();
                Log.d(WiFiDirectActivity.TAG, "recieved file written " + f.getAbsolutePath());
                Log.d(WiFiDirectActivity.TAG, "server: copying files " + f.toString());
                InputStream inputstream=clientSocket.getInputStream();
                HostView.copyFile(inputstream, new FileOutputStream(f));
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

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
