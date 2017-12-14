package myTrace;
import java.util.ArrayList;

import java.io.*;

public class VectorClock implements Serializable {
    
    public ArrayList<Integer> vc;
    public int NonNullLength;

    public VectorClock(){
        vc = new ArrayList<Integer>();
        // int i = ThreadState.getThreadNum();
        // extendArray(ThreadState.getTid());
        NonNullLength = vc.size();
    }

    public VectorClock(int epoch){
        vc = new ArrayList<Integer>();
        vc.add(epoch);
        NonNullLength = 1;
    }

    public VectorClock(int length, int epoch){
        vc = new ArrayList<Integer>();
        extendArray(length);
        set(epoch);
        NonNullLength = length;
    }

    public VectorClock(VectorClock obj){
        vc = new ArrayList<Integer>(obj.vc);
        this.NonNullLength = obj.getNonNullLength();
    }

    /******************************************************************/ 
    public int size(){
        return vc.size();
    }

    public boolean isEpoch(){
        if(vc.size()==1) return true;
        else    return false;
    }

    /******************************************************************/ 
    public int getEpoch(){
        return vc.get(0);
    }

    // index = 0以外で使うことないな...
    public int getTid(int index){
        return vc.get(index) >> 24;
    }

    public Integer get(int index){
        extendArray(index);
        return vc.get(index);
    }

    public int getNonNullLength(){
        return NonNullLength;
    }
    /******************************************************************/ 
    public void set(int epoch){
        set(epoch >> 24,epoch);
    }

    public void set(int index,Integer element){
        extendArray(index);
        vc.set(index,element);
    }

    public void setNull(int index){
        if(vc.get(index)==null) return;
        set(index,null);
        NonNullLength--;
    }
    /******************************************************************/
    public void extendArray(int expectIndex){
        int start = vc.size();
        /*
         * vc = [1,2,3] vc.size() = 3
         * if (expectIndex == 2)  return
         * if (expectIndex == 3)  vcサイズを1拡張
         */
        if(start > expectIndex)     return;
        for (int i = start; i <= expectIndex; i++) {
            vc.add(i,i<<24);
        }
    }

    // TODO max取れてない問題
    public void max(VectorClock vc2){
        extendArray(vc2.size()-1);
        for (int i = 0; i<vc.size(); i++) {
            vc.set(i,Math.max(this.get(i),vc2.get(i)));
        }
    }
    /******************************************************************/
    public void printArray(String arrayName){
        System.out.print(arrayName+"[ ");
        vc.forEach(node -> {
            if(node == null)    System.out.print("Null ");
            else                System.out.print(Integer.toHexString(node)+" ");
        });
        System.out.print("]");
    }

}
