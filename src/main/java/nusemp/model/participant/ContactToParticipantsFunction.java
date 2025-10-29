package nusemp.model.participant;

import java.util.List;

import nusemp.model.contact.Contact;

/**
 * Function that obtains a list of Participants from the given contact.
 */
@FunctionalInterface
public interface ContactToParticipantsFunction {
    List<Participant> apply(Contact contact);
}
