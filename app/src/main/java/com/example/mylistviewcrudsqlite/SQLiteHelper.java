package com.example.mylistviewcrudsqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

public class SQLiteHelper extends SQLiteOpenHelper {

    //constructor
    SQLiteHelper(Context context,
                 String tarjeta,
                 SQLiteDatabase.CursorFactory factory,
                 int version){
        super(context, tarjeta, factory, version);
    }

    public void queryData (String sql){
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL(sql);
    }

    //insertData
    public void insertData(String tarjeta, String vencimiento, String cupo, String deuda,String nombre, String franquicia, byte[] image){
        SQLiteDatabase database = getWritableDatabase();
        //query to insert record in database table
        String sql = "INSERT INTO RECORD VALUES (NULL, ?, ?, ?, ?, ?, ?, ?)"; //where "RECORD" is table name in data base we will create in mainActivity

        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();

        statement.bindString(1,tarjeta);
        statement.bindString(2,vencimiento);
        statement.bindString(3, cupo);
        statement.bindString(4, deuda);
        statement.bindString(5,nombre);
        statement.bindString(6, franquicia);
        statement.bindBlob(7,image);

        statement.executeInsert();

    }

    //updateData
    public void  updateData (String tarjeta, String vencimiento, String cupo, String deuda, String nombre, String franquicia, byte[] image, int id){
        SQLiteDatabase database = getWritableDatabase();
        //query to update record
        String sql = "UPDATE RECORD SET tarjeta=?, vencimiento=?, cupo=?, deuda=?, nombre=?, franquicia=?, image=? WHERE id=?";

        SQLiteStatement statement = database.compileStatement(sql);

        statement.bindString(1,tarjeta);
        statement.bindString(2,vencimiento);
        statement.bindString(3,cupo);
        statement.bindString(4,deuda);
        statement.bindString(5,nombre);
        statement.bindString(6,franquicia);
        statement.bindBlob(7,image);
        statement.bindDouble(8,(double)id);

        statement.execute();
        database.close();

    }

    //delete Data

    public void  deleteData (int id){
        SQLiteDatabase database = getWritableDatabase();
        //query to delete redord using id
        String sql = "DELETE FROM RECORD WHERE id=?";

        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();
        statement.bindDouble(1,(double)id);

        statement.execute();
        database.close();
    }

    public Cursor getData (String sql) {
        SQLiteDatabase database = getReadableDatabase();
        return database.rawQuery(sql, null);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
