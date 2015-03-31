package com.example.androiddj;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

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
import java.net.*;
public class Stream_mic extends Activity {
    private static final int SOCKET_TIMEOUT = 5000;
    private Button startButton,stopButton;
    private MediaRecorder myAudioRecorder;
    public byte[] buffer;
    public static DatagramSocket socket;
    private int port=8988;
    AudioRecord recorder;

    private int sampleRate = 44100;
    private int channelConfig = AudioFormat.CHANNEL_IN_MONO;
    private int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    //int minBufSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);


    int minBufSize= 1024;


    private boolean status = true;
    private AudioTrack speaker;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stream_mic);

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
            recorder.release();
            Log.d("VS","Recorder released");
        }

    };

    private final OnClickListener startListener = new OnClickListener() {

        @Override
        public void onClick(View arg0) {
            status = true;
            startStreaming();
            Log.d("2", "Recorder initialized");
        }

    };





    /////////




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

//-------------------






    ///------------------------------




    ///////////

    public void startStreaming() {


        Thread streamThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Socket clientSocket = new Socket();
                    clientSocket.bind(null);
                    clientSocket.connect((new InetSocketAddress((DeviceDetailFragment.info.groupOwnerAddress.getHostAddress()),port)), SOCKET_TIMEOUT);
                    Log.d(WiFiDirectActivity.TAG, "Client socket - " + clientSocket.isConnected());
                    OutputStream out_stream = clientSocket.getOutputStream();
                    PrintWriter pw = new PrintWriter(out_stream);
                    // sent that microphone data has now ended
                    Log.d(WiFiDirectActivity.TAG, "MICROPHONE_androiddj_end");
                    pw.println("MICROPHONE_androiddj_start");
                    pw.flush();

                    DatagramSocket socket = new DatagramSocket();
                    Log.d("VS", "Socket Created");


                    Log.d("VS","Buffer created of size " + minBufSize);
                    DatagramPacket packet;

                    final InetAddress destination = InetAddress.getByName(DeviceDetailFragment.info.groupOwnerAddress.getHostAddress());// address of the host of the party u are joined to
                    Log.d("VS", "Address retrieved");


                    //recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,sampleRate,channelConfig,audioFormat,minBufSize*10);

                    Log.d("VS", "Recorder initialized");
                    //




















                    //










                    //      recorder = findAudioRecord();
                    recorder= new AudioRecord(MediaRecorder.AudioSource.DEFAULT, 44100, channelConfig, audioFormat, AudioRecord.getMinBufferSize(44100, channelConfig, audioFormat));

//recorder= new AudioRecord(44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT;)
                    minBufSize= recorder.getMinBufferSize(
                            44100,
                            AudioFormat.CHANNEL_IN_MONO,
                            AudioFormat.ENCODING_PCM_16BIT);
                    //  recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,sampleRate,channelConfig,audioFormat,minBufSize);
                    Log.d("VS", "Recorder initialized");
                    byte[] buffer = new byte[minBufSize];

                    //recorder.OutputFormat.THREE_GPP;
                    //recorder.release();
                    recorder.startRecording();
//------------
                    speaker = new AudioTrack(AudioManager.STREAM_VOICE_CALL,sampleRate,AudioFormat.CHANNEL_OUT_STEREO,audioFormat,minBufSize,AudioTrack.MODE_STREAM);
                    speaker.setPlaybackRate(22100);
                    //  speaker.play();
                    //--------------------------------
                    while(status == true) {
/*

//reading data from MIC into buffer
                    minBufSize = recorder.read(buffer, 0, buffer.length);

                    //putting buffer in the packet
                    packet = new DatagramPacket (buffer,buffer.length,destination,port);

                    socket.send(packet);
                    System.out.println("MinBufferSize: " +minBufSize);



 */


                        //reading data from MIC into buffer
                        recorder.read(buffer, 0, minBufSize);
                        // minBufSize = (int)(1024*3.5);
                        //   Log.d("min buffer size"+ minBufSize );
                        Log.d("VS","minimum buffer size created is  " + minBufSize);
                        Log.d("1", "Recorder initialized");
                        //putting buffer in the packet
                        packet = new DatagramPacket (buffer,buffer.length,destination,port);

                        socket.send(packet);
                        String sentence = new String( packet.getData().toString());
                        System.out.println("sent packet: " + packet.getData());
                        //    System.out.println();
                        System.out.println("MinBufferSize: " +minBufSize);
///////////////////


//                        speaker = new AudioTrack(AudioManager.STREAM_MUSIC,sampleRate,channelConfig,audioFormat,minBufSize,AudioTrack.MODE_STREAM);
                        speaker.write(buffer, 0, buffer.length);




                        //////////////////////////

                    }
                    buffer = "end".getBytes();
                    packet  = new DatagramPacket(buffer,buffer.length,destination,port);
                    socket.send(packet);
                    recorder.release();
                    status=false;
                  /*

                    Socket clientSocket = new Socket();
                    clientSocket.bind(null);
                    clientSocket.connect((new InetSocketAddress((DeviceDetailFragment.info.groupOwnerAddress.getHostAddress()),port)), SOCKET_TIMEOUT);
                    Log.d(WiFiDirectActivity.TAG, "Client socket - " + clientSocket.isConnected());
                    OutputStream outstream = clientSocket.getOutputStream();
                    PrintWriter pw = new PrintWriter(outstream);
                    // sent that microphone data has now ended
                    Log.d(WiFiDirectActivity.TAG, "MICROPHONE_androiddj_end");
                    pw.println("MICROPHONE_androiddj_end");
                    pw.flush();
                    */



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
