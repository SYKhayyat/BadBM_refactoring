package edu.touro.mcon152.bm.observers;

import edu.touro.mcon152.bm.persist.DiskRun;

/**
 * This is the interface for observers. Eah observer implements this interface,
 * and overrides one method, onComplete.
 */
public interface I_Observer {
    /**
     * This method is used by the observer to define what to do when it observes that the run is complete.
     * @param run The DiskRun that has been completed.
     */
    public void onComplete(DiskRun run);
}
