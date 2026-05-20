package edu.touro.mcon152.bm;

import edu.touro.mcon152.bm.command.SimpleExecutor;
import edu.touro.mcon152.bm.command.benchmark.BenchmarkBase;
import edu.touro.mcon152.bm.command.benchmark.ReadBenchmark;
import edu.touro.mcon152.bm.command.benchmark.WriteBenchmark;
import edu.touro.mcon152.bm.observers.DBObserver;
import edu.touro.mcon152.bm.observers.GUIObserver;
import edu.touro.mcon152.bm.observers.I_Observer;

import java.util.logging.Level;
import java.util.logging.Logger;

import static edu.touro.mcon152.bm.App.*;

/**
 * Execute disk benchmarking. This class works together with an interface (edu.touro.mcon152.bm.I_UI)
 * representing a user interface. This class is in no way reliant on Swing or any other specific
 * implementation of the interface.
 * <p>
 * Depends on static values that describe the benchmark to be done having been set in edu.touro.mcon152.bm.App and edu.touro.mcon152.bm.ui.Gui classes.
 * The edu.touro.mcon152.bm.persist.DiskRun class is used to keep track of and persist info about each benchmark at a higher level (a run),
 * while the edu.touro.mcon152.bm.DiskMark class described each iteration's result, which is displayed by the UI as the benchmark run
 * progresses.
 * <p>
 * This class only knows how to do 'read' or 'write' disk benchmarks. It is instantiated by the
 * startBenchmark() method.
 */

public class DiskWorker {

    // Record any success or failure status returned from SwingWorker (might be us or super)
    Boolean lastStatus = true;  // so far unknown
    I_UI userInterface;
    BenchmarkBase benchmark;

    /**
     * This gets the user interface for use in DIP.
     * @param userInterface The way it displays information.
     */
    public void setUserInterface(I_UI userInterface){
        this.userInterface = userInterface;
    }
    /**
     * This checks if the run has been cancelled.
     */
    public boolean hasBeenCancelled(){
        return userInterface.hasBeenCancelled();
    }

    public void init(){
        userInterface.init();
    }

    /**
     * This runs the actual benchmark.
     * @return the status of the benchmark.
     * @throws Exception
     */
    protected Boolean runBenchmark() throws Exception {
        SimpleExecutor executor = new SimpleExecutor();
        I_Observer db = new DBObserver();
        I_Observer gui = new GUIObserver(userInterface);
        executor.register(db);
        executor.register(gui);

        /*
          We 'got here' because: 1: End-user clicked 'Start' on the benchmark UI,
          which triggered the start-benchmark event associated with the edu.touro.mcon152.bm.App::startBenchmark()
          method.  2: startBenchmark() then instantiated a edu.touro.mcon152.bm.DiskWorker, and called
          its (super class's) execute() method, causing Swing to eventually
          call this doInBackground() method.
         */
        Logger.getLogger(App.class.getName()).log(Level.INFO, "*** New process started ***");
        msg("Running readTest " + App.readTest + "   writeTest " + App.writeTest);
        msg("num files: " + App.numOfMarks + ", num blks: " + App.numOfBlocks
                + ", blk size (kb): " + App.blockSizeKb + ", blockSequence: " + App.blockSequence);

        /*
          The DiskWriter allows a Write, Read, or both types of BMs to be started. They are done serially.
         */
        if (writeTest){
            benchmark = new WriteBenchmark(userInterface, App.numOfMarks, App.numOfBlocks,
                    App.blockSizeKb, App.blockSequence, App.nextMarkNumber, App.multiFile,
                    App.writeSyncEnable, App.dataDir);
            lastStatus = executor.execute(benchmark);
            if (!lastStatus){
                return false;
            }
        }
        /*
          Most benchmarking systems will try to do some cleanup in between 2 benchmark operations to
          make it more 'fair'. For example a networking benchmark might close and re-open sockets,
          a memory benchmark might clear or invalidate the Op Systems TLB or other caches, etc.
         */
        // try renaming all files to clear catch
        if (App.readTest && App.writeTest && !hasBeenCancelled()) {
            String s =  """
                            For valid READ measurements please clear the disk cache by
                            using the included RAMMap.exe or flushmem.exe utilities.
                            Removable drives can be disconnected and reconnected.
                            For system drives use the WRITE and READ operations\s
                            independantly by doing a cold reboot after the WRITE""";
            String s1 = "Clear Disk Cache Now";
            userInterface.showBlockingMessage(s, s1);
        }
        // Same as above, just for Read operations instead of Writes.
        if (App.readTest) {
            benchmark = new ReadBenchmark(userInterface, App.numOfMarks, App.numOfBlocks,
                    App.blockSizeKb, App.blockSequence, App.nextMarkNumber, App.multiFile,
                    App.writeSyncEnable, App.dataDir);
            lastStatus = executor.execute(benchmark);
            if (!lastStatus){
                return false;
            }
        }
        App.nextMarkNumber += App.numOfMarks;
        return lastStatus;
    }
    public void finish(){
        userInterface.finish();
    }
    public Boolean getLastStatus() {
        return lastStatus;
    }
}