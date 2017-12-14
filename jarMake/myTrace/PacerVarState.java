package myTrace;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class PacerVarState {
    
    private Integer W;
    public  VectorClock R;
    private String fName;
    private int lastAccessLine;
    public static AtomicInteger raceNum = new AtomicInteger();

    public PacerVarState(String fname) {
        W = new Integer(0);
        R = new VectorClock(0);
        fName = fname;
    }

    public int getTid(int epoch) {
        return epoch >> 24;
    }

    /*********************************************************************************/
    public void read(int lineNum) {
        if (!PacerSampling.sampling && R == null && W == null){
            /*** Non-Sampling and Non-Cheking***/
            printInfo("Xread");
            return;
        }
     
        if (R != null && R.isEpoch() && R.getEpoch() == ThreadState.getEP()) {
            /*** READ SAME EPOCH ***/
            if(!PacerSampling.sampling){
                R = null;
                printInfo("Xread");
            }
            else    printInfo("read");
            return;
        }
      
        if(W != null && W > ThreadState.getNC(getTid(W))) {
            //****** WRITE READ RACE ******//
            printErr('W','R',lineNum);
        }
        
        
        // update read state
        if (PacerSampling.sampling) {
            // **** Sampling **** //
            if (R == null) {
                /*** 非サンプリング期間からの復帰 ***/
                R = new VectorClock(ThreadState.getEP());
            }
            else if (R.isEpoch()) {
                if (R.getEpoch() <= ThreadState.getNC(R.getTid(0))) {
                    /*** READ EXCLUSIVE ***/
                    R.set(0,ThreadState.getEP());
                }
                else {
                    /*** READ SHARE ***/
                    R = new VectorClock(ThreadState.threadNum.get(),R.getEpoch());
                    R.set(ThreadState.getEP());
                }
            }
            else {
                /*** READ SHARED ***/
                R.set(ThreadState.getEP());
            }
            printInfo("read");
        }
        else if (R != null){
            // ***** Non-Sampling & Metadata != Null ***** //
            if(R.isEpoch() || R.getNonNullLength() == 1) {
                R = null;
            }
            else {
                R.setNull(ThreadState.getTid());
            }
            printInfo("read");
        }
        else {
            // **** Non-Sampling & Metadata == Null ***** //
            printInfo("Xread");
        }
        
        lastAccessLine = lineNum;
        
    }

    /*********************************************************************************/
    public void write(int lineNum) {
        if(!PacerSampling.sampling && R==null && W == null) {
            // ***** Non-Sampling ***/
            printInfo("Xwrite");
            return;
        }

        if(W != null && W ==  ThreadState.getEP()) {
            /*** WRITE SAME EPOCH ***/
            if(!PacerSampling.sampling){
                W = null;
                R = null;
                printInfo("Xwrite");
            }
            else    printInfo("write");
            return;
        }

        //write-write race?
        if(W != null && W > ThreadState.getNC(getTid(W))) {
            //***** WRITE=WRITE RACE *****//
            printErr('W','W',lineNum);
        }

        //read-write race?
        if(R != null) {
            if(R.isEpoch()) {
                if(R.getEpoch() > ThreadState.getNC(R.getTid(0))){
                    //***** READ-WRITE RACE *****//
                    printErr('R','W',lineNum);
                }
            }
            else {
                for (int i = 0; i <= R.size(); i++) {
                    if(R.get(i) != null && R.get(i) > ThreadState.getNC(i)) {
                        //***** READ-WRITE RACE *****//
                        printErr('R','W',lineNum);
                        break;
                    }
                }
            }
        }

        // update write state
        if(PacerSampling.sampling) {
            W = ThreadState.getEP();
        }
        else {
            W = null;
        }

        // reset read data
        R = null;
        printInfo("write");
        lastAccessLine = lineNum;
    }

    /*********************************************************************************/
    public void printInfo(String str){
        synchronized(ThreadState.lock){
            ThreadState.printInfo();
            String wStr = (W != null) ? Integer.toHexString(W) : "Null";
            System.out.print("\tVarState:"+str+"\t"+fName);
            System.out.print("\t\twriteEP:"+wStr);
            if(R==null) System.out.print("\treadMap: Null");
            else {
                R.printArray("\tReadMap");
            }
            System.out.print("\n");
        }
    }

    /*********************************************************************************/
    public void printErr(char c1,char c2,int line){
        int tid1,tid2;
        String str1,str2;
        if(c1=='R'){
          if(R == null || R.size() > 1) tid1 = -1;
          else tid1 = R.get(0) >> 24;
        }
        else if(c1=='W') {
          tid1 = W >> 24;
        }
        else {
         tid1 = -2;
        }
        tid2 = ThreadState.getTid();
        
        str1 = "("+tid1+","+lastAccessLine+","+c1+")";
        str2 = "("+tid2+","+line+","+c2+")";
        synchronized(ThreadState.lock){
            System.out.print(raceNum.get());
            System.out.println("* race * FieldName:"+fName+"\t"+str1+" "+str2);
        }
        raceNum.getAndIncrement();
    }

}
