package edu.touro.mcon152.bm;

import edu.touro.mcon152.bm.persist.DiskRun;
import edu.touro.mcon152.bm.ui.Gui;

import javax.swing.*;
import java.util.List;
import java.util.logging.Logger;

import static edu.touro.mcon152.bm.App.dataDir;

/**
 * A class that extends SwingWorker to provide a UI implementation to work with DiskWorker.
 * <p>
 * This is a refactored version of UI of the original badBM.
 * It is used to have SwingWorker interact with DiskWorker.
 */

public class SwingUI extends SwingWorker<Boolean, DiskMark> implements I_UI {

    DiskWorker dw;

    @Override
    public void setDW(DiskWorker dw){
        this.dw = dw;
        dw.setUserInterface(this);
    }

    public void finish(){
        if (App.autoRemoveData) {
            Util.deleteDirectory(dataDir);
        }
        App.state = App.State.IDLE_STATE;
        Gui.mainFrame.adjustSensitivity();
    }


    @Override
    public void init(){
        Gui.updateLegend();  // init chart legend info
        if (App.autoReset) {
            App.resetTestData();
            Gui.resetTestData();
        }
    }
    @Override
    public void displayRun(DiskRun run){
        SwingUtilities.invokeLater(() -> {
            Gui.runPanel.addRun(run);
        });
    }
    @Override
    public void setTitle(DiskRun run){
        SwingUtilities.invokeLater(() -> {
            Gui.chartPanel.getChart().getTitle().setVisible(true);
            Gui.chartPanel.getChart().getTitle().setText(run.getDiskInfo());
        });
    }


    @Override
    public boolean hasBeenCancelled() {
        return isCancelled();
    }

    @Override
    public void setRunProgress(int progress) {
        setProgress(progress);
    }

    @Override
    public void setResults(DiskMark wMark) {
        publish(wMark);
    }


    @Override
    public void showMessage(String s, String s1) {SwingUtilities.invokeLater(() -> {
        JOptionPane.showMessageDialog(Gui.mainFrame, s, s1, JOptionPane.PLAIN_MESSAGE);
    });
    }

    @Override
    public void done(){
        try {
            super.get(); // optional, just to catch exceptions
        } catch (Exception e) {
            Logger.getLogger(App.class.getName()).warning("Problem obtaining final status: " + e.getMessage());
        }
        finish();
    }

    @Override
    protected Boolean doInBackground() throws Exception {
        Boolean status;
        init();
        status = dw.runBenchmark();
        return status;
    }
    /**
     * Process a list of 'chunks' that have been processed, ie that our thread has previously
     * published to Swing. For my info, watch Professor Cohen's video -
     * Module_6_RefactorBadBM Swing_DiskWorker_Tutorial.mp4
     * @param markList a list of edu.touro.mcon152.bm.DiskMark objects reflecting some completed benchmarks
     */
    @Override
    protected void process(List<DiskMark> markList) {
        markList.stream().forEach((dm) -> {
            if (dm.type == DiskMark.MarkType.WRITE) {
                Gui.addWriteMark(dm);
            } else {
                Gui.addReadMark(dm);
            }
        });
    }
}
