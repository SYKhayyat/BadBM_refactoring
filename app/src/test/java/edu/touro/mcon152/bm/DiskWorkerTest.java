package edu.touro.mcon152.bm;

import edu.touro.mcon152.bm.persist.DiskRun;
import edu.touro.mcon152.bm.ui.Gui;
import edu.touro.mcon152.bm.ui.MainFrame;

import java.io.File;
import java.util.Properties;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DiskWorkerTest {
    /**
     * Bruteforce setup of static classes/fields to allow DiskWorker to run.
     *
     * @author lcmcohen
     */

    private static void setupDefaultAsPerProperties()
    {
        /// Do the minimum of what  App.init() would do to allow to run.
        Gui.mainFrame = new MainFrame();
        App.p = new Properties();
        App.loadConfig();

        Gui.progressBar = Gui.mainFrame.getProgressBar(); //must be set or get Nullptr

        // configure the embedded DB in .jDiskMark
        System.setProperty("derby.system.home", App.APP_CACHE_DIR);

        // code from startBenchmark
        //4. create data dir reference

        // may be null when tests not run in original proj dir, so use a default area
        if (App.locationDir == null) {
            App.locationDir = new File(System.getProperty("user.home"));
        }

        App.dataDir = new File(App.locationDir.getAbsolutePath()+File.separator+App.DATADIRNAME);

        //5. remove existing test data if exist
        if (App.dataDir.exists()) {
            if (App.dataDir.delete()) {
                App.msg("removed existing data dir");
            } else {
                App.msg("unable to remove existing data dir");
            }
        }
        else
        {
            App.dataDir.mkdirs(); // create data dir if not already present
        }
    }
    @Test
    public void testBenchmarkRunsW() throws Exception{
        setupDefaultAsPerProperties();
        App.numOfMarks = 2;
        App.numOfBlocks = 2;
        App.writeTest = true;
        App.readTest = false;

        DiskWorker dw = new DiskWorker();
        Console_UI console = new Console_UI();
        console.setDW(dw);
        Boolean result = console.runBenchmark();

        assertTrue(console.getFinished());

        DiskRun run = console.getLastRun();
        assertTrue(run.getRunAvg() > 0);  // Average speed positive
        assertTrue(run.getRunMax() >= run.getRunAvg());  // Max >= Avg
        assertTrue(run.getRunMin() <= run.getRunAvg());  // Min <= Avg
        assertTrue(run.getRunMin() > 0);  // Min is positive

        assertEquals(App.numOfMarks, console.getMarks().size());
        assertEquals(100, console.getLastProgress());


        for (DiskMark mark : console.getMarks()) {
            assertTrue(mark.getBwMbSec() > 0);  // Speed should be positive
            assertTrue(mark.getMarkNum() > 0);  // Mark number should be positive
        }

        Assertions.assertTrue(result);
    }
    @Test
    public void testBenchmarkRunsR() throws Exception{
        setupDefaultAsPerProperties();
        App.numOfMarks = 2;
        App.numOfBlocks = 2;
        App.writeTest = false;
        App.readTest = true;

        DiskWorker dw = new DiskWorker();
        Console_UI console = new Console_UI();
        console.setDW(dw);
        Boolean result = console.runBenchmark();

        assertTrue(console.getFinished());

        DiskRun run = console.getLastRun();
        assertTrue(run.getRunAvg() > 0);  // Average speed positive
        assertTrue(run.getRunMax() >= run.getRunAvg());  // Max >= Avg
        assertTrue(run.getRunMin() <= run.getRunAvg());  // Min <= Avg
        assertTrue(run.getRunMin() > 0);  // Min is positive

        assertEquals(App.numOfMarks, console.getMarks().size());
        assertEquals(100, console.getLastProgress());


        for (DiskMark mark : console.getMarks()) {
            assertTrue(mark.getBwMbSec() > 0);  // Speed should be positive
            assertTrue(mark.getMarkNum() > 0);  // Mark number should be positive
        }

        Assertions.assertTrue(result);
    }
    @Test
    public void testBenchmarkRunsRW() throws Exception{
        setupDefaultAsPerProperties();
        App.numOfMarks = 2;
        App.numOfBlocks = 2;
        App.writeTest = true;
        App.readTest = true;

        DiskWorker dw = new DiskWorker();
        Console_UI console = new Console_UI();
        console.setDW(dw);
        Boolean result = console.runBenchmark();

        assertTrue(console.getFinished());

        DiskRun run = console.getLastRun();
        assertTrue(run.getRunAvg() > 0);  // Average speed positive
        assertTrue(run.getRunMax() >= run.getRunAvg());  // Max >= Avg
        assertTrue(run.getRunMin() <= run.getRunAvg());  // Min <= Avg
        assertTrue(run.getRunMin() > 0);  // Min is positive

        assertEquals(App.numOfMarks * 2, console.getMarks().size());
        assertEquals(100, console.getLastProgress());


        for (DiskMark mark : console.getMarks()) {
            assertTrue(mark.getBwMbSec() > 0);  // Speed should be positive
            assertTrue(mark.getMarkNum() > 0);  // Mark number should be positive
        }

        Assertions.assertTrue(result);
    }


}
