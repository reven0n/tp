package nusemp.model.event;

import java.util.ArrayList;
import java.util.List;

import nusemp.model.contact.Contact;

/**
 * Represents the contact status of an event.
 */
public class ContactStatus {
    private final Status status;
    private final Contact contact;

    /**
     * Overloaded constructor that creates a ContactStatus with specified status and contact.
     * @param status Status of the contact.
     * @param contact Contact associated with the status.
     */
    public ContactStatus(Status status, Contact contact) {
        this.status = status;
        this.contact = contact;
    }

    /**
     * Overloaded constructor that creates a ContactStatus with default status GOING.
     * @param contact Contact associated with the status.
     */
    public ContactStatus(Contact contact) {
        this.status = Status.GOING;
        this.contact = contact;
    }

    public Status getStatus() {
        return status;
    }

    public Contact getContact() {
        return contact;
    }

    public boolean hasSameContact(Contact otherContact) {
        return this.contact.equals(otherContact);
    }

    /**
     * Converts a list of contacts to a list of ContactStatus with default status GOING.
     * @param contacts List of contacts to be converted.
     * @return List of ContactStatus objects.
     */
    public static List<ContactStatus> convertToContactStatusList(List<Contact> contacts) {
        List<ContactStatus> contactStatusList = new ArrayList<>();
        for (Contact contact : contacts) {
            contactStatusList.add(new ContactStatus(contact));
        }
        return contactStatusList;
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

        if (!(other instanceof ContactStatus)) {
            return false;
        }

        ContactStatus otherStatus = (ContactStatus) other;
        return status == otherStatus.status && contact.equals(otherStatus.contact);
    }
}
