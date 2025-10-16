package nusemp.logic.commands;

import static nusemp.logic.commands.CommandTestUtil.DESC_AMY;
import static nusemp.logic.commands.CommandTestUtil.DESC_BOB;
import static nusemp.logic.commands.CommandTestUtil.VALID_CONTACT_NAME_BOB;
import static nusemp.logic.commands.CommandTestUtil.VALID_CONTACT_PHONE_BOB;
import static nusemp.logic.commands.CommandTestUtil.VALID_CONTACT_TAG_HUSBAND;
import static nusemp.logic.commands.CommandTestUtil.assertCommandFailure;
import static nusemp.logic.commands.CommandTestUtil.assertCommandSuccess;
import static nusemp.logic.commands.CommandTestUtil.showPersonAtIndex;
import static nusemp.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static nusemp.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static nusemp.testutil.TypicalPersons.getTypicalAddressBook;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import nusemp.commons.core.index.Index;
import nusemp.logic.Messages;
import nusemp.logic.commands.ContactEditCommand.EditPersonDescriptor;
import nusemp.model.AddressBook;
import nusemp.model.Model;
import nusemp.model.ModelManager;
import nusemp.model.UserPrefs;
import nusemp.model.person.Person;
import nusemp.testutil.EditPersonDescriptorBuilder;
import nusemp.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for ContactEditCommand.
 */
public class ContactEditCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_allFieldsSpecifiedUnfilteredList_success() {
        Person editedPerson = new PersonBuilder().build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(editedPerson).build();
        ContactEditCommand contactEditCommand = new ContactEditCommand(INDEX_FIRST_PERSON, descriptor);

        String expectedMessage = String.format(ContactEditCommand.MESSAGE_EDIT_PERSON_SUCCESS,
                Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(model.getFilteredPersonList().get(0), editedPerson);

        assertCommandSuccess(contactEditCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_someFieldsSpecifiedUnfilteredList_success() {
        Index indexLastPerson = Index.fromOneBased(model.getFilteredPersonList().size());
        Person lastPerson = model.getFilteredPersonList().get(indexLastPerson.getZeroBased());

        PersonBuilder personInList = new PersonBuilder(lastPerson);
        Person editedPerson = personInList.withName(VALID_CONTACT_NAME_BOB).withPhone(VALID_CONTACT_PHONE_BOB)
                .withTags(VALID_CONTACT_TAG_HUSBAND).build();

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_CONTACT_NAME_BOB)
                .withPhone(VALID_CONTACT_PHONE_BOB).withTags(VALID_CONTACT_TAG_HUSBAND).build();
        ContactEditCommand contactEditCommand = new ContactEditCommand(indexLastPerson, descriptor);

        String expectedMessage = String.format(ContactEditCommand.MESSAGE_EDIT_PERSON_SUCCESS,
                Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(lastPerson, editedPerson);

        assertCommandSuccess(contactEditCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_noFieldSpecifiedUnfilteredList_success() {
        ContactEditCommand contactEditCommand = new ContactEditCommand(INDEX_FIRST_PERSON, new EditPersonDescriptor());
        Person editedPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());

        String expectedMessage = String.format(ContactEditCommand.MESSAGE_EDIT_PERSON_SUCCESS,
                Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());

        assertCommandSuccess(contactEditCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_filteredList_success() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Person personInFilteredList = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person editedPerson = new PersonBuilder(personInFilteredList).withName(VALID_CONTACT_NAME_BOB).build();
        ContactEditCommand contactEditCommand = new ContactEditCommand(INDEX_FIRST_PERSON,
                new EditPersonDescriptorBuilder().withName(VALID_CONTACT_NAME_BOB).build());

        String expectedMessage = String.format(ContactEditCommand.MESSAGE_EDIT_PERSON_SUCCESS,
                Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(model.getFilteredPersonList().get(0), editedPerson);

        assertCommandSuccess(contactEditCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_duplicatePersonUnfilteredList_failure() {
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(firstPerson).build();
        ContactEditCommand contactEditCommand = new ContactEditCommand(INDEX_SECOND_PERSON, descriptor);

        assertCommandFailure(contactEditCommand, model,
                String.format(ContactEditCommand.MESSAGE_DUPLICATE_PERSON, firstPerson.getEmail()));
    }

    @Test
    public void execute_duplicatePersonFilteredList_failure() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        // edit person in filtered list into a duplicate in address book
        Person personInList = model.getAddressBook().getPersonList().get(INDEX_SECOND_PERSON.getZeroBased());
        ContactEditCommand contactEditCommand = new ContactEditCommand(INDEX_FIRST_PERSON,
                new EditPersonDescriptorBuilder(personInList).build());

        assertCommandFailure(contactEditCommand, model,
                String.format(ContactEditCommand.MESSAGE_DUPLICATE_PERSON, personInList.getEmail()));
    }

    @Test
    public void execute_invalidPersonIndexUnfilteredList_failure() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_CONTACT_NAME_BOB).build();
        ContactEditCommand contactEditCommand = new ContactEditCommand(outOfBoundIndex, descriptor);

        assertCommandFailure(contactEditCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    /**
     * Edit filtered list where index is larger than size of filtered list,
     * but smaller than size of address book
     */
    @Test
    public void execute_invalidPersonIndexFilteredList_failure() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        Index outOfBoundIndex = INDEX_SECOND_PERSON;
        // ensures that outOfBoundIndex is still in bounds of address book list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getAddressBook().getPersonList().size());

        ContactEditCommand contactEditCommand = new ContactEditCommand(outOfBoundIndex,
                new EditPersonDescriptorBuilder().withName(VALID_CONTACT_NAME_BOB).build());

        assertCommandFailure(contactEditCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        final ContactEditCommand standardCommand = new ContactEditCommand(INDEX_FIRST_PERSON, DESC_AMY);

        // same values -> returns true
        EditPersonDescriptor copyDescriptor = new EditPersonDescriptor(DESC_AMY);
        ContactEditCommand commandWithSameValues = new ContactEditCommand(INDEX_FIRST_PERSON, copyDescriptor);
        assertTrue(standardCommand.equals(commandWithSameValues));

        // same object -> returns true
        assertTrue(standardCommand.equals(standardCommand));

        // null -> returns false
        assertFalse(standardCommand.equals(null));

        // different types -> returns false
        assertFalse(standardCommand.equals(new ContactListCommand()));

        // different index -> returns false
        assertFalse(standardCommand.equals(new ContactEditCommand(INDEX_SECOND_PERSON, DESC_AMY)));

        // different descriptor -> returns false
        assertFalse(standardCommand.equals(new ContactEditCommand(INDEX_FIRST_PERSON, DESC_BOB)));
    }

    @Test
    public void toStringMethod() {
        Index index = Index.fromOneBased(1);
        EditPersonDescriptor editPersonDescriptor = new EditPersonDescriptor();
        ContactEditCommand contactEditCommand = new ContactEditCommand(index, editPersonDescriptor);
        String expected = ContactEditCommand.class.getCanonicalName() + "{index=" + index + ", editPersonDescriptor="
                + editPersonDescriptor + "}";
        assertEquals(expected, contactEditCommand.toString());
    }

}
