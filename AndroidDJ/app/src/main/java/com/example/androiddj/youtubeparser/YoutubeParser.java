package com.example.androiddj.youtubeparser;

import android.util.JsonReader;
import android.util.JsonToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by Ashish Singh on 05-Apr-15.
 */
public class YoutubeParser {


    public List<List<String>> readJsonStream(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            return readResult(reader);
        } finally {
            reader.close();
        }
    }

    //read Items array
    public List<List<String>> readItemsArray(JsonReader reader) throws IOException {
        List<List<String>> items = new ArrayList<>();

        reader.beginArray();
        while (reader.hasNext()) {
            List<String> it=readItem(reader);
            if(it!=null)
            items.add(it);
        }
        reader.endArray();
        return items;
    }

    //read items
    public List<String> readItem(JsonReader reader) throws IOException {
        ArrayList<String> item = new ArrayList<>(5);
        List<String> temp;
        boolean isValid=true;
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            switch (name) {
                case "id":
                    if (reader.peek() == JsonToken.STRING)
                        item.add(0, reader.nextString());
                    else {
                        String s = readVideoId(reader);
                        if (s == null) isValid = false;
                        item.add(0, s);
                    }

                    break;
                case "snippet":
                    temp = readSnippet(reader);
                    item.add(1, temp.get(0));
                    item.add(2, temp.get(1));
                    item.add(3, temp.get(2));
                    item.add(4, temp.get(3));
                    break;
                default:
                    reader.skipValue();
                    break;
            }
        }
        reader.endObject();
        if (isValid)
        return item;
        else return null;
    }

    //parse result
    public List<List<String>> readResult(JsonReader reader) throws IOException {
        //long id = -1;
        //String text = null;
        //User user = null;
        //List geo = null;
        String nextPageToken=null;
        List<List<String>> list = null;
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("nextPageToken")) {
                nextPageToken = reader.nextString();

            } else if (name.equals("items")&& reader.peek() != JsonToken.NULL) {
                list = readItemsArray(reader);
            } else {
                reader.skipValue();
            }
        }
        TaskFragment.nextPageToken=nextPageToken;
        reader.endObject();
        return list;
    }


    // return video id from id object
    public String readVideoId(JsonReader reader) throws IOException {
        String videoId = null;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("videoId")) {
                videoId = reader.nextString();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return videoId;
    }

/*    public long readTotalResults(JsonReader reader) throws IOException {
        long results =0;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("totalResults")) {
                results=reader.nextLong();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return results;
    }*/

    // read url
    public String readUrl(JsonReader reader) throws IOException {
        String url = null;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("url")) {
                url = reader.nextString();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return url;
    }

    //read snippet
    public List<String> readSnippet(JsonReader reader) throws IOException {
        ArrayList<String> snippet = new ArrayList<>(4);
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            switch (name) {
                case "title":
                    snippet.add(0, reader.nextString());
                    break;
                case "thumbnails":
                    List<String> temp;
                    temp = readThumbnails(reader);
                    snippet.add(1, temp.get(0));
                    snippet.add(2, temp.get(1));
                    snippet.add(3, temp.get(2));
                    break;
                default:
                    reader.skipValue();
                    break;
            }
        }
        reader.endObject();
        return snippet;
    }

    //read thumbnails
    public List<String> readThumbnails(JsonReader reader) throws IOException {
        ArrayList<String> thumbnails = new ArrayList<>(3);
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            switch (name) {
                case "default":
                    thumbnails.add(0, readUrl(reader));
                    break;
                case "medium":
                    thumbnails.add(1, readUrl(reader));
                    break;
                case "high":
                    thumbnails.add(2, readUrl(reader));
                    break;
                default:
                    reader.skipValue();
                    break;
            }
        }
        reader.endObject();
        return thumbnails;
    }


}