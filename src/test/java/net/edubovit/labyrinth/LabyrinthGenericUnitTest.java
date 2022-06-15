package net.edubovit.labyrinth;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LabyrinthGenericUnitTest {

    @Test
    void unitTestFrameworkWorks_positive() {
        assertEquals(6, 2 + 2 * 2);
    }

    @Test
    void unitTestFrameworkWorks_negative() {
        assertThrows(RuntimeException.class, () -> {
            throw new NullPointerException();
        });
    }

}
