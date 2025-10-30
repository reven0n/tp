package nusemp.model.participant;

import java.util.List;

import nusemp.model.event.Event;

/**
 * Function that obtains a list of Participants from the given event.
 */
@FunctionalInterface
public interface EventToParticipantsFunction {
    List<Participant> apply(Event event);
}
