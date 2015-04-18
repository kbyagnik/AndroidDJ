package com.example.androiddj;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
public class StreamMic extends Activity {
    private static final int SOCKET_TIMEOUT = 5000;
    private Button startButton,stopButton;
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
    int minBufSize= 1024;
    private boolean status = false;
    private AudioTrack speaker;
    public static StreamMic activity=null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stream_mic);
        activity = this;
        startButton = (Button) findViewById (R.id.start_button);
        stopButton = (Button) findViewById (R.id.stop_button);

        startButton.setOnClickListener (startListener);
        stopButton.setOnClickListener (stopListener);

        minBufSize += 2048;
        System.out.println("minBufSize: " + minBufSize);
    }

    private final OnClickListener stopListener = new OnClickListener() {

        @Override
        public void onClick(View arg0) {
            status = false;
//            recorder.release();
//            Log.d("VS","Recorder released");
            stopButton.setEnabled(false);
            startButton.setEnabled(true);
        }

    };

    private final OnClickListener startListener = new OnClickListener() {

        @Override
        public void onClick(View arg0) {
            status = true;
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
            startStreaming();
//            Log.d("2", "Recorder initialized");
        }

    };

    private static int[] mSampleRates = new int[] { 8000, 11025, 22050, 44100 };
    public AudioRecord findAudioRecord() {
        for (int rate : mSampleRates) {
            for (short audioFormat : new short[] { AudioFormat.ENCODING_PCM_8BIT, AudioFormat.ENCODING_PCM_16BIT }) {
                for (short channelConfig : new short[] { AudioFormat.CHANNEL_IN_MONO, AudioFormat.CHANNEL_IN_STEREO }) {
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
                        Log.e("errorrrr", rate + "Exception, keep trying.",e);
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
                    clientSocket.connect((new InetSocketAddress((DeviceDetailFragment.info.groupOwnerAddress.getHostAddress()),serverport)), SOCKET_TIMEOUT);
//                    Log.d("Streaming", "Client socket - " + clientSocket.isConnected());
                    OutputStream out_stream = clientSocket.getOutputStream();
                    PrintWriter pw = new PrintWriter(out_stream);
                    // sent that microphone data has now ended
//                    Log.d("Streaming", "MICROPHONE_androiddj_end");
                    Log.i("MicUsing","Sending string - MICROPHONE_androiddj_start");
                    pw.println("MICROPHONE_androiddj_start");
                    pw.flush();
                    Log.i("MicUsing","Sent string - MICROPHONE_androiddj_start");
                    InputStreamReader isr = new InputStreamReader(clientSocket.getInputStream());
                    BufferedReader bsr = new BufferedReader(isr);
                    String allow = bsr.readLine();
                    Log.i("MicUsing","Using mic allowed - "+allow);
                    if(allow.equals("yes"))
                    {
                        DatagramSocket socket = new DatagramSocket();
//                        Log.d("VS", "Socket Created");
//                        Log.d("VS","Buffer created of size " + minBufSize);
                        DatagramPacket packet;

                        final InetAddress destination = InetAddress.getByName(DeviceDetailFragment.info.groupOwnerAddress.getHostAddress());// address of the host of the party u are joined to
//                        Log.d("VS", "Address retrieved");

                        //recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,sampleRate,channelConfig,audioFormat,minBufSize*10);

//                        Log.d("VS", "Recorder initialized");

                        //      recorder = findAudioRecord();
                        recorder= new AudioRecord(MediaRecorder.AudioSource.DEFAULT, 44100, channelConfig, audioFormat, AudioRecord.getMinBufferSize(44100, channelConfig, audioFormat));

//recorder= new AudioRecord(44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT;)
                        minBufSize= recorder.getMinBufferSize(
                                44100,
                                AudioFormat.CHANNEL_IN_MONO,
                                AudioFormat.ENCODING_PCM_16BIT);
                        //  recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,sampleRate,channelConfig,audioFormat,minBufSize);
//                        Log.d("VS", "Recorder initialized");
                        byte[] buffer = new byte[minBufSize];

                        //recorder.OutputFormat.THREE_GPP;
                        //recorder.release();
                        recorder.startRecording();
//------------
                        speaker = new AudioTrack(AudioManager.STREAM_VOICE_CALL,sampleRate,AudioFormat.CHANNEL_OUT_STEREO,audioFormat,minBufSize,AudioTrack.MODE_STREAM);
                        speaker.setPlaybackRate(22100);
                        //  speaker.play();
                        //--------------------------------
                        while(status) {
                            //reading data from MIC into buffer
                            recorder.read(buffer, 0, minBufSize);
                            // minBufSize = (int)(1024*3.5);
                            //   Log.d("min buffer size"+ minBufSize );
//                            Log.d("VS","minimum buffer size created is  " + minBufSize);
//                            Log.d("1", "Recorder initialized");
                            //putting buffer in the packet
                            packet = new DatagramPacket (buffer,buffer.length,destination,dataport);

                            socket.send(packet);
                            String sentence = new String( packet.getData().toString());
//                            System.out.println("sent packet: " + packet.getData());
                            //    System.out.println();
//                            System.out.println("MinBufferSize: " +minBufSize);

//                        speaker = new AudioTrack(AudioManager.STREAM_MUSIC,sampleRate,channelConfig,audioFormat,minBufSize,AudioTrack.MODE_STREAM);
                            speaker.write(buffer, 0, buffer.length);

                        }

                        buffer = "end".getBytes();
                        Log.d("MicUsing","end packet sending");
                        packet  = new DatagramPacket(buffer,buffer.length,destination,dataport);
                        Log.d("MicUsing","end packet sending");
                        socket.send(packet);
                        Log.d("MicUsing","release recorder");
                        recorder.release();
                        Log.d("MicUsing","socket closing");
                        socket.close();
                        clientSocket.close();
                    }
                    else
                    {
                        Log.d("MicUsing","Mic Already in use");
                        activity.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(activity, "Mic Already in Use", Toast.LENGTH_LONG).show();
                                stopButton.callOnClick();
                            }
                        });
                        clientSocket.close();
                    }


                } catch(UnknownHostException e) {
                    Log.e("VS", "UnknownHostException");
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("VS", "IOException");
                }
            }

        });
        streamThread.start();
    }
}
