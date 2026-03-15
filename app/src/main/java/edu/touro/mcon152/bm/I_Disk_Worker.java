package edu.touro.mcon152.bm;

import java.beans.PropertyChangeListener;

public interface I_Disk_Worker {
    void start();
    Boolean getLastStatus();
    void cancelRun(boolean state);
    void addDiskWorkerPropertyChangeListener(PropertyChangeListener listener);
}
