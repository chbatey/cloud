package info.batey.cassandra.load.config;

import org.junit.Test;

public class ProfileTest {
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
}