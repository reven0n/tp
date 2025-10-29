package nusemp.model.participant;

import java.util.List;

import nusemp.model.contact.Contact;
import nusemp.model.event.Event;

/**
 * Read-only version of ParticipantMap.
 */
public interface ReadOnlyParticipantMap {

    /**
     * Gets all participants that contain the given contact.
     */
    public List<Participant> getParticipants(Contact contact);

    /**
     * Gets all participants that contain the given event.
     */
    public List<Participant> getParticipants(Event event);

    /**
     * Gets all participants.
     */
    public List<Participant> getAllParticipants();
}
