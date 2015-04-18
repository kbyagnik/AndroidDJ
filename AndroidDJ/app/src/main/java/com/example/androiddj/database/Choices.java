package com.example.androiddj.database;

public class Choices {

    private String _uuid;
    private int _sid;
    private int _flag; // 0 for add song ,-1 for downvote ,1 for upvote

    public Choices()
    {

    }

    public Choices(String uid,int id,int flag)
    {
        this._uuid=uid;
        this._sid=id;
        this._flag=flag;
    }

    //getting the uid of the user
    public String getUUID()
    {
        return _uuid;
    }

    //Uid setter method
    public void setUUID(String uid)
    {
        this._uuid = uid;
    }

    public int getSID()
    {
        return _sid;
    }

    public void setSID(int id)
    {
        this._sid = id;
    }

    public int getFlag()
    {
        return _flag;
    }

    public void setFlag(int flag)
    {
        this._flag = flag;
    }


}
