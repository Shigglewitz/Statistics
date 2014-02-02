package org.dkeeney.git.statistics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URISyntaxException;
import java.util.List;

import org.dkeeney.git.Parser;
import org.dkeeney.git.commit.Commit;
import org.dkeeney.git.commit.Module;
import org.dkeeney.git.commit.TestContainer;
import org.junit.Ignore;
import org.junit.Test;

public class ParserTest {
    private static final File testDir = getRootDirectory();

    public static File getRootDirectory() {
        try {
            return new File(Thread.currentThread().getContextClassLoader()
                    .getResource("").toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Test
    public void testParseCommitHistory() {
        Parser p = new Parser();
        List<Commit> commitHistory = p.parseCommitHistory(new File(testDir
                .getAbsolutePath() + "/testGitCommits/gitlog.txt"));
        assertEquals("Wrong number of commits parsed", 70, commitHistory.size());
        assertEquals("Wrong hash for beginning of commit history",
                "6df90d8db099a57e25a2173f6fa30471eaa002bb", commitHistory
                        .get(0).getHash());
        assertEquals("Wrong hash for end of commit history",
                "a67d6d293e1eec8e94d198c590eade1e5fe14a1f",
                commitHistory.get(commitHistory.size() - 1).getHash());
        assertEquals(
                "Wrong commit comment found",
                "Merge branch 'feature/refactor-modules' into develop"
                        + System.lineSeparator(),
                commitHistory.get(commitHistory.size() - 2).getComments());
        assertEquals("Wrong author found",
                "U-Daniel-PC\\Daniel <Daniel@Daniel-PC.(none)>", commitHistory
                        .get(31).getAuthor());
        assertEquals("Wrong author found",
                "Shigglewitz <shigglewitz@gmail.com>", commitHistory.get(32)
                        .getAuthor());
        for (Commit c : commitHistory) {
            if (c.getComments().startsWith("Merge branch")) {
                assertTrue("Merge attribute misisng for commit " + c.getHash(),
                        c.getMerge() != null && !c.getMerge().equals(""));
            }
        }
    }

    @Ignore
    @Test
    public void testLoadCommitData() {
        Parser p = new Parser();
        Commit c = new Commit("a67d6d293e1eec8e94d198c590eade1e5fe14a1f");
        p.loadCommitData(c, "testGitStats/");
        List<Module> modules = c.getModules();
        List<TestContainer> tests = null;
        Module m = null;
        String[] moduleNames = { "Common Config", "Test Utils", "Utilities",
                "Equations", "Graphing" };
        assertNotNull("No modules found", modules);
        assertEquals("Wrong number of modules found", moduleNames.length,
                modules.size());
        for (int i = 0; i < moduleNames.length; i++) {
            assertEquals("Modules in wrong order", moduleNames[i],
                    modules.get(i).getName());
        }

        m = modules.get(0);
        tests = m.getTestContainers();
        assertEquals("Wrong test class name.",
                "org.dkeeney.config.ConstantsTest", tests.get(0).getName());
        assertEquals("Wrong number of tests ran", 1, tests.get(0).getTestsRun());
        assertEquals("Wrong number of tests failed", 0, tests.get(0)
                .getTestsFailed());
        assertEquals("Wrong number of tests in error", 0, tests.get(0)
                .getTestsError());
        assertEquals("Wrong amount of time elapsed", "0.055", tests.get(0)
                .getTimeElapsed());

        m = modules.get(2);
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
