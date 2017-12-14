package myTrace;

import java.util.*;

public class PacerThreadState extends ThreadState {
    
    // version vector
    public static ThreadLocal<VectorClock> VV
        = new ThreadLocal<VectorClock>(){
        
            protected synchronized VectorClock initialValue(){
                return new VectorClock(threadNum.get(),0);
            }
        };

    public static int getVV(int i){
        return VV.get().get(i);
    }

    public static void setVV(int i,int Vepoch){
        VV.get().set(i,Vepoch);
    }

    public static int getVepoch(){
        return VV.get().get(tid.get());
    }

    public static void incVV(){
        int t = tid.get();
        setVV(t,getVV(t)+1);
    }

    //*****************************************************************//
    public static void cloneNC(){
        NC.set(new NodeClock(NC.get()));
    }

    
    //*****************************************************************//
    public static void forkParent(){
        if(PacerSampling.sampling){
            ThreadState.forkParent();
            incVV();
        }
    }

    public static void forkChild(NodeClock nc){
        // 冗長なjoinとなる可能性がない
        // 親VV -> 子VV とすればより正確
        ThreadState.forkChild(nc);
        incVV();
    }

    public static void joinParent(NodeClock nc){
        // 本来は冗長性の検査
        // 子スレッドVVを引数に
        ThreadState.joinParent(nc);
        // if(親スレッドが更新されたら)
        incVV();
        // setVV(子スレッドVepoch)
    }

    public static void joinChild(){
        if(PacerSampling.sampling){
            ThreadState.joinChild();
            incVV();
        }
    }

    //*****************************************************************//
    public static void printVV(){
        synchronized(lock){
            VV.get().printArray(" VersionVector");
            print("\n");
        }
    }
}
