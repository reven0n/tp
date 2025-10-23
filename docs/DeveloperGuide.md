---
  layout: default.md
  title: "Developer Guide"
  pageNav: 3
---

# NUS Event Mailer Pro Developer Guide

<!-- * Table of Contents -->
<page-nav-print />

---

## **Acknowledgements**

This project is based on the AddressBook-Level3 project created by the SE-EDU initiative.

AI was used throughout the development of this project:

- GitHub Copilot was used for auto-completing code snippets.
- Claude Sonnet 4.5 was used to generate the unit tests.
- Claude Haiku 4.5 was used to review long documents and tool-use to ensure document consistency.

_{ list here sources of all reused/adapted ideas, code, documentation, and third-party libraries -- include links to the original source as well }_

---

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

---

## **Design**

### Architecture

<puml src="diagrams/ArchitectureDiagram.puml" width="280" />

The **_Architecture Diagram_** given above explains the high-level design of the App.

Given below is a quick overview of main components and how they interact with each other.

**Main components of the architecture**

**`Main`** (consisting of classes [`Main`](https://github.com/AY2526S1-CS2103T-F15b-2/tp/tree/master/src/main/java/nusemp/Main.java) and [`MainApp`](https://github.com/AY2526S1-CS2103T-F15b-2/tp/tree/master/src/main/java/nusemp/MainApp.java)) is in charge of the app launch and shut down.

- At app launch, it initializes the other components in the correct sequence, and connects them up with each other.
- At shut down, it shuts down the other components and invokes cleanup methods where necessary.

The bulk of the app's work is done by the following four components:

- [**`UI`**](#ui-component): The UI of the App.
- [**`Logic`**](#logic-component): The command executor.
- [**`Model`**](#model-component): Holds the data of the App in memory.
- [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

**How the architecture components interact with each other**

The _Sequence Diagram_ below shows how the components interact with each other for the scenario where the user issues the command `delete 1`.

<puml src="diagrams/ArchitectureSequenceDiagram.puml" width="574" />

Each of the four main components (also shown in the diagram above),

- defines its _API_ in an `interface` with the same name as the Component.
- implements its functionality using a concrete `{Component Name}Manager` class (which follows the corresponding API `interface` mentioned in the previous point.

For example, the `Logic` component defines its API in the `Logic.java` interface and implements its functionality using the `LogicManager.java` class which follows the `Logic` interface. Other components interact with a given component through its interface rather than the concrete class (reason: to prevent outside component's being coupled to the implementation of a component), as illustrated in the (partial) class diagram below.

<puml src="diagrams/ComponentManagers.puml" width="300" />

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/AY2526S1-CS2103T-F15b-2/tp/tree/master/src/main/java/nusemp/ui/Ui.java)

<puml src="diagrams/UiClassDiagram.puml" alt="Structure of the UI Component"/>

The UI consists of a `MainWindow` that is made up of parts e.g.`CommandBox`, `ResultDisplay`, `PersonListPanel`, `StatusBarFooter` etc. All these, including the `MainWindow`, inherit from the abstract `UiPart` class which captures the commonalities between classes that represent parts of the visible GUI.

The `UI` component uses the JavaFx UI framework. The layout of these UI parts are defined in matching `.fxml` files that are in the `src/main/resources/view` folder. For example, the layout of the [`MainWindow`](https://github.com/AY2526S1-CS2103T-F15b-2/tp/tree/master/src/main/java/nusemp/ui/MainWindow.java) is specified in [`MainWindow.fxml`](https://github.com/AY2526S1-CS2103T-F15b-2/tp/tree/master/src/main/resources/view/MainWindow.fxml)

The `UI` component,

- executes user commands using the `Logic` component.
- listens for changes to `Model` data so that the UI can be updated with the modified data.
- keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
- depends on some classes in the `Model` component, as it displays `Person` object residing in the `Model`.

### Logic component

**API** : [`Logic.java`](https://github.com/AY2526S1-CS2103T-F15b-2/tp/tree/master/src/main/java/nusemp/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<puml src="diagrams/LogicClassDiagram.puml" width="550"/>

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("delete 1")` API call as an example.

<puml src="diagrams/DeleteSequenceDiagram.puml" alt="Interactions Inside the Logic Component for the `delete 1` Command" />

<box type="info" seamless>

**Note:** The lifeline for `DeleteCommandParser` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline continues till the end of diagram.
</box>

How the `Logic` component works:

1. When `Logic` is called upon to execute a command, it is passed to an `AddressBookParser` object which in turn creates a parser that matches the command (e.g., `DeleteCommandParser`) and uses it to parse the command.
1. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `DeleteCommand`) which is executed by the `LogicManager`.
1. The command can communicate with the `Model` when it is executed (e.g. to delete a person).<br>
   Note that although this is shown as a single step in the diagram above (for simplicity), in the code it can take several interactions (between the command object and the `Model`) to achieve.
1. The result of the command execution is encapsulated as a `CommandResult` object which is returned back from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<puml src="diagrams/ParserClasses.puml" width="600"/>

How the parsing works:

- When called upon to parse a user command, the `AddressBookParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name e.g., `AddCommandParser`) which uses the other classes shown above to parse the user command and create a `XYZCommand` object (e.g., `AddCommand`) which the `AddressBookParser` returns back as a `Command` object.
- All `XYZCommandParser` classes (e.g., `AddCommandParser`, `DeleteCommandParser`, ...) inherit from the `Parser` interface so that they can be treated similarly where possible e.g, during testing.

### Model component

**API** : [`Model.java`](https://github.com/AY2526S1-CS2103T-F15b-2/tp/tree/master/src/main/java/nusemp/model/Model.java)

<puml src="diagrams/ModelClassDiagram.puml" width="450" />

The `Model` component,

- stores the NUS EMP data i.e., all `Person` objects (which are contained in a `UniquePersonList` object) and `Event` objects (which are contained in a `UniqueEventList` object).
- stores the currently 'selected' `Person` objects (e.g., results of a search query) as a separate _filtered_ list which is exposed to outsiders as an unmodifiable `ObservableList<Person>` that can be 'observed' e.g. the UI can be bound to this list so that the UI automatically updates when the data in the list change.
- stores a `UserPref` object that represents the user’s preferences. This is exposed to the outside as a `ReadOnlyUserPref` objects.
- does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)

<box type="info" seamless>

**Note:** An alternative (arguably, a more OOP) model is given below. It has a `Tag` list in the `AddressBook`, which `Person` references. This allows `AddressBook` to only require one `Tag` object per unique tag, instead of each `Person` needing their own `Tag` objects.<br>

<puml src="diagrams/BetterModelClassDiagram.puml" width="450" />

</box>

### Storage component

**API** : [`Storage.java`](https://github.com/AY2526S1-CS2103T-F15b-2/tp/tree/master/src/main/java/nusemp/storage/Storage.java)

<puml src="diagrams/StorageClassDiagram.puml" width="550" />

The `Storage` component,

- can save both address book data and user preference data in JSON format, and read them back into corresponding objects.
- inherits from both `AddressBookStorage` and `UserPrefStorage`, which means it can be treated as either one (if only the functionality of only one is needed).
- depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `nusemp.commons` package.

---

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### \[Proposed\] Data archiving

_{Explain here how the data archiving feature will be implemented}_

_{Explain here how the data archiving feature will be implemented}_

---

## **Documentation, logging, testing, configuration, dev-ops**

- [Documentation guide](Documentation.md)
- [Testing guide](Testing.md)
- [Logging guide](Logging.md)
- [Configuration guide](Configuration.md)
- [DevOps guide](DevOps.md)

---

## **Appendix: Requirements**

### Product scope

**Target user profile**:

Event organizers at NUS who manage large contact databases, send bulk emails regularly, prefer keyboard-driven CLI interfaces for efficiency, and need automated mailing recommendations for targeted communication with event participants and stakeholders.

**Value proposition**:

Streamlines event communication workflow by integrating contact management with intelligent mailing recommendations and bulk email capabilities in a fast, keyboard-centric CLI interface, enabling event organizers to efficiently manage contacts and execute targeted email campaigns without switching between multiple tools.

### User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

| Priority | As a …          | I want to …                                                                | So that I can…                                         |
| -------- | --------------- | -------------------------------------------------------------------------- | ------------------------------------------------------ |
| `* * *`  | event organizer | add a contact with standard fields (Name, Phone, Email, Address)           | build my core contact database                         |
| `* * *`  | event organizer | associate a specific Role (e.g., 'Speaker', 'Attendee', 'VIP', 'Sponsor')  | categorize and filter my contacts effectively          |
| `* * *`  | event organizer | delete a contact from the address book                                     | remove outdated or irrelevant entries                  |
| `* * *`  | event organizer | create a new event with name, date, time, and venue                        | start organizing my contacts around it                 |
| `* * *`  | event organizer | view a list of all my events, showing key details and their status         | get an overview of upcoming, past, or cancelled events |
| `* * *`  | event organizer | associate contacts from my address book with a specific event              | build an attendee list for the event                   |
| `* * *`  | event organizer | get a list of contacts defined by tags, roles, or event association        | target communications and manage groups efficiently    |
| `* *`    | event organizer | set RSVP status for a contact for a specific event                         | track attendance commitments                           |
| `* *`    | event organizer | edit any field of an existing contact, including role and RSVP status      | keep contact information up-to-date                    |
| `* *`    | event organizer | view all details of a contact, including role, tags, and associated events | see a clean, readable summary                          |
| `* *`    | event organizer | find contacts by searching any field (Name, Role, Tag, Email)              | quickly locate specific individuals                    |
| `* *`    | event organizer | add multiple tags to a contact                                             | perform complex filtering                              |
| `* *`    | event organizer | filter contact list by tags and roles                                      | create highly specific lists                           |
| `* *`    | event organizer | list all contacts, with option to sort by Name or Role                     | get a general overview                                 |
| `* *`    | event organizer | remove a contact from an event without deleting from address book          | manage event participation flexibly                    |
| `* *`    | event organizer | archive a past event                                                       | keep main view uncluttered but retain data             |
| `* *`    | event organizer | set event status to 'Cancelled'                                            | exclude it from mailings                               |
| `* *`    | event organizer | import contacts from a CSV file                                            | quickly populate my address book                       |
| `* *`    | event organizer | export contacts or filtered subset to CSV                                  | backup or use in another application                   |
| `* *`    | user            | create a complete backup of application data                               | safeguard my information                               |
| `* *`    | user            | restore application data from a backup file                                | recover from data loss                                 |
| `* *`    | user            | specify storage location for data and backups                              | organize my files as needed                            |
| `*`      | user            | sort contacts by name                                                      | locate a person easily                                 |
| `*`      | user            | manage additional entity types related to contacts (tasks, loans, grades)  | extend functionality as needed                         |

### Use cases

For all use cases below, the **System** is NUS Event Mailer Pro (NUS EMP) and the **Actor** is the user, unless specified otherwise.

**Use case: UC1 - Add a contact**

**MSS**

1. User requests to add a contact, providing the required fields.
2. System adds the contact.
3. System shows success message.

Use case ends.

**Extensions**

- 1a. Required fields are missing or invalid.
  - 1a1. System shows an error message, indicating which fields are missing or invalid.
  - Use case resumes at step 1.
- 1b. Contact already exists.
  - 1b1. System shows an error message, indicating that the contact already exists.
  - Use case resumes at step 1.
- 2a. Error encountered when saving contact to storage.
  - 2a1. System shows an error message.
  - Use case ends.

---

**Use case: UC2 - Associate a role with a contact**

**MSS**

1. User requests to assign a role to a contact.
2. System updates the contact's role.
3. System shows success message.

Use case ends.

**Extensions**

- 1a. Role is invalid or not recognized.
  - 1a1. System shows an error message, indicating the invalid roles.
  - Use case resumes at step 1.
- 1b. Given contact is invalid.
  - 1b1. System shows an error message, indicating the invalid contact.
  - Use case resumes at step 1.
- 2a. Error encountered when saving contact to storage.
  - 2a1. System shows an error message.
  - Use case ends.

---

**Use case: UC3 - Create an event**

**MSS**

1. User requests to create a new event, providing the name, date, time, and venue.
2. System adds the event.
3. System shows success message.

Use case ends.

**Extensions**

- 1a. Required fields are missing or fields are invalid.
  - 1a1. System shows an error message, indicating which fields are missing or invalid.
  - Use case resumes at step 1.
- 2a. Error encountered when saving event to storage.
  - 2a1. System shows an error message.
  - Use case ends.

---

**Use case: UC4 - Associate contacts with an event**

**MSS**

1. User requests to associate contacts to an event.
2. System links the contacts to the event.
3. System shows success message.

Use case ends.

**Extensions**

- 1a. Given event is invalid, or does not exist.
  - 1a1. System shows an error message, indicating the invalid event.
  - Use case resumes at step 1.
- 1b. Given contact is invalid, or does not exist.
  - 1b1. System shows an error message, indicating the invalid contact.
  - Use case resumes at step 1.
- 2a. Error encountered when saving the linking of contacts to event to storage.
  - 2a1. System shows an error message.
  - Use case ends.

---

**Use case: UC5 - List contacts**

**MSS**

1. User requests to list contacts.
2. System shows a list of all contacts.

Use case ends.

**Extensions**

- 2a. No contacts exist.
  - 2a1. System informs the user that there are no contacts.
  - Use case ends.

---

**Use case: UC6 - List events**

**MSS**

1. User requests to list events.
2. System shows a list of all events with key details and status.

Use case ends.

**Extensions**

- 2a. No events exist.
  - 2a1. System informs the user that there are no events.
  - Use case ends.

---

**Use case: UC7 - Delete a contact**

**MSS**

1. User requests to delete a specific contact.
2. System deletes the contact.
3. System shows success message.

Use case ends.

**Extensions**

- 1a. Given contact is invalid, or does not exist.
  - 1a1. System shows an error message, indicating the invalid contact.
  - Use case resumes at step 1.
- 2a. Error encountered when deleting contact from storage.
  - 2a1. System shows an error message.
  - Use case ends.

---

**Use case: UC8 - Filter contacts by tags, roles, or event association**

**MSS**

1. User requests to list all contacts that follows the filter criteria (tags, roles, event).
2. System displays the filtered contact list.

Use case ends.

**Extensions**

- 1a. No contacts match the criteria.
  - 1a1. System informs user that no contacts match the criteria.
  - Use case ends.

### Non-Functional Requirements

#### Technical

- Should run on any mainstream OS which has Java 17 installed
- Should work without an installer
- Should run on various screen resolutions:
  - _Work well_ for screen resolutions 1920x1080 and higher, and for screen scales 100% and 125%
    - i.e. all text and UI elements should be clearly visible and usable, with proper scaling and without any clipping or overflow
  - _Usable_ for screen resolutions 1280x720 and higher, and for screen scales 150%
    - i.e. all text and UI elements should be visible and usable, with minimal clipping or overflow

#### Performance

- Should have a response time of 500ms or less for any user command or action
- Should be able to handle up to 10000 contacts and 1000 events while following the response time requirement above
- Should launch within 3 seconds on standard hardware
- Should not exceed 500MB in memory when handling 10000 contacts
- Data storage should not exceed 5MB when handling 10000 contacts

#### Features

- All features should work offline
- Should be designed such that fast-typing users can do actions faster than using a standard GUI
- UI design should be clean and simple, with consistent spacing and alignment, reasonable use of colors, and readable fonts
- Command syntax should be consistent and intuitive
  - Commands should start with either `contact` or `event` to indicate the entity being operated on
  - General command format: `{contact | event} commandName [parameters] [options]`
  - User-supplied parameters should follow this format: `--parameterName parameterValue`
  - User-supplied options should follow this format: `-optionName`
- Command response should be useful enough such that the user has no doubts on whether the operation was successful
- Command errors should be helpful enough for users to fix their commands without referring to the user guide each time
- Data storage of contacts or events should use a human editable file
- Should explicitly warn and allow user to recover data manually in case of data corruption

#### Development

##### Code

- Should be developed on top of the AddressBook-Level3 project
- Should be developed iteratively in a breadth-first manner
- Should have a clear and consistent coding style, following the [Java coding standards](https://se-education.org/guides/conventions/java/index.html)
- Should have tests for every public method or class, and have at least 75% code coverage
- Should not have a remote server
- Should not use a DBMS
- Should use OOP principles
- Should log every high-level action that the system performs (e.g. save data, respond to command), and every error encountered
- Should use assertions where there are assumptions made, to catch programming errors during development
- Should use checked exceptions to handle any user input errors
- All errors or exceptions that are outside the programmer's control (e.g. file I/O errors) should not cause the program to crash, and should be handled gracefully with appropriate user messages
- Should use defensive programming when appropriate

##### Collaboration

- Should use GitHub Issues to track tasks, bugs, and features
- Should follow the [Git conventions](https://se-education.org/guides/conventions/git.html) for commit messages and branch names
- PR names should follow this format: `[#issueNumber] description`, where description is a short summary of the changes made in the PR, following the same Git conventions for commit messages.
- When merging a PR, the merge commit message should follow this format: `description (#prNumber)`, where description is the same description found in the PR title.
  - For larger PRs, the merge commit description should be a summary of the changes made in the PR, while still following the Git conventions.
- Should pass all status checks and have at least one approving review from a different team member before merging a PR.

##### Submission

- Should package the application into a single jar file, with file size not exceeding 100MB
- Should design the user guide and developer guide to be PDF friendly, with each file size not exceeding 15MB

### Glossary

- **Event organizer**: A person organizes events and manages contacts. The intended users of the app.
- **Contact**: An entity that represents a person that may be participating in an event.
- **Event**: An entity that represents an event that the user is organizing.
- **Mainstream OS**: Windows, Linux, MacOS
- **Standard hardware**: A computer with at least 4GB RAM, Intel i5 processor (or equivalent), and SSD.
- **GUI**: Graphical User Interface. An interface that is primarily visual and mouse-driven, with minimal keyboard shortcuts.
- **Human editable file**: A file that can be opened and edited using a standard text editor, such as Notepad.
- **Data corruption**: Storage data cannot be read or parsed correctly, due to invalid format or invalid values.
- **Breadth-first manner**: Implementing a basic version of all features first, then iterating to improve each feature.
- **Code coverage**: The percentage of code that is executed during testing. Measured using the Codecov tool.
- **DBMS**: Database management system, e.g. MySQL, PostgreSQL, etc.
- **OOP principles**: Object-Oriented programming principles, e.g. Encapsulation, Abstraction, Inheritance, Polymorphism, etc.
- **Defensive programming**: A programming approach that anticipates and handles potential errors or misuse of the code, to prevent crashes or unexpected behavior. See [here](https://nus-cs2103-ay2526s1.github.io/website/se-book-adapted/chapters/errorHandling.html#defensive-programming) for more details.
- **PR**: Pull Request. A feature of GitHub that allows developers to collaborate on code changes.
- **Status checks**: Automated tests that run on the code in a PR to ensure that it meets the project's quality standards. In this project, the status checks include:
  - No checkstyle violations
  - All tests pass
  - Build is successful on all 3 platforms: Windows, Linux, MacOS

---

## **Appendix: Instructions for manual testing**

Given below are instructions to test the app manually.

<box type="info" seamless>

**Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more _exploratory_ testing.

</box>

### Launch and shutdown

1. Initial launch

   1. Download the jar file and copy into an empty folder

   1. Double-click the jar file Expected: Shows the GUI with a set of sample contacts. The window size may not be optimum.

1. Saving window preferences

   1. Resize the window to an optimum size. Move the window to a different location. Close the window.

   1. Re-launch the app by double-clicking the jar file.<br>
      Expected: The most recent window size and location is retained.

1. _{ more test cases … }_

### Deleting a person

1. Deleting a person while all persons are being shown

   1. Prerequisites: List all persons using the `list` command. Multiple persons in the list.

   1. Test case: `delete 1`<br>
      Expected: First contact is deleted from the list. Details of the deleted contact shown in the status message. Timestamp in the status bar is updated.

   1. Test case: `delete 0`<br>
      Expected: No person is deleted. Error details shown in the status message. Status bar remains the same.

   1. Other incorrect delete commands to try: `delete`, `delete x`, `...` (where x is larger than the list size)<br>
      Expected: Similar to previous.

1. _{ more test cases … }_

### Saving data

1. Dealing with missing/corrupted data files

   1. _{explain how to simulate a missing/corrupted file, and the expected behavior}_

1. _{ more test cases … }_
