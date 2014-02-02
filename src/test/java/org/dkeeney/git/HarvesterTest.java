package org.dkeeney.git;

import static org.junit.Assert.assertEquals;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Scanner;

import org.dkeeney.git.Harvester;
import org.junit.Test;

public class HarvesterTest {
    @Test
    public void testParsing() throws URISyntaxException, IOException {
        File testDir = new File(Thread.currentThread().getContextClassLoader()
                .getResource("").toURI());
        File testOutput;
        StringBuilder builder = new StringBuilder();
        BufferedWriter writer = null;
        Harvester h = new Harvester();
        Scanner in = null;
        String[] commitHashes = { "d1cc3b22cb4846d6f58d7671efd94624930fd94c",
                "d75c50f0c4f069f0fd672ef638d87d5d214a51e5",
                "67dad4264ad6aa706a82fe870bcf97d58c8b85d5",
                "10720e45331b347aa79f0d153fe885805757d931",
                "4b8602e75f3a3af6619602ec2358ebf0b6b752b8",
                "14a75aaeeb9f30ac003d85d3b1787b8da440478a",
                "e931c05b12a4953ef52489b4906630468111de33",
                "0546f3b9a89ff29a40ad6b9084d79c941f9241d2",
                "368aa34b3dff3149cbe34f805a99ec8a75b5d44b" };

        for (String s : commitHashes) {
            builder.append(String.format("commit %s%n", s));
        }

        File testLog = new File(testDir.getAbsolutePath() + "/log.txt");
        try {
            writer = new BufferedWriter(new FileWriter(testLog));
            writer.write(builder.toString());
            writer.close();
        } finally {
            try {
                writer.close();
            } catch (Exception e) {
            }
        }

        testLog = new File(testDir.getAbsolutePath() + "/log.txt");
        testOutput = new File(testDir.getAbsolutePath() + "/output.txt");

        h.parseCommitHashes(testLog, testOutput);

        testOutput = new File(testDir.getAbsolutePath() + "/output.txt");

        try {
            in = new Scanner(testOutput);
            int i = 0;
            while (in.hasNextLine()) {
                assertEquals(commitHashes[(commitHashes.length - 1) - i++],
                        in.nextLine());
            }
        } finally {
            in.close();
        }
    }
}
