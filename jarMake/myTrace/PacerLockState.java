package myTrace;

import java.util.*;

public class PacerLockState extends LockState {

    // 各ThreadVCに対応するshareFlag
    // 本来ならPacerThreadStateクラス内に置くべき内容だが
    // PacerLockStateクラス内でのみ使用するためここに
    public static ThreadLocal<Boolean> shareFlag = new ThreadLocal<Boolean>(){
        protected synchronized Boolean initialValue(){
            return new Boolean(false);
        }
    };

    // Version epoch
    public int Vepoch;

    public PacerLockState(String oname){
        super(oname);
        Vepoch = 0;
    }

   @Override 
    public void FTacquire(){
        synchronized(this){
            // 冗長なjoinの検出
            if(PacerThreadState.getVV(getTid(Vepoch)) >= Vepoch) {
                printInfo("Xacq");
                return;
            }
            
            // lockVC =< ThreadVCならば冗長なjoin
            // for (int i = 0; i < lockVC.size(); i++) {
            //     if(lockVC.get(i) > ThreadState.getC(i)) {
            //         if(isShared()) {
            //             // shareされていたものを複製
            //             PacerThreadState.cloneC();
            //             setShared(false);
            //         }
            //         // joinを実行
            //         super.FTacquire();
            //         PacerThreadState.incVV();
            //         break;
            //     }
            // }
            PacerThreadState.cloneNC();
            setShared(false);
            super.FTacquire();
            PacerThreadState.incVV();
            // VV に Vepochの情報を追加
            PacerThreadState.setVV(getTid(Vepoch),Vepoch);
        }
    }

   @Override
    public void FTrelease() {
        synchronized(this){
            if (PacerSampling.sampling) {
                super.FTrelease();
                PacerThreadState.incVV();
                // setShared(false);
            }
            else {
                setShared(true);
                lockNC = ThreadState.NC.get();
                printInfo("Xrel");
            }
            Vepoch = PacerThreadState.getVepoch();
        }
    }

   public int getTid(int num){
       return num >> 24;
   }

   public static boolean isShared(){
       return shareFlag.get().booleanValue();
   }

   public static void setShared(boolean bool){
       shareFlag.set(new Boolean(bool));
   }



}
