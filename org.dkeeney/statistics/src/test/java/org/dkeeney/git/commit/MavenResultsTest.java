package org.dkeeney.git.commit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

public class MavenResultsTest {
    private static final String STATS_FOLDER = "testGitStats/";

    @Test
    public void testLoadMavenResults() {
        Commit c = new Commit("a67d6d293e1eec8e94d198c590eade1e5fe14a1f");
        c.loadCommitData(STATS_FOLDER);
        List<Module> modules = c.getMavenResults().getModules();
        List<TestContainer> tests = null;
        Module m = null;
        String[] moduleNames = { "DKeeney Projects", "Common Config",
                "Test Utils", "Utilities", "Equations", "Graphing" };

        assertNotNull("No modules found", modules);
        assertEquals("Wrong number of modules found", moduleNames.length,
                modules.size());
        for (int i = 0; i < moduleNames.length; i++) {
            assertEquals("Modules in wrong order", moduleNames[i],
                    modules.get(i).getName());
            assertEquals("Wrong version in module " + modules.get(i).getName(),
                    "1.0.0", modules.get(i).getVersion());
            assertEquals("Wrong module outcome", Module.Outcome.SUCCESS,
                    modules.get(i).getOutcome());
        }

        m = modules.get(1);
        tests = m.getTestContainers();
        assertEquals("Wrong test class name.",
                "org.dkeeney.config.ConstantsTest", tests.get(0).getName());
        assertEquals("Wrong number of tests ran", 1, tests.get(0).getTestsRun());
        assertEquals("Wrong number of tests failed", 0, tests.get(0)
                .getTestsFailed());
        assertEquals("Wrong number of tests in error", 0, tests.get(0)
                .getTestsError());
        assertEquals("Wrong amount of time elapsed", "0.055 sec", tests.get(0)
                .getTimeElapsed());
        assertFalse("Test class should not have failed", tests.get(0)
                .isFailed());

        m = modules.get(3);
        tests = m.getTestContainers();
        assertEquals("Wrong test class name.",
                "org.dkeeney.utils.ColorUtilsTest", tests.get(0).getName());
        assertEquals("Wrong test class name.",
                "org.dkeeney.utils.ImageMakerTest", tests.get(1).getName());
        assertEquals("Wrong test class name.", "org.dkeeney.utils.UtilsTest",
                tests.get(2).getName());
        assertFalse("Test class should not have failed", tests.get(0)
                .isFailed());
        assertFalse("Test class should not have failed", tests.get(1)
                .isFailed());
        assertEquals("Wrong number of tests ran", 4, tests.get(0).getTestsRun());
        assertEquals("Wrong number of tests ran", 3, tests.get(1).getTestsRun());
        assertEquals("Wrong number of tests ran", 6, tests.get(2).getTestsRun());
        assertEquals("Wrong total number of tests ran", 13, m.getTestsRun());

        assertEquals("Wrong outcome", MavenResults.Outcome.SUCCESS, c
                .getMavenResults().getOutcome());
    }

    @Test
    public void testLoadMavenResultsWithoutReactorBuildOrder() {
        Commit c = new Commit("75f085922f776ff41d3c3365dab6cc0e99f93047");
        c.loadCommitData(STATS_FOLDER);
        List<Module> modules = c.getMavenResults().getModules();
        List<TestContainer> tests = null;
        Module m = null;
        String moduleName = "Graphing";

        assertNotNull("No modules found", modules);
        assertEquals("Not enough modules found", 1, modules.size());

        m = modules.get(0);
        assertEquals("Wrong name for module", moduleName, m.getName());
        assertEquals("Wrong version for module", "0.0.1-SNAPSHOT",
                m.getVersion());
        assertEquals("Wrong module outcome", Module.Outcome.SUCCESS,
                m.getOutcome());

        tests = m.getTestContainers();
        assertEquals("Wrong number of test classes", 2, tests.size());
        assertEquals("Wrong test class",
                "org.dkeeney.graphing.equations.EquationTest", tests.get(0)
                        .getName());
        assertEquals("Wrong number of tests ran", 7, tests.get(0).getTestsRun());
        assertEquals("Wrong test class", "org.dkeeney.utils.UtilsTest", tests
                .get(1).getName());
        assertFalse("Test class should not have failed", tests.get(0)
                .isFailed());
        assertEquals("Wrong number of tests ran", 4, tests.get(1).getTestsRun());
        assertEquals("Wrong time elapsed", "0 sec", tests.get(1)
                .getTimeElapsed());
        assertFalse("Test class should not have failed", tests.get(1)
                .isFailed());
        assertEquals("Wrong total number of tests ran", 11, m.getTestsRun());

        assertEquals("Wrong outcome", MavenResults.Outcome.SUCCESS, c
                .getMavenResults().getOutcome());
    }

    @Test
    public void testLoadMavenResultsWithTotalFailure() {
        Commit c = new Commit("6df90d8db099a57e25a2173f6fa30471eaa002bb");
        c.loadCommitData(STATS_FOLDER);
        List<Module> modules = c.getMavenResults().getModules();

        assertEquals("Wrong outcome", MavenResults.Outcome.ERROR, c
                .getMavenResults().getOutcome());
        assertTrue(
                "Wrong failure cause",
                c.getMavenResults()
                        .getFailureReason()
                        .startsWith(
                                "org.apache.maven.project.ProjectBuildingException"));
        assertNotNull("Did not initialize module list", modules);
        assertEquals("Should not have found any modules", 0, modules.size());
    }

