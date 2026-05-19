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

import static edu.touro.mcon152.bm.DiskMark.MarkType.READ;

public class ReadBenchmark extends BenchmarkBase {
    final int KILOBYTE = 1024;
    final int MEGABYTE = 1024 * 1024;
    /*
          init vars that keep track of benchmarks, and a large read buffer
     */

    DiskMark rMark;  // declare vars that will point to objects used to pass progress to UI
    private final int numOfMarks, numOfBlocks, blockSizeKB;
    private final DiskRun.BlockSequence blockSequence;
    private final int startFileNum;
    private final boolean multiFile, writeSyncEnabled;
    private final File dataDir;
    int blockSize;
    int unitsTotal;


    public ReadBenchmark(
            I_UI userInterface, int numOfMarks, int numOfBlocks,
            int blockSizeKb, DiskRun.BlockSequence blockSequence,
            int startFileNum, boolean multiFile, boolean writeSyncEnable, File dataDir){
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
    public boolean execute(){
        File testFile = null;
        int wUnitsComplete = 0;
        int rUnitsComplete = 0;
        int unitsComplete;
        float percentComplete;
        byte[] blockArr = super.makeBuffer(blockSize);
        DiskRun run = new DiskRun(DiskRun.IOMode.READ, blockSequence);
        setRunInfo(run, numOfMarks, numOfBlocks, blockSizeKB, dataDir);
        if (!multiFile) {
            testFile = new File(dataDir, "testdata.jdm");
        }

        userInterface.log("disk info: (" + run.getDiskInfo() + ")");

        userInterface.setTitle(run);

        for (int m = startFileNum; m < startFileNum + numOfMarks && !hasBeenCancelled(); m++) {

            if (multiFile) {
                testFile = new File(dataDir.getAbsolutePath()
                        + File.separator + "testdata" + m + ".jdm");
            }
            rMark = new DiskMark(READ);  // starting to keep track of a new benchmark
            rMark.setMarkNum(m);
            long startTime = System.nanoTime();
            long totalBytesReadInMark = 0;

            try {
                if (testFile == null) throw new IllegalStateException("testFile should have been initialized");
                try (RandomAccessFile rAccFile = new RandomAccessFile(testFile, "r")) {
                    for (int b = 0; b < numOfBlocks; b++) {
                        if (blockSequence == DiskRun.BlockSequence.RANDOM) {
                            int rLoc = Util.randInt(0, numOfBlocks - 1);
                            rAccFile.seek((long) rLoc * blockSize);
                        } else {
                            rAccFile.seek((long) b * blockSize);
                        }
                        rAccFile.readFully(blockArr, 0, blockSize);
                        totalBytesReadInMark += blockSize;
                        rUnitsComplete++;
                        unitsComplete = rUnitsComplete + wUnitsComplete;
                        percentComplete = (float) unitsComplete / (float) unitsTotal * 100f;
                        userInterface.setRunProgress((int) percentComplete);
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
                String emsg = "May not have done Write Benchmarks, so no data available to read." +
                        ex.getMessage();
                userInterface.showMessage(emsg, "Unable to read");
                userInterface.log(emsg);
                return false;
            }
            long endTime = System.nanoTime();
            long elapsedTimeNs = endTime - startTime;
            double sec = (double) elapsedTimeNs / (double) 1000000000;
            double mbRead = (double) totalBytesReadInMark / (double) MEGABYTE;
            rMark.setBwMbSec(mbRead / sec);
            userInterface.log("m:" + m + " READ IO is " + rMark.getBwMbSec() + " MB/s    "
                    + "(MBread " + mbRead + " in " + sec + " sec)");
            App.updateMetrics(rMark);
            userInterface.setResults(rMark);

            run.setRunMax(rMark.getCumMax());
            run.setRunMin(rMark.getCumMin());
            run.setRunAvg(rMark.getCumAvg());
            run.setEndTime(new Date());
        }

        /*
         Persist info about the Read BM Run (e.g. into Derby Database) and add it to a GUI panel
         */
        EntityManager em = EM.getEntityManager();
        em.getTransaction().begin();
        em.persist(run);
        em.getTransaction().commit();

        userInterface.displayRun(run);
        return true;
    }
    }

