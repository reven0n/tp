package nusemp.logic.commands;

import static nusemp.logic.Messages.MESSAGE_CONTACTS_LISTED_OVERVIEW;
import static nusemp.logic.commands.CommandTestUtil.assertCommandSuccess;
import static nusemp.testutil.TypicalContacts.ALICE;
import static nusemp.testutil.TypicalContacts.BENSON;
import static nusemp.testutil.TypicalContacts.CARL;
import static nusemp.testutil.TypicalContacts.DANIEL;
import static nusemp.testutil.TypicalContacts.ELLE;
import static nusemp.testutil.TypicalContacts.FIONA;
import static nusemp.testutil.TypicalContacts.getTypicalAppData;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import nusemp.logic.commands.contact.ContactFindCommand;
import nusemp.model.Model;
import nusemp.model.ModelManager;
import nusemp.model.UserPrefs;
import nusemp.model.contact.ContactMatchesAnyPredicatePredicate;
import nusemp.model.contact.EmailContainsKeywordsPredicate;
import nusemp.model.contact.NameContainsKeywordsPredicate;
import nusemp.model.contact.TagContainsKeywordsPredicate;

/**
 * Contains integration tests (interaction with the Model) for {@code ContactFindCommand}.
 */
public class ContactFindCommandTest {
    private Model model = new ModelManager(getTypicalAppData(), new UserPrefs());
    private Model expectedModel = new ModelManager(getTypicalAppData(), new UserPrefs());

    @Test
    public void equals() {
        NameContainsKeywordsPredicate firstPredicate =
                new NameContainsKeywordsPredicate(Collections.singletonList("first"));
        NameContainsKeywordsPredicate secondPredicate =
                new NameContainsKeywordsPredicate(Collections.singletonList("second"));

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
        NameContainsKeywordsPredicate predicate = preparePredicate(" ");
        ContactFindCommand command = new ContactFindCommand(predicate);
        expectedModel.updateFilteredContactList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(Collections.emptyList(), model.getFilteredContactList());
    }

    @Test
    public void execute_multipleKeywords_multipleContactsFound() {
        String expectedMessage = String.format(MESSAGE_CONTACTS_LISTED_OVERVIEW, 3);
        NameContainsKeywordsPredicate predicate = preparePredicate("Kurz Elle Kunz");
        ContactFindCommand command = new ContactFindCommand(predicate);
        expectedModel.updateFilteredContactList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(Arrays.asList(CARL, ELLE, FIONA), model.getFilteredContactList());
    }

    @Test
    public void execute_emailKeyword_contactsFound() {
        String expectedMessage = String.format(MESSAGE_CONTACTS_LISTED_OVERVIEW, 1);
        EmailContainsKeywordsPredicate predicate =
                new EmailContainsKeywordsPredicate(Collections.singletonList("alice"));
        ContactFindCommand command = new ContactFindCommand(predicate);
        expectedModel.updateFilteredContactList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(Arrays.asList(ALICE), model.getFilteredContactList());
    }

    @Test
    public void execute_tagKeyword_contactsFound() {
        String expectedMessage = String.format(MESSAGE_CONTACTS_LISTED_OVERVIEW, 3);
        TagContainsKeywordsPredicate predicate =
                new TagContainsKeywordsPredicate(Collections.singletonList("friends"));
        ContactFindCommand command = new ContactFindCommand(predicate);
        expectedModel.updateFilteredContactList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(Arrays.asList(ALICE, BENSON, DANIEL), model.getFilteredContactList());
    }

    @Test
    public void execute_multiplePredicatesOr_contactsFound() {
        String expectedMessage = String.format(MESSAGE_CONTACTS_LISTED_OVERVIEW, 3);
        NameContainsKeywordsPredicate namePredicate =
                new NameContainsKeywordsPredicate(Collections.singletonList("Alice"));
        EmailContainsKeywordsPredicate emailPredicate =
                new EmailContainsKeywordsPredicate(Collections.singletonList("cornelia"));
        TagContainsKeywordsPredicate tagPredicate =
                new TagContainsKeywordsPredicate(Collections.singletonList("owesMoney"));
        ContactMatchesAnyPredicatePredicate combinedPredicate =
                new ContactMatchesAnyPredicatePredicate(Arrays.asList(namePredicate, emailPredicate, tagPredicate));
        ContactFindCommand command = new ContactFindCommand(combinedPredicate);
        expectedModel.updateFilteredContactList(combinedPredicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(Arrays.asList(ALICE, BENSON, DANIEL), model.getFilteredContactList());
    }

    @Test
    public void toStringMethod() {
        NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(Arrays.asList("keyword"));
        ContactFindCommand contactFindCommand = new ContactFindCommand(predicate);
        String expected = ContactFindCommand.class.getCanonicalName() + "{predicate=" + predicate + "}";
        assertEquals(expected, contactFindCommand.toString());
    }

    /**
     * Parses {@code userInput} into a {@code NameContainsKeywordsPredicate}.
     */
    private NameContainsKeywordsPredicate preparePredicate(String userInput) {
        return new NameContainsKeywordsPredicate(Arrays.asList(userInput.split("\\s+")));
    }
}
