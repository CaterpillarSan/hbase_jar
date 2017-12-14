package myTrace;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

// 各スレッドのVector Clockメタデータ用
// fork/joinのVC操作メソッドもあり
public class ThreadState {
    // 生成された総スレッド数
    public static AtomicInteger threadNum = new AtomicInteger(-1);
    
    //スレッド識別子(スレッドローカル変数で実装)
    public static ThreadLocal<Integer> tid = new ThreadLocal<Integer>(){
        protected synchronized Integer initialValue(){
            return threadNum.incrementAndGet();
        }
    };


    //vector clock(スレッドローカル変数で実装)
    public static volatile ThreadLocal<NodeClock> NC
        = new ThreadLocal<NodeClock>(){
        protected synchronized NodeClock initialValue(){
            NodeClock list = new NodeClock();
            System.out.println("ThreadLocal初期化");
            return list;
        }
    };

        // とりあえずのlockオブジェクト
    public static Object lock = new Object();
    public static Object fork_lock = new Object();

    // getter
    public static int getEP(){  return NC.get().get(getTid());}
    public static int getTid(){ return tid.get();}
    public static int getNC(int tid){    return NC.get().get(tid);}
    public static int getThreadNum(){  return threadNum.get(); }
    // setter
    public static void setNC(int tid,int num){   NC.get().set(tid,num);}
    
    // VCの自スレッド要素をインクリメント(使用頻度の高い処理) 
    public static void incNC(){
        int t = getTid();
        setNC(t,getEP()+1);
    }

       
    //****************************************************************//
    /*** fork ***/
    // 親スレッド側の処理
    public static void forkParent(){
        print("fork @parent");
        incNC();
    }
    // 子スレッド側の処理
    public static void forkChild(NodeClock nc){
        synchronized(lock){
            NC.get().max(nc);
            incNC();
            print("fork @child");
        }
    }
    
    /*** join ***/
    // 子スレッド側の処理
    public static void joinChild(){
        print("join @child");
        incNC();
    }

    // 親スレッドの処理
    public static void joinParent(NodeClock nc){
        // synchronized(lock){
            // VCのサイズ変更
            // VC.get().extendArray(vc.size()-1);
            // for (int i = 0; i < vc.size(); i++) {
            //     setC(i,Math.max(getC(i),vc.get(i)));
            // }
            NC.get().max(nc);
            incNC();
            print("join @parent");
        // }
    }

    //****************************************************************//

    public static void printInfo(){
        synchronized(lock){
            NC.get().printArray("ThreadState");
            System.out.print("\ttid:"+getTid());
        }
    }
    public static void print(String str){
        synchronized(lock){
            NC.get().printArray("ThreadState");
            System.out.println("\ttid:"+getTid()+"\t"+str);
        }
    }


}


