package myTrace;

/**
 * 標準ライブラリをオーバーライドするクラスのサンプル
 */

public class K_Object extends java.lang.Object implements K_LockInterface {

    public PacerLockState FastTrack_LS = new PacerLockState("Object");
    public static PacerLockState FastTrack_static_LS = new PacerLockState("Object");

    public PacerLockState getLockState(){
        return FastTrack_LS;
    }

}
