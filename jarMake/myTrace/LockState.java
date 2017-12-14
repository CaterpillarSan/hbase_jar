package myTrace;
import java.util.*;

/*** 同期オブジェクトのメタデータとその処理 ***/
public class LockState {

    public NodeClock lockNC;   //Vector Clock
    public String objName;

    public LockState(String oname){
        lockNC = new NodeClock();
        objName = oname;
    }

    /*********************************************************************************/
    // lock
    // - acquire
    // - release
    
    /*** acquire ***/
    public void FTacquire(){
        synchronized(lockNC){
            ThreadState.NC.get().max(lockNC);
            printInfo("acq");
        }
    }
    
    /*** release ***/
    public void FTrelease(){
        synchronized(lockNC){
            //deep copy
            lockNC = new NodeClock(ThreadState.NC.get());
            ThreadState.incNC();
            printInfo("rel");
        }
    }

    /*********************************************************************************/

    public void printInfo(String str){
        synchronized(ThreadState.lock){
            ThreadState.printInfo();
            System.out.print("\tLockState:"+str+"\t"+objName+"\t");
            lockNC.printArray("LockVC");
            System.out.print("\n");
        }
    }
   
}
