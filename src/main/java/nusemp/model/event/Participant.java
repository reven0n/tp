package nusemp.model.event;

import nusemp.model.contact.Contact;

/**
 * Represents the contact status of an event.
 */
public class Participant {
    private final Status status;
    private final Contact contact;

    /**
     * Overloaded constructor that creates a ContactStatus with specified status and contact.
     * @param contact Contact associated with the status.
     * @param status Status of the contact.
     */
    public Participant(Contact contact, Status status) {
        this.status = status;
        this.contact = contact;
    }

    /**
     * Overloaded constructor that creates a ContactStatus with default status ATTENDING.
     * @param contact Contact associated with the status.
     */
    public Participant(Contact contact) {
        this.status = Status.ATTENDING;
        this.contact = contact;
    }

    public Status getStatus() {
        return status;
    }

    public Contact getContact() {
        return contact;
    }

    public boolean containsContact(Contact otherContact) {
        return this.contact.equals(otherContact);
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
