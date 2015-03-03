package com.example.androiddj;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.DialogInterface.OnClickListener;
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

class ListViewAdapter1
{
	final List<String> songs;
	final ListView list;
	final Context context;
	
	public ListViewAdapter1(ArrayList<String> songs,ListView list,Context context)
	{		
        this.songs = songs;
        this.list = list;
        this.context = context;
		CreateListView();
	}
	
	public void CreateListView()
    {
		for(int i=0;i<10;i++)
		{
			((List<String>) ((View) songs).getParent()).add("Song"+Integer.toString(i));
		}
		final int indexChanged[] = new int[1];
		final String initialValue[] = new String[1];
		indexChanged[0] = -1;
		initialValue[0] = "";
         list.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1,songs));
         list.setOnItemClickListener(new OnItemClickListener()
           {
                @Override
                public void onItemClick(AdapterView<?> parent, final View view, final int position,long id)
                {
                    //args2 is the listViews Selected index
                	if(indexChanged[0] != -1)
                	{
                		songs.set(indexChanged[0], initialValue[0]);
                	}
                	indexChanged[0] = position;
                	initialValue[0] = songs.get(position);
                	//songs.set(position, songs.get(position) + " is selected");
                	view.animate().setDuration(200).alpha(0).withEndAction(new Runnable() {
                      @Override
                      public void run() {
                        songs.set(position, songs.get(position) + " is selected");
                        ArrayAdapter adapter = (ArrayAdapter) list.getAdapter();
                        adapter.notifyDataSetChanged();
                        view.setAlpha(1);
                      }
                    });
                }
           });
    }
	
}


public class ListViewAdapter extends ArrayAdapter<String>
{
	 
private List<String> StringList;
private Context context;
private int pos;
final String tag = "DJ Debugging";
 
public ListViewAdapter(List<String> StringList, Context ctx,int position) {
    super(ctx, R.layout.listview_content, StringList);
    pos = position;
    this.StringList = StringList;
    this.context = ctx;
}
 
public View getView(final int position, View convertView, ViewGroup parent) {
     
    // First let's verify the convertView is not null
    if (convertView == null) {
        // This a new view we inflate the new layout
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.listview_content, parent, false);
    }
        // Now we can fill the layout with the right values
        final TextView tv = (TextView) convertView.findViewById(R.id.listViewItem);
        String p = StringList.get(position);
        tv.setText(p);
        Log.i(tag,Integer.toString(position) + "    " + Integer.toString(pos));
        Button upvote = (Button)convertView.findViewById(R.id.upvote);
        Button downvote = (Button)convertView.findViewById(R.id.downvote);
        final TextView vote = (TextView)convertView.findViewById(R.id.votedByUser);
        if(pos == position)
        {
        	upvote.setVisibility(View.VISIBLE);
        	downvote.setVisibility(View.VISIBLE);
        	if(vote.getText().length() == 0)
        	{
            	vote.setVisibility(View.GONE);
        	}
        	else
        	{
            	vote.setVisibility(View.VISIBLE);
        	}
        }
        else
        {
        	upvote.setVisibility(View.GONE);
        	downvote.setVisibility(View.GONE);
        	vote.setVisibility(View.GONE);
        }
        upvote.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				vote.setText("Upvoted");
				vote.setVisibility(View.VISIBLE);
				vote.setTextColor(context.getResources().getColor(R.color.GREEN));
			}
		});
        
        downvote.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				vote.setText("Downvoted");
				vote.setVisibility(View.VISIBLE);
				vote.setTextColor(context.getResources().getColor(R.color.RED));
			}
		});

        /*for(int i=0;i<10;i++)
		{
			StringList.add("Song"+Integer.toString(i));
		}*/
     
     
    return convertView;
}

public void setPosition(int position)
{
	pos = position;
}
}