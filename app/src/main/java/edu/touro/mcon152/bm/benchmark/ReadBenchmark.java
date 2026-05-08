package edu.touro.mcon152.bm.benchmark;

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

import static edu.touro.mcon152.bm.App.*;
import static edu.touro.mcon152.bm.App.MEGABYTE;
import static edu.touro.mcon152.bm.App.msg;
import static edu.touro.mcon152.bm.App.numOfBlocks;
import static edu.touro.mcon152.bm.App.testFile;
import static edu.touro.mcon152.bm.DiskMark.MarkType.READ;

public class ReadBenchmark extends BenchmarkBase {
    /*
          init vars that keep track of benchmarks, and a large read buffer
     */
    int startFileNum = App.nextMarkNumber;
    int wUnitsComplete = 0, rUnitsComplete = 0, unitsComplete;
    int wUnitsTotal = App.writeTest ? numOfBlocks * numOfMarks : 0;
    int rUnitsTotal = App.readTest ? numOfBlocks * numOfMarks : 0;
    int unitsTotal = wUnitsTotal + rUnitsTotal;
    float percentComplete;

    DiskMark rMark;  // declare vars that will point to objects used to pass progress to UI

    public ReadBenchmark(I_UI userInterface) {
        super(userInterface);
    }

    /**
     * This is the main worker loop. It does a full benchmark.
     * @return boolean    the success or failure of the run.
     */
    @Override
    public boolean runBenchmark(){
        byte[] blockArr = super.makeBuffer();
        DiskRun run = new DiskRun(DiskRun.IOMode.READ, App.blockSequence);
        setRunInfo(run);

        msg("disk info: (" + run.getDiskInfo() + ")");

        userInterface.setTitle(run);

        for (int m = startFileNum; m < startFileNum + App.numOfMarks && !hasBeenCancelled(); m++) {

            if (App.multiFile) {
                testFile = new File(dataDir.getAbsolutePath()
                        + File.separator + "testdata" + m + ".jdm");
            }
            rMark = new DiskMark(READ);  // starting to keep track of a new benchmark
            rMark.setMarkNum(m);
            long startTime = System.nanoTime();
            long totalBytesReadInMark = 0;

            try {
                try (RandomAccessFile rAccFile = new RandomAccessFile(testFile, "r")) {
                    for (int b = 0; b < numOfBlocks; b++) {
                        if (App.blockSequence == DiskRun.BlockSequence.RANDOM) {
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
                msg(emsg);
                return false;
            }
            long endTime = System.nanoTime();
            long elapsedTimeNs = endTime - startTime;
            double sec = (double) elapsedTimeNs / (double) 1000000000;
            double mbRead = (double) totalBytesReadInMark / (double) MEGABYTE;
            rMark.setBwMbSec(mbRead / sec);
            msg("m:" + m + " READ IO is " + rMark.getBwMbSec() + " MB/s    "
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

