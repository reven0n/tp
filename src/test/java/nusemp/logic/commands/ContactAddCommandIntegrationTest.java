package nusemp.logic.commands;

import static nusemp.logic.commands.CommandTestUtil.assertCommandFailure;
import static nusemp.logic.commands.CommandTestUtil.assertCommandSuccess;
import static nusemp.testutil.TypicalAppData.getTypicalAppDataWithoutEvent;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import nusemp.logic.Messages;
import nusemp.logic.commands.contact.ContactAddCommand;
import nusemp.model.Model;
import nusemp.model.ModelManager;
import nusemp.model.UserPrefs;
import nusemp.model.contact.Contact;
import nusemp.testutil.ContactBuilder;

/**
 * Contains integration tests (interaction with the Model) for {@code ContactAddCommand}.
 */
public class ContactAddCommandIntegrationTest {

    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalAppDataWithoutEvent(), new UserPrefs());
    }

    @Test
    public void execute_newContact_success() {
        Contact validContact = new ContactBuilder().build();

        Model expectedModel = new ModelManager(model.getAppData(), new UserPrefs());
        expectedModel.addContact(validContact);

        assertCommandSuccess(new ContactAddCommand(validContact), model,
                String.format(ContactAddCommand.MESSAGE_SUCCESS, Messages.format(validContact)),
                expectedModel);
    }

    @Test
    public void execute_duplicateContact_throwsCommandException() {
        Contact contactInList = model.getAppData().getContactList().get(0);
        assertCommandFailure(new ContactAddCommand(contactInList), model,
                String.format(ContactAddCommand.MESSAGE_DUPLICATE_CONTACT, contactInList.getEmail()));
    }

}
