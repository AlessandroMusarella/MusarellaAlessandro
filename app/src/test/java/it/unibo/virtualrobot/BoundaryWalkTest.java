package it.unibo.virtualrobot;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertTrue;

public class BoundaryWalkTest {
    private BoundaryWalk app;
    private Pattern movePattern;

    @Before
    public void systemSetUp() {
        System.out.println("TestBoundaryWalk | setUp: robot should be at HOME-DOWN ");
        app = new BoundaryWalk();
        movePattern = Pattern.compile("w+lw+lw+lw+l");
    }

    @After
    public void terminate() {
        System.out.println("%%%  TestBoundaryWalk |  terminates ");
    }

    @Test
    public void testBoundaryWalk() {
        System.out.println("TestBoundaryWalk | test doBoundaryWalk ");

        String result = app.doBoundaryWalk();
        System.out.println(result);
        Matcher matcher = movePattern.matcher(result);

        assertTrue(matcher.matches());
    }
}