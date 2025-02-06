package com.example;

import static org.junit.Assert.assertTrue;
import org.junit.Test;
import it.safedrivemonitor.Runners.App;

public class AppTest {
    @Test
    public void shouldAnswerWithTrue() {
        App app = new App();
        assertTrue("App instance should be created successfully", app != null);
    }
}
