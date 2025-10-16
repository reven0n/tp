package nusemp.logic.commands;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import nusemp.commons.core.index.Index;

class EventLinkCommandTest {
    @Test
    void constructor_nullParameters_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new EventLinkCommand(null, Index.fromOneBased(1)));
        assertThrows(NullPointerException.class, () -> new EventLinkCommand(Index.fromOneBased(1), null));
    }


}