    @Test
    public void testLoadMavenResultsWithTestErrors() {
        Commit c = new Commit("b4103a023c52324755094ccf95b06413512a434c");
        c.loadCommitData(STATS_FOLDER);
        List<Module> modules = c.getMavenResults().getModules();
        String moduleName = "Graphing";
        List<TestContainer> tests = null;
        Module m = null;

        assertNotNull("No modules found", modules);
        assertEquals("Wrong number of modules found", 1, modules.size());

        m = modules.get(0);
        assertEquals("Wrong module name", moduleName, modules.get(0).getName());
        assertEquals("Wrong module outcome", Module.Outcome.FAILURE,
                m.getOutcome());
        tests = m.getTestContainers();
        assertEquals("Wrong test class name",
                "org.dkeeney.graphing.equations.EquationTest", tests.get(0)
                        .getName());
        assertEquals("Wrong number of tests run", 10, tests.get(0)
                .getTestsRun());
        assertEquals("Wrong number of tests in error", 1, tests.get(0)
                .getTestsError());
        assertTrue("Test class should have failed", tests.get(0).isFailed());
        assertEquals("Wrong test class name", "org.dkeeney.utils.UtilsTest",
                tests.get(1).getName());
        assertEquals("Wrong number of tests run", 5, tests.get(1).getTestsRun());
        assertEquals("Wrong number of tests in error", 0, tests.get(1)
                .getTestsError());
        assertFalse("Test class should not have failed", tests.get(1)
                .isFailed());

        assertEquals("Wrong failure reason",
                "Tests in error: " + System.lineSeparator(),
                m.getFailureReason());
        assertEquals("Wrong number of test failures", 1, tests.get(0)
                .getFailedTests().size());
        assertEquals("Wrong test failure", "testEquationsWithParens", tests
                .get(0).getFailedTests().get(0));

        assertEquals("Wrong outcome", MavenResults.Outcome.FAILURE, c
                .getMavenResults().getOutcome());
    }

    @Test
    public void testLoadMavenResultsWithCompilationErrors() {
        Commit c = new Commit("cb43054eae782cb57d64b5f6532eefc764706789");
        c.loadCommitData(STATS_FOLDER);
        List<Module> modules = c.getMavenResults().getModules();
        Module m = null;

        m = modules.get(0);
        assertEquals("Wrong module outcome", Module.Outcome.COMPILATION_ERROR,
                m.getOutcome());
        assertEquals(
                "Wrong compilation failures",
                "EquationTest.java:[128,28] error: non-static method mapVariables(String,Map<String,Double>) cannot be referenced from a static context"
                        + System.lineSeparator()
                        + "EquationTest.java:[142,24] error: non-static method mapVariables(String,Map<String,Double>) cannot be referenced from a static context"
                        + System.lineSeparator(), m.getFailureReason());

        assertEquals("Wrong outcome", MavenResults.Outcome.FAILURE, c
                .getMavenResults().getOutcome());
    }

    @Test
    public void testLoadMavenResultsWithCompilationErrorsAndReactor() {
        Commit c = new Commit("2633c092ae597ab8d7a71f71bdb671e4f7550152");
        c.loadCommitData(STATS_FOLDER);
        List<Module> modules = c.getMavenResults().getModules();
        Module m = null;
        String[] moduleNames = { "DKeeney Projects", "Equations", "Graphing",
                "Utilities" };

        assertNotNull("Did not find any modules", modules);
        assertEquals("Did not find the right amount of modules",
                moduleNames.length, modules.size());
        for (int i = 0; i > moduleNames.length; i++) {
            assertEquals("Modules in incorrect order", moduleNames[i], modules
                    .get(0).getName());
        }

        m = modules.get(0);
        assertEquals("Wrong module outcome", Module.Outcome.SUCCESS,
                m.getOutcome());
        m = modules.get(1);
        assertEquals("Wrong module outcome", Module.Outcome.COMPILATION_ERROR,
                m.getOutcome());
        assertEquals("Wrong failure reason",
                "Equation.java:[27,24] error: package org.dkeeney.utils does not exist"
                        + System.lineSeparator()
                        + "Equation.java:[61,16] error: cannot find symbol"
                        + System.lineSeparator(), m.getFailureReason());
        m = modules.get(2);
        assertEquals("Wrong module outcome", Module.Outcome.SKIPPED,
                m.getOutcome());
        m = modules.get(3);
        assertEquals("Wrong module outcome", Module.Outcome.SKIPPED,
                m.getOutcome());

        assertEquals("Wrong outcome", MavenResults.Outcome.FAILURE, c
                .getMavenResults().getOutcome());
    }

    @Ignore
    @Test
    public void testLoadMavenResultsWithTestFailuresAndErrors() {
        Commit c = new Commit("9c6e8d091a19241a8ddb5934fdfc042547c8bcf7");
        c.loadCommitData(STATS_FOLDER);
        assertTrue(false);
    }
}
