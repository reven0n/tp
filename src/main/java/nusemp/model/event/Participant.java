package nusemp.model.event;

import static nusemp.commons.util.CollectionUtil.requireAllNonNull;

import nusemp.model.contact.Contact;

/**
 * Represents the contact and their participation status in an event.
 */
public class Participant {
    private final ParticipantStatus status;
    private final Contact contact;

    /**
     * Overloaded constructor that creates a {@code Participant} with specified status and contact.
     * @param contact Contact associated with the status.
     * @param status Status of the contact.
     */
    public Participant(Contact contact, ParticipantStatus status) {
        requireAllNonNull(contact, status);
        this.status = status;
        this.contact = contact;
    }

    /**
     * Overloaded constructor that creates a {@code Participant} with default status ATTENDING.
     * @param contact Contact associated with the status.
     */
    public Participant(Contact contact) {
        requireAllNonNull(contact);
        this.status = ParticipantStatus.AVAILABLE;
        this.contact = contact;
    }

    public ParticipantStatus getStatus() {
        return status;
    }

    public Contact getContact() {
        return contact;
    }

    public boolean equalsContact(Contact otherContact) {
        return this.contact.equals(otherContact);
    }

    /**
     * Checks if this participant has the same contact as another participant using less strict comparison.
     */
    public boolean hasSameContact(Participant otherParticipant) {
        return this.contact.isSameContact(otherParticipant.getContact());
    }

    @Override
    public String toString() {
        return "Contact: " + contact.toString() + ", Status: " + status.toString();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof Participant)) {
            return false;
        }

        Participant otherStatus = (Participant) other;
        return status == otherStatus.status && contact.equals(otherStatus.contact);
    }
}
