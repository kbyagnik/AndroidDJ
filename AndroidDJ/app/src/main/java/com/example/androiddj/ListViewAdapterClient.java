package com.example.androiddj;

import android.text.TextUtils.TruncateAt;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androiddj.database.DatabaseHandler;
import com.example.androiddj.database.Songs;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

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
private HashSet<Integer> songsDownloaded;

    public ListViewAdapterClient(List<Songs> StringList, Context ctx,int position,DatabaseHandler db) {
    super(ctx, R.layout.listview_content, StringList);
    Log.i(tag, "Inside list view adapter constructor");
    pos = position;
    this.StringList = StringList;
    this.context = ctx;
    this.client = (ClientView) ctx;
    this.db = db;
    songsUpvoted = new HashSet<>();
    songsDownvoted = new HashSet<>();
    songsDownloaded = new HashSet<>();

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
            name.setSelected(true);
            name.setEllipsize(TruncateAt.MARQUEE);
            name.setSelected(true);
            name.setSingleLine(true);

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
            final Button saveSong = (Button) convertView.findViewById(R.id.saveSong);



            saveSong.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Songs song = StringList.get(position);

                    Toast.makeText(context,"Saving song - "+song.getName(),Toast.LENGTH_SHORT);
                    Log.d(tag, "save song clicked: Song name - " + song.getName() +
                            " Song id - " + song.getID());
                    songsDownloaded.add(song.getID());
//                    saveSong.setEnabled(false);
//                    saveSong.setAlpha((float)0.5);
                    receiveSong(song);
                }
            });

	       /*if(pos == position)
	        {*/
            boolean upvoted = songsUpvoted.contains(id);
            boolean downvoted = songsDownvoted.contains(id);
            if(upvoted)
            {
                    /*vote.setVisibility(View.VISIBLE);*/
                upvote.setAlpha((float)0.5);
                upvote.setEnabled(false);
                downvote.setEnabled(false);
                downvote.setAlpha((float)0.25);
                for(int i=0;i<StringList.size();i++)
                {
                    if(StringList.get(i).getID() == id)
                    {
                        Log.i("voting",StringList.get(i).getName() + " " + Integer.toString(StringList.get(i).getID()));
                    }
                }
            }
            else if(downvoted)
            {
                upvote.setEnabled(false);
                downvote.setEnabled(false);
                upvote.setAlpha((float)0.25);
                downvote.setAlpha((float)0.5);
                    /*vote.setVisibility(View.VISIBLE);*/
            }
            else
            {
                upvote.setEnabled(true);
                downvote.setEnabled(true);
                upvote.setAlpha((float)1.0);
                downvote.setAlpha((float)1.0);
            }
            if(songsDownloaded.contains(id))
            {
                saveSong.setEnabled(false);
                saveSong.setAlpha((float)0.5);
            }
	        /*}
	        else
	        {
	        	upvote.setVisibility(View.GONE);
	        	downvote.setVisibility(View.GONE);
	        	upvotes.setVisibility(View.GONE);
	        	downvotes.setVisibility(View.GONE);
	        	vote.setVisibility(View.GONE);
	        }*/
            upvote.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Log.i(tag, "inside on click");
                    Log.i(tag, "Got song with id " + Integer.toString(id));
                    int upvotesCount = p.getUpvotes();
                    upvote.setEnabled(false);
                    songsUpvoted.add(new Integer(id));
                    Log.i("voting","up" + id);
                    Log.i("voting",songsUpvoted.toString());
                    for(int i=0;i<StringList.size();i++)
                    {
                        Log.i("voting",StringList.get(i).getName() + " " + Integer.toString(StringList.get(i).getID()));
                    }
                   downvote.setEnabled(false);
                    upvote.setAlpha((float)0.5);
                    downvote.setAlpha((float)0.25);
                    p.setUpvotes(upvotesCount + 1);
                    //send json string to host with upvote request for the song

                    Log.i(tag, "Incrementing upvotes by sending upvote request to host");
					/*song.setUpvotes(upvotesCount + 1);
					Log.i(tag, "upvotes incremented");
					db.updateSong(id, song.getStatus(),song.getUpvotes(),song.getDownvotes(),song.getAging());*/
                    //Log.i(tag, "database updated " + " upvotes " + song.getUpvotes());
                    upvotes.setText(Integer.toString(upvotesCount + 1));
                    Log.i(tag, "upvotes shown");
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
					/*Songs song = db.getSong(id);*/
                    upvote.setEnabled(false);
                    downvote.setEnabled(false);
                    upvote.setAlpha((float)0.25);
                    downvote.setAlpha((float)0.5);
                    //send json string to host with downvote request for the song
                    int downvotesCount = p.getDownvotes();
                    songsDownvoted.add(new Integer(id));
                    Log.i("voting","down" + id);
//                    receiveSong("SEND_SONG_"+p.getName());
                    p.setDownvotes(downvotesCount + 1);
					/*db.updateSong(id, song.getStatus(),song.getUpvotes(),song.getDownvotes(),song.getAging());*/
                    downvotes.setText(Integer.toString(downvotesCount + 1));
                    p.setDownvotes(downvotesCount + 1);
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
    public void receiveSong(Songs song){
//        Log.d(WiFiDirectActivity.TAG, "Intent----------- " + uri);
        Intent serviceIntent = new Intent(getContext(), SongTransferService.class);
        serviceIntent.setAction(SongTransferService.ACTION_SEND_FILE);
  //      serviceIntent.putExtra(FileTransferService.EXTRAS_FILE_PATH, uri.toString());
        serviceIntent.putExtra(SongTransferService.EXTRAS_SONG_ID,song.getID());
        serviceIntent.putExtra(SongTransferService.EXTRAS_SONG_NAME,song.getName());
        serviceIntent.putExtra(SongTransferService.EXTRAS_GROUP_OWNER_ADDRESS,
                DeviceDetailFragment.info.groupOwnerAddress.getHostAddress());
        serviceIntent.putExtra(SongTransferService.EXTRAS_GROUP_OWNER_PORT, 8986);
        getContext().startService(serviceIntent);

    }



}


