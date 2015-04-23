package com.example.androiddj.youtubeparser;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androiddj.R;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeStandalonePlayer;

import java.util.ArrayList;
import java.util.List;


/**
 *
 * Created by Ashish Singh on 05-Apr-15.
 */
public class ListViewWithBaseAdapter extends Activity implements TaskFragment.TaskCallbacks,SearchView.OnQueryTextListener{
    static Activity mActivity;
    public static String URL ="https://www.googleapis.com/youtube/v3/search?videoEmbeddable=true&safeSearch=none&order=relevance&part=snippet&type=video&regionCode=in&videoCategoryId=10&maxResults=10&q=";
    public static String URL2="https://www.googleapis.com/youtube/v3/videos?part=snippet&chart=mostPopular&maxResults=20&regionCode=in&videoCategoryId=10";
    String query="";
    String heading="";
	public CodeLearnAdapter chapterListAdapter;

    private static final String TAG_TASK_FRAGMENT = "task_fragment";

    private TaskFragment mTaskFragment;
    SearchView mSearchView;

    @Override
    public void makeToast(String msg){
        Toast.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void newList(){
        mTaskFragment.videoLists=new ArrayList<>();
        chapterListAdapter.videoListList=mTaskFragment.videoLists;
    }
    @Override
    public void onPostExecute(){
        Log.i("ash", "notify");
        ListViewWithBaseAdapter.this.chapterListAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        setContentView(R.layout.activity_list_view_with_simple_adapter);

        if(savedInstanceState==null) {


            Bundle bundle = getIntent().getExtras();
            if (bundle.getString("query") != null) {
                String temp;
                temp = bundle.getString("query");
                heading=temp;
                temp = temp.replaceAll("\n", " ");
                while (temp.contentEquals("  "))
                    temp = temp.replaceFirst("  ", " ");
                query = temp.replaceAll(" ", "%20");
            }

        }
        else {
            this.heading=savedInstanceState.getString("my_heading");
            this.query=savedInstanceState.getString("my_query");
        }

        FragmentManager fm = getFragmentManager();
        mTaskFragment = (TaskFragment) fm.findFragmentByTag(TAG_TASK_FRAGMENT);

        // If the Fragment is non-null, then it is currently being
        // retained across a configuration change.
            if (mTaskFragment == null) {
                mTaskFragment = new TaskFragment();
                fm.beginTransaction().add(mTaskFragment, TAG_TASK_FRAGMENT).commit();
                mTaskFragment.fetchData(query);
            }

        ActionBar actionBar = getActionBar();
        assert actionBar != null;
        if(heading.contentEquals(""))
            actionBar.setTitle("Showing: Popular");
        else actionBar.setTitle("Showing: \""+heading+"\"");

            chapterListAdapter = new CodeLearnAdapter();

            final ListView codeLearnLessons = (ListView) findViewById(R.id.listView1);
            codeLearnLessons.setAdapter(chapterListAdapter);

            codeLearnLessons.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                        long arg3) {

                    videoList chapter = chapterListAdapter.getCodeLearnChapter(arg2);
                    if (chapter.videoId!=null) {
                        FragmentManager manager = getFragmentManager();
                        YoutubeDialogFragment dialog = new YoutubeDialogFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString(YoutubeDialogFragment.videoId, chapter.videoId);
                        bundle.putString("Description",chapter.videoDescription);
                        dialog.setArguments(bundle);
                        dialog.show(manager, "Dialog");
                    }
                    else if (mTaskFragment!=null && chapterListAdapter.videoListList.get(arg2).thumbUrl!=null && chapterListAdapter.videoListList.get(arg2).thumbUrl.contentEquals("Show_More_Allowed")){
                        chapterListAdapter.videoListList.get(arg2).videoDescription="Loading";
                        chapterListAdapter.videoListList.get(arg2).thumbUrl=null;
                        chapterListAdapter.notifyDataSetChanged();
                        mTaskFragment.showMore();
                    }

                }
            });


    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        savedInstanceState.putString("my_query", this.query);
        savedInstanceState.putString("my_heading", this.heading);
        // etc.
    }
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.
        this.query = savedInstanceState.getString("my_query");
        this.heading = savedInstanceState.getString("my_heading");
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        heading=query;
        ActionBar actionBar= getActionBar();
        assert actionBar!=null;
        actionBar.setTitle("Showing: \""+heading+"\"");
        query = query.replaceAll("\n", " ");
        while (query.contentEquals("  "))
           query = query.replaceFirst("  ", " ");
        query = query.replaceAll(" ", "%20");
        this.query=query;
        mTaskFragment.videoLists=new ArrayList<>();
        videoList v = new videoList();
        v.videoDescription="Connecting...\nPlease Wait";
        chapterListAdapter.videoListList=mTaskFragment.videoLists;
        chapterListAdapter.notifyDataSetChanged();
        mTaskFragment.videoLists.add(v);
        mTaskFragment.newIntent=true;
        mTaskFragment.fetchData(this.query);
        Log.i("submit", "query");
        mSearchView.clearFocus();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    class CodeLearnAdapter extends BaseAdapter {

            public List<videoList> videoListList = mTaskFragment.getDataForListView(query);
            int height, width;

            public CodeLearnAdapter() {
                DisplayMetrics metrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(metrics);
                height = metrics.heightPixels;
                width = metrics.widthPixels;
            }

            @Override
            public int getCount() {
                return videoListList.size();
            }

            @Override
            public videoList getItem(int arg0) {
                return videoListList.get(arg0);
            }

            @Override
            public long getItemId(int arg0) {
                return arg0;
            }

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @SuppressWarnings("deprecation")
            @Override
            public View getView(int arg0, View arg1, ViewGroup arg2) {
                if (arg1 == null) {
                    LayoutInflater inflater = (LayoutInflater) ListViewWithBaseAdapter.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    arg1 = inflater.inflate(R.layout.listitem, arg2, false);
                }
                TextView chapterDesc = (TextView) arg1.findViewById(R.id.textView1);
                ImageView image = (ImageView) arg1.findViewById(R.id.imageView1);
                videoList chapter = videoListList.get(arg0);
                chapterDesc.setText(chapter.videoDescription);
                if (chapter.bitmap != null) {
                    image.setVisibility(View.VISIBLE);
                    chapterDesc.setTextColor(Color.WHITE);
                    int sdk = android.os.Build.VERSION.SDK_INT;
                    if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        arg1.setBackgroundDrawable(getResources().getDrawable(R.drawable.list_item_background));
                    } else {
                        arg1.setBackground(getResources().getDrawable(R.drawable.list_item_background));
                    }
                    //arg1.setBackgroundColor(Color.WHITE);
                    image.setImageBitmap(Bitmap.createScaledBitmap(chapter.bitmap, (int) (width * 0.4), (int) (width * 0.4 * 9 / 16), false));
                }else if(chapter.videoId==null){
                    if(chapter.videoDescription.contentEquals("Loading"))
                    {
                        arg1.setBackgroundColor(Color.GREEN);
                       chapterDesc.setTextColor(Color.BLACK);
                    }
                    else{
                    chapterDesc.setTextColor(Color.WHITE);
                    arg1.setBackgroundColor(getResources().getColor(R.color.dark_blue));
                    }
                    image.setVisibility(View.GONE);
                }
                else{
                    image.setVisibility(View.VISIBLE);
                    chapterDesc.setTextColor(Color.WHITE);
                    int sdk = android.os.Build.VERSION.SDK_INT;
                    if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        arg1.setBackgroundDrawable(getResources().getDrawable(R.drawable.list_item_background));
                    } else {
                        arg1.setBackground(getResources().getDrawable(R.drawable.list_item_background));
                    }

                    image.setBackgroundColor(Color.BLACK);
                    Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.loading);
                    image.setImageBitmap(Bitmap.createScaledBitmap(b, (int) (width * 0.4), (int) (width * 0.4 * 9 / 16), false));
                }
                return arg1;
            }

            public videoList getCodeLearnChapter(int position) {
                return videoListList.get(position);
            }
        }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.youtube_search);
        mSearchView = (SearchView) searchItem.getActionView();
        mSearchView.setOnQueryTextListener(this);
        mSearchView.clearFocus();
        mSearchView.setQueryHint("Enter query");
        mSearchView.setSubmitButtonEnabled(true);
        return super.onCreateOptionsMenu(menu);
    }


    //

        private static final int REQ_START_STANDALONE_PLAYER = 1;
        private static final int REQ_RESOLVE_SERVICE_MISSING = 2;
        private static final String DEVELOPER_KEY="AIzaSyAniiilDhSSjvOCNEGge7TakYkOaCqTtZg";
        //@Override
        public static void play(String VIDEO_ID) {
            //super.onCreate(savedInstanceState);
           // final Bundle bundle = getIntent().getExtras();
            //final String VIDEO_ID =bundle.getString("videoId");
            final int startTimeMillis=0;
            final boolean autoplay=true;
            final boolean lightboxMode=true;
           // setContentView(R.layout.standalone_player);
            Intent intent = YouTubeStandalonePlayer.createVideoIntent(
                    mActivity, DEVELOPER_KEY, VIDEO_ID, startTimeMillis, autoplay, lightboxMode);
            if (intent != null) {
                if (canResolveIntent(intent)) {
                    mActivity.startActivityForResult(intent, REQ_START_STANDALONE_PLAYER);
                } else {
                    // Could not resolve the intent - must need to install or update the YouTube API service.
                    YouTubeInitializationResult.SERVICE_MISSING
                            .getErrorDialog(mActivity, REQ_RESOLVE_SERVICE_MISSING).show();
                }
            }
        }




        private static boolean canResolveIntent(Intent intent) {
            List<ResolveInfo> resolveInfo = mActivity.getPackageManager().queryIntentActivities(intent, 0);
            return resolveInfo != null && !resolveInfo.isEmpty();
        }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_START_STANDALONE_PLAYER && resultCode != RESULT_OK) {
            YouTubeInitializationResult errorReason =
                    YouTubeStandalonePlayer.getReturnedInitializationResult(data);
            if (errorReason.isUserRecoverableError()) {
                errorReason.getErrorDialog(this, 0).show();
            } else {
                String errorMessage =
                        String.format(getString(R.string.error_player), errorReason.toString());
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
            }
        }
    }


}
