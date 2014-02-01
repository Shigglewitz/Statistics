package org.dkeeney.git;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.Stack;

public class Statistics {
    public static void main(String[] args) throws IOException {
        File gitLog = new File(
                "statistics/src/main/resources/gitCommits/gitlog.txt");
        File commitsFile = new File(
                "statistics/src/main/resources/gitCommits/commits.txt");
        Scanner in = new Scanner(gitLog);
        Stack<String> commits = new Stack<>();
        String nextLine;
        StringBuilder commitsTxt = new StringBuilder();
        BufferedWriter writer = null;
        try {
            while (in.hasNextLine()) {
                nextLine = in.nextLine();
                if (nextLine.matches("commit [0-9a-f]{40}")) {
                    commits.push(nextLine.split(" ")[1]);
                }
            }
            while (commits.size() > 0) {
                commitsTxt.append(String.format(commits.pop() + "%n"));
            }
            writer = new BufferedWriter(new FileWriter(commitsFile));
            writer.write(commitsTxt.toString());
        } finally {
            in.close();
            writer.close();
        }
    }
}
