package edu.touro.mcon152.bm;

import edu.touro.mcon152.bm.persist.DiskRun;

import java.beans.PropertyChangeListener;

/**
 * This interface abstracts all of the UI interactions that could be needed by DiskWorker,
 * and can be implemented by any class that provides a user interface for using DiskWorker.
 */

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
    void showBlockingMessage(String s, String s1);

}
