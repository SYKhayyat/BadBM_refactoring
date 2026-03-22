package edu.touro.mcon152.bm;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.eclipse.persistence.jpa.jpql.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.Duration;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UtilTest {

    /**
     * This test demonstrates many principles:
     * The Right Principle - In this test I put in right inputs and got a obviously right answer.
     * In other words, I have everything working just as it should.
     * Border Conditions - Range - This checks for range, since I make sure that no numbers that
     * are less than min or greater than max are ever returned.
     * Border Conditions - Cardinality - This checks for cardinality,
     * since I ensure that every number within the range is produced.
     * After running the method enough times, this test shows that all eligible values will be produced.
     */
    @Test
    public void randIntTest(){
        int lowerBound = 2;
        int upperBound = 6;

        int result = Util.randInt(lowerBound, upperBound);

        Assertions.assertEquals(4, result, 2);
        HashSet<Integer> randomInts = new HashSet<>();
        for (int i = 0; i < 500; i++) {
            randomInts.add(Util.randInt(lowerBound, upperBound));
        }
        int setSize = randomInts.size();
        Assertions.assertEquals(5, setSize);
        boolean inBounds = true;
        for (int i :randomInts){
            if (i < 2 || i > 6){
                inBounds = false;
            }
        }
        Assertions.assertTrue(inBounds);
    }

    @Test
    public void randIntBoundariesTest(){
        int lowerBound = -1;
        int upperBound = 6;

        HashSet<Integer> randomInts = new HashSet<>();
        for (int i = 0; i < 50; i++) {
            randomInts.add(Util.randInt(lowerBound, upperBound));
        }
        int setSize = randomInts.size();
        Assertions.assertEquals(8, setSize);
        boolean inBounds = true;
        for (int i :randomInts){
            if (i < -1 || i > 6){
                inBounds = false;
            }
        }
        Assertions.assertTrue(inBounds);
    }

    /**
     * This test illustrates Error Conditions, as it checks to ensure that an appropriate error is thrown.
     */
    @Test
    public void randIntBoundariesTestTwo(){
        int lowerBound = 8;
        int upperBound = 6;

        Assertions.assertThrows(IllegalArgumentException.class, () -> Util.randInt(lowerBound, upperBound));        HashSet<Integer> randomInts = new HashSet<>();
        // i will point out that the error text message is very misleading.
    }
    @Test
    public void randIntBoundariesTestThree(){
        int lowerBound = 8;
        int upperBound = 8;

        HashSet<Integer> randomInts = new HashSet<>();
        for (int i = 0; i < 50; i++) {
            randomInts.add(Util.randInt(lowerBound, upperBound));
        }
        int setSize = randomInts.size();
        Assertions.assertEquals(1, setSize);
        boolean inBounds = true;
        for (int i :randomInts){
            if (i < 8 || i > 8){
                inBounds = false;
            }
        }
        Assertions.assertTrue(inBounds);
    }

    /**
     * This test illustrates Performance, as it tests that the method is quick enough
     * to be done many times within a short timeframe.
     */
    @Test
    public void randIntPerformanceTest(){
        Assertions.assertTimeout(Duration.ofMillis(500), () -> {
            for (int i = 0; i < 1000; i++) {
                Util.randInt(1, 100);
            }
        });
    }

    /**
     * This is my parameterized test.
     * @param num - the double to be converted into a string.
     * @param expected - the string returned.
     */
    @ParameterizedTest
    @CsvSource({
            "10.12, 10.12",
            "0.0003, 0",
            ".1234567890987654321, 0.12",
            "-5.3, -5.3"
    })
    public void displayStringTest(double num, String expected) {
        String result = Util.displayString(num);
        assertEquals(expected, result);
    }
}
