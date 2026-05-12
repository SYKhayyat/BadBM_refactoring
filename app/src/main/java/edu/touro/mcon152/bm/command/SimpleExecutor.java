package edu.touro.mcon152.bm.command;

public class SimpleExecutor {
    public boolean execute(I_BenchmarkCommand command){
        return command.execute();
    }
}
