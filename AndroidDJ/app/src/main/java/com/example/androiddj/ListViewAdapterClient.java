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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.example.androiddj.database.DatabaseHandler;
import com.example.androiddj.database.Songs;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class ListViewAdapterClient extends ArrayAdapter<Songs>
{
	 
private List<Songs> StringList;
private Context context;
private int pos;
final String tag = "DJ Debugging";
final ClientView client;
final DatabaseHandler db;
private HashSet<Integer> songsUpvoted;
private HashSet<Integer> songsDownvoted;


    public ListViewAdapterClient(List<Songs> StringList, Context ctx,int position,DatabaseHandler db) {
    super(ctx, R.layout.listview_content, StringList);
    Log.i(tag, "Inside list view adapter constructor");
    pos = position;
    this.StringList = StringList;
    this.context = ctx;
    this.client = (ClientView) ctx;
    this.db = db;
    songsUpvoted = new HashSet<Integer>();
    songsDownvoted = new HashSet<Integer>();

}
 
public View getView(final int position, View convertView, ViewGroup parent) {
     Log.i(tag, "Inside get view function");
    // First let's verify the convertView is not null
    if (convertView == null) {
        // This a new view we inflate the new layout
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.listview_content, parent, false);
    }
        // Now we can fill the layout with the right values
        if(StringList.size() == 0)
        {
        	Log.i(tag, "List is empty");
        }
        else
        {
            final TextView name = (TextView) convertView.findViewById(R.id.listViewItem);
	        final Songs p = StringList.get(position);
	        Log.i(tag, "Song id " + Integer.toString(p.getID()));
	        final int id = p.getID();
	        name.setText(p.getName());
	        final TextView upvotes = (TextView) convertView.findViewById(R.id.upvoteCount);
	        final TextView downvotes = (TextView) convertView.findViewById(R.id.downvoteCount);
	        upvotes.setText(Integer.toString(p.getUpvotes()));
	        downvotes.setText(Integer.toString(p.getDownvotes()));
	        Log.i(tag,Integer.toString(position) + "    " + Integer.toString(pos));
	        final Button upvote = (Button)convertView.findViewById(R.id.upvote);
	        final Button downvote = (Button)convertView.findViewById(R.id.downvote);
	        final TextView vote = (TextView)convertView.findViewById(R.id.votedByUser);
	        if(pos == position)
	        {
                boolean upvoted = songsUpvoted.contains(id);
                boolean downvoted = songsDownvoted.contains(id);
                upvote.setVisibility(View.VISIBLE);
                downvote.setVisibility(View.VISIBLE);
                upvote.setEnabled(!(upvoted || downvoted));
                downvote.setEnabled(!(upvoted || downvoted));
                upvotes.setVisibility(View.VISIBLE);
	        	downvotes.setVisibility(View.VISIBLE);
	        	if(upvoted)
                {
                    vote.setText("Upvoted");
                    vote.setVisibility(View.VISIBLE);
                    vote.setTextColor(context.getResources().getColor(R.color.GREEN));
                }
                else if(downvoted)
                {
                    vote.setText("Downvoted");
                    vote.setVisibility(View.VISIBLE);
                    vote.setTextColor(context.getResources().getColor(R.color.RED));
                }
	        }
	        else
	        {
	        	upvote.setVisibility(View.GONE);
	        	downvote.setVisibility(View.GONE);
	        	upvotes.setVisibility(View.GONE);
	        	downvotes.setVisibility(View.GONE);
	        	vote.setVisibility(View.GONE);
	        }
	        upvote.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					vote.setText("Upvoted");
					Log.i(tag, "inside on click");
					Log.i(tag, "Got song with id " + Integer.toString(id));
					int upvotesCount = p.getUpvotes();
                    upvote.setEnabled(false);
                    songsUpvoted.add(new Integer(id));
                    downvote.setEnabled(false);
					//send json string to host with upvote request for the song
					
					Log.i(tag, "Incrementing upvotes by sending upvote request to host");
					/*song.setUpvotes(upvotesCount + 1);
					Log.i(tag, "upvotes incremented");
					db.updateSong(id, song.getStatus(),song.getUpvotes(),song.getDownvotes(),song.getAging());*/
					vote.setVisibility(View.VISIBLE);
					//Log.i(tag, "database updated " + " upvotes " + song.getUpvotes());
					upvotes.setText(Integer.toString(upvotesCount + 1));
					Log.i(tag, "upvotes shown");
					vote.setTextColor(context.getResources().getColor(R.color.GREEN));






                    upvote.setVisibility(View.GONE);
                    downvote.setVisibility(View.GONE);
                    upvotes.setVisibility(View.GONE);
                    downvotes.setVisibility(View.GONE);
                    vote.setVisibility(View.GONE);


                    //JSONArray jsonArray = new JSONArray();
                    JSONObject jsonobject = new JSONObject();
                    try {
                        jsonobject.put("id", p.getID());
                        jsonobject.put("name", p.getName());
                        jsonobject.put("upvotes", p.getUpvotes());
                        jsonobject.put("downvotes", p.getDownvotes());
                        jsonobject.put("type","upvote");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Log.i(tag, jsonobject.toString());

                    streamVotes(jsonobject.toString());





                }
			});
	        
	        downvote.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					vote.setText("Downvoted");
					/*Songs song = db.getSong(id);*/
                    upvote.setEnabled(false);
                    downvote.setEnabled(false);
					//send json string to host with downvote request for the song
					int downvotesCount = p.getDownvotes();
                    songsDownvoted.add(new Integer(id));
