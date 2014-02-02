package org.dkeeney.git.commit;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dkeeney.git.Parser;

public class Commit {
    private final List<Module> modules;
    private final String hash;
    private String author;
    private String merge;
    private Date created;
    private String comments;

    private enum ParseState {
        FIND_BUILD_ORDER, PARSE_BUILD_ORDER, FIND_PROJECT_START, FIND_TESTS, FIND_TEST_RUN, FIND_TEST_RESULT, FIND_PROJECT_RESULT, PARSE_SUMMARY
    }

    private static final String MAVEN_SEPARATOR = "[INFO] ------------------------------------------------------------------------";
    private static final String BUILD_ORDER_START = "[INFO] Reactor Build Order:";
    private static final String BUILD_ORDER_LISTING_REGEX = "\\[INFO\\] [^\\s].+";
    private static final String PROJECT_START_REGEX = "\\[INFO\\] Building .+";
    private static final String TEST_START = " T E S T S";
    private static final String TEST_RUNNING_REGEX = "Running [^\\s]+";
    private static final String TEST_RESULT_REGEX = "Tests run: ([\\d]+), "
            + "Failures: ([\\d]+), " + "Errors: ([\\d]+), "
            + "Skipped: ([\\d]+), " + "Time elapsed: (.*)";
    private static final Pattern TEST_RESULT_PATTERN = Pattern
            .compile(TEST_RESULT_REGEX);
    private static final String PROJECT_RESULTS = "Results :";
    private static final String PROJECT_RESULT_REGEX = "Tests run: ([\\d]+), "
            + "Failures: ([\\d]+), " + "Errors: ([\\d]+), "
            + "Skipped: ([\\d]+)";
    private static final Pattern PROJECT_RESULT_PATTERN = Pattern
            .compile(PROJECT_RESULT_REGEX);
    private static final String REACTOR_SUMMARY = "[INFO] Reactor Summary:";

    public Commit(String hash) {
        this.hash = hash;
        this.modules = new ArrayList<Module>();
        this.comments = "";
    }

    public void loadCommitData() {
        this.loadCommitData("gitStats/");
    }

    public void loadCommitData(String folder) {
        File logFile = new File(Parser.testDir.getAbsolutePath() + "/" + folder
                + this.getHash() + ".txt");
        Scanner in = null;
        ParseState searchState = ParseState.FIND_BUILD_ORDER;
        String line;
        int currentModule = -1;
        int currentTest = 0;
        Matcher matcher;
        boolean announcementIncoming = false;

        try {
            in = new Scanner(logFile);
            while (in.hasNextLine()) {
                line = in.nextLine();
                switch (searchState) {
                case FIND_BUILD_ORDER:
                    if (line.equals(BUILD_ORDER_START)) {
                        // get past the blank line
                        in.nextLine();
                        searchState = ParseState.PARSE_BUILD_ORDER;
                    }
                    break;
                case PARSE_BUILD_ORDER:
                    if (line.matches(BUILD_ORDER_LISTING_REGEX)) {
                        this.modules.add(new Module(line.split(" ", 2)[1]
                                .trim()));
                    } else {
                        searchState = ParseState.FIND_PROJECT_START;
                    }
                    break;
                case FIND_PROJECT_START:
                    if (announcementIncoming) {
                        if (line.matches(PROJECT_START_REGEX)) {
                            searchState = ParseState.FIND_TESTS;
                            currentModule++;
                        } else if (line.equals(REACTOR_SUMMARY)) {
                            searchState = ParseState.PARSE_SUMMARY;
                        }
                    }
                    break;
                case FIND_TESTS:
                    if (announcementIncoming
                            && line.matches(PROJECT_START_REGEX)) {
                        currentModule++;
                    } else if (line.equals(TEST_START)) {
                        searchState = ParseState.FIND_TEST_RUN;
                        currentTest = -1;
                    }
                    break;
                case FIND_TEST_RUN:
                    if (line.matches(TEST_RUNNING_REGEX)) {
                        this.modules.get(currentModule).addTestContainer(
                                new TestContainer(line.split(" ")[1]));
                        searchState = ParseState.FIND_TEST_RESULT;
                        currentTest++;
                    } else if (line.equals(PROJECT_RESULTS)) {
                        searchState = ParseState.FIND_PROJECT_RESULT;
                    }
                    break;
                case FIND_TEST_RESULT:
                    if (line.matches(TEST_RESULT_REGEX)) {
                        matcher = TEST_RESULT_PATTERN.matcher(line);
                        if (matcher.find()) {
                            this.modules
                                    .get(currentModule)
                                    .getTestContainers()
                                    .get(currentTest)
                                    .update(Integer.parseInt(matcher.group(1)),
                                            Integer.parseInt(matcher.group(2)),
                                            Integer.parseInt(matcher.group(3)),
                                            Integer.parseInt(matcher.group(4)),
                                            matcher.group(5));
                        }
                        searchState = ParseState.FIND_TEST_RUN;
                    }
                    break;
                case FIND_PROJECT_RESULT:
                    if (line.matches(PROJECT_RESULT_REGEX)) {
                        matcher = PROJECT_RESULT_PATTERN.matcher(line);
                        if (matcher.find()) {
                            this.modules.get(currentModule).update(
                                    Integer.parseInt(matcher.group(1)),
                                    Integer.parseInt(matcher.group(2)),
                                    Integer.parseInt(matcher.group(3)),
                                    Integer.parseInt(matcher.group(4)));
                        }
                        searchState = ParseState.FIND_PROJECT_START;
                    }
                    break;
                case PARSE_SUMMARY:
                    break;
                default:
                    break;
                }
                announcementIncoming = line.equals(MAVEN_SEPARATOR);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            in.close();
        }
    }

    public void addModule(Module modules) {
        this.modules.add(modules);
    }

    public List<Module> getModules() {
        return Collections.unmodifiableList(this.modules);
    }

    public String getAuthor() {
        return this.author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getMerge() {
        return this.merge;
    }

    public void setMerge(String merge) {
        this.merge = merge;
    }

    public Date getCreated() {
        return this.created;
    }

    public String getComments() {
        return this.comments;
    }

    public void addCommentLine(String commentLine) {
        this.comments += commentLine + System.lineSeparator();
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getHash() {
        return this.hash;
    }
}
