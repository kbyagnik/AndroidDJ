package com.example.androiddj.database;

public class Users {

    private String _uid;
    private int _count;

    public Users()
    {

    }

    public Users(String uid)
    {
        this._uid=uid;
        this._count=0;
    }

    public Users(String uid,int count)
    {
        this._uid=uid;
        this._count=count;
    }

    //getting the uid of the user
    public String getUID()
    {
        return _uid;
    }

    //Uid setter method
    public void setUID(String uid)
    {
        this._uid = uid;
    }

    public int getCount()
    {
        return _count;
    }

    public void setCount(int count)
    {
        this._count = count;
    }


}
