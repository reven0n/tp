package nusemp.model;

import static nusemp.commons.util.CollectionUtil.requireAllNonNull;

import nusemp.model.contact.Contact;
import nusemp.model.event.Event;
import nusemp.model.event.ParticipantStatus;

/**
 * Represents the link between a Contact and an Event along with the ParticipantStatus.
 */
public class ParticipantEvent {
    private final Contact contact;
    private final Event event;
    private final ParticipantStatus status;

    /**
     * Creates a ParticipantEvent with the specified contact, event, and status.
     * @param contact The contact involved in the event.
     * @param event The event the contact is participating in.
     * @param status The participation status of the contact in the event.
     */
    public ParticipantEvent(Contact contact, Event event, ParticipantStatus status) {
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
        if (!(other instanceof ParticipantEvent)) {
            return false;
        }
        ParticipantEvent otherParticipantEvent = (ParticipantEvent) other;
        return this.contact.equals(otherParticipantEvent.contact)
                && this.event.equals(otherParticipantEvent.event)
                && this.status == otherParticipantEvent.status;
    }
}
