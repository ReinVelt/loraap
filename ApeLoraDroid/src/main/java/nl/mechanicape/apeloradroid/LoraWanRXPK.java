package nl.mechanicape.apeloradroid;


import android.util.Base64;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by rein on 8-2-17.
 */
public class LoraWanRXPK {
    public byte[] PHYPayload;
    public byte   MHDR; //1 byte
    public byte[] MACPayload; //unknown bytes
    public byte[] MIC; //4 bytes

    public byte[] FHDR; //7 bytes
    public byte   FPort; //1 byte
    public byte[] FRMPayload;

    public byte[] DevAddr; //4 bytes
    public byte   Fctrl;  //1 bytes
    public byte[] Fcnt;   //2 bytes

    /*private macheader=bindata[0];
    private devaddr=(1+bindata[4]<<24)+(bindata[3]<<16)+(1+bindata[2]<<8)+(bindata[1]<<0);
    private fctrl=bindata[5];
    private fcounter = (bindata[7] << 8) + bindata[6];
    private fport=bindata[8];
    byte[] mic={ bindata[bindata.length-1],bindata[bindata.length-2],bindata[bindata.length-3],bindata[bindata.length-4]};
*/

    public  LoraWanRXPK(byte[] data)
    {
        byte[] base64DecodedData;
        try {
            base64DecodedData=Base64.decode(data, 0);
        }
        catch (Exception e)
        {
            base64DecodedData=data;
        }
        parseRawData(base64DecodedData);
    }

    private void parseRawData(byte[] data)
    {
        PHYPayload=data;

        //parse PHYPayload elements

        MHDR=data[0];
        int MACPayloadLength=(PHYPayload.length-5); //1 bytes for MHDR and 4 for MIC
        MACPayload= byteCopy(PHYPayload, 1, MACPayloadLength);
        MIC= byteCopy(data, PHYPayload.length - 4, 4);
        //parse MACPayload elements
        FHDR=byteCopy(MACPayload, 0, 7); //7 bytes
        FPort=MACPayload[7];
        FRMPayload=byteCopy(MACPayload, 8, MACPayload.length - 8);

        //parse FHDR elements
        DevAddr=reverse(byteCopy(FHDR, 0, 4)); //4 bytes
        Fctrl=FHDR[4];  //1 bytes
        Fcnt=reverse(byteCopy(FHDR, 5, 2));   //2 bytes


    }

    public byte[] byteCopy(byte[] data,int start,int length)
    {
        //copy selected subset
        byte[] byteArray=new byte[length];
        for (int i=0; i<length;i++)
        {
            byteArray[i]=data[start+i];
        }
        return byteArray;
    }

    public static byte[] reverse(byte[] array) {
        byte[] byteArr=new byte[array.length];
        for (int i=0; i<array.length;i++)
        {
            byteArr[i]=array[(array.length-1)-i];
        }
        return byteArr;
    }

    /*public static byte[] payloadDecode(byte[] rawData)
    {

        int[] intKey = {0x2b,0x7e,0x15,0x16,0x28,0xae,0xd2,0xa6,0xab,0xf7,0x15,0x88,0x09,0xcf,0x4f,0x3c};
        ByteBuffer byteBuffer = ByteBuffer.allocate(intKey.length * 4);
        IntBuffer intBuffer = byteBuffer.asIntBuffer();
        intBuffer.put(intKey);
        byte[] rawKey = byteBuffer.array();

        //SecretKeySpec skeySpec = new SecretKeySpec(rawKey, "AES");
        //Cipher cipher = Cipher.getInstance("AES");
        //cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        //return cipher.doFinal(rawData);

    }*/


}