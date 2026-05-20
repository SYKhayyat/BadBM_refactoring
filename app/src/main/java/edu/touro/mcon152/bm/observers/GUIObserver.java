package edu.touro.mcon152.bm.observers;

import edu.touro.mcon152.bm.I_UI;
import edu.touro.mcon152.bm.persist.DiskRun;
/**
 * This class helps us the Observer pattern to persist new runs to the database.
 * This is the Observer that is in charge of updating the DB.
 * This class is not so properly named (see submission), as it is really the observer
 * pattern ar use in the UI interface we decoupled from Swing.
 * Therefore, it is used to register the console UI as well.
 */
public class GUIObserver implements  I_Observer{
    private I_UI userInterface;

    public GUIObserver(I_UI userInterface){
        this.userInterface = userInterface;
    }
    public void setUI(I_UI userInterface){
        this.userInterface = userInterface;
    }
    @Override
    public void onComplete(DiskRun run) {
        userInterface.displayRun(run);
    }
}
