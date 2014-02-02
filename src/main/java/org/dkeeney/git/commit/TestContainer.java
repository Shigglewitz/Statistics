package org.dkeeney.git.commit;

public class TestContainer {
    private final String name;
    private final int testsRun;
    private final int testsFailed;
    private final int testsError;
    private final int testsSkipped;
    private final String timeElapsed;

    public TestContainer(String name, int testsRun, int testsFailed,
            int testsError, int testsSkipped, String timeElapsed) {
        this.name = name;
        this.testsRun = testsRun;
        this.testsFailed = testsFailed;
        this.testsError = testsError;
        this.testsSkipped = testsSkipped;
        this.timeElapsed = timeElapsed;
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
}
