package stdata.ibrdtn;

import ibrdtn.api.object.Block;
import ibrdtn.api.object.ByteArrayBlockData;
import ibrdtn.api.object.ExtensionBlock;
import ibrdtn.api.object.SDNV;
import ibrdtn.api.object.SDNVOutputStream;
import ibrdtn.api.object.Block.Data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GeoRoutingExtensionBlock {

	public static final int type = 194;
    // flags: currently unused
    private SDNV flags = new SDNV(0);
    // count of associated GeoRouting entries
    private SDNV count = new SDNV(0);
    private List<GeoRoutingExtensionBlockEntry> entries = 
    		new ArrayList<GeoRoutingExtensionBlockEntry>();
    static long defaultMarginOfError = 1048; // 1/1000 * 1024 * 1024 -- approximately equal to 1meter
    
    public GeoRoutingExtensionBlock(){
    }
    
    public void addEntry(long latitude, long longitude){
    	addEntry(latitude, longitude, defaultMarginOfError);
    }
    
    public void addEntry(long latitude, long longitude, long marginOfError){
    	GeoRoutingExtensionBlockEntry toAdd = 
    			new GeoRoutingExtensionBlockEntry(new SDNV(7));
    	toAdd.setLatitude(new SDNV(latitude));
    	toAdd.setLongitude(new SDNV(longitude));
    	toAdd.setMarginOfError(new SDNV(marginOfError));
    	entries.add(toAdd);
    	//this is bad... what's the better way to keep count updated?
    	count = new SDNV(entries.size());
    }
    
    public void addEntry(String eid){
    	GeoRoutingExtensionBlockEntry toAdd =
    			new GeoRoutingExtensionBlockEntry(new SDNV(11));
    	toAdd.setEID(eid);
    	entries.add(toAdd);
    	//this is bad... what's the better way to keep count updated?
    	count = new SDNV(entries.size());
    }
    
    public void addEntry(String eid, long latitude, long longitude){
    	addEntry(eid, latitude, longitude, defaultMarginOfError);
    }
    
    public void addEntry(String eid, long latitude, long longitude, long marginOfError){
    	GeoRoutingExtensionBlockEntry toAdd =
    			new GeoRoutingExtensionBlockEntry(new SDNV(15));
    	toAdd.setLatitude(new SDNV(latitude));
    	toAdd.setLongitude(new SDNV(longitude));
    	toAdd.setEID(eid);
    	toAdd.setMarginOfError(new SDNV(marginOfError));
    	entries.add(toAdd);
    	//this is bad... what's the better way to keep count updated?
    	count = new SDNV(entries.size());
    }
    
    public ExtensionBlock getExtensionBlock(){
        ExtensionBlock myExtensionBlock = new ExtensionBlock(type, getData());
    	return myExtensionBlock;
    }
    
    private int getNumBytes(){
    	int bytes = 0;
    	bytes += flags.length;
    	bytes += count.length;
    	for(GeoRoutingExtensionBlockEntry e : entries){
    		bytes += e.getNumBytes();
    	}
    	return bytes;
    }
    
    private Data getData() {
    	byte[] data = new byte[getNumBytes()];
        byte[] flagsbytes = flags.getBytes();
        byte[] countbytes = count.getBytes();
        int byteArrayCounter = 0;
        System.arraycopy(flagsbytes, 0, data, byteArrayCounter, flagsbytes.length);
        byteArrayCounter += flagsbytes.length;
        System.arraycopy(countbytes, 0, data, byteArrayCounter, countbytes.length);
        byteArrayCounter += countbytes.length;
        for(GeoRoutingExtensionBlockEntry e : entries){
        	byte[] nextEntryBytes = e.getBytes();
        	System.arraycopy(nextEntryBytes, 0, data, byteArrayCounter, nextEntryBytes.length);
        	byteArrayCounter += nextEntryBytes.length;
        }
        return new ByteArrayBlockData(data);
    }
    
    void setData(Block.Data data){
   	 	System.out.println("-----CALLING GEOROUTING.SETDATA-----");
   	 	SDNVOutputStream stream = new SDNVOutputStream();
   	 	try {
   	 		data.writeTo(stream);
   	 		stream.flush();
   	 		flags = stream.nextSDNV();
   	 		System.out.println("flags: " + flags.getValue());
   	 		count = stream.nextSDNV();
   	 		System.out.println("count: " + count.getValue());
   	 		for(int i = 0; i<count.getValue(); i++){
   	 			SDNV entryFlags = stream.nextSDNV();
   	 			System.out.println("entryFlags: " + entryFlags.getValue());
   	 			byte flagsByte = entryFlags.getBytes()[0];
   	 			GeoRoutingExtensionBlockEntry nextEntry = 
   	 					new GeoRoutingExtensionBlockEntry(entryFlags);
   	 			nextEntry.setMarginOfError(stream.nextSDNV());
   	 			System.out.println("margin of error: " + nextEntry.getMarginOfError().getValue());
   	 			if((flagsByte & (byte) 0x08) != 0){
   	 				//then there is eid data
              		 SDNV eidLength = stream.nextSDNV(); // note this is probably not really an SDNV
              		 System.out.println("eid Length: " + eidLength.getValue());
              		 byte[] eidBytes = new byte[(int)eidLength.getValue()];
              		 int counter = 0;
              		 while(counter < eidLength.getValue()){
              			 System.out.println("counter: " + counter);
              			 SDNV next = stream.nextSDNV();
              			 System.out.println("next: " + next.getValue());
              			 byte[] sdnvBytes = next.getBytes();
              			 for(int j = 0; j<sdnvBytes.length; j++){
              				 eidBytes[counter] = sdnvBytes[j];
              				 counter++;
              			 }
              		 }
              		 nextEntry.setEID(new String(eidBytes));
   	 			}
   	 			if((flagsByte & (byte) 0x04) != 0){
   	 				//then there is geo data
   	 				nextEntry.setLatitude(stream.nextSDNV());
   	 				nextEntry.setLongitude(stream.nextSDNV());
   	 			}
           		entries.add(nextEntry);
           	}
       } catch (IOException ioe) {
       		ioe.printStackTrace();
       }
   }
    
   void setData(byte[] data){
   		setData(new ByteArrayBlockData(data));
   }
	
}

