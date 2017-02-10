package nl.mechanicape.apeloradroid;

/**
 * Created by rein on 1-2-17.
 */
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Base64;
import android.util.TimeUtils;
import nl.mechanicape.apeloradroid.DatabaseHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class LoraMessage {
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

    //public int datum= Date
    public long id;
    public long datum;
    public long  tmst;
    public int chan;
    public int rfch;
    public double freq;
    public int stat;
    public String modu;
    public String datr;
    public String codr;
    public int lsnr;
    public int rssi;
    public int size;
    public String dataEncoded;
    public byte[] data;
    public LoraWanRXPK RXPKObject;

    public String name;
    public String description;

    public LoraMessage(JSONObject object)
    {

        try
        {
            JSONArray records=object.getJSONArray(TABLE_RXPK);
            for (int i=0; i<records.length();i++)
            {
                JSONObject fields=records.getJSONObject(i);
                id=0;
                datum= System.currentTimeMillis()/1000;
                tmst= fields.getLong(TMST);
                chan= fields.getInt(CHAN);
                rfch= fields.getInt(RFCH);
                freq= fields.getDouble(FREQ);
                stat= fields.getInt(STAT);
                modu= fields.getString(MODU);
                datr= fields.getString(DATR);
                codr= fields.getString(CODR);
                lsnr= fields.getInt(LSNR);
                rssi= fields.getInt(RSSI);
                size= fields.getInt(SIZE);
                dataEncoded= fields.getString(DATA);
                data= Base64.decode(data,0);
                RXPKObject=new LoraWanRXPK(data);

            }
        }
        catch (JSONException e)
        {

        }

    }

    public LoraMessage(long fid, long fdatum, long ftmst, int fchan, int frfch, float ffreq, int fstat, String fmodu, String fdatr, String fcodr, int flsnr, int frssi, int fsize, String fdata)
    {
                id=fid;
                datum= fdatum;
                tmst=ftmst;
                chan=fchan;
                rfch= frfch;
                freq= ffreq;
                stat= fstat;
                modu= fmodu;
                datr= fdatr;
                codr= fcodr;
                lsnr= flsnr;
                rssi= frssi;
                size= fsize;
                dataEncoded= fdata;
                data= Base64.decode(fdata,0);
                RXPKObject=new LoraWanRXPK(data);
    }



    public ContentValues  getContentValues()
    {
        ContentValues values = new ContentValues();
        values.put(DATUM,datum);
        values.put(TMST,tmst);
        values.put(CHAN,chan);
        values.put(RFCH,rfch);
        values.put(FREQ,freq);
        values.put(STAT,stat);
        values.put(MODU,modu);
        values.put(DATR,datr);
        values.put(CODR,codr);
        values.put(LSNR,lsnr);
        values.put(RSSI,rssi);
        values.put(SIZE,size);
        values.put(DATA,data);
        return values;
    }





}
