package edu.touro.mcon152.bm.command;

import edu.touro.mcon152.bm.observers.I_Observer;
import edu.touro.mcon152.bm.persist.DiskRun;

import java.util.LinkedList;
import java.util.List;

/**
 * This is the executor.
 * It is the subject for the executor pattern.
 * It allows us to register an observer. It holds a list of these observers.
 * Then, after it calls the execute of the command, it calls the onComplete of each observer.
 */
public class SimpleExecutor {
    List<I_Observer> observers = new LinkedList<>();
    public boolean execute(I_BenchmarkCommand command){
        boolean success = false;
        if (command.execute()){
            success = true;
            DiskRun run = command.getResult();
            for (I_Observer obs: observers){
                obs.onComplete(run);
                System.out.println("WriteBenchmark1: run is " + run);
            }
        }
        return success;
    }
    public void register(I_Observer observer){
        observers.add(observer);
    }
}
