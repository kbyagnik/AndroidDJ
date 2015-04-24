package com.example.androiddj.youtubeparser;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.example.androiddj.DeviceDetailFragment;
import com.example.androiddj.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 *
 * Created by Ashish Singh on 06-Apr-15.
 */
public class YoutubeDialogFragment extends DialogFragment {
    public static final String videoId = "videoId";
    public String url;
    public String title;
    public String tag="Stream link";
    Activity mActivity;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //Resources res = getActivity().getResources();
        final Bundle bundle = getArguments();
        final String VIDEO_ID =bundle.getString(videoId);
        title = bundle.getString("Description");
//        url="https://www.youtube.com/watch?v="+VIDEO_ID;
        url = VIDEO_ID;
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setItems(R.array.youtube_dialog_list, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                        if(which==1){
                            //play the video in youtube app
//                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                            startActivity(browserIntent);

                            JSONObject jsonObject = new JSONObject();

                            try {
                                jsonObject.put("type","youtube");
                                jsonObject.put("url",url);
                                jsonObject.put("title",title);
                                String linkStream = jsonObject.toString();
                                streamLink(linkStream);
                            } catch (JSONException e) {
                                Log.d("streamLink","json");
                                e.printStackTrace();
                            }
                        }
                        else if(which==0){
                            ListViewWithBaseAdapter.play(VIDEO_ID);
                        }
                        dialog.dismiss();
                    }
            })
            .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK, so save the mSelectedItems results somewhere
                    // or return them to the component that opened the dialog
                    dialog.dismiss();
                }
            });
        return builder.create();
    }

    @Override
    public  void onAttach(Activity activity){
        super.onAttach(activity);
        mActivity=activity;
    }





    public void streamLink(final String linkString){

        Thread streamThread = new Thread(new Runnable() {

            @Override
            public void run() {
                int port = 8119;
                String tag1 = "streamLink";
                final int SOCKET_TIMEOUT = 5000;
                try {
                    Socket clientSocket = new Socket();
                    clientSocket.bind(null);
                    clientSocket.connect((new InetSocketAddress((DeviceDetailFragment.info.groupOwnerAddress.getHostAddress()),port)), SOCKET_TIMEOUT);
                    Log.d(tag1, "Client socket - " + clientSocket.isConnected());
                    OutputStream out_stream = clientSocket.getOutputStream();
                    PrintWriter pw = new PrintWriter(out_stream);
                    // sent that microphone data has now ended
                    Log.d(tag1, "link sending " + linkString);
                    pw.println(linkString);
                    pw.flush();
                    clientSocket.close();

                } catch(UnknownHostException e) {
                    Log.e(tag1, "UnknownHostException");
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(tag1, "IOException");
                }
            }

        });
        streamThread.start();


    }





}