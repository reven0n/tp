package nusemp.logic.commands;

import static nusemp.logic.commands.CommandTestUtil.DESC_AMY;
import static nusemp.logic.commands.CommandTestUtil.DESC_BOB;
import static nusemp.logic.commands.CommandTestUtil.VALID_CONTACT_ADDRESS_BOB;
import static nusemp.logic.commands.CommandTestUtil.VALID_CONTACT_EMAIL_BOB;
import static nusemp.logic.commands.CommandTestUtil.VALID_CONTACT_NAME_BOB;
import static nusemp.logic.commands.CommandTestUtil.VALID_CONTACT_PHONE_BOB;
import static nusemp.logic.commands.CommandTestUtil.VALID_CONTACT_TAG_HUSBAND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import nusemp.logic.commands.contact.ContactEditCommand;
import nusemp.logic.commands.contact.ContactEditCommand.EditContactDescriptor;
import nusemp.testutil.EditContactDescriptorBuilder;

public class EditContactDescriptorTest {

    @Test
    public void equals() {
        // same values -> returns true
        EditContactDescriptor descriptorWithSameValues = new ContactEditCommand.EditContactDescriptor(DESC_AMY);
        assertTrue(DESC_AMY.equals(descriptorWithSameValues));

        // same object -> returns true
        assertTrue(DESC_AMY.equals(DESC_AMY));

        // null -> returns false
        assertFalse(DESC_AMY.equals(null));

        // different types -> returns false
        assertFalse(DESC_AMY.equals(5));

        // different values -> returns false
        assertFalse(DESC_AMY.equals(DESC_BOB));

        // different name -> returns false
        ContactEditCommand.EditContactDescriptor editedAmy = new EditContactDescriptorBuilder(DESC_AMY)
                .withName(VALID_CONTACT_NAME_BOB).build();
        assertFalse(DESC_AMY.equals(editedAmy));

        // different phone -> returns false
        editedAmy = new EditContactDescriptorBuilder(DESC_AMY).withPhone(VALID_CONTACT_PHONE_BOB).build();
        assertFalse(DESC_AMY.equals(editedAmy));

        // different email -> returns false
        editedAmy = new EditContactDescriptorBuilder(DESC_AMY).withEmail(VALID_CONTACT_EMAIL_BOB).build();
        assertFalse(DESC_AMY.equals(editedAmy));

        // different address -> returns false
        editedAmy = new EditContactDescriptorBuilder(DESC_AMY).withAddress(VALID_CONTACT_ADDRESS_BOB).build();
        assertFalse(DESC_AMY.equals(editedAmy));

        // different tags -> returns false
        editedAmy = new EditContactDescriptorBuilder(DESC_AMY).withTags(VALID_CONTACT_TAG_HUSBAND).build();
        assertFalse(DESC_AMY.equals(editedAmy));
    }

    @Test
    public void toStringMethod() {
        EditContactDescriptor editContactDescriptor = new EditContactDescriptor();
        String expected = EditContactDescriptor.class.getCanonicalName() + "{name="
                + editContactDescriptor.getName().orElse(null) + ", phone="
                + editContactDescriptor.getPhone().orElse(null) + ", email="
                + editContactDescriptor.getEmail().orElse(null) + ", address="
                + editContactDescriptor.getAddress().orElse(null) + ", tags="
                + editContactDescriptor.getTags().orElse(null) + "}";
        assertEquals(expected, editContactDescriptor.toString());
    }
}
