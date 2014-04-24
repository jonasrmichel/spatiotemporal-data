package stdata.ibrdtn;

import java.io.IOException;

import ibrdtn.api.object.ByteArrayBlockData;
import ibrdtn.api.object.ExtensionBlock;
import ibrdtn.api.object.SDNV;
import ibrdtn.api.object.Block;
import ibrdtn.api.object.SDNVOutputStream;
import ibrdtn.api.object.Block.Data;
import ibrdtn.api.object.Block.InvalidDataException;

public class TrackingExtensionBlock{
    public static final int type = 193;
    private static final int DEFAULT_INTERVAL = 30;
    private SDNV flags = new SDNV(0);
    // logging interval in seconds
    private SDNV interval = new SDNV(DEFAULT_INTERVAL);
    // count of associated TrackingEntry entries
    private SDNV count = new SDNV(0);
    //private List<SDNV> entries;
    
    //So we "have a" extension block
    //private ExtensionBlock myExtensionBlock = new ExtensionBlock(type);
    
    // Datum.getTrajectoryHead() and then this is just a linked list (previous until null)
    
    public TrackingExtensionBlock(){
    }
    
    public TrackingExtensionBlock(long interval){
    	this.interval = new SDNV(interval);
    }
    
    public TrackingExtensionBlock(Block.Data data){
    	SDNVOutputStream stream = new SDNVOutputStream();
        try {
            data.writeTo(stream);
            stream.flush();
            flags = stream.nextSDNV();
            interval = stream.nextSDNV();
            count = stream.nextSDNV();
        }
        catch(IOException ioe){
        	ioe.printStackTrace();
        }
    }
    
    public ExtensionBlock getExtensionBlock(){
        ExtensionBlock myExtensionBlock = new ExtensionBlock(type, getData());
    	return myExtensionBlock;
    }

    private Data getData() {
        byte[] data = new byte[flags.length + interval.length + count.length];
        byte[] flagsbytes = flags.getBytes();
        byte[] intervalbytes = interval.getBytes();
        byte[] countbytes = count.getBytes();
        int byteArrayCounter = 0;
        System.arraycopy(flagsbytes, 0, data, byteArrayCounter, flagsbytes.length);
        byteArrayCounter += flagsbytes.length;
        System.arraycopy(intervalbytes, 0, data, byteArrayCounter, intervalbytes.length);
        byteArrayCounter += intervalbytes.length;
        System.arraycopy(countbytes, 0, data, byteArrayCounter, countbytes.length);
        return new ByteArrayBlockData(data);
    }
    
    public SDNV getFlags(){
    	return flags;
    }
    
    public SDNV getInterval(){
    	return interval;
    }
    
    public SDNV getCount(){
    	return count;
    }
 
    
    public String toString(){
    	return "TrackingExtensionBlock: length=" + (flags.length + interval.length + count.length)
                + ", flags=" + Long.toBinaryString(flags.getValue())
                + ", interval=" + interval.getValue()
                + ", count=" + count.getValue();
    }
    
}
