package edu.touro.mcon152.bm.command.benchmark;

import edu.touro.mcon152.bm.App;
import edu.touro.mcon152.bm.DiskMark;
import edu.touro.mcon152.bm.I_UI;
import edu.touro.mcon152.bm.Util;
import edu.touro.mcon152.bm.persist.DiskRun;
import edu.touro.mcon152.bm.persist.EM;
import jakarta.persistence.EntityManager;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import static edu.touro.mcon152.bm.DiskMark.MarkType.WRITE;

/**
 * This is the command for write benchmarks.
 */
public class WriteBenchmark extends BenchmarkBase{
    final int KILOBYTE = 1024;
    final int MEGABYTE = 1024 * 1024;
    DiskMark wMark;
    /*
          init vars that keep track of benchmarks, and a large write buffer
     */
    private final int numOfMarks, numOfBlocks, blockSizeKB;
    private final DiskRun.BlockSequence blockSequence;
    private final int startFileNum;
    private final boolean multiFile, writeSyncEnabled;
    private final File dataDir;
    int blockSize;
    int unitsTotal;

    public WriteBenchmark(
            I_UI userInterface, int numOfMarks, int numOfBlocks,
            int blockSizeKb, DiskRun.BlockSequence blockSequence,
            int startFileNum, boolean multiFile, boolean writeSyncEnable, File dataDir) {
        super(userInterface);
        this.numOfMarks = numOfMarks;
        this.numOfBlocks = numOfBlocks;
        this.blockSizeKB = blockSizeKb;
        this. blockSequence = blockSequence;
        this.startFileNum = startFileNum;
        this. multiFile = multiFile;
        this.writeSyncEnabled = writeSyncEnable;
        this.dataDir = dataDir;
        blockSize = this.blockSizeKB * KILOBYTE;
        unitsTotal = this.numOfMarks * this.numOfBlocks;

    }
    /**
     * This is the main worker loop. It does a full benchmark.
     * @return boolean    the success or failure of the run.
     */
    @Override
    public boolean execute() {
        File testFile = null;
        int wUnitsComplete = 0;
        int rUnitsComplete = 0;
        int unitsComplete;
        float percentComplete;
        byte[] blockArr = super.makeBuffer(blockSize);
        DiskRun run = new DiskRun(DiskRun.IOMode.WRITE, blockSequence);
        setRunInfo(run, numOfMarks, numOfBlocks, blockSizeKB, dataDir);

        // Tell logger and GUI to display what we know so far about the Run
        userInterface.log("disk info: (" + run.getDiskInfo() + ")");
        userInterface.setTitle(run);
        // Create a test data file using the default file system and config-specified location
        if (!multiFile) {
            testFile = new File(dataDir.getAbsolutePath() + File.separator + "testdata.jdm");
        }

            /*
              Begin an outer loop for specified duration (number of 'marks') of benchmark,
              that keeps writing data (in its own loop - for specified # of blocks). Each 'Mark' is timed
              and is reported to the GUI for display as each Mark completes.
             */
        for (int m = startFileNum; m < startFileNum + numOfMarks && !hasBeenCancelled(); m++) {

            if (multiFile) {
                testFile = new File(dataDir.getAbsolutePath()
                        + File.separator + "testdata" + m + ".jdm");
            }
            wMark = new DiskMark(WRITE);    // starting to keep track of a new benchmark
            wMark.setMarkNum(m);
            long startTime = System.nanoTime();
            long totalBytesWrittenInMark = 0;

            String mode = "rw";
            if (writeSyncEnabled) {
                mode = "rwd";
            }

            try {
                if (testFile == null) throw new IllegalStateException("testFile should have been initialized");
                try (RandomAccessFile rAccFile = new RandomAccessFile(testFile, mode)) {
                    for (int b = 0; b < numOfBlocks; b++) {
                        if (blockSequence == DiskRun.BlockSequence.RANDOM) {
                            int rLoc = Util.randInt(0, numOfBlocks - 1);
                            rAccFile.seek((long) rLoc * blockSize);
                        } else {
                            rAccFile.seek((long) b * blockSize);
                        }
                        rAccFile.write(blockArr, 0, blockSize);
                        totalBytesWrittenInMark += blockSize;
                        wUnitsComplete++;
                        unitsComplete = rUnitsComplete + wUnitsComplete;
                        percentComplete = (float) unitsComplete / (float) unitsTotal * 100f;

                            /*
                              Report to GUI what percentage level of Entire BM (#Marks * #Blocks) is done.
                             */
                        userInterface.setRunProgress((int) percentComplete);
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }

                /*
                  Compute duration, throughput of this Mark's step of BM
                 */
            long endTime = System.nanoTime();
            long elapsedTimeNs = endTime - startTime;
            double sec = (double) elapsedTimeNs / (double) 1000000000;
            double mbWritten = (double) totalBytesWrittenInMark / (double) MEGABYTE;
            wMark.setBwMbSec(mbWritten / sec);
            userInterface.log("m:" + m + " write IO is " + wMark.getBwMbSecAsString() + " MB/s     "
                    + "(" + Util.displayString(mbWritten) + "MB written in "
                    + Util.displayString(sec) + " sec)");
            App.updateMetrics(wMark);

                /*
                  Let the GUI know the interim result described by the current Mark
                 */
            userInterface.setResults(wMark);

            // Keep track of statistics to be displayed and persisted after all Marks are done.
            run.setRunMax(wMark.getCumMax());
            run.setRunMin(wMark.getCumMin());
            run.setRunAvg(wMark.getCumAvg());
            run.setEndTime(new Date());
        } // END outer loop for specified duration (number of 'marks') for WRITE benchmark

            /*
              Persist info about the Write BM Run (e.g. into Derby Database) and add it to a GUI panel
             */
        EntityManager em = EM.getEntityManager();
        em.getTransaction().begin();
        em.persist(run);
        em.getTransaction().commit();

        userInterface.displayRun(run);
        return true;
    }
    }


