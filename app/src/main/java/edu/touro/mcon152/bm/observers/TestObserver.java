package edu.touro.mcon152.bm.observers;

import edu.touro.mcon152.bm.persist.DiskRun;

/**
 * This class helps us test te Observer pattern.
 * It has  a flag, which checks if it was called.
 */
public class TestObserver implements I_Observer{
    public static boolean observed = false;
    @Override
    public void onComplete(DiskRun run) {
        observed = true;
    }
}
