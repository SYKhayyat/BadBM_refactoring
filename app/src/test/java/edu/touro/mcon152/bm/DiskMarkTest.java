package edu.touro.mcon152.bm;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

public class DiskMarkTest {

    /**
     * This test illustrates Cross-Checking,
     * since I use a simpler built-in method to verify the output of the method tested.
     */
    @Test
    public void getBwMBSecAsStringTest(){
        DiskMark dm = new DiskMark(DiskMark.MarkType.WRITE);
        double num = 12.04;
        dm.setBwMbSec(num);
        String actual = dm.getBwMbSecAsString();
        String expected = String.valueOf(num);
        Assertions.assertEquals(actual, expected);
    }
    @Test
    public void diskMarkToStringTest(){
        DiskMark dm = new DiskMark(DiskMark.MarkType.WRITE);
        dm.setBwMbSec(12.32);
        dm.setMarkNum(5);
        dm.setCumAvg(32.167);
        String actual = dm.toString();
        String expected =  "Mark(WRITE): 5 bwMbSec: 12.32 avg: 32.167";
        Assertions.assertEquals(actual, expected);
    }

    /**
     * This test illustrates the Border Case of Existence,
     * since it checks what happens when a null is input.
     */
    @Test
    public void diskMarkToStringTestNull(){
        DiskMark dm = null;
        boolean valid = true;
        Assertions.assertThrows(NullPointerException.class, () -> {
            dm.setBwMbSec(12.32);
            dm.setMarkNum(5);
            dm.setCumAvg(32.167);
            String actual = dm.toString();
            String expected = "Mark(WRITE): 5 bwMbSec: 12.32 avg: 32.167";
        });        HashSet<Integer> randomInts = new HashSet<>();

    }
}
