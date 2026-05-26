package edu.touro.mcon152.bm.observers;

import edu.touro.mcon152.bm.DiskMark;
import edu.touro.mcon152.bm.externalsys.SlackManager;
import edu.touro.mcon152.bm.persist.DiskRun;

import java.util.List;
import java.util.logging.Logger;

/**
 * This is a Slack Observer.
 * It is called when a read benchmark is finished and it is found
 * that there was a slow mark - which took three percent longer
 * than the average benchmark. Then, it sends a message to Slack.
 */
public class SlackObserver implements I_Observer{
    SlackManager manager = new SlackManager("BadBM");
    @Override
    public void onComplete(DiskRun run) {
        List<DiskMark> markList = run.getMarkList();
        if (markList == null
        || markList.isEmpty()
        || run.getIoMode() != DiskRun.IOMode.READ){
            return;
        }
        long sum = 0;
        long max = 0;
        for (DiskMark mark: markList){
            long time = mark.getTime();
            sum += time;
            if (time > max){
                max = time;
            }
        }
        double average = (double) sum / markList.size();
        if (average * 1.03 < max){
            Boolean worked = manager.postMsg2OurChannel(":smile: Benchmark completed");
            System.out.println("It worked!");
        }

    }
}
