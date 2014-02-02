package org.dkeeney.git.commit;

public class TestContainer {
    private final String name;
    private int testsRun;
    private int testsFailed;
    private int testsError;
    private int testsSkipped;
    private String timeElapsed;

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
    }

    public void update(int run, int failed, int error, int skipped,
            String elapsed) {
        this.testsRun = run;
        this.testsFailed = failed;
        this.testsError = error;
        this.testsSkipped = skipped;
        this.timeElapsed = elapsed;
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
