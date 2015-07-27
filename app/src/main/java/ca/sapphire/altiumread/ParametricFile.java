package ca.sapphire.altiumread;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Admin on 25/07/15.
 */

//
//public class ParametricFile {
//    public List<Map<String, Object>> records = new ArrayList<Map<String, Object>>();
//    public String signature;
//
//    public JSONObject getJSON() {
//        return new JSONObject(records.get(0));
//    }
//
//    public List<Map<String, Object>> getRecords() {
//        return records;
//    }
//
//    public boolean load(DocumentInputStream inputStream) throws IOException {
//        records.clear();
//        while (inputStream.available() >= 4) {
//            Map<String, Object> record = readRecord(inputStream);
//            if (record != null && !record.isEmpty()) {
//                records.add(record);
//            }
//        }
//
//        signature = (String) records.remove(0).get("HEADER");
//
////	    buildObjectHierarchy();
//
//        return !records.isEmpty();
//    }
//
//    private void buildObjectHierarchy() {
//
//        System.out.println( "Records:" + records.size() );
//
//        for (int i = records.size() - 1; i > 0; i--) {
//            Map<String, Object> current = records.get(i);
//            String s = (String) current.get("OWNERINDEX");
//            if (s == null || s.isEmpty())
//                s = "0";
//            int ownerIndex = Integer.decode(s);
//
//            records.remove(i);
//
//            Map<String, Object> owner = records.get(ownerIndex);
//
//            System.out.println( "Owner:" + owner.size() );
//            if( owner.size() <= 3 )
//                System.out.println( "2 Owner:" + owner );
//
//
//
//            List<Object> children = (List<Object>) owner.get("children");
//            if (children == null) {
//                children = new ArrayList<Object>();
//                owner.put("children", children);
//            }
//
//            children.add(current);
//        }
//    }
//
//    private Map<String, Object> readRecord(DocumentInputStream inputStream) throws IOException {
//        String line = readLine(inputStream);
//
//        if (line == null) return null;
//
//        Map<String, Object> result = new HashMap<String, Object>();
//
//        String pairs[] = line.split("\\|");
//
////	    System.out.println( "----------" );
//
//        boolean isWire = false;
//
//        for (String pair : pairs) {
//            if (pair.trim().isEmpty()) continue;
//
//            if( pair.trim().equals( "RECORD=27" ) ) {
//                isWire = true;
////  	          System.out.println( pair.trim() );
//
//            }
//
//            String[] data = pair.split("=");
//            if (data.length == 2) {
//                result.put(data[0], data[1]);
//            }
//
//        }
//        return result;
//    }
//
//    private String readLine(DocumentInputStream inputStream) throws IOException {
//        int length = inputStream.readInt();
//        if (length == -1) return null;
//
//        byte[] buffer = new byte[length];
//        inputStream.read(buffer, 0, length);
//        if (buffer[0] == 0) return null;
//
//        return new String(buffer).split("\u0000")[0];
//    }
//
//}
