package ca.sapphire.altiumread;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Admin on 25/07/15.
 */
public class CompoundFile {
    BufferedInputStream in;
    boolean littleEndian = true;

    public final static String TAG = "CompoundFile";

    byte[] header = new byte[512];
    ArrayList<byte[]> sectors = new ArrayList<byte[]>();

    public CompoundFile( String fileName ) {
        try {
            in = new BufferedInputStream( new FileInputStream( fileName ));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        // header is always 512 bytes
        readNextSector( header, 512 );

        byte[] fileID = new byte[] {-48,-49,17,-32,-95,-79,26,-31};  // d0 cf 11 e0 a1 b1 1a e1

        // see if it's actually a Compound Document File
        byte[] headerID = new byte[8];
        System.arraycopy( header, 0, headerID, 0, 8 );

        if( !Arrays.equals( headerID, fileID )) {
            Log.i( TAG, "Not a Compound Document File.  ID did not match.");
            for (int i = 0; i < 8; i++)
                Log.i( TAG, "ID: " + i + (int) header[i]  );
            return;
        }

        // Ignore UID, Revision and Version                     // offset 8, size 16+2+2 = 20
        // get the Endian bytes                                 // offset 28, size 2
        littleEndian = true;
        if( header[28]==(byte) 0xff && (byte) header[29]==0xfe )
            littleEndian = false;
        short sectorSize = getShort(header, 30);                // offset 30, size 2
        short shortSectorSize = getShort(header, 32);           // offset 32, size 2
        // ignore 10                                            // offset 34, size 10
        int numberOfSectors = getInt(header, 44);               // offset 44, size 4
        int directorySectorID = getInt(header, 48);             // offset 48, size 4
        // ignore 4                                             // offset 52, size 4
        int standardStreamSize = getInt(header, 56);            // offset 56, size 4
        int shortSectorID = getInt(header, 60);                 // offset 60, size 4
        int numberOfShortSectors = getInt(header, 64);          // offset 64, size 4
        int masterSectorID = getInt(header, 68);                // offset 68, size 4
        int masterNumberOfSectors = getInt(header, 72);         // offset 72, size 4

        Log.i( TAG, "Total sectors: " + numberOfSectors );
        Log.i( TAG, "Total short sectors: " + numberOfShortSectors );
        Log.i( TAG, "Total sectors in table: " + masterNumberOfSectors );

        Log.i( TAG, "Directory sector: " + directorySectorID );
        Log.i( TAG, "Short sector: " + shortSectorID );
        Log.i( TAG, "Master sector: " + masterSectorID );

        int a = 2^3;

        byte[] buffer = new byte[sectorSize];
        for (int i = 0; i < numberOfSectors; i++) {
            readNextSector( );

        }


    }

    public void readNextSector( byte[] buffer, int bytes ) {
        try {
            in.read( buffer, 0, bytes );
        } catch (IOException e) {
            e.printStackTrace();
            Log.i(TAG, "readNextSector: unable to read in a full sector.");
        }

    }

    public short getShort() {
        try {
            if( littleEndian )
                return (short)( (in.read()) | (in.read() << 8) );
            else
                return (short) ( (in.read() << 8) | (in.read()) );
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public short getShort( byte[] buffer, int index ) {
        if( littleEndian )
            return (short)( (buffer[index]) | (buffer[index+1] << 8) );
        else
            return (short)( (buffer[index+1]) | (buffer[index] << 8) );
    }

    public int getInt() {
        try {
            if( littleEndian )
                return (in.read()) | (in.read() << 8) | (in.read() << 16) | (in.read() << 8);
            else
                return (in.read() << 24) | (in.read() << 16) | (in.read() << 8) | (in.read());
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int getInt( byte[] buffer, int index ) {
        if( littleEndian )
            return (buffer[index]) | (buffer[index+1] << 8) | (buffer[index+2] << 16) | (buffer[index+3] << 8);
        else
            return (buffer[index+3]) | (buffer[index+2] << 8) | (buffer[index+1] << 16) | (buffer[index] << 8);
    }
}
