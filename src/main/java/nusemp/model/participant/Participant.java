package nusemp.model.participant;

import static nusemp.commons.util.CollectionUtil.requireAllNonNull;

import nusemp.model.contact.Contact;
import nusemp.model.event.Event;

/**
 * Represents the link between a Contact and an Event along with the ParticipantStatus.
 */
public class Participant {
    private final Contact contact;
    private final Event event;
    private final ParticipantStatus status;

    /**
     * Creates a Participant with the specified contact, event, and status.
     * @param contact The contact involved in the event.
     * @param event The event the contact is participating in.
     * @param status The participation status of the contact in the event.
     */
    public Participant(Contact contact, Event event, ParticipantStatus status) {
        requireAllNonNull(contact, event, status);
        this.contact = contact;
        this.event = event;
        this.status = status;
    }

    public Event getEvent() {
        return event;
    }

    public Contact getContact() {
        return contact;
    }

    public ParticipantStatus getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Participant)) {
            return false;
        }
        Participant otherParticipant = (Participant) other;
        return this.contact.equals(otherParticipant.contact)
                && this.event.equals(otherParticipant.event)
                && this.status == otherParticipant.status;
    }
}
