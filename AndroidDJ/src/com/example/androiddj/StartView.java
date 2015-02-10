package com.example.androiddj;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

public class StartView extends Activity {
    private String tag = "DJ Debugging";
    LinearLayout list;
    private List<RelativeLayout> List_file;
    private ArrayAdapter<RelativeLayout> listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(tag,"Going to call oncreate");
        super.onCreate(savedInstanceState);
        Log.i(tag, "calling setcontent view");
        setContentView(R.layout.start_view);
        Log.i(tag, "Going to create list_file");
        List_file = new ArrayList<RelativeLayout>();
        Log.i(tag,"Going to create list");
        list = (LinearLayout)findViewById(R.id.listview);

        Log.i(tag,"Going to call create list view");
        CreateListView();
        Log.i(tag,"Finished create list view");
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

    /*
     * This function will create the list view, put this function in files whereever you require
     * any scroll list
    */
    private void CreateListView()
    {
        for(int i=0;i<15;i++)
        {
            RelativeLayout rlt = new CustomLayout(getApplicationContext(),"Song "+Integer.toString(i));
            View ruler = new View(rlt.getContext());
            ruler.setBackgroundColor(Color.LTGRAY);
            list.addView(rlt);
            list.addView(ruler, new ViewGroup.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, 2));
        }
        View ruler = new View(getApplicationContext());
        ruler.setBackgroundColor(Color.LTGRAY);
        list.addView(ruler, new ViewGroup.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, 2));

//        listAdapter = new ArrayAdapter<RelativeLayout>(StartView.this, R.layout.list_item_layout,List_file);
//        list.setAdapter(listAdapter);

//        list.setOnItemClickListener(new OnItemClickListener()
//        {
//            int indexChanged=-1;
//            String initialValue="";
//
//            @Override
//            public void onItemClick(AdapterView<?> parent, final View view, final int position,long id)
//            {
//                TextView song;
//                CustomLayout rlt;
//                //args2 is the listViews Selected index
//                if(indexChanged != -1)
//                {
//                    rlt = (CustomLayout)List_file.get(indexChanged);
//                    rlt.song.setText(initialValue);
//                }
//
//                indexChanged = position;
//                rlt = (CustomLayout)List_file.get(indexChanged);
//                initialValue = (String)rlt.song.getText();
//                rlt.song.setText(initialValue+" is selected");
//
////                listAdapter.notifyDataSetChanged();
//            }
//        });

    }
}
