package myTrace;
import java.util.*;
//
// KCatch instrumentation replaces all occurrences of 'Thread' (in each
// expression) with 'K_Thread'.
//
public class K_Thread extends Thread implements K_Runnable {
    // KCatch associates each thread with its local vector clock. 
    // If the thread is created using K_Runnable (for example, using statement
    // 't = new K_Thread(new K_RunnableImpl());'), then KCatch uses the 'ftRun'
    // field to maintain the thread's vector clock.
    // Otherwise, KCatch uses the 'VC' field to directly manage the clock.
    
    private K_Runnable ftRun = null;
    // protected ArrayList<Integer> VC_tmp;
    protected NodeClock NC_tmp;

    public K_Thread () {
	    super();
    }

    public K_Thread (K_Runnable r) {
	    super (r);
	    ftRun = r;
    }

 //    K_Thread (K_Runnable r, ArrayList<Integer> parentVC
 //    {
	// super (r);	
	// ftRun = r;
	// ftRun.setVC ();
 //    }

    // TODO: Add wrapper constructors conforming to the signitures of Thread
    //       constructors. 

    public void setNC () {
        if (ftRun != null)
            ftRun.setNC ();
        else
            NC_tmp = new NodeClock(ThreadState.NC.get());
    }

    public void setNC (NodeClock nc){
        if (ftRun != null)
            ftRun.setNC (nc);
        else{
            NC_tmp = new NodeClock(nc);
        }
    }

    public NodeClock getNC() {
        if (ftRun != null) {
            return ftRun.getNC();
        }
        else
            return NC_tmp;
    }
}
