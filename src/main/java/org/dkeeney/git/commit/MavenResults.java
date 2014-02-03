package org.dkeeney.git.commit;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dkeeney.git.Parser;

public class MavenResults {
    private final List<Module> modules;
    private final String hash;
    private Outcome outcome;
    private String failureReason;

    private enum ParseState {
        FIND_BUILD_ORDER, PARSE_BUILD_ORDER, FIND_PROJECT_START, FIND_TESTS, PARSE_COMPILATION_ERRORS, FIND_TEST_RUN, FIND_TEST_RESULT, FIND_PROJECT_RESULT, PARSE_TEST_FAILURES, PARSE_SUMMARY
    }

    public enum Outcome {
        SUCCESS, FAILURE, ERROR
    }

    public enum TestOutcome {
        RUN, FAIL, ERROR, SKIP
    }

    private static final String MAVEN_SEPARATOR = "[INFO] ------------------------------------------------------------------------";
    private static final String BUILD_ORDER_START = "[INFO] Reactor Build Order:";
    private static final String BUILD_ORDER_LISTING_REGEX = "\\[INFO\\] [^\\s].+";
    private static final String PROJECT_START = "[INFO] Building ";
    private static final String PROJECT_START_REGEX = "\\[INFO\\] Building .+";
    private static final Pattern PROJECT_START_PATTERN = Pattern
            .compile("\\[INFO\\] Building (.*) (.*)$");
    private static final String COMPILATION_ERROR = "[ERROR] COMPILATION ERROR : ";
    private static final String COMPILATION_ERROR_REGEX = "\\[ERROR\\].*?([a-zA-Z0-9_$]+\\.java:\\[[\\d]+,[\\d]+\\] error:.*)";
    private static final Pattern COMPILATION_ERROR_PATTERN = Pattern
            .compile(COMPILATION_ERROR_REGEX);
    // maven is weird about white space on this line
    private static final String COMPILATION_ERROR_SUMMATION_REGEX = "\\[INFO\\] [\\d]+[ ]?error[s]? ?";
    private static final String TEST_START = " T E S T S";
    private static final String TEST_RUNNING_REGEX = "Running [^\\s]+";
    private static final String TEST_RESULT_REGEX = "Tests run: ([\\d]+), "
            + "Failures: ([\\d]+), " + "Errors: ([\\d]+), "
            + "Skipped: ([\\d]+), " + "Time elapsed: (.+?)( <<< FAILURE!)?$";
    private static final Pattern TEST_RESULT_PATTERN = Pattern
            .compile(TEST_RESULT_REGEX);
    private static final String PROJECT_RESULTS = "Results :";
    private static final String TESTS_IN_ERROR = "Tests in error: ";
    private static final String TEST_ERROR_REGEX = "\\s+([^\\s(]+)\\(([^\\s)]+)\\):(.*)";
    private static final Pattern TEST_ERROR_PATTERN = Pattern
            .compile(TEST_ERROR_REGEX);
    private static final String PROJECT_RESULT_REGEX = "Tests run: ([\\d]+), "
            + "Failures: ([\\d]+), " + "Errors: ([\\d]+), "
            + "Skipped: ([\\d]+)";
    private static final Pattern PROJECT_RESULT_PATTERN = Pattern
            .compile(PROJECT_RESULT_REGEX);
    private static final String REACTOR_SUMMARY = "[INFO] Reactor Summary:";
    private static final String REACTOR_OUTCOME_REGEX = "\\[INFO\\] .*? \\.+ ([^\\s]+)( \\[.*?\\])?";
    private static final Pattern REACTOR_OUTCOME_PATTERN = Pattern
            .compile(REACTOR_OUTCOME_REGEX);
    private static final String BUILD_OUTCOME_REGEX = "\\[INFO\\] BUILD .+";

    public MavenResults(String hash) {
        this.hash = hash;
        this.modules = new ArrayList<Module>();
    }

    public void loadMavenResults() {
        this.loadMavenResults("gitStats/");
    }

