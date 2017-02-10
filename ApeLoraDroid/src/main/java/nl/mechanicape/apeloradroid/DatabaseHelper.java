package nl.mechanicape.apeloradroid;

/**
 * Created by rein on 5-2-17.
 */
//import java.text.SimpleDateFormat;
//import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import android.util.Log;

import java.util.ArrayList;

//@TODO - This file needs some refactoring. To many code in one file
//        better split it up...one file per model should be nice
//        but it works for now.
public class DatabaseHelper extends SQLiteOpenHelper {

    // Logcat tag
    private static final String LOG = "DatabaseHelper";

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "lorapedroid";

    // Table Names
    // Common column names
    private static final String TABLE_RXPK= "rxpk";
    private static final String ID   = "id";
    private static final String DATUM= "datum";
    private static final String TMST = "tmst";
    private static final String CHAN = "chan";
    private static final String RFCH = "rfch";
    private static final String FREQ = "freq";
    private static final String STAT = "stat";
    private static final String MODU = "modu";
    private static final String DATR = "datr";
    private static final String CODR = "codr";
    private static final String LSNR = "lsnr";
    private static final String RSSI = "rssi";
    private static final String SIZE = "size";
    private static final String DATA = "data";




    // Table Create Statements
    private static final String CREATE_TABLE_RXPK = "CREATE TABLE "+ TABLE_RXPK + " (" +
            ID +          " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE," +
            DATUM+        " TIMESTAMP NOT NULL," +
            TMST+         " INTEGER NOT NULL, "+
            CHAN +        " INTEGER NOT NULL,"+
            RFCH+         " INTEGER NOT NULL,"  +
            FREQ+         " REAL NOT NULL, "+
            STAT +        " INTEGER NOT NULL," +
            MODU +        " TEXT NOT NULL,"+
            DATR+         " TEXT NOT NULL,"+
            CODR+         " TEXT NOT NULL, "+
            LSNR +        " INTEGER NOT NULL," +
            RSSI+         " INTEGER NOT NULL,"+
            SIZE+         " INTEGER NOT NULL,"+
            DATA+         " BLOB NOT NULL"
            + ")";



    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // creating required tables
        db.execSQL(CREATE_TABLE_RXPK);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RXPK);

        // create new tables
        onCreate(db);
    }



    public long saveRxpk(ContentValues values) {

        long id=0;
        try{
            SQLiteDatabase db = this.getWritableDatabase();
            id = db.insert(TABLE_RXPK, null, values);
        }
        catch(Exception e)
        {

        }
        return id;
    }



    public ArrayList<LoraMessage> getMessages()
    {
        ArrayList<LoraMessage> messages=new ArrayList<LoraMessage>();
        String selectQuery ="SELECT  * FROM " +TABLE_RXPK +" ORDER BY "+DATUM +" ASC"+" LIMIT 1000";
        Log.e(LOG, selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to arr
        if (c.moveToFirst()) {
            do {
                long id=c.getLong(c.getColumnIndex(ID));
                long datum=c.getLong(c.getColumnIndex(DATUM));
                long  tmst=c.getLong(c.getColumnIndex(TMST));;
                int chan=c.getInt(c.getColumnIndex(CHAN));;
                int rfch=c.getInt(c.getColumnIndex(RFCH));;
                float freq=c.getFloat(c.getColumnIndex(FREQ));
                int stat=c.getInt(c.getColumnIndex(STAT));;
                String modu=c.getString(c.getColumnIndex(MODU));;
                String datr=c.getString(c.getColumnIndex(DATR));;
                String codr=c.getString(c.getColumnIndex(CODR));;
                int lsnr=c.getInt(c.getColumnIndex(LSNR));;
                int rssi=c.getInt(c.getColumnIndex(RSSI));;
                int size=c.getInt(c.getColumnIndex(SIZE));;
                String data=new String(c.getBlob(c.getColumnIndex(DATA)));
                messages.add(new LoraMessage(id,datum,tmst,chan,rfch,freq,stat,modu,datr,codr,lsnr,rssi,size,data));
            } while (c.moveToNext());
        }
        c.close();
        return messages;
    }




}

