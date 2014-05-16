package stdata.ibrdtn;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import ibrdtn.api.object.ByteArrayBlockData;
import ibrdtn.api.object.ExtensionBlock;
import ibrdtn.api.object.SDNV;
import ibrdtn.api.object.EID;
import ibrdtn.api.object.Block;
import ibrdtn.api.object.SDNVOutputStream;
import ibrdtn.api.object.Block.Data;
import ibrdtn.api.object.Block.InvalidDataException;

public class TrackingExtensionBlock{
    public static final int type = 193;
    private static final int DEFAULT_INTERVAL = 30;
    // flags: geotracking turned on: 1 - trackhops; 2 - track geo; 4 - tracktimestamp
    private SDNV flags = new SDNV(7);
    // logging interval in seconds
    private SDNV interval = new SDNV(DEFAULT_INTERVAL);
    // count of associated TrackingEntry entries
    private SDNV count = new SDNV(0);
    private List<TrackingExtensionBlockEntry> entries = 
    		new ArrayList<TrackingExtensionBlockEntry>();
    
    //So we "have a" extension block
    //private ExtensionBlock myExtensionBlock = new ExtensionBlock(type);
    
    // Datum.getTrajectoryHead() and then this is just a linked list (previous until null)
    
    public TrackingExtensionBlock(){
    }
    
    public TrackingExtensionBlock(long interval){
    	this.interval = new SDNV(interval);
    }
    
    public TrackingExtensionBlock(Block.Data data){
    	setData(data);
    }
    
    public TrackingExtensionBlock(byte[] data){
    	setData(new ByteArrayBlockData(data));
    }
    
    public ExtensionBlock getExtensionBlock(){
        ExtensionBlock myExtensionBlock = new ExtensionBlock(type, getData());
    	return myExtensionBlock;
    }
    
    public List<TrackingExtensionBlockEntry> getEntries(){
    	return entries;
    }

    public Data getData() {
    	//right now, the getData method doesn't include the entries. This is because,
    	//for now, the only time getData is called is when we're sending a NEW tracking
    	//bundle. In that case, there shouldn't yet be any entries anyway.
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
    
     void setData(Block.Data data){
    	SDNVOutputStream stream = new SDNVOutputStream();
        try {
            data.writeTo(stream);
            stream.flush();
            //every stream should have to have the first three values
            flags = stream.nextSDNV();
            interval = stream.nextSDNV();
            count = stream.nextSDNV();
            for(int i = 0; i<count.getValue(); i++){
            	SDNV type = stream.nextSDNV();
            	// oh, hey, look, terrible style! This should be refactored out of type code.
            	TrackingExtensionBlockEntry nextEntry = null;
            	if(type.equals(TrackingExtensionBlockEntry.HOPTYPE)){
            		 nextEntry = new HopDataBlockEntry();
            		 //the next SDNV should then be a timestamp
            		 nextEntry.setTimestamp(stream.nextSDNV());
            		 // for the eid that comes next, the first value is the length, in bytes
            		 // then the subsequent bytes are the actual data...
            		 SDNV eidLength = stream.nextSDNV(); // note this is probably not really an SDNV
            		 int counter = 0;
            		 byte[] eidBytes = new byte[(int)eidLength.getValue()];
            		 while(counter < eidLength.getValue()){
            			 SDNV next = stream.nextSDNV();
            			 byte[] sdnvBytes = next.getBytes();
            			 for(int j = 0; j<sdnvBytes.length; j++){
            				 eidBytes[counter] = sdnvBytes[j];
            				 counter++;
            			 }
            		 }
            		 ((HopDataBlockEntry)nextEntry).setEID(new String(eidBytes));
            	}
            	else if(type.equals(TrackingExtensionBlockEntry.GEOTYPE)){
            		nextEntry = new GeoDataBlockEntry();
            		//the next SDNV should then be a timestamp
            		nextEntry.setTimestamp(stream.nextSDNV());
            		// next up is the latitude then the longitude
            		((GeoDataBlockEntry)nextEntry).setLatitude(stream.nextSDNV());
            		((GeoDataBlockEntry)nextEntry).setLongitude(stream.nextSDNV());
            	}
            	if(nextEntry != null){
            		entries.add(nextEntry);
            		nextEntry = null;
            	}
            }
        } catch (IOException ioe) {
        	ioe.printStackTrace();
        }
    }
     
    void setData(byte[] data){
    	setData(new ByteArrayBlockData(data));
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

abstract class TrackingExtensionBlockEntry {
	
	protected static SDNV HOPTYPE = new SDNV(43);
	protected static SDNV GEOTYPE = new SDNV(42);
	
	protected SDNV type;
	protected SDNV timestamp;
	
	TrackingExtensionBlockEntry(SDNV type){
		this.type = type;
	}
	
	void setTimestamp(SDNV timestamp){
		this.timestamp = timestamp;
	}
	
	SDNV getTimeStamp(){
		return timestamp;
	}
	
}

class HopDataBlockEntry extends TrackingExtensionBlockEntry {

	private String eid;
	
	HopDataBlockEntry(){
		super(HOPTYPE);
	}
	
	void setEID(String eid){
		this.eid = eid;
	}
	
	String getEID(){
		return eid;
	}
	
	
}

class GeoDataBlockEntry extends TrackingExtensionBlockEntry {
	
	private SDNV latitude;
	private SDNV longitude;
	
	GeoDataBlockEntry(){
		super(GEOTYPE);
	}
	
	void setLatitude(SDNV latitude){
		this.latitude = latitude;
	}
	
	void setLongitude(SDNV longitude){
		this.longitude = longitude;
	}
	
	SDNV getLatitude(){
		return latitude;
	}
	
	SDNV getLongitude(){
		return longitude;
	}
}