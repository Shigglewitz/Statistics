package org.dkeeney.git.commit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TestContainer {
    private final String name;
    private int testsRun;
    private int testsFailed;
    private int testsError;
    private int testsSkipped;
    private String timeElapsed;
    private boolean failed;
    private final List<String> failedTests;

    public TestContainer(String name) {
        this(name, 0, 0, 0, 0, "");
    }

    public TestContainer(String name, int testsRun, int testsFailed,
            int testsError, int testsSkipped, String timeElapsed) {
        this.name = name;
        this.testsRun = testsRun;
        this.testsFailed = testsFailed;
        this.testsError = testsError;
        this.testsSkipped = testsSkipped;
        this.timeElapsed = timeElapsed;
        this.failed = false;
        this.failedTests = new ArrayList<>();
    }

    public void update(int run, int failed, int error, int skipped,
            String elapsed, String failureIndication) {
        this.testsRun = run;
        this.testsFailed = failed;
        this.testsError = error;
        this.testsSkipped = skipped;
        this.timeElapsed = elapsed;
        this.failed = failureIndication != null
                && !"".equals(failureIndication);
    }

    public void addFailedTest(String failedTest) {
        this.failedTests.add(failedTest);
    }

    public List<String> getFailedTests() {
        return Collections.unmodifiableList(this.failedTests);
    }

    public String getName() {
        return this.name;
    }

    public int getTestsRun() {
        return this.testsRun;
    }

    public int getTestsFailed() {
        return this.testsFailed;
    }

    public int getTestsError() {
        return this.testsError;
    }

    public int getTestsSkipped() {
        return this.testsSkipped;
    }

    public String getTimeElapsed() {
        return this.timeElapsed;
    }

    public boolean isFailed() {
        return this.failed;
    }
}
