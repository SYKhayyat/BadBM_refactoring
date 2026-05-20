package edu.touro.mcon152.bm.observers;

import edu.touro.mcon152.bm.persist.DiskRun;
import edu.touro.mcon152.bm.persist.EM;
import jakarta.persistence.EntityManager;

/**
 * This class helps us the Observer pattern to persist new runs to the database.
 * This is the Observer that is in charge of updating the DB.
 */
public class DBObserver implements  I_Observer{

    @Override
    public void onComplete(DiskRun run) {
        /*
         Persist info about the Read BM Run (e.g. into Derby Database) and add it to a GUI panel
         */
        EntityManager em = EM.getEntityManager();
        em.getTransaction().begin();
        em.persist(run);
        em.getTransaction().commit();
    }
}
