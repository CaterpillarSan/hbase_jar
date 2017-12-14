package myTrace;

import java.util.*;

public interface K_Runnable extends Runnable
{
    public void setNC ();
    public NodeClock getNC();
    public void setNC(NodeClock nc);
}
