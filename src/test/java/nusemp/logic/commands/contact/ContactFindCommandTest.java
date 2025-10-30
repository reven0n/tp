package nusemp.logic.commands.contact;

import static nusemp.logic.Messages.MESSAGE_CONTACTS_LISTED_OVERVIEW;
import static nusemp.logic.commands.CommandTestUtil.assertCommandSuccess;
import static nusemp.testutil.TypicalAppData.getTypicalAppDataWithoutEvent;
import static nusemp.testutil.TypicalContacts.ALICE;
import static nusemp.testutil.TypicalContacts.BENSON;
import static nusemp.testutil.TypicalContacts.CARL;
import static nusemp.testutil.TypicalContacts.DANIEL;
import static nusemp.testutil.TypicalContacts.ELLE;
import static nusemp.testutil.TypicalContacts.FIONA;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import nusemp.model.Model;
import nusemp.model.ModelManager;
import nusemp.model.UserPrefs;
import nusemp.model.contact.ContactMatchesAllPredicates;
import nusemp.model.contact.ContactEmailContainsKeywordsPredicate;
import nusemp.model.contact.ContactNameContainsKeywordsPredicate;
import nusemp.model.contact.ContactTagContainsKeywordsPredicate;

/**
 * Contains integration tests (interaction with the Model) for {@code ContactFindCommand}.
 */
public class ContactFindCommandTest {
    private Model model = new ModelManager(getTypicalAppDataWithoutEvent(), new UserPrefs());
    private Model expectedModel = new ModelManager(getTypicalAppDataWithoutEvent(), new UserPrefs());

    @Test
    public void equals() {
        ContactNameContainsKeywordsPredicate firstPredicate =
                new ContactNameContainsKeywordsPredicate(Collections.singletonList("first"));
        ContactNameContainsKeywordsPredicate secondPredicate =
                new ContactNameContainsKeywordsPredicate(Collections.singletonList("second"));

        ContactFindCommand findFirstCommand = new ContactFindCommand(firstPredicate);
        ContactFindCommand findSecondCommand = new ContactFindCommand(secondPredicate);

        // same object -> returns true
        assertTrue(findFirstCommand.equals(findFirstCommand));

        // same values -> returns true
        ContactFindCommand findFirstCommandCopy = new ContactFindCommand(firstPredicate);
        assertTrue(findFirstCommand.equals(findFirstCommandCopy));

        // different types -> returns false
        assertFalse(findFirstCommand.equals(1));

        // null -> returns false
        assertFalse(findFirstCommand.equals(null));

        // different contact -> returns false
        assertFalse(findFirstCommand.equals(findSecondCommand));
    }

    @Test
    public void execute_zeroKeywords_noContactFound() {
        String expectedMessage = String.format(MESSAGE_CONTACTS_LISTED_OVERVIEW, 0);
        ContactNameContainsKeywordsPredicate predicate = preparePredicate(" ");
        ContactFindCommand command = new ContactFindCommand(predicate);
        expectedModel.updateFilteredContactList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(Collections.emptyList(), model.getFilteredContactList());
    }

    @Test
    public void execute_multipleKeywords_multipleContactsFound() {
        String expectedMessage = String.format(MESSAGE_CONTACTS_LISTED_OVERVIEW, 3);
        ContactNameContainsKeywordsPredicate predicate = preparePredicate("Kurz Elle Kunz");
        ContactFindCommand command = new ContactFindCommand(predicate);
        expectedModel.updateFilteredContactList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(Arrays.asList(CARL, ELLE, FIONA), model.getFilteredContactList());
    }

    @Test
    public void execute_emailKeyword_contactsFound() {
        String expectedMessage = String.format(MESSAGE_CONTACTS_LISTED_OVERVIEW, 1);
        ContactEmailContainsKeywordsPredicate predicate =
                new ContactEmailContainsKeywordsPredicate(Collections.singletonList("alice"));
        ContactFindCommand command = new ContactFindCommand(predicate);
        expectedModel.updateFilteredContactList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(Arrays.asList(ALICE), model.getFilteredContactList());
    }

    @Test
    public void execute_tagKeyword_contactsFound() {
        String expectedMessage = String.format(MESSAGE_CONTACTS_LISTED_OVERVIEW, 3);
        ContactTagContainsKeywordsPredicate predicate =
                new ContactTagContainsKeywordsPredicate(Collections.singletonList("friends"));
        ContactFindCommand command = new ContactFindCommand(predicate);
        expectedModel.updateFilteredContactList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(Arrays.asList(ALICE, BENSON, DANIEL), model.getFilteredContactList());
    }

    @Test
    public void execute_multiplePredicatesAnd_contactsFound() {
        String expectedMessage = String.format(MESSAGE_CONTACTS_LISTED_OVERVIEW, 1);
        // BENSON has name "Benson Meier", email "johnd@example.com", tags ["owesMoney", "friends"]
        ContactNameContainsKeywordsPredicate namePredicate =
                new ContactNameContainsKeywordsPredicate(Collections.singletonList("Benson"));
        ContactTagContainsKeywordsPredicate tagPredicate =
                new ContactTagContainsKeywordsPredicate(Collections.singletonList("owesMoney"));
        ContactMatchesAllPredicates combinedPredicate =
                new ContactMatchesAllPredicates(Arrays.asList(namePredicate, tagPredicate));
        ContactFindCommand command = new ContactFindCommand(combinedPredicate);
        expectedModel.updateFilteredContactList(combinedPredicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(Arrays.asList(BENSON), model.getFilteredContactList());
    }

    @Test
    public void toStringMethod() {
        ContactNameContainsKeywordsPredicate predicate = new ContactNameContainsKeywordsPredicate(Arrays.asList("keyword"));
        ContactFindCommand contactFindCommand = new ContactFindCommand(predicate);
        String expected = ContactFindCommand.class.getCanonicalName() + "{predicate=" + predicate + "}";
        assertEquals(expected, contactFindCommand.toString());
    }

    /**
     * Parses {@code userInput} into a {@code ContactNameContainsKeywordsPredicate}.
     */
    private ContactNameContainsKeywordsPredicate preparePredicate(String userInput) {
        return new ContactNameContainsKeywordsPredicate(Arrays.asList(userInput.split("\\s+")));
    }
}
