package org.dkeeney.git.commit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Module {
    private final String name;
    private int testsRun;
    private int testsFailed;
    private int testsError;
    private int testsSkipped;
    private String version;
    private final List<TestContainer> tests;
    private String failureReason;

    public Module(String name) {
        this(name, 0, 0, 0, 0);
    }

    public Module(String name, int testsRun, int testsFailed, int testsError,
            int testsSkipped) {
        this.name = name;
        this.testsRun = testsRun;
        this.testsFailed = testsFailed;
        this.testsError = testsError;
        this.testsSkipped = testsSkipped;
        this.tests = new ArrayList<>();
        this.failureReason = "";
    }

    public boolean successfulBuild() {
        return this.testsFailed == 0 && this.testsError == 0;
    }

    public void addFailedTest(String testMethod, String testClass) {
        for (TestContainer t : this.tests) {
            if (testClass.equals(t.getName())) {
                t.addFailedTest(testMethod);
            }
        }
    }

    public void addTestContainer(TestContainer testContainer) {
        this.tests.add(testContainer);
    }

    public List<TestContainer> getTestContainers() {
        return Collections.unmodifiableList(this.tests);
    }

    public void update(int run, int failed, int error, int skipped) {
        this.testsRun = run;
        this.testsFailed = failed;
        this.testsError = error;
        this.testsSkipped = skipped;
    }

    public void addFailureReason(String reason) {
        this.failureReason = this.failureReason + reason
                + System.lineSeparator();
    }

    public String getFailureReason() {
        return this.failureReason;
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

    public String getVersion() {
        return this.version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
