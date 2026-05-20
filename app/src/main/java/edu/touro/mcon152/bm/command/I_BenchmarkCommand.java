package edu.touro.mcon152.bm.command;

import edu.touro.mcon152.bm.persist.DiskRun;

/**
 * This is the interface for command objects.
 * Read and Write will implement this interface.
 */
public interface I_BenchmarkCommand {
    boolean execute();
    DiskRun getResult();
}
