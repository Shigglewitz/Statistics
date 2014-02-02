package org.dkeeney.git.commit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;

public class CommitTest {
    @Test
    public void testLoadCommitData() {
        Commit c = new Commit("a67d6d293e1eec8e94d198c590eade1e5fe14a1f");
        c.loadCommitData("testGitStats/");
        List<Module> modules = c.getModules();
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
}
