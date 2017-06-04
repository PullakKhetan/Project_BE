package com.example.pullak.a6_final;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Pullak Khetan on 27-May-17.
 */

public class Db_Handler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION=2;
    private static final String DATABASE_NAME="objects.db";
    private static final String TABLE_OBJECTS_LOCATION="objects_location_history";
    private static final String COLUMN_ADDRESS="address";
    private static final String COLUMN_LATITUDE="latitude";
    private static final String COLUMN_LONGITUDE="longitude";
    private static final String COLUMN_DATE="date";
    private static final String COLUMN_TIME="time";
    public Db_Handler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String query="CREATE TABLE " + TABLE_OBJECTS_LOCATION + "(" +
                "_ID INTEGER PRIMARY KEY AUTOINCREMENT,"+
                COLUMN_ADDRESS + " TEXT," +
                COLUMN_LATITUDE + " TEXT," +
                COLUMN_LONGITUDE + " TEXT," +
                COLUMN_DATE + " TEXT," +
                COLUMN_TIME + " TEXT" +
                ");";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " +TABLE_OBJECTS_LOCATION+";");
        onCreate(db);

    }
    public void addLocation(Objects_location ob){
        SQLiteDatabase db=getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(COLUMN_ADDRESS,ob.getAddress());
        values.put(COLUMN_LATITUDE,ob.getLatitude());
        values.put(COLUMN_LONGITUDE,ob.getLongitude());
        values.put(COLUMN_DATE,ob.getDate());
        values.put(COLUMN_TIME,ob.getTime());
       long r= db.insert(TABLE_OBJECTS_LOCATION,null,values);
        if(r>0){
            Log.i("in db_handler","row added");
        }
        db.close();
    }
    public String getLastLocation(String address){
        String loc=null;
        SQLiteDatabase db=getWritableDatabase();
        String query="SELECT "+ COLUMN_LATITUDE +","+ COLUMN_LONGITUDE + " FROM "+TABLE_OBJECTS_LOCATION+" WHERE "+ COLUMN_ADDRESS +"=\""+address+"\";";
        Cursor c=db.rawQuery(query,null);
        boolean check=c.moveToLast();
        if (check) {
            Log.i("in database handler","cursor went to last row");
             loc=c.getString(c.getColumnIndex(COLUMN_LATITUDE))+"$"+c.getString(c.getColumnIndex(COLUMN_LONGITUDE));
        }
        db.close();
        return loc;
    }
}
