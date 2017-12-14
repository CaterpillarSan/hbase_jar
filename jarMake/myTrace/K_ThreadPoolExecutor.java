package myTrace;

import java.util.concurrent.*;

public class K_ThreadPoolExecutor extends ThreadPoolExecutor {

    protected NodeClock nc;
    private boolean singleFlag;
    
    public K_ThreadPoolExecutor (int corePoolSize, int maxPoolSize,long keepAliveTime, TimeUnit unit,BlockingQueue<Runnable> workQueue){
        super(corePoolSize,maxPoolSize,keepAliveTime,unit,workQueue);
        singleFlag = isSingle(maxPoolSize);
    }

    public K_ThreadPoolExecutor (int corePoolSize, int maxPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler){
        super(corePoolSize,maxPoolSize,keepAliveTime,unit,workQueue,handler);
        singleFlag = isSingle(maxPoolSize);
    }

    public K_ThreadPoolExecutor (int corePoolSize, int maxPoolSize,long keepAliveTime, TimeUnit unit,BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory){
        super(corePoolSize,maxPoolSize,keepAliveTime,unit,workQueue,threadFactory);
        singleFlag = isSingle(maxPoolSize);
    }

    public K_ThreadPoolExecutor (int corePoolSize, int maxPoolSize,long keepAliveTime, TimeUnit unit,BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler){
        super(corePoolSize,maxPoolSize,keepAliveTime,unit,workQueue,threadFactory,handler);
        singleFlag = isSingle(maxPoolSize);
    }

    public static ExecutorService newSingleThreadExecutor(){
        return (ExecutorService)new K_ThreadPoolExecutor(1,1,0L,TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>());
    }

    public static ExecutorService newCachedThreadPool(){
        return (ExecutorService)new K_ThreadPoolExecutor(0,Integer.MAX_VALUE,60L, TimeUnit.SECONDS,new SynchronousQueue<Runnable>());
    }

    public static ExecutorService newFixedThreadPool(int num){
        return (ExecutorService)new K_ThreadPoolExecutor(num,num,0L,TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>());
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        // Runnableにvcをセットすれば良い?
        // Thread t.join()などが行われるなら,このthreadに何か計装しなければならない?
        super.beforeExecute(t, r);
    }


    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        if(singleFlag)  nc = new NodeClock(((K_Runnable)r).getNC());
    }

    @Override
    public void execute(Runnable command){
        // 1.singleなら,this.vcと親Thread.vcのMaxをRunnableに追加
        //   else 親スレッド.vcをRunnableに追加
        // 2.親スレッドをインクリメント
        // if(singleFlag){
        //     // Max  
        //     vc = new VectorClock();
        //     vc.max(((K_Runnable)command).getVC());
        // }
        // else{
        //     vc = new VectorClock(ThreadState.VC.get());
        // }
        // ThreadState.incC();
        // ((K_Runnable)command).setVC(vc);
        super.execute(command);
    }
    
    public boolean isSingle(int maxPoolSize){
        if(maxPoolSize == 1) return true;
        else return false;
    }


}
