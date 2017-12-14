package myTrace;

public class K_LockStateManager {
    public static void FTacquire(K_LockInterface obj){
        PacerLockState lock = obj.getLockState();
        lock.FTacquire();
    }

    public static void FTacquire(Object obj){
        if(obj instanceof K_LockInterface) {
            FTacquire((K_LockInterface)obj);
        }
    }

    public static void FTrelease(K_LockInterface obj){
        PacerLockState lock = obj.getLockState();
        lock.FTrelease();
    }
}
