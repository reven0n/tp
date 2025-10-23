package nusemp.logic.commands.event;

import static nusemp.logic.commands.CommandTestUtil.assertCommandSuccess;
import static nusemp.testutil.TypicalAppData.getTypicalAppDataWithEvents;
import static nusemp.testutil.TypicalAppData.getTypicalAppDataWithoutEvent;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

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
        int expectedEventCount = model.getFilteredEventList().size();
        assertCommandSuccess(new EventListCommand(), model,
                String.format(EventListCommand.MESSAGE_SUCCESS, expectedEventCount), model);

        Model anotherModel = new ModelManager(getTypicalAppDataWithoutEvent(), new UserPrefs());
        int expectedAnotherEventCount = anotherModel.getFilteredEventList().size();
        assertCommandSuccess(new EventListCommand(), anotherModel,
                String.format(EventListCommand.MESSAGE_SUCCESS, expectedAnotherEventCount), anotherModel);
    }
}
