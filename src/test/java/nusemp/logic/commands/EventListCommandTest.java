package nusemp.logic.commands;

import static nusemp.logic.commands.CommandTestUtil.assertCommandSuccess;
import static nusemp.testutil.TypicalAppData.getTypicalAppDataWithoutEvent;
import static nusemp.testutil.TypicalEvents.getTypicalAppDataWithEvents;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import nusemp.logic.commands.event.EventListCommand;
import nusemp.model.Model;
import nusemp.model.ModelManager;
import nusemp.model.UserPrefs;

class EventListCommandTest {
    private Model model = new ModelManager(getTypicalAppDataWithEvents(), new UserPrefs());

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> {
            new EventListCommand().execute(null);
        });
    }

    @Test
    public void execute_validModel_success() {
        assertCommandSuccess(new EventListCommand(), model,
                String.format(EventListCommand.MESSAGE_SUCCESS, 4), model);

        Model anotherModel = new ModelManager(getTypicalAppDataWithoutEvent(), new UserPrefs());
        assertCommandSuccess(new EventListCommand(), anotherModel,
                String.format(EventListCommand.MESSAGE_SUCCESS, 0), anotherModel);
    }
}
