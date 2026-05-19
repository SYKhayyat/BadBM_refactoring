package edu.touro.mcon152.bm.command;

/**
 * This is the executor.
 * It is very simple, and it just calls the execute of the Command object it takes.
 */
public class SimpleExecutor {
    public boolean execute(I_BenchmarkCommand command){
        return command.execute();
    }
}
