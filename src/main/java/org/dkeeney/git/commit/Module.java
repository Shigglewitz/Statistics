package org.dkeeney.git.commit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Module {
    private final String name;
    private final int testsRun;
    private final int testsFailed;
    private final int testsError;
    private final int testsSkipped;
    private final List<TestContainer> tests;

    public Module(String name, int testsRun, int testsFailed, int testsError,
            int testsSkipped) {
        this.name = name;
        this.testsRun = testsRun;
        this.testsFailed = testsFailed;
        this.testsError = testsError;
        this.testsSkipped = testsSkipped;
        this.tests = new ArrayList<>();
    }

    public boolean successfulBuild() {
        return this.testsFailed == 0 && this.testsError == 0;
    }

    public void addTestContainer(TestContainer testContainer) {
        this.tests.add(testContainer);
    }

    public List<TestContainer> getTestContainers() {
        return Collections.unmodifiableList(this.tests);
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
}
