package info.batey.cassandra.load.config;

import org.junit.Test;

import java.io.FileNotFoundException;

public class ProfileCommandTest {
    @Test(expected = RuntimeException.class)
    public void keyspaceInTableIsMandatory() throws Exception {
        Profile profile = new Profile();
        profile.validate();
    }

    @Test
    public void testLoad() throws Exception {
        Profile profile = Profile.parse("example-profile.yaml");
        System.out.println(profile);
    }

    @Test(expected = FileNotFoundException.class)
    public void fileNotFound() throws Exception {
        Profile.parse("idonotexist.yaml");
    }
}