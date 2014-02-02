package org.dkeeney.git.commit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;

public class MavenResultsTest {
    @Test
    public void testLoadCommitData() {
        Commit c = new Commit("a67d6d293e1eec8e94d198c590eade1e5fe14a1f");
        c.loadCommitData("testGitStats/");
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

        m = modules.get(3);
        tests = m.getTestContainers();
        assertEquals("Wrong test class name.",
                "org.dkeeney.utils.ColorUtilsTest", tests.get(0).getName());
        assertEquals("Wrong test class name.",
                "org.dkeeney.utils.ImageMakerTest", tests.get(1).getName());
        assertEquals("Wrong test class name.", "org.dkeeney.utils.UtilsTest",
                tests.get(2).getName());
        assertEquals("Wrong number of tests ran", 4, tests.get(0).getTestsRun());
        assertEquals("Wrong number of tests ran", 3, tests.get(1).getTestsRun());
        assertEquals("Wrong number of tests ran", 6, tests.get(2).getTestsRun());
        assertEquals("Wrong total number of tests ran", 13, m.getTestsRun());
    }

    @Test
    public void testLoadCommitDataWithoutReactorBuildOrder() {
        Commit c = new Commit("75f085922f776ff41d3c3365dab6cc0e99f93047");
        c.loadCommitData("testGitStats/");
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

        tests = m.getTestContainers();
        assertEquals("Wrong number of test classes", 2, tests.size());
        assertEquals("Wrong test class",
                "org.dkeeney.graphing.equations.EquationTest", tests.get(0)
                        .getName());
        assertEquals("Wrong number of tests ran", 7, tests.get(0).getTestsRun());
        assertEquals("Wrong test class", "org.dkeeney.utils.UtilsTest", tests
                .get(1).getName());
        assertEquals("Wrong number of tests ran", 4, tests.get(1).getTestsRun());
        assertEquals("Wrong time elapsed", "0 sec", tests.get(1)
                .getTimeElapsed());
        assertEquals("Wrong total number of tests ran", 11, m.getTestsRun());
    }
}
