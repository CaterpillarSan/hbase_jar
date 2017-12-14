package myTrace;

import java.io.*;
import java.util.HashMap;

public class NodeClock implements Serializable {

    public HashMap<Long,VectorClock> nc;
    public VectorClock localVC;
    public int epoch;

    public NodeClock(){
        nc = new HashMap<Long,VectorClock>();
        localVC = new VectorClock();
        nc.put(PacerSampling.PID,localVC);
    }

    public NodeClock(int epoch){
        nc = new HashMap<Long,VectorClock>();
        localVC = new VectorClock(epoch);
        nc.put(PacerSampling.PID,localVC);
    }

    public NodeClock(int length, int epoch){
        nc = new HashMap<Long,VectorClock>();
        localVC = new VectorClock(length,epoch);
        nc.put(PacerSampling.PID,localVC);
    }

    public NodeClock(NodeClock obj){
        nc = new HashMap<Long,VectorClock>(obj.nc.size());
        localVC = new VectorClock(obj.localVC);
        nc.put(PacerSampling.PID,localVC);
        max(obj); 
    }

    /******************************************************************/ 
    public boolean isEpoch(){
        if(epoch == Integer.MAX_VALUE) return false;
        else return true;
    }

    /******************************************************************/ 
    public int getEpoch(){
        return epoch;
    }

    public int getEpochTid(){
        return epoch >> 24;
    }

    public Integer get(int index){
        return localVC.get(index);
    }

    /******************************************************************/ 
    public void set(int epoch){
        localVC.set(epoch);
    }

    public void set(int index, Integer element){
        localVC.set(index,element);
    }

    public void setNull(int index){
        localVC.setNull(index);
        if(localVC.NonNullLength==0) localVC = null;
    }

    /******************************************************************/
    public void max(NodeClock node){
        // java8
        // nc2.nc.forEach((key,value) -> {
        //     if(nc.containsKey(key)) nc.get(key).max(value);
        //     else nc.put(key,new VectorClock(value));
        // });
        
        // java6
        for(Long key : node.nc.keySet()){
            VectorClock vc = node.nc.get(key);
            if(nc.containsKey(key)) nc.get(key).max(vc);
            else nc.put(key,new VectorClock(vc));
        }
    }

    /******************************************************************/
    public void printArray(String str){
        System.out.print(str+"<");
        // nc.forEach((key,value) -> {
        //     value.printArray(" ");
        // });
        for (VectorClock vc : nc.values()){
            vc.printArray(" ");
        }
        System.out.print(" >");
    }




}
