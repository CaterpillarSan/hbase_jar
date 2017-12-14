package myTrace;

public interface K_LockInterface {

    PacerLockState getLockState();

    public void myWait() throws Exception;
}
