package myTrace;

import java.util.*;
import java.lang.management.*;

public class PacerSampling extends Thread{

    public static volatile boolean sampling = false;
    public static final String pName = ManagementFactory.getRuntimeMXBean().getName();
    public static final long PID = Long.valueOf(pName.split("@")[0]);
    public static boolean stopRequested = false;
    public Random rnd = new Random();
    public int raito = 100;
    public int random;
    public int waitTime = 10;

    public void run(){
        System.out.println("PID:"+PID);
        while(!stopRequested){
            synchronized(ThreadState.lock){
                random = rnd.nextInt(100);
                // すごく適当な確率計算
                if (random < raito) {
                   if(sampling == false){
                        System.out.println("********************************************************");
                        System.out.println("******************** Sampling start ********************");
                        sampling = true;
                        
                        // VCt[t]++ をしないといけないのを思い出した
                   }
                  
                }
                else {
                    if(sampling == true){
                        System.out.println("******************** Sampling stop *********************");
                        System.out.println("********************************************************");
                        sampling = false;
                    }
                }
            }
            try {
                Thread.sleep(waitTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        if(sampling == true){
            System.out.println("******************** Sampling stop *********************");
            System.out.println("********************************************************");
        }
        System.out.println("Raito = "+raito+", Sampling finish.");
    
    }

}
