package nusemp.logic.commands.event;

import static nusemp.logic.Messages.MESSAGE_EVENTS_LISTED_OVERVIEW;
import static nusemp.logic.commands.CommandTestUtil.assertCommandSuccess;
import static nusemp.testutil.TypicalAppData.getTypicalAppDataWithEvents;
import static nusemp.testutil.TypicalEvents.CONFERENCE_EMPTY;
import static nusemp.testutil.TypicalEvents.MEETING_EMPTY;
import static nusemp.testutil.TypicalEvents.MEETING_WITH_TAGS;
import static nusemp.testutil.TypicalEvents.WORKSHOP_FILLED;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import nusemp.model.Model;
import nusemp.model.ModelManager;
import nusemp.model.UserPrefs;
import nusemp.model.event.EventDateContainsKeywordsPredicate;
import nusemp.model.event.EventMatchesAnyPredicatePredicate;
import nusemp.model.event.EventNameContainsKeywordsPredicate;
import nusemp.model.event.EventStatusPredicate;
import nusemp.model.event.EventTagContainsKeywordsPredicate;

class EventFindCommandTest {
    private Model model = new ModelManager(getTypicalAppDataWithEvents(), new UserPrefs());
    private Model expectedModel = new ModelManager(getTypicalAppDataWithEvents(), new UserPrefs());

    @Test
    public void execute_zeroKeywords_noEventFound() {
        String expectedMessage = String.format(MESSAGE_EVENTS_LISTED_OVERVIEW, 0);
        EventNameContainsKeywordsPredicate predicate = preparePredicate(" ");
        EventFindCommand command = new EventFindCommand(predicate);
        expectedModel.updateFilteredEventList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(Collections.emptyList(), model.getFilteredEventList());
    }

    @Test
    public void execute_multipleKeywords_multipleEventsFound() {
        String expectedMessage = String.format(MESSAGE_EVENTS_LISTED_OVERVIEW, 2);
        EventNameContainsKeywordsPredicate predicate = preparePredicate("ConferEnce Meeting");
        EventFindCommand command = new EventFindCommand(predicate);
        expectedModel.updateFilteredEventList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(Arrays.asList(MEETING_EMPTY, CONFERENCE_EMPTY), model.getFilteredEventList());
    }

    @Test
    public void execute_dateKeyword_eventFound() {
        String expectedMessage = String.format(MESSAGE_EVENTS_LISTED_OVERVIEW, 2);
        EventDateContainsKeywordsPredicate predicate = new EventDateContainsKeywordsPredicate(MEETING_EMPTY.getDate());
        EventFindCommand command = new EventFindCommand(predicate);
        expectedModel.updateFilteredEventList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(Arrays.asList(MEETING_EMPTY, WORKSHOP_FILLED),
                model.getFilteredEventList());
    }

    @Test
    public void execute_statusKeyword_eventFound() {
        String expectedMessage = String.format(MESSAGE_EVENTS_LISTED_OVERVIEW, 1);
        EventStatusPredicate predicate = new EventStatusPredicate(Arrays.asList("donE"));
        EventFindCommand command = new EventFindCommand(predicate);
        expectedModel.updateFilteredEventList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(Arrays.asList(WORKSHOP_FILLED),
                model.getFilteredEventList());
    }

    @Test
    public void execute_tagKeyword_eventsFound() {
        Model model = new ModelManager(getTypicalAppDataWithEvents(), new UserPrefs());
        Model expectedModel = new ModelManager(getTypicalAppDataWithEvents(), new UserPrefs());
        model.setEvent(MEETING_EMPTY, MEETING_WITH_TAGS);
        expectedModel.setEvent(MEETING_EMPTY, MEETING_WITH_TAGS);

        String expectedMessage = String.format(MESSAGE_EVENTS_LISTED_OVERVIEW, 1);
        EventTagContainsKeywordsPredicate predicate = new EventTagContainsKeywordsPredicate(
                Arrays.asList("sic", "Network"));
        EventFindCommand command = new EventFindCommand(predicate);
        expectedModel.updateFilteredEventList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(Arrays.asList(MEETING_WITH_TAGS), model.getFilteredEventList());
    }

    @Test
    public void execute_multipleFieldPredicates_eventsFound() {
        String expectedMessage = String.format(MESSAGE_EVENTS_LISTED_OVERVIEW, 3);
        EventNameContainsKeywordsPredicate namePredicate = preparePredicate("conference");
        EventDateContainsKeywordsPredicate datePredicate = new EventDateContainsKeywordsPredicate(
                MEETING_EMPTY.getDate());
        EventMatchesAnyPredicatePredicate predicate = new EventMatchesAnyPredicatePredicate(
                Arrays.asList(namePredicate, datePredicate));
        EventFindCommand command = new EventFindCommand(predicate);
        expectedModel.updateFilteredEventList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(Arrays.asList(MEETING_EMPTY, CONFERENCE_EMPTY, WORKSHOP_FILLED), model.getFilteredEventList());
    }

    @Test
    public void equals() {
        EventNameContainsKeywordsPredicate firstPredicate =
                new EventNameContainsKeywordsPredicate(Collections.singletonList("first"));
        EventNameContainsKeywordsPredicate secondPredicate =
                new EventNameContainsKeywordsPredicate(Collections.singletonList("second"));
        EventFindCommand findFirstCommand = new EventFindCommand(firstPredicate);
        EventFindCommand findSecondCommand = new EventFindCommand(secondPredicate);
        // same object -> returns true
        assertEquals(findFirstCommand, findFirstCommand);
    }

    private EventNameContainsKeywordsPredicate preparePredicate(String userInput) {
        return new EventNameContainsKeywordsPredicate(Arrays.asList(userInput.split("\\s+")));
    }
}
