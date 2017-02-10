package nl.mechanicape.apeloradroid;


import org.apache.commons.lang3.ArrayUtils;

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
        parseRawData(data);
    }

    private void parseRawData(byte[] data)
    {
        PHYPayload=data;

        //parse PHYPayload elements

        MHDR=data[0];
        int MACPayloadLength=(PHYPayload.length-5); //1 bytes for MHDR and 4 for MIC
        MACPayload= ocopy(PHYPayload, 1, MACPayloadLength);
        MIC= ocopy(data, PHYPayload.length - 4, 4);


        //parse MACPayload elements
        FHDR=ocopy(MACPayload, 0, 7); //7 bytes
        FPort=MACPayload[7];
        FRMPayload=ocopy(MACPayload, 8, MACPayload.length - 8);

        //parse FHDR elements
        DevAddr=ocopy(FHDR, 0, 4); //4 bytes
        Fctrl=FHDR[4];  //1 bytes
        Fcnt=ocopy(FHDR, 5, 2);   //2 bytes


    }




//under construction
    public byte[] ocopy(byte[] data,int start,int length)
    {

        //copy only selected subset
        byte[] byteArray=new byte[length];
        for (int i=0; i<length;i++)
        {
          byteArray[i]=data[start+i];
        }
        byte[] reversed=byteArray;
        ArrayUtils.reverse(byteArray);
        return byteArray;


    }



    //hier gaat het fout.. reverse werkt op geen enkele manier
    //krijg semi random data terug......iets met pointers????
    //reverse moet dus beter !!!! maaar hoe?

    public byte[] reverse(byte[] array) {
        byte[] byteArr=new byte[array.length];
        for (int i=0; i<array.length;i++)
        {
            byteArr[i]=array[(array.length-1)-i];
        }
        return byteArr;
    }



}