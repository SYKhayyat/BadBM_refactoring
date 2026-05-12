package edu.touro.mcon152.bm;

import edu.touro.mcon152.bm.command.SimpleExecutor;
import edu.touro.mcon152.bm.command.benchmark.ReadBenchmark;
import edu.touro.mcon152.bm.command.benchmark.WriteBenchmark;
import edu.touro.mcon152.bm.persist.DiskRun;
import edu.touro.mcon152.bm.ui.Gui;
import edu.touro.mcon152.bm.ui.MainFrame;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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
        File propFile = new File("build.properties");
        if (!propFile.exists()) {
            try {
                propFile.createNewFile(); // Creates a dummy file so the code doesn't crash
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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

    @Test
    public void testComnmand(){
        File tempDir = new File(System.getProperty("java.io.tmpdir"), "jdm_test_" + System.currentTimeMillis());
        tempDir.mkdirs();
        Console_UI myUI =  new Console_UI();
        WriteBenchmark write = new WriteBenchmark(myUI, 25, 128, 2048, DiskRun.BlockSequence.SEQUENTIAL, 1, true, true, tempDir);
        SimpleExecutor exec = new SimpleExecutor();
        boolean success = exec.execute(write);
        Assertions.assertTrue(success);
        myUI.finish();
        assertTrue(myUI.getFinished());
        assertEquals(100, myUI.getLastProgress());
        assertEquals(25, myUI.getMarks().size());
        for (DiskMark mark: myUI.getMarks()){
            assertTrue(mark.getBwMbSec() > 0);
            assertTrue(mark.getMarkNum() >= 1 && mark.getMarkNum() <= 25);
        }
        assertNotNull(myUI.getLastRun());
        assertTrue(myUI.getLastRun().getRunAvg() > 0);
        assertTrue(myUI.getLastRun().getRunMax() >= myUI.getLastRun().getRunAvg());
        assertTrue(myUI.getLastRun().getRunMin() > 0);
        Console_UI myUI2 =  new Console_UI();
        ReadBenchmark read = new ReadBenchmark(myUI2, 25, 128, 2048, DiskRun.BlockSequence.SEQUENTIAL, 1, true, true, tempDir);
        success = exec.execute(read);
        Assertions.assertTrue(success);
        myUI2.finish();
        assertTrue(myUI2.getFinished());
        assertEquals(100, myUI2.getLastProgress());
        assertEquals(25, myUI2.getMarks().size());
        for (DiskMark mark: myUI2.getMarks()){
            assertTrue(mark.getBwMbSec() > 0);
            assertTrue(mark.getMarkNum() >= 1 && mark.getMarkNum() <= 25);
        }
        assertNotNull(myUI2.getLastRun());
        assertTrue(myUI2.getLastRun().getRunAvg() > 0);
        assertTrue(myUI2.getLastRun().getRunMax() >= myUI.getLastRun().getRunAvg());
        assertTrue(myUI2.getLastRun().getRunMin() > 0);
    }


}
