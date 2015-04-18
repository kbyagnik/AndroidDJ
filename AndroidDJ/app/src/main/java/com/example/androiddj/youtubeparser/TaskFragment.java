package com.example.androiddj.youtubeparser;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import org.apache.http.HttpStatus;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static android.graphics.BitmapFactory.decodeStream;

/**
 * This Fragment manages a single background task and retains
 * itself across configuration changes.
 */
public class TaskFragment extends Fragment {


    public List<videoList> videoLists;
    public static String nextPageToken=null;
    public String q=null;
    private DownloadAsyncTask mDownloadTask=null;
    public boolean newIntent=false;



    /**
     * Callback interface through which the fragment will report the
     * task's progress and results back to the Activity.
     */
    interface TaskCallbacks {

        void onPostExecute();

        void newList();
        void makeToast(String msg);
    }

    private TaskCallbacks mCallbacks;

    public void fetchData(String query) {
        if(mCallbacks!=null)
            mCallbacks.makeToast("Loading");
        this.q=query;
        nextPageToken=null;

        mDownloadTask = (DownloadAsyncTask)new DownloadAsyncTask().execute(query);

    }

    @SuppressWarnings("unchecked")
    private void downloadThumb(){
        new ImageDownloaderTask().execute(videoLists);
    }

    public List<videoList> getDataForListView(String query) {

        newIntent=false;
        if(videoLists==null){
            videoLists=new ArrayList<>();
            videoList item = new videoList();
            item.videoDescription="Connecting...\nPlease Wait";
            videoLists.add(item);
            if(mDownloadTask==null)
                mDownloadTask=(DownloadAsyncTask)new DownloadAsyncTask().execute(q=query);
        }
        return videoLists;
    }

    public void showMore(){
        newIntent=false;
        new DownloadAsyncTask().execute(this.q);

    }


    /**
     * Hold a reference to the parent Activity so we can report the
     * task's current progress and results. The Android framework
     * will pass us a reference to the newly created Activity after
     * each configuration change.
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (TaskCallbacks) activity;
    }

    /**
     * This method will only be called once when the retained
     * Fragment is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retain this fragment across configuration changes.
        setRetainInstance(true);

        // Create and execute the background task.

    }

    /**
     * Set the callback to null so we don't accidentally leak the
     * Activity instance.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    private InputStream downloadUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        return conn.getInputStream();
    }

    class ImageDownloaderTask extends AsyncTask<List<videoList>, Void, Void> {


        @SafeVarargs
        @Override
        protected final Void doInBackground(List<videoList>... params) {
            if(params.length>0) {
                List<videoList> l = params[0];
                int i;
                if (l != null) {
                    int l_size;
                    l_size = l.size() - 1;
                    for (i = 0; i < l_size; i++) {
                        if (l.get(i).bitmap == null)
                            l.get(i).bitmap = downloadBitmap(l.get(i).thumbUrl);
                        publishProgress();
                    }
                    if (nextPageToken == null && l.get(i).bitmap == null)
                        l.get(i).bitmap = downloadBitmap(l.get(i).thumbUrl);
                }
            }
            return null;
        }

        protected Bitmap downloadBitmap(String url) {
            HttpURLConnection urlConnection = null;
            try {
                URL uri = new URL(url);
                urlConnection = (HttpURLConnection) uri.openConnection();
                int statusCode = urlConnection.getResponseCode();
                if (statusCode != HttpStatus.SC_OK) {
                    return null;
                }

                InputStream inputStream = urlConnection.getInputStream();
                if (inputStream != null) {
                    Log.i("stream", "oo");
                    return decodeStream(inputStream);
                }
            } catch (Exception e) {
                if(urlConnection!=null)
                urlConnection.disconnect();
                Log.w("ImageDownloader", "Error downloading image from " + url);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... v){
            if(mCallbacks!=null)
                mCallbacks.onPostExecute();
        }
        @Override
        protected void onPostExecute(Void v) {
            if (mCallbacks != null)
                mCallbacks.onPostExecute();
        }
    }


    class DownloadAsyncTask extends AsyncTask<String, Void, List<List<String>>> {

        @Override
        protected List<List<String>> doInBackground(String... params) {
            String query = params[0];
            List<List<String>> result;
            try {
                YoutubeParser parser = new YoutubeParser();
                if (query.contentEquals("") && nextPageToken==null)
                    result = parser.readJsonStream(downloadUrl((ListViewWithBaseAdapter.URL2+"&key="+DeveloperKey.getKey())));
                else if(query.contentEquals("")&& nextPageToken!=null)
                    result = parser.readJsonStream(downloadUrl(ListViewWithBaseAdapter.URL2+"&pageToken="+nextPageToken+"&key="+DeveloperKey.getKey()));
                else if(nextPageToken!=null)
                    result=parser.readJsonStream(downloadUrl(ListViewWithBaseAdapter.URL + query+"&pageToken="+nextPageToken+"&key="+DeveloperKey.getKey()));
                else
                    result = parser.readJsonStream(downloadUrl((ListViewWithBaseAdapter.URL + query+"&key="+DeveloperKey.getKey())));
                return result;
            } catch (IOException e) {
                Log.e("error", "Downloading Data Failed");
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<List<String>> result) {
            if (videoLists == null || newIntent){
                mCallbacks.newList();
            }

            if(result==null){
                if(mCallbacks!=null){
                    mCallbacks.makeToast("Internet Connection Failed");
                    if(videoLists.size()==1)
                        videoLists.get(0).videoDescription="\nFailed.............\n\nCheck Your Internet Connection";
                    else{
                        videoLists.get(videoLists.size()-1).videoDescription="Failed.. Try Again\n";
                        videoLists.get(videoLists.size()-1).thumbUrl="Show_More_Allowed";
                    }
                    mCallbacks.onPostExecute();
                }
            }else if (result.size()!=0) {
                if (videoLists.size()>0)
                videoLists.remove(videoLists.size()-1);
                List temp;
                for (int i = 0; i < result.size(); i++) {
                    temp = (ArrayList) result.get(i);
                    videoList chapter = new videoList();
                    chapter.videoId = (String) temp.get(0);
                    chapter.videoDescription = (String) temp.get(1);
                    chapter.thumbUrl = (String) temp.get(2);
                    videoLists.add(chapter);
                }
                if(nextPageToken!=null){
                    videoList chapter = new videoList();
                    chapter.videoDescription="Show More\n";
                    chapter.thumbUrl="Show_More_Allowed";
                    videoLists.add(chapter);
                }
                if(mCallbacks!=null)
                    mCallbacks.onPostExecute();
                downloadThumb();
            } else if(mCallbacks != null){
                mCallbacks.makeToast("No Songs Found");
                if(videoLists.size()==1)
                videoLists.get(0).videoDescription="\nNo Songs Found...\n\nRefine your query and search again";
                mCallbacks.onPostExecute();
            }
        }
    }
}