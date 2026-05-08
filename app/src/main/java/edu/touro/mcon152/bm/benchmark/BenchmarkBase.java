package edu.touro.mcon152.bm.benchmark;

import edu.touro.mcon152.bm.App;
import edu.touro.mcon152.bm.I_UI;
import edu.touro.mcon152.bm.Util;
import edu.touro.mcon152.bm.persist.DiskRun;

import static edu.touro.mcon152.bm.App.*;

/**
 * This is an abstract superclass.
 * It is a Benchmark, and it defines values and methods that are common amongst different types of benchmarks.
 */
public abstract class BenchmarkBase {
    I_UI userInterface;
    protected int blockSize = blockSizeKb * KILOBYTE;

    public BenchmarkBase(I_UI myUI){
        this.userInterface = myUI;
    }

    /**
     * Sets up the buffer array; taken from diskWorker.
     * @return byte[] the buffer.
     */
    public byte[] makeBuffer(){
        byte[] blockArr = new byte[blockSize];
        initializeBuffer(blockArr);
        return blockArr;
    }

    /**
     * This sets up the buffer for testing purposes.
     * @param blockArr
     */
    private static void initializeBuffer(byte[] blockArr) {
        for (int b = 0; b < blockArr.length; b++) {
            if (b % 2 == 0) {
                blockArr[b] = (byte) 0xFF;
            }
        }
    }

    /**
     * This is in charge of all the settings of the current run of the software.
     * @param run
     */
    protected static void setRunInfo(DiskRun run) {
        run.setNumMarks(App.numOfMarks);
        run.setNumBlocks(App.numOfBlocks);
        run.setBlockSize(App.blockSizeKb);
        run.setTxSize(App.targetTxSizeKb());
        run.setDiskInfo(Util.getDiskInfo(dataDir));
    }
    public boolean hasBeenCancelled(){
        return userInterface.hasBeenCancelled();
    }

    public abstract boolean runBenchmark();
}
