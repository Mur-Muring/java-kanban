package Test.Manager;

import manager.Managers;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {
    @Test
    public void checkThatGetDefaulTasktNotNull() {
        assertNotNull(Managers.getDefaultTask());
    }

    @Test
    public void checkThatGetDefaultHistoryNotNull() {
        assertNotNull(Managers.getDefaultHistory());
    }

}