package org.dkeeney.git;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URISyntaxException;
import java.util.List;

import org.dkeeney.git.commit.Commit;
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
}
