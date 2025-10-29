package nusemp.model.participant;

import java.util.List;

import nusemp.model.event.Event;

/**
 * Function that obtains a list of Participants from the given contact.
 */
@FunctionalInterface
public interface EventToParticipantsFunction {
    List<Participant> apply(Event event);
}