//                    receiveSong("SEND_SONG_"+p.getName());
                    //song.setDownvotes(downvotesCount + 1);
					/*db.updateSong(id, song.getStatus(),song.getUpvotes(),song.getDownvotes(),song.getAging());*/
					vote.setVisibility(View.VISIBLE);
					downvotes.setText(Integer.toString(downvotesCount + 1));
					vote.setTextColor(context.getResources().getColor(R.color.RED));


                    upvote.setVisibility(View.GONE);
                    downvote.setVisibility(View.GONE);
                    upvotes.setVisibility(View.GONE);
                    downvotes.setVisibility(View.GONE);
                    vote.setVisibility(View.GONE);


                    //JSONArray jsonArray = new JSONArray();
                    JSONObject jsonobject = new JSONObject();
                    try {
                        jsonobject.put("id", p.getID());
                        jsonobject.put("name", p.getName());
                        jsonobject.put("upvotes", p.getUpvotes());
                        jsonobject.put("downvotes", p.getDownvotes());
                        jsonobject.put("type","downvote");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Log.i(tag, jsonobject.toString());

                    streamVotes(jsonobject.toString());




				}
			});
	     
        }
    return convertView;
}

public void streamVotes(final String votes){



    Thread streamThread = new Thread(new Runnable() {

        @Override
        public void run() {
            int port = 8111;
            final int SOCKET_TIMEOUT = 5000;
            try {
                Socket clientSocket = new Socket();
                clientSocket.bind(null);
                clientSocket.connect((new InetSocketAddress((DeviceDetailFragment.info.groupOwnerAddress.getHostAddress()),port)), SOCKET_TIMEOUT);
                Log.d(tag, "Client socket - " + clientSocket.isConnected());
                OutputStream out_stream = clientSocket.getOutputStream();
                PrintWriter pw = new PrintWriter(out_stream);
                // sent that microphone data has now ended
                Log.d("Streaming", "vote sending " + votes);
                //pw.println("vote_send");
                //pw.flush();
                pw.println(votes);
                pw.flush();
                clientSocket.close();

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

public void setPosition(int position)
{
	pos = position;
}

    public void setList(ArrayList<Songs> songs)
    {
        StringList.clear();
        StringList.addAll(songs);
        Log.i(tag,"a" + Integer.toString(songs.size()) + "b");
        Log.i(tag,"a" + Integer.toString(StringList.size()) + "b");
        try {
            Log.i(tag, songs.get(9).getName());
        }
        catch(Exception e)
        {
            Log.i(tag,e.toString());
        }
        notifyDataSetChanged();
    }
    public void receiveSong(String songName){
//        Log.d(WiFiDirectActivity.TAG, "Intent----------- " + uri);
        Intent serviceIntent = new Intent(getContext(), SongTransferService.class);
        serviceIntent.setAction(SongTransferService.ACTION_SEND_FILE);
  //      serviceIntent.putExtra(FileTransferService.EXTRAS_FILE_PATH, uri.toString());
        serviceIntent.putExtra(SongTransferService.EXTRAS_SONG_NAME,songName);
        serviceIntent.putExtra(SongTransferService.EXTRAS_GROUP_OWNER_ADDRESS,
                DeviceDetailFragment.info.groupOwnerAddress.getHostAddress());
        serviceIntent.putExtra(SongTransferService.EXTRAS_GROUP_OWNER_PORT, 8986);
        getContext().startService(serviceIntent);

    }



}