    public void loadMavenResults(String folder) {
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
                    } else if (announcementIncoming
                            && line.matches(PROJECT_START_REGEX)) {
                        searchState = ParseState.FIND_TESTS;
                        currentModule++;
                        matcher = PROJECT_START_PATTERN.matcher(line);
                        if (matcher.find()) {
                            this.modules.add(new Module(matcher.group(1)));
                            this.modules.get(currentModule).setVersion(
                                    matcher.group(2));
                        }
                    } else if (line.startsWith("[ERROR]")) {
                        this.outcome = Outcome.ERROR;
                        this.failureReason = in.nextLine();
                        return;
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
                            this.modules
                                    .get(currentModule)
                                    .setVersion(
                                            line.substring((PROJECT_START + this.modules
                                                    .get(currentModule)
                                                    .getName()).length() + 1));
                        } else if (line.equals(REACTOR_SUMMARY)) {
                            searchState = ParseState.PARSE_SUMMARY;
                            currentModule = 0;
                        } else if (line.matches(BUILD_OUTCOME_REGEX)) {
                            this.modules.get(currentModule)
                                    .setOutcomeFromMavenString(line);
                            this.setOutcome(line);
                        }
                    }
                    break;
                case FIND_TESTS:
                    if (announcementIncoming
                            && line.matches(PROJECT_START_REGEX)) {
                        currentModule++;
                        this.modules
                                .get(currentModule)
                                .setVersion(
                                        line.substring((PROJECT_START + this.modules
                                                .get(currentModule).getName())
                                                .length() + 1));
                    } else if (line.equals(TEST_START)) {
                        searchState = ParseState.FIND_TEST_RUN;
                        currentTest = -1;
                    } else if (line.equals(COMPILATION_ERROR)) {
                        this.modules.get(currentModule).setOutcome(
                                Module.Outcome.COMPILATION_ERROR);
                        searchState = ParseState.PARSE_COMPILATION_ERRORS;
                    }
                    break;
                case PARSE_COMPILATION_ERRORS:
                    if (line.matches(COMPILATION_ERROR_REGEX)) {
                        matcher = COMPILATION_ERROR_PATTERN.matcher(line);
                        if (matcher.find()) {
                            this.modules.get(currentModule).addFailureReason(
                                    matcher.group(1));
                        }
                    } else if (line.matches(COMPILATION_ERROR_SUMMATION_REGEX)) {
                        searchState = ParseState.FIND_PROJECT_START;
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
                                            matcher.group(5), matcher.group(6));
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
                    } else if (line.equals(TESTS_IN_ERROR)) {
                        this.modules.get(currentModule).addFailureReason(line);
                        searchState = ParseState.PARSE_TEST_FAILURES;
                    }
                    break;
                case PARSE_TEST_FAILURES:
                    if (line.matches(TEST_ERROR_REGEX)) {
                        matcher = TEST_ERROR_PATTERN.matcher(line);
                        if (matcher.find()) {
                            this.modules.get(currentModule).addFailedTest(
                                    matcher.group(1), matcher.group(2));
                        }
                    } else {
                        searchState = ParseState.FIND_PROJECT_RESULT;
                    }
                    break;
                case PARSE_SUMMARY:
                    if (line.matches(REACTOR_OUTCOME_REGEX)) {
                        matcher = REACTOR_OUTCOME_PATTERN.matcher(line);
                        if (matcher.find()) {
                            this.modules.get(currentModule).setOutcome(
                                    Module.Outcome.valueOf(matcher.group(1)));
                        }
                        currentModule++;
                    } else if (line.matches(BUILD_OUTCOME_REGEX)) {
                        this.setOutcome(line);
                    }
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

    private void setOutcome(String mavenLine) {
        this.outcome = Outcome.valueOf(mavenLine.split(" ", 3)[2]);
    }

    public void addModule(Module modules) {
        this.modules.add(modules);
    }

    public List<Module> getModules() {
        return Collections.unmodifiableList(this.modules);
    }

    public String getHash() {
        return this.hash;
    }

    public Outcome getOutcome() {
        return this.outcome;
    }

    public String getFailureReason() {
        return this.failureReason;
    }

    public int getTotalTestsRun() {
        return this.getTotalTests(TestOutcome.RUN);
    }

    public int getTotalTestsError() {
        return this.getTotalTests(TestOutcome.ERROR);
    }

    public int getTotalTestsFail() {
        return this.getTotalTests(TestOutcome.FAIL);
    }

    public int getTotalTestsSkip() {
        return this.getTotalTests(TestOutcome.SKIP);
    }

    private int getTotalTests(TestOutcome testOutcome) {
        if (this.modules == null) {
            return 0;
        } else {
            int sum = 0;
            for (Module m : this.modules) {
                switch (testOutcome) {
                case RUN:
                    sum += m.getTestsRun();
                    break;
                case FAIL:
                    sum += m.getTestsFailed();
                    break;
                case ERROR:
                    sum += m.getTestsError();
                    break;
                case SKIP:
                    sum += m.getTestsSkipped();
                    break;
                }
            }

            return sum;
        }
    }
}
