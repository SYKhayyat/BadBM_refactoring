package edu.touro.mcon152.bm.command.benchmark;

import edu.touro.mcon152.bm.App;
import edu.touro.mcon152.bm.I_UI;
import edu.touro.mcon152.bm.Util;
import edu.touro.mcon152.bm.command.I_BenchmarkCommand;
import edu.touro.mcon152.bm.persist.DiskRun;

import java.io.File;

import static edu.touro.mcon152.bm.App.*;

/**
 * This is an abstract superclass.
 * It is a Benchmark, and it defines values and methods that are common amongst different types of benchmarks.
 */
public abstract class BenchmarkBase implements I_BenchmarkCommand {
    I_UI userInterface;

    public BenchmarkBase(I_UI myUI){
        this.userInterface = myUI;
    }

    /**
     * Sets up the buffer array; taken from diskWorker.
     * @return byte[] the buffer.
     */
    public byte[] makeBuffer(int blockSize){
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
    protected static void setRunInfo(DiskRun run, int numOfMarks, int numOfBlocks, int blockSizeKB, File dataDir) {
        run.setNumMarks(numOfMarks);
        run.setNumBlocks(numOfBlocks);
        run.setBlockSize(blockSizeKB);
        run.setTxSize(App.targetTxSizeKb());
        run.setDiskInfo(Util.getDiskInfo(dataDir));
    }
    public boolean hasBeenCancelled(){
        return userInterface.hasBeenCancelled();
    }

    public abstract boolean execute();
}
