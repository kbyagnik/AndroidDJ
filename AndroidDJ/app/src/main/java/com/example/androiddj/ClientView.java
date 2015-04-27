package com.example.androiddj;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.androiddj.database.DatabaseHandler;
import com.example.androiddj.database.Songs;
import com.example.androiddj.youtubeparser.ListViewHome;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class ClientView extends Activity {
    private String tag = "DJ Debugging";
    ListView list;
    ListViewAdapterClient adapter;
    public static String LEAVE_PARTY = "leave";
    final int pos = -1;
    public static String folder;
    private boolean downloading = false;
    private Handler downloadHandler;
    private ArrayList<Songs> songs;
    private View layout = null;
    protected static final int CHOOSE_FILE_RESULT_CODE = 20;

    private static final int SOCKET_TIMEOUT = 5000;
    private Button startButton, stopButton;
    private MediaRecorder myAudioRecorder;
    public byte[] buffer;
    public static DatagramSocket socket;
    private int serverport = 8987;
    private int dataport = 8989;
    AudioRecord recorder;

    private int sampleRate = 44100;
    private int channelConfig = AudioFormat.CHANNEL_IN_MONO;
    private int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    //int minBufSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);
    int minBufSize = 1024;
    private boolean status = false;
    private AudioTrack speaker;
    public static StreamMic activity = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        folder = Environment.getExternalStorageDirectory() + "/AndroidDJ-SavedSongs/";
        File dirs = new File(folder);

        if (!dirs.exists())
            dirs.mkdirs();

        Log.i(tag, "Going to call oncreate");
        super.onCreate(savedInstanceState);
        Log.i(tag, "calling setcontent view");
        setContentView(R.layout.activity_main);

        startButton = (Button) findViewById(R.id.client_mic_start);
        stopButton = (Button) findViewById(R.id.client_mic_stop);

        startButton.setOnClickListener(startListener);
        stopButton.setOnClickListener(stopListener);

        minBufSize += 2048;
        System.out.println("minBufSize: " + minBufSize);

        Log.i(tag, "Going to create list_file");
        findViewById(R.id.host_layout).setVisibility(View.INVISIBLE);
        findViewById(R.id.client_layout).setVisibility(View.VISIBLE);
        /*
		 * Here json string has to be used to get the list of the songs
		 */

        songs = new ArrayList<>();
        Log.i(tag, "Going to create list");
        list = (ListView) findViewById(R.id.listview);
        adapter = new ListViewAdapterClient(songs, ClientView.this, pos, new DatabaseHandler(this));
        list.setAdapter(adapter);
        Log.i(tag, "Adapter set");
        Log.i(tag, "Defining on click listener");

        list.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Log.i(tag, "Item at " + Integer.toString(position) + " is clicked");
                adapter.setPosition(position);
                position = pos;
                adapter.notifyDataSetChanged();
            }
        });

        findViewById(R.id.add_file).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("audio/*");
                        Log.d(WiFiDirectActivity.TAG, "Start sending file");
                        startActivityForResult(intent, CHOOSE_FILE_RESULT_CODE);
                    }
                });

        findViewById(R.id.leave_party).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(tag, "Disconnect");
                Intent intent = new Intent();
                intent.putExtra(LEAVE_PARTY, "yes");
                setResult(1, intent);
                finish();
            }
        });

        findViewById(R.id.youtube).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ClientView.this, ListViewHome.class);
                        Log.d(WiFiDirectActivity.TAG, "Start youtube");
                        startActivity(intent);
                    }
                });

        downloadHandler = new Handler();

        Runnable downloadFile = new Runnable() {
            @Override
            public void run() {
                if (!downloading) {
                    Log.d(tag, "Starting Download Playlist..........");
                    try {
                        PlaylistTransfer p = new PlaylistTransfer(getApplicationContext());
                        p.execute();
                    } catch (IOException e) {
                        Log.e(tag, e.getMessage());
                    }
                }

                Log.d(tag, "handler attached....");
                downloadHandler.postDelayed(this, 500);
            }
        };

        Thread download = new Thread(downloadFile);
        download.start();

        Log.i(tag, "Going to call create list view");
        Log.i(tag, "Finished create list view");
    }

    private final View.OnClickListener stopListener = new View.OnClickListener() {

        @Override
        public void onClick(View arg0) {
            status = false;
            startButton.setVisibility(View.VISIBLE);
            stopButton.setVisibility(View.INVISIBLE);

            stopButton.setEnabled(false);
            startButton.setEnabled(true);
        }

    };

    private final View.OnClickListener startListener = new View.OnClickListener() {

        @Override
        public void onClick(View arg0) {
            status = true;

            stopButton.setVisibility(View.VISIBLE);
            startButton.setVisibility(View.INVISIBLE);


            startButton.setEnabled(false);
            stopButton.setEnabled(true);
            startStreaming();
        }

    };

    private static int[] mSampleRates = new int[]{8000, 11025, 22050, 44100};

    public AudioRecord findAudioRecord() {
        for (int rate : mSampleRates) {
            for (short audioFormat : new short[]{AudioFormat.ENCODING_PCM_8BIT, AudioFormat.ENCODING_PCM_16BIT}) {
                for (short channelConfig : new short[]{AudioFormat.CHANNEL_IN_MONO, AudioFormat.CHANNEL_IN_STEREO}) {
                    try {
                        Log.d("errror_fining_channel", "Attempting rate " + rate + "Hz, bits: " + audioFormat + ", channel: "
                                + channelConfig);
                        int bufferSize = AudioRecord.getMinBufferSize(rate, channelConfig, audioFormat);

                        if (bufferSize != AudioRecord.ERROR_BAD_VALUE) {
                            // check if we can instantiate and have a success
                            AudioRecord recorder = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, rate, channelConfig, audioFormat, bufferSize);

                            if (recorder.getState() == AudioRecord.STATE_INITIALIZED)
                                return recorder;
                        }
                    } catch (Exception e) {
                        Log.e("errorrrr", rate + "Exception, keep trying.", e);
                    }
                }
            }
        }
        return null;
    }

    public void startStreaming() {


        Thread streamThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Socket clientSocket = new Socket();
                    clientSocket.bind(null);
                    clientSocket.connect((new InetSocketAddress((DeviceDetailFragment.info.groupOwnerAddress.getHostAddress()), serverport)), SOCKET_TIMEOUT);
                    OutputStream out_stream = clientSocket.getOutputStream();
                    PrintWriter pw = new PrintWriter(out_stream);
                    Log.i("MicUsing", "Sending string - MICROPHONE_androiddj_start");
                    pw.println("MICROPHONE_androiddj_start");
                    pw.flush();
                    Log.i("MicUsing", "Sent string - MICROPHONE_androiddj_start");
                    InputStreamReader isr = new InputStreamReader(clientSocket.getInputStream());
                    BufferedReader bsr = new BufferedReader(isr);
                    String allow = bsr.readLine();
                    Log.i("MicUsing", "Using mic allowed - " + allow);
                    if (allow.equals("yes")) {
                        DatagramSocket socket = new DatagramSocket();
                        DatagramPacket packet;

                        final InetAddress destination = InetAddress.getByName(DeviceDetailFragment.info.groupOwnerAddress.getHostAddress());// address of the host of the party u are joined to
                        recorder = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, 44100, channelConfig, audioFormat, AudioRecord.getMinBufferSize(44100, channelConfig, audioFormat));

                        minBufSize = recorder.getMinBufferSize(
                                44100,
                                AudioFormat.CHANNEL_IN_MONO,
                                AudioFormat.ENCODING_PCM_16BIT);
                        byte[] buffer = new byte[minBufSize];

                        recorder.startRecording();
                        speaker = new AudioTrack(AudioManager.STREAM_VOICE_CALL, sampleRate, AudioFormat.CHANNEL_OUT_STEREO, audioFormat, minBufSize, AudioTrack.MODE_STREAM);
                        speaker.setPlaybackRate(22100);
                        while (status) {
                            recorder.read(buffer, 0, minBufSize);
                            packet = new DatagramPacket(buffer, buffer.length, destination, dataport);

                            socket.send(packet);
                            speaker.write(buffer, 0, buffer.length);

                        }

                        buffer = "end".getBytes();
                        Log.d("MicUsing", "end packet sending");
                        packet = new DatagramPacket(buffer, buffer.length, destination, dataport);
                        Log.d("MicUsing", "end packet sending");
                        socket.send(packet);
                        Log.d("MicUsing", "release recorder");
                        recorder.release();
                        Log.d("MicUsing", "socket closing");
                        socket.close();
                        clientSocket.close();
                    } else {
                        Log.d("MicUsing", "Mic Already in use");
                        activity.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(activity, "Mic Already in Use", Toast.LENGTH_LONG).show();
                                stopButton.callOnClick();
                            }
                        });
                        clientSocket.close();
                    }


                } catch (UnknownHostException e) {
                    Log.e("VS", "UnknownHostException");
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("VS", "IOException");
                }
            }

        });
        streamThread.start();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != RESULT_OK) {
            Log.d(WiFiDirectActivity.TAG, "Error in sending");
            Toast.makeText(this, "Error in sending", Toast.LENGTH_SHORT);
            return;
        }

        if (requestCode == CHOOSE_FILE_RESULT_CODE) {
            Uri uri = data.getData();
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            cursor.moveToFirst();

            Log.d(WiFiDirectActivity.TAG, "filepath" + " " + cursor.getString(nameIndex));

            Log.d(WiFiDirectActivity.TAG, "Intent----------- " + uri);
            Intent serviceIntent = new Intent(this, FileTransferService.class);
            serviceIntent.setAction(FileTransferService.ACTION_SEND_FILE);
            serviceIntent.putExtra(FileTransferService.EXTRAS_FILE_PATH, uri.toString());
            serviceIntent.putExtra(FileTransferService.EXTRAS_FILE_NAME, cursor.getString(nameIndex));
            serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_ADDRESS,
                    DeviceDetailFragment.info.groupOwnerAddress.getHostAddress());
            serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_PORT, 8988);
            startService(serviceIntent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private ArrayList<Songs> addSongs() {
        ArrayList<Songs> songs = new ArrayList<Songs>();
        for (int i = 0; i < 10; i++) {
            songs.add(new Songs(i + 1, "Song " + Integer.toString(i + 1)));
        }

        return songs;
    }


    @Override
    public void onRestart() {
        super.onRestart();
        Log.i(tag, "Activity is restarted");
    }


    @Override
    public void onStop() {
        super.onStop();
        Log.i(tag, "Activity is stopped");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(tag, "Activity is destroyed");
        songs.clear();
    }

    public String getPlaylist() {
        int port = 8122;
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
            Log.i(tag, "Socket not opened on port" + Integer.toString(port));
        }
        try {
            Log.d(tag, "Server-Socket opened for playlist transfer");

            downloading = true;
            Socket client = serverSocket.accept();
            Log.d(tag, "connection done");

            InputStream inputstream = client.getInputStream();
            InputStreamReader isr = new InputStreamReader(inputstream);
            BufferedReader br = new BufferedReader(isr);
            Log.d(tag, "recieving playlist");
            String playlist = br.readLine();
            Log.d(tag, "recieved Playlist " + playlist);
            serverSocket.close();
            return playlist;
        } catch (IOException e) {
            Log.e(tag, e.getMessage());
            boolean downloading = false;
            Log.d(tag, "Downloading Error: " + downloading);
            return null;
        }
    }

    public void updateList() {
        String db = getPlaylist();
        Log.i(tag, db);
        //UpdateListView
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:

                moveTaskToBack(true);

                return true;
        }
        return false;
    }

    private class PlaylistTransfer extends AsyncTask<Void, Void, String> {

        private Context context;

        /**
         * @param context
         */
        public PlaylistTransfer(Context context) throws IOException {
            this.context = context;
        }

        @Override
        protected String doInBackground(Void... params) {

            String playlist = getPlaylist();
            return playlist;
        }

        /*
         * (non-Javadoc)
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(String result) {
            Log.i("playlist1", result);
            if (result != null) {
                downloading = false;
                try {
                    ArrayList<Songs> songsArray = new ArrayList<Songs>();
                    JSONArray jsonArray = new JSONArray(result);
                    Log.i("playlist", Integer.toString(jsonArray.length()));
                    JSONObject jsonObject = new JSONObject();
                    Log.i("playlist", "going to enter for loop");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);
                        Log.i("playlist", jsonObject.toString());
                        Log.i("playlist", "inside for loop " + Integer.toString(i));
                        Log.i("playlist", "a" + Integer.toString(jsonArray.length()) + "b");
//                    Log.i("playlist",jsonObject.toString());
                        Songs newSong = new Songs();
                        Log.i("playlist", "new song created");
                        newSong.setID(jsonObject.getInt("id"));
                        newSong.setName(jsonObject.getString("name"));
                        newSong.setUpvotes(jsonObject.getInt("upvotes"));
                        newSong.setDownvotes(jsonObject.getInt("downvotes"));

                        songsArray.add(newSong);

                    }
                    songs = songsArray;
                    Log.i("playlist", Integer.toString(songs.size()));

                    adapter.setList(songs);
                    Log.i("playlist", Integer.toString(songs.size()));
                    adapter.notifyDataSetChanged();
                } catch (Exception e) {
                    Log.i("playlist", e.toString());
                }
                // function to parse json result

                Log.d(tag, "Downloading Completed");
            }

        }

    }

    public void refreshList() {
        adapter.notifyDataSetChanged();
    }
}
