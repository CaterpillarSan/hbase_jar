package myTrace;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class VarState {

    public static final int READSHARED = Integer.MAX_VALUE;
    public ArrayList<Integer> readVC;
    public int readEP,writeEP;
    public String fieldName;
    public Object lock = new Object(); // <- 何に使ってたんだろう
    private int lastAccessLine;
    public static AtomicInteger raceNum = new AtomicInteger();

    public VarState(String fname){
        // int tid = ThreadState.getTid();
        readEP = 0;
        writeEP = 0;
        fieldName = fname;
    }

    // read,writeのスレッド番号を取得
    // 1つにまとめようかな
    public int getwTid(){
        return writeEP >> 24;
    }
    
    // readEP == READSHAREDの場合の処理
    public int getrTid(){
        return readEP >> 24;
    }

    public int getReadVC(int id){
        // エラー処理
        return readVC.get(id);
    }

    public void setReadVC(int id, int epoch){
        // 引数をepochだけにできる
        // エラー処理
        readVC.set(id,epoch);
    }

    // readメタデータの初期化を行う
    public void resetRead(){
        readEP = 0;
        readVC = null;
    }

    /*********************************************************************************/
    // Reads
    // - read same epoch
    // - read shared
    // - read exclusive
    // - read share (read shareに移行するとき)
    public void read(int lineNum){
     
        if(readEP == ThreadState.getEP()) {
            /*** read same epoch ***/
            //success
            //state no change
            printInfo("read");
            return;
        }
        
        //write-read race?
        if(writeEP > ThreadState.getNC(getwTid())){
            /*** read shared || exclusive || share ***/
            //error
            printErr('W','R',lineNum);
        }
        
        //update read state
        if(readEP == READSHARED){
            /*** read shared ***/
            setReadVC(ThreadState.getTid(),ThreadState.getEP());
        } else {
            if(readEP <= ThreadState.getNC(getrTid())) {
                /*** read exclusive ***/
                readEP = ThreadState.getEP();
            } else {
                /*** read share ***/
                if(readVC == null) readVC = new ArrayList<Integer>(Arrays.asList(0x00000000,0x01000000,0x02000000));
                setReadVC(getrTid(),readEP);
                setReadVC(ThreadState.getTid(),ThreadState.getEP());
                readEP = READSHARED;
            }
        }
        lastAccessLine = lineNum;
        printInfo("read");
    }
    /*********************************************************************************/
    // writes 
    // - write same epoch
    // - write exclusive
    // - write shared
    public void write(int lineNum){

        if(writeEP == ThreadState.getEP()){
            /*** write same epoch ***/
            // state: no change
            printInfo("write");
            return;
        }
        
        //write-write race?
        if(writeEP > ThreadState.getNC(getwTid())){
            /*** write exclusive || write shared ***/
            //error
            printErr('W','W',lineNum);
        }
        
        //read-write race?
        if(readEP != READSHARED){
            /*** write excliusive ***/
            if (readEP > ThreadState.getNC(getrTid())){
                //error
                printErr('R','W',lineNum);
            }
        } else { 
            /*** write shared ***/
            for(int i = 0;i <= ThreadState.threadNum.get();i++){
                if(getReadVC(i) > ThreadState.getNC(i)){
                    //error
                    printErr('R','W',lineNum);
                    break;
                }
            }
            // reset read state
            resetRead();
        }
        
        //update write state
        writeEP = ThreadState.getEP();
        lastAccessLine = lineNum;
        printInfo("write");
    }

    /*********************************************************************************/
    public void printInfo(String str){
        synchronized(ThreadState.lock){
            ThreadState.printInfo();
            System.out.print("\tVarState: "+str+"\t"+fieldName);
            System.out.print("\t\twriteEP:"+Integer.toHexString(writeEP));
            if(readEP == READSHARED)
                printArray();
            else
                System.out.println("\treadEp:"+Integer.toHexString(readEP));
        }
   }
   
    public void printArray(){
        System.out.print("\treadVC: [ ");
        for (int i = 0; i < readVC.size(); i++) {
            System.out.print(Integer.toHexString(readVC.get(i))+" ");
        }
        System.out.println("]");
  } 

  public void printErr(char c1,char c2,int line){
      int tid1,tid2;
      String str1,str2;
      if(c1=='R'){
          if(readEP == READSHARED) tid1 = -1;
          else tid1 = getrTid(); 
      }
      else if(c1=='W') {
          tid1 = getwTid();
      }
      else {
         tid1 = -2;
      }
      tid2 = ThreadState.getTid();
      /*** DEBUG ***/ System.out.print(raceNum.get());
      str1 = "("+tid1+","+lastAccessLine+","+c1+")";
      str2 = "("+tid2+","+line+","+c2+")";
      synchronized(ThreadState.lock){
          System.out.println("* race * FieldName:"+fieldName+"\t"+str1+" "+str2);
      }
    raceNum.getAndIncrement();
  }
}
