package edu.touro.mcon152.bm;

import edu.touro.mcon152.bm.persist.DiskRun;

import java.beans.PropertyChangeListener;

public interface I_UI {
    void setDW(DiskWorker dw);
    void init();
    void setTitle(DiskRun run);
    boolean hasBeenCancelled();
    void setRunProgress(int progress);
    void setResults(DiskMark wMark);
    void displayRun(DiskRun run);
    void finish();
    void showMessage(String s, String s1);

}
