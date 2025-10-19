package nusemp.logic.commands;

import static nusemp.logic.commands.CommandTestUtil.DESC_AMY;
import static nusemp.logic.commands.CommandTestUtil.DESC_BOB;
import static nusemp.logic.commands.CommandTestUtil.VALID_CONTACT_NAME_BOB;
import static nusemp.logic.commands.CommandTestUtil.VALID_CONTACT_PHONE_BOB;
import static nusemp.logic.commands.CommandTestUtil.VALID_CONTACT_TAG_HUSBAND;
import static nusemp.logic.commands.CommandTestUtil.assertCommandFailure;
import static nusemp.logic.commands.CommandTestUtil.assertCommandSuccess;
import static nusemp.logic.commands.CommandTestUtil.showContactAtIndex;
import static nusemp.testutil.TypicalContacts.getTypicalAppData;
import static nusemp.testutil.TypicalIndexes.INDEX_FIRST_CONTACT;
import static nusemp.testutil.TypicalIndexes.INDEX_SECOND_CONTACT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import nusemp.commons.core.index.Index;
import nusemp.logic.Messages;
import nusemp.logic.commands.contact.ContactEditCommand;
import nusemp.logic.commands.contact.ContactEditCommand.EditContactDescriptor;
import nusemp.logic.commands.contact.ContactListCommand;
import nusemp.model.AppData;
import nusemp.model.Model;
import nusemp.model.ModelManager;
import nusemp.model.UserPrefs;
import nusemp.model.contact.Contact;
import nusemp.testutil.ContactBuilder;
import nusemp.testutil.EditContactDescriptorBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for ContactEditCommand.
 */
public class ContactEditCommandTest {

    private Model model = new ModelManager(getTypicalAppData(), new UserPrefs());

