package org.dkeeney.git.statistics;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;

import org.junit.Test;

public class StatisticsTest {
    @Test
    public void test() throws FileNotFoundException {
        File stats = new File("src/main/resources/git/gitlog.txt");
        assertTrue("Statistics file does not exist!", stats.exists());
        // Scanner in = new Scanner(stats);
        // while (in.hasNextLine()) {
        // System.out.println(in.nextLine());
        // }
        // in.close();
    }
}
