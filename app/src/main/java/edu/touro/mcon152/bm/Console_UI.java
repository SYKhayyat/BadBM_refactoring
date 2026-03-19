package edu.touro.mcon152.bm;

import edu.touro.mcon152.bm.persist.DiskRun;

import java.util.ArrayList;
import java.util.List;

public class Console_UI implements I_UI{
    DiskWorker dw;
    private int lastProgress;
    private List<DiskMark> marks = new ArrayList<>();
    private DiskRun lastRun = null;
    private boolean finished = false;
    private boolean cancelled = false;

    public void cancel() {
        cancelled = true;
    }


    public int getLastProgress(){
        return lastProgress;
    }
    public List<DiskMark> getMarks(){
        return marks;
    }
    public DiskRun getLastRun(){
        return lastRun;
    }
    public boolean getFinished(){
        return finished;
    }
    public Boolean runBenchmark() throws Exception{
        init();
        try {
            return dw.runBenchmark();
        } finally {
            finish();
        }
    }
    @Override
    public void setDW(DiskWorker dw) {
        this.dw = dw;
        dw.setUserInterface(this);
    }

    @Override
    public void init() {
        if (App.autoReset) {
            App.resetTestData();
        }
    }

    @Override
    public void setTitle(DiskRun run) {
        System.out.println(run.getDiskInfo());
    }

    @Override
    public boolean hasBeenCancelled() {
        return cancelled;
    }

    @Override
    public void setRunProgress(int progress) {
        lastProgress = progress;
        System.out.println("Progress: " + progress + "%");
    }

    @Override
    public void setResults(DiskMark wMark) {
        marks.add(wMark);
        System.out.println("Mark " + wMark.getMarkNum() + ": " + wMark.getBwMbSecAsString() + " MB/s");
    }

    @Override
    public void displayRun(DiskRun run) {
        lastRun = run;
        System.out.println("Run complete:");
        System.out.println("  Avg: " + run.getRunAvg() + " MB/s");
        System.out.println("  Max: " + run.getRunMax() + " MB/s");
        System.out.println("  Min: " + run.getRunMin() + " MB/s");
    }

    @Override
    public void finish() {
        finished = true;
        if (App.autoRemoveData) {
            Util.deleteDirectory(App.dataDir);
        }
        App.state = App.State.IDLE_STATE;
    }

    @Override
    public void showMessage(String s, String s1) {
        System.err.println(s1);
        System.err.println(s);
    }
}