class GeoRoutingExtensionBlockEntry {
	
    // flags: required: 1; ordered: 2; geo_required: 4; eid_required: 8
	private SDNV flags = new SDNV(15);
	private SDNV marginOfError = new SDNV(GeoRoutingExtensionBlock.defaultMarginOfError);
	private String eid;
	private SDNV latitude;
	private SDNV longitude;
	
	GeoRoutingExtensionBlockEntry(){
	}
	
	GeoRoutingExtensionBlockEntry(SDNV flags){
		this.flags = flags;
	}
	
	void setEID(String eid){
		this.eid = eid;
	}
	
	String getEID(){
		return eid;
	}
	
	SDNV getMarginOfError(){
		return marginOfError;
	}
	
	void setMarginOfError(SDNV marginOfError){
		this.marginOfError = marginOfError;
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
	
	int getNumBytes(){
		int bytes = 0;
		bytes += flags.length;
		if(marginOfError != null){
			bytes += marginOfError.length;
		}
		if(eid != null){
			bytes += eid.getBytes().length;
		}
		if(latitude != null){
			bytes += latitude.length;
		}
		if(longitude != null){
			bytes += longitude.length;
		}
		return bytes;
	}
	
	byte[] getBytes(){
		byte[] toReturn = new byte[getNumBytes()];
	    byte[] flagsbytes = flags.getBytes();
	    int byteArrayCounter = 0;
        System.arraycopy(flagsbytes, 0, toReturn, byteArrayCounter, flagsbytes.length);
        byteArrayCounter += flagsbytes.length;
	    byte[] marginOfErrorBytes = marginOfError.getBytes();
	    System.out.println("Margin of error: " + marginOfError.getValue());
	    for(int i = 0; i<marginOfErrorBytes.length; i++){
	    	System.out.print(marginOfErrorBytes[i] + " ");
	    }
	    System.out.println();
	    System.arraycopy(marginOfErrorBytes, 0, toReturn, byteArrayCounter, marginOfErrorBytes.length);
	    byteArrayCounter += marginOfErrorBytes.length;
        if(eid != null){
        	byte[] eidBytes = eid.getBytes();
        	System.arraycopy(eidBytes, 0, toReturn, byteArrayCounter, eidBytes.length);
        	byteArrayCounter += eidBytes.length;
        }
        if(latitude != null){
        	byte[] latitudeBytes = latitude.getBytes();
        	System.arraycopy(latitudeBytes, 0, toReturn, byteArrayCounter, latitudeBytes.length);
        	byteArrayCounter += latitudeBytes.length;
        }
        if(longitude != null){
        	byte[] longitudeBytes = longitude.getBytes();
        	System.arraycopy(longitudeBytes, 0, toReturn, byteArrayCounter, longitudeBytes.length);
        	byteArrayCounter += longitudeBytes.length;
        }
		return toReturn;
	}
	
}
