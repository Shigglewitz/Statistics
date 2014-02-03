package org.dkeeney.git;

import java.awt.Color;
import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import org.dkeeney.git.commit.Commit;
import org.dkeeney.graphing.DataGrapher;
import org.dkeeney.utils.ImageMaker;

public class Parser {
    private static final String GIT_COMMIT_REGEX = "commit [0-9a-f]{40}";
    private static final String GIT_MERGE_REGEX = "Merge: [0-9a-f]{7} [0-9a-f]{7}";
    private static final String GIT_AUTHOR_REGEX = "Author: .+";
    private static final String GIT_DATE_REGEX = "Date: .+";
    private static final String GIT_COMMENT_REGEX = "    .+";
    private static final DateFormat SDF = new SimpleDateFormat(
            "EEE MMM dd HH:mm:ss yyyy ZZZZZ");
    public static final File testDir = getRootDirectory();

    public static File getRootDirectory() {
        try {
            return new File(Thread.currentThread().getContextClassLoader()
                    .getResource("").toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Commit> parseCommitHistory() {
        return this.parseCommitHistory(new File(testDir.getAbsolutePath()
                + "/gitCommits/gitlog.txt"));
    }

    public List<Commit> parseCommitHistory(File gitLog) {
        Scanner in = null;
        List<Commit> commitHistory = new ArrayList<>();
        Commit commit = null;
        String line;

        try {
            in = new Scanner(gitLog);
            while (in.hasNextLine()) {
                line = in.nextLine();
                if (line.matches(GIT_COMMIT_REGEX)) {
                    if (commit != null) {
                        commitHistory.add(commit);
                    }
                    commit = new Commit(line.split(" ")[1]);
                } else if (line.matches(GIT_MERGE_REGEX)) {
                    commit.setMerge(line.split(" ", 2)[1]);
                } else if (line.matches(GIT_AUTHOR_REGEX)) {
                    commit.setAuthor(line.split(" ", 2)[1]);
                } else if (line.matches(GIT_DATE_REGEX)) {
                    commit.setCreated(this.parseDate(line.split(" ", 2)[1]
                            .trim()));
                } else if (line.matches(GIT_COMMENT_REGEX)) {
                    commit.addCommentLine(line.split("    ", 2)[1].trim());
                }
            }

            if (commit != null) {
                commitHistory.add(commit);
            }

            Collections.reverse(commitHistory);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            in.close();
        }

        return commitHistory;
    }

    public Date parseDate(String date) {
        try {
            return SDF.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) throws IOException {
        Parser p = new Parser();
        List<Commit> commitHistory = p.parseCommitHistory();
        List<Point> totalTestsRun = new ArrayList<>();
        List<Point> totalTestsFail = new ArrayList<>();
        List<Point> totalTestsError = new ArrayList<>();
        List<Point> totalTestsSkip = new ArrayList<>();
        int i = 0;
        for (Commit c : commitHistory) {
            c.loadCommitData();
            totalTestsRun.add(new Point(i, c.getMavenResults()
                    .getTotalTestsRun()));
            totalTestsFail.add(new Point(i, c.getMavenResults()
                    .getTotalTestsFail()));
            totalTestsError.add(new Point(i, c.getMavenResults()
                    .getTotalTestsError()));
            totalTestsSkip.add(new Point(i, c.getMavenResults()
                    .getTotalTestsSkip()));
            i++;
        }
        DataGrapher dg = new DataGrapher();
        dg.addPoints(totalTestsRun, Color.GREEN);
        dg.addPoints(totalTestsError, Color.RED);
        dg.addPoints(totalTestsFail, Color.BLUE);
        dg.addPoints(totalTestsSkip, Color.BLACK);
        ImageMaker.saveImage(dg.getGraph(), "commit-history");
        System.out.println("Success");

    }
}