    @Test
    public void execute_allFieldsSpecifiedUnfilteredList_success() {
        Contact editedContact = new ContactBuilder().build();
        EditContactDescriptor descriptor = new EditContactDescriptorBuilder(editedContact).build();
        ContactEditCommand contactEditCommand = new ContactEditCommand(INDEX_FIRST_CONTACT, descriptor);

        String expectedMessage = String.format(ContactEditCommand.MESSAGE_EDIT_CONTACT_SUCCESS,
                Messages.format(editedContact));

        Model expectedModel = new ModelManager(new AppData(model.getAppData()), new UserPrefs());
        expectedModel.setContact(model.getFilteredContactList().get(0), editedContact);

        assertCommandSuccess(contactEditCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_someFieldsSpecifiedUnfilteredList_success() {
        Index indexLastContact = Index.fromOneBased(model.getFilteredContactList().size());
        Contact lastContact = model.getFilteredContactList().get(indexLastContact.getZeroBased());

        ContactBuilder contactInList = new ContactBuilder(lastContact);
        Contact editedContact = contactInList.withName(VALID_CONTACT_NAME_BOB).withPhone(VALID_CONTACT_PHONE_BOB)
                .withTags(VALID_CONTACT_TAG_HUSBAND).build();

        ContactEditCommand.EditContactDescriptor descriptor = new EditContactDescriptorBuilder()
                .withName(VALID_CONTACT_NAME_BOB).withPhone(VALID_CONTACT_PHONE_BOB).withTags(VALID_CONTACT_TAG_HUSBAND)
                .build();
        ContactEditCommand contactEditCommand = new ContactEditCommand(indexLastContact, descriptor);

        String expectedMessage = String.format(ContactEditCommand.MESSAGE_EDIT_CONTACT_SUCCESS,
                Messages.format(editedContact));

        Model expectedModel = new ModelManager(new AppData(model.getAppData()), new UserPrefs());
        expectedModel.setContact(lastContact, editedContact);

        assertCommandSuccess(contactEditCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_noFieldSpecifiedUnfilteredList_success() {
        ContactEditCommand contactEditCommand =
                new ContactEditCommand(INDEX_FIRST_CONTACT, new ContactEditCommand.EditContactDescriptor());
        Contact editedContact = model.getFilteredContactList().get(INDEX_FIRST_CONTACT.getZeroBased());

        String expectedMessage = String.format(ContactEditCommand.MESSAGE_EDIT_CONTACT_SUCCESS,
                Messages.format(editedContact));

        Model expectedModel = new ModelManager(new AppData(model.getAppData()), new UserPrefs());

        assertCommandSuccess(contactEditCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_filteredList_success() {
        showContactAtIndex(model, INDEX_FIRST_CONTACT);

        Contact contactInFilteredList = model.getFilteredContactList().get(INDEX_FIRST_CONTACT.getZeroBased());
        Contact editedContact = new ContactBuilder(contactInFilteredList).withName(VALID_CONTACT_NAME_BOB).build();
        ContactEditCommand contactEditCommand = new ContactEditCommand(INDEX_FIRST_CONTACT,
                new EditContactDescriptorBuilder().withName(VALID_CONTACT_NAME_BOB).build());

        String expectedMessage = String.format(ContactEditCommand.MESSAGE_EDIT_CONTACT_SUCCESS,
                Messages.format(editedContact));

        Model expectedModel = new ModelManager(new AppData(model.getAppData()), new UserPrefs());
        expectedModel.setContact(model.getFilteredContactList().get(0), editedContact);

        assertCommandSuccess(contactEditCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_duplicateContactUnfilteredList_failure() {
        Contact firstContact = model.getFilteredContactList().get(INDEX_FIRST_CONTACT.getZeroBased());
        EditContactDescriptor descriptor = new EditContactDescriptorBuilder(firstContact).build();
        ContactEditCommand contactEditCommand = new ContactEditCommand(INDEX_SECOND_CONTACT, descriptor);

        assertCommandFailure(contactEditCommand, model,
                String.format(ContactEditCommand.MESSAGE_DUPLICATE_CONTACT, firstContact.getEmail()));
    }

    @Test
    public void execute_duplicateContactFilteredList_failure() {
        showContactAtIndex(model, INDEX_FIRST_CONTACT);

        // edit contact in filtered list into a duplicate
        Contact contactInList = model.getAppData().getContactList().get(INDEX_SECOND_CONTACT.getZeroBased());
        ContactEditCommand contactEditCommand = new ContactEditCommand(INDEX_FIRST_CONTACT,
                new EditContactDescriptorBuilder(contactInList).build());

        assertCommandFailure(contactEditCommand, model,
                String.format(ContactEditCommand.MESSAGE_DUPLICATE_CONTACT, contactInList.getEmail()));
    }

    @Test
    public void execute_invalidContactIndexUnfilteredList_failure() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredContactList().size() + 1);
        EditContactDescriptor descriptor = new EditContactDescriptorBuilder().withName(VALID_CONTACT_NAME_BOB).build();
        ContactEditCommand contactEditCommand = new ContactEditCommand(outOfBoundIndex, descriptor);

        assertCommandFailure(contactEditCommand, model, Messages.MESSAGE_INVALID_CONTACT_DISPLAYED_INDEX);
    }

    /**
     * Edit filtered list where index is larger than size of filtered list,
     * but smaller than size of the full contact list
     */
    @Test
    public void execute_invalidContactIndexFilteredList_failure() {
        showContactAtIndex(model, INDEX_FIRST_CONTACT);
        Index outOfBoundIndex = INDEX_SECOND_CONTACT;
        // ensures that outOfBoundIndex is still in bounds of contact list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getAppData().getContactList().size());

        ContactEditCommand contactEditCommand = new ContactEditCommand(outOfBoundIndex,
                new EditContactDescriptorBuilder().withName(VALID_CONTACT_NAME_BOB).build());

        assertCommandFailure(contactEditCommand, model, Messages.MESSAGE_INVALID_CONTACT_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        final ContactEditCommand standardCommand = new ContactEditCommand(INDEX_FIRST_CONTACT, DESC_AMY);

        // same values -> returns true
        EditContactDescriptor copyDescriptor = new EditContactDescriptor(DESC_AMY);
        ContactEditCommand commandWithSameValues = new ContactEditCommand(INDEX_FIRST_CONTACT, copyDescriptor);
        assertTrue(standardCommand.equals(commandWithSameValues));

        // same object -> returns true
        assertTrue(standardCommand.equals(standardCommand));

        // null -> returns false
        assertFalse(standardCommand.equals(null));

        // different types -> returns false
        assertFalse(standardCommand.equals(new ContactListCommand()));

        // different index -> returns false
        assertFalse(standardCommand.equals(new ContactEditCommand(INDEX_SECOND_CONTACT, DESC_AMY)));

        // different descriptor -> returns false
        assertFalse(standardCommand.equals(new ContactEditCommand(INDEX_FIRST_CONTACT, DESC_BOB)));
    }

    @Test
    public void toStringMethod() {
        Index index = Index.fromOneBased(1);
        ContactEditCommand.EditContactDescriptor editContactDescriptor = new ContactEditCommand.EditContactDescriptor();
        ContactEditCommand contactEditCommand = new ContactEditCommand(index, editContactDescriptor);
        String expected = ContactEditCommand.class.getCanonicalName() + "{index=" + index + ", editContactDescriptor="
                + editContactDescriptor + "}";
        assertEquals(expected, contactEditCommand.toString());
    }

}
