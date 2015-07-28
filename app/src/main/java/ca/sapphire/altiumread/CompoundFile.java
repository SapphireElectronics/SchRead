package ca.sapphire.altiumread;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Admin on 25/07/15.
 */
public class CompoundFile {
    BufferedInputStream in;
    boolean littleEndian = true;

    public final static String TAG = "CompoundFile";

    Header header;

    int[] sat;
    ArrayList<byte[]> sector = new ArrayList<byte[]>();
    ArrayList<byte[]> shortSector = new ArrayList<byte[]>();

    public CompoundFile( String fileName ) {
        try {
            in = new BufferedInputStream( new FileInputStream( fileName ));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        // Read HEADER
        // header is always 512 bytes
        byte[] buffer = new byte[512];
        readNextSector( buffer, 512 );

        header = new Header();
        if( header.read( buffer ) < 0 ) {
            Log.i(TAG, "Unable to validate the header entry.");
            return;
        }


        Log.i( TAG, "Total sectors: " + header.numberOfSectors );
        Log.i( TAG, "Total short sectors: " + header.numberOfShortSectors );
        Log.i( TAG, "Total sectors in table: " + header.masterNumberOfSectors );

        Log.i( TAG, "Directory sector: " + header.directorySectorID );
        Log.i( TAG, "Short sector: " + header.shortSectorID );
        Log.i( TAG, "Master sector: " + header.masterSectorID );


        // Read MSAT
        // currently only 109 entries are supported (entries are stored in Header)
        if( header.numberOfSectors > 109 || header.masterSectorID != -2 ) {
            Log.i( TAG, "Too many Master Sectors to read.");
            return;
        }


        // Read sectors
        int sectorBytes = 1 << header.sectorSize;

        for (int i = 0; i < header.numberOfSectors; i++) {
            byte[] newSector = new byte[sectorBytes];
            readNextSector( newSector, sectorBytes );
            sector.add( newSector );
        }
        Log.i(TAG, "Read in " + header.numberOfSectors + " sectors.");

        // Read SAT
        // only one sector to read, generally Sector 0
        sat = new int[sectorBytes/4*header.numberOfSectors];
//        byte[] satBuffer = sector.get(0);

        for (int j = 0; j < header.numberOfSectors; j++) {
            byte[] satBuffer = sector.get(j);
            for (int i = 0; i < sectorBytes / 4; i++) {
                sat[i+j*sectorBytes/4] = getInt(satBuffer, i * 4);
            }
        }


        // Read SSAT


        // Read Directory
        // Follow the Directory chain in the SAT until we get the value -2

        Directory dir = new Directory();
        int currentSector = header.directorySectorID;

        while( currentSector != -2 ) {
            dir.read( sector.get( currentSector), 0 );
            // do the actual sector read here.
            currentSector = sat[currentSector];
        }


        Log.i(TAG, "Read first Dir sector.");

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

    public void getChars( byte[] buffer, int index, char[] charBuffer, int length )
    {
        for (int i = 0; i < length; i++) {
            charBuffer[i] = (char) (buffer[i*2] + ( buffer[i*2+1] << 8) );
        }
    }

    public class Header {
        public final byte[] fileID = new byte[]{-48, -49, 17, -32, -95, -79, 26, -31};  // d0 cf 11 e0 a1 b1 1a e1
        public byte[] headerID = new byte[8];
        public boolean littleEndian;
        short sectorSize;
        short shortSectorSize;
        int numberOfSectors;
        int directorySectorID;
        int standardStreamSize;
        int shortSectorID;
        int numberOfShortSectors;
        int masterSectorID;
        int masterNumberOfSectors;
        public int[] msat = new int[109];

        public int read(byte[] buffer) {
            // see if it's actually a Compound Document File
            System.arraycopy(buffer, 0, headerID, 0, 8);

            if (!Arrays.equals(headerID, fileID)) {
                Log.i(TAG, "Not a Compound Document File.  ID did not match.");
                for (int i = 0; i < 8; i++)
                    Log.i(TAG, "ID: " + i + (int) buffer[i]);
                return -1;
            }

            // Ignore UID, Revision and Version                             // offset 8, size 16+2+2 = 20
            // get the Endian bytes                                         // offset 28, size 2
            littleEndian = (buffer[28] == (byte) 0xfe) && (buffer[29] == (byte) 0xff);
            sectorSize = getShort(buffer, 30);                  // offset 30, size 2
            shortSectorSize = getShort(buffer, 32);             // offset 32, size 2
            // ignore 10                                                    // offset 34, size 10
            numberOfSectors = getInt(buffer, 44);                 // offset 44, size 4
            directorySectorID = getInt(buffer, 48);               // offset 48, size 4
            // ignore 4                                                     // offset 52, size 4
            standardStreamSize = getInt(buffer, 56);              // offset 56, size 4
            shortSectorID = getInt(buffer, 60);                   // offset 60, size 4
            numberOfShortSectors = getInt(buffer, 64);            // offset 64, size 4
            masterSectorID = getInt(buffer, 68);                  // offset 68, size 4
            masterNumberOfSectors = getInt(buffer, 72);           // offset 72, size 4

            for (int i = 0; i < 109; i++) {
                msat[i] = getInt(buffer, 76 + (i * 4));
            }
            return 0;
        }
    }

    public class Directory {
        public char[] name = new char[32];
        public short nameSize;
        public byte type;
        public byte colour;
        public int leftDirID;
        public int rightDirID;
        public int rootDirID;
        public byte[] uniqueID = new byte[16];
        public int flags;
        public byte[] timeStampCreation = new byte[8];
        public byte[] timeStampModification = new byte[8];
        public int sectorID;
        public int streamSize;
        public int unused;

        public void read( byte[] buffer, int index) {
            getChars( buffer, index, name, 32 );
            nameSize = getShort( buffer, index + 64 );
            type = buffer[index+66];
            colour = buffer[index+67];
            leftDirID = getInt( buffer, index+68 );
            rightDirID = getInt( buffer, index+72 );
            rootDirID = getInt( buffer, index+76 );
            System.arraycopy( buffer, index+80, uniqueID, 0, 16 );
            flags = getInt( buffer, index+96 );
            System.arraycopy( buffer, index+100, timeStampCreation, 0, 8 );
            System.arraycopy( buffer, index+108, timeStampModification, 0, 8 );
            sectorID = getInt( buffer, index+116 );
            streamSize = getInt( buffer, index+120 );
            unused = getInt( buffer, index+124 );
            // total of 128 bytes
        }
    }
}
