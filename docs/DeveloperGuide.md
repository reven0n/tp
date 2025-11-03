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

---

## **1. Getting Started**

### 1.1 Project Overview

NUS Event Mailer Pro (NUS EMP) is a desktop application designed for NUS event organizers to manage contacts and events efficiently. The application features a graphical user interface (GUI) with an integrated command terminal for keyboard-driven input, built using Java 17 and JavaFX.

**Core Capabilities:**

- Contact management with tagging and role assignment
- Event creation and status tracking
- Contact-event association management
- Bulk email campaign preparation
- Data import/export capabilities

### 1.2 Quick Setup

Refer to the guide [_Setting up and getting started_](SettingUp.md) for detailed installation and setup instructions.

### 1.3 Development Workflow

1. **Clone the repository** and import into your preferred IDE
2. **Run the application** using the Main class or Gradle task
3. **Understand the architecture** by reviewing Section 2
4. **Follow the coding standards** outlined in Section 6
5. **Write tests** for new features following Section 6 guidelines
6. **Update documentation** as part of your pull request

---

## **2. System Architecture**

### 2.1 High-Level Architecture

<puml src="diagrams/ArchitectureDiagram.puml" width="250" alt="Architecture Diagram"/>

The application follows a clean architecture pattern with five main components:

- **UI**: JavaFX-based graphical interface with command terminal
- **Logic**: Command parsing and execution engine
- **Model**: Data entities and state management
- **Storage**: JSON-based persistence layer
- **Commons**: Shared utilities and core components

### 2.2 Component Overview

#### 2.2.1 Main Component

The `Main` class handles application lifecycle:

- Initializes components in correct sequence during startup
- Manages graceful shutdown with cleanup operations
- Coordinates component dependencies and wiring

#### 2.2.2 UI Component

**API**: [`Ui.java`](https://github.com/AY2526S1-CS2103T-F15b-2/tp/blob/master/src/main/java/nusemp/ui/Ui.java)

<puml src="diagrams/UiClassDiagram.puml" width="600" alt="Ui Class Diagram"/>

The UI component consists of:

- `MainWindow`: Primary application window
- `CommandBox`: User input interface
- `ResultDisplay`: Command feedback display
- Both of `CommandBox` and `ResultDisplay` are automatically hidden, unless the user presses the terminal button located on the sidebar or presses the keyboard shortcut (Ctrl + T) which will bring them up
- `ContactListPanel`/`EventListPanel`: Data presentation panels that fetch their data from the `model` component
- `StatusBarFooter`: Status information display

All UI components inherit from `UiPart` abstract class and use FXML for layout definitions.

#### 2.2.3 Logic Component

**API**: [`Logic.java`](https://github.com/AY2526S1-CS2103T-F15b-2/tp/blob/master/src/main/java/nusemp/logic/Logic.java)

<puml src="diagrams/LogicClassDiagram.puml" width="400" alt="Logic class diagram"/>

The Logic component manages:

- **Command parsing**: Translates user input into executable commands
- **Command execution**: Executes commands against the model
- **Result formatting**: Returns formatted results to the UI

Key classes:

- `LogicManager`: Main logic coordinator
- `AppParser`: Routes commands to appropriate parsers
- `Command` hierarchy: Individual command implementations

#### 2.2.4 Model Component

<puml src="diagrams/ModelClassDiagram.puml" width="500" alt="Model Class Diagram"/>

**API**: [`Model.java`](https://github.com/AY2526S1-CS2103T-F15b-2/tp/blob/master/src/main/java/nusemp/model/Model.java)

The Model component maintains application state:

- **Data storage**: `UniqueContactList`, `UniqueEventList`
- **Filtered views**: Observable lists for UI binding
- **User preferences**: Application settings and preferences
- **Data relationships**: Contact-event associations with `ParticipantMap`

For more details, refer to [Section 3](#3-core-domain-model).

#### 2.2.5 Storage Component

<puml src="diagrams/StorageClassDiagram.puml" width="500" alt="Storage class diagram"/>

**API**: [`Storage.java`](https://github.com/AY2526S1-CS2103T-F15b-2/tp/blob/master/src/main/java/nusemp/storage/Storage.java)

The Storage component handles persistence:

- **JSON serialization**: Convert objects to/from JSON
- **File operations**: Read/write data files
- **Backup management**: Automatic backup creation
- **Corruption handling**: Data recovery mechanisms

#### 2.2.6 Commons Component

The Commons component provides shared utilities:

- **Core classes**: AppParameters, CommandType enum, Messages constants
- **Utilities**: String formatting, collection utilities, validation helpers
- **Exceptions**: Base exception classes for error handling
- **Logging**: Centralized logging configuration and utilities

### 2.3 Component Interactions

<puml src="diagrams/ComponentInteractionSequenceDiagram.puml" width="600" alt="Component Interaction Sequence Diagram"/>

The components interact through well-defined interfaces:

- UI calls Logic to execute commands
- Logic updates Model state
- Model notifies UI of changes through observable lists
- Storage persists Model data on changes

---

## **3. Core Domain Model**

### 3.1 Data Model Overview

<puml src="diagrams/ModelClassDiagram.puml" width="500" alt="Model Class Diagram"/>

The `Model` component,
- stores all data in NUS EMP
- stores the currently "selected" `Contact`s and `Event`s as a filtered list, exposed to outsiders as an unmodifiable `ObservableList`, which can be observed for UI updates
- stores a `UserPref` object that represents the user’s preferences, e.g. dark theme, window sizes
- does not depend on the other main components, as its single responsibility is to manage the data of NUS EMP

The `Model` component manages three core entity types:

- **Contact**: Individuals with contact information and tags
- **Event**: Events with names, statuses, dates, venues and tags
- **Participant**: A relationship that contains the RSVP status of a contact for a specific event. It is a simple data class with three fields: `Contact`, `Event` and `ParticipantStatus`.

### 3.2 Contact and Event

<puml src="diagrams/ContactEventClassDiagram.puml" width="500" alt="Class Diagram for both Contact and Event"/>

Both `Contact` and `Event` classes share several design characteristics:
- Immutable data structure that contains various fields, which are in turn represented by their own classes: `Name`, `Email`, `Phone`, `Address`, `Tag`, `Date`, `EventStatus`
- Primary key system for uniqueness: email address (case-insensitive) for `Contact`, name for `Event`
- An internal field `invalidationToggle` to provide invalidation mechanism for observable list updates

For the field classes:
- Each class encapsulates validation logic and formatting rules specific to its own field type.
- Each field class must be provided during the construction of `Contact` or `Event` objects, even optional fields.
- Optional fields (i.e. `Phone` and `Address`) are represented using empty values instead.

## 3.3 Participant Handling

Participant links between contacts and events are handled through a `ParticipantMap`:
- It internally uses two HashMaps to maintain bidirectional relationships.
- Keys used are `ContactKey` and `EventKey`, which are simple classes that only store the primary key fields (i.e. case-insensitive email for `ContactKey` and name for `EventKey`).
- Lookups for contacts and their linked events are done through these keys for efficiency.

<puml src="diagrams/ParticipantMapActivityDiagram.puml" alt="ParticipantMap Activity Diagram" />

## **4. Command System**

### 4.1 Command Processing Flow

The command system follows a consistent pattern:

1. **Input Parsing**: `AppParser` routes commands to specific parsers
2. **Command Creation**: Parsers create appropriate `Command` objects
3. **Validation**: Model validates entities on construction, Commands validate business rules, Storage validates data integrity
4. **Execution**: Commands execute against the model
5. **Result Formatting**: Success/error messages returned to UI

### 4.2 Command Categories

#### 4.2.1 Contact Commands

- `contact add`: Add new contact with name, email, phone, address, and tags
- `contact delete`: Remove contact by index
- `contact edit`: Modify contact details (name, email, phone, address, tags)
- `contact find`: Search contacts by keywords or specific fields
- `contact list`: Display all contacts with indices
- `contact show`: View contact details and associated events

#### 4.2.2 Event Commands

- `event add`: Create new event with name, date, address, and tags
- `event delete`: Remove event by index
- `event edit`: Modify event details (name, date, address, status, tags)
- `event list`: Display all events with indices and status
- `event show`: View event details and associated contacts
- `event link`: Associate contacts with events
- `event unlink`: Remove contact associations from events
- `event export`: Export all contacts from an event to clipboard
- `event rsvp`: Update contact's RSVP status for an event

#### 4.2.3 System Commands

- `help`: Display command help (opens user guide in browser)
- `exit`: Terminate application

### 4.3 Parser Architecture

<puml src="diagrams/ParserClasses.puml" width="550" alt="Parser Class Diagram"/>

The parser system uses a direct interface implementation approach:

- `Parser<T>`: Generic interface for all command parsers
- `AppParser`: Main command router using regex patterns and CommandType enum
- Individual parsers: Each implements `Parser<SpecificCommand>` directly
- `CommandType` enum: Routes commands to appropriate parsing logic (CONTACT, EVENT, HELP, EXIT, UNKNOWN)

**Key Components:**

- `BASIC_COMMAND_FORMAT`: Initial command type separation
- `CONTACT_COMMAND_FORMAT`: Contact command parsing pattern
- `EVENT_COMMAND_FORMAT`: Event command parsing pattern
- `ArgumentTokenizer`: Handles prefix-based argument parsing (--name, -n, etc.)

### 4.4 Error Handling Flow

<puml src="diagrams/ErrorHandlingFlowDiagram.puml" width="600" alt="Error Handling Flow Diagram"/>

Error handling follows these principles:

- **Input validation**: Validate before processing using `ParseException`
- **Command execution errors**: Handle runtime errors using `CommandException`
- **Clear messages**: Use `Messages` class constants for consistent error feedback
- **Logging**: Log parsing failures using `LogsCenter` for debugging
- **Exception propagation**: Handle at appropriate levels (parsing vs execution)

**Exception Types:**

- `ParseException`: Input format and validation errors
- `CommandException`: Command execution errors
- `IllegalValueException`: Base class for parsing-related errors

---

## **5. Component Interactions**

### 5.1 Add Contact Flow

<puml src="diagrams/ContactAddSequenceDiagram.puml" alt="Contact Add Sequence Diagram"/>

**Steps:**

1. User inputs `contact add` command
2. UI passes input to Logic component
3. AppParser routes to ContactAddCommandParser
4. Parser validates input and creates ContactAddCommand
5. Command executes against Model
6. Model validates and adds contact to UniqueContactList
7. Storage persists updated data
8. UI updates through observable list binding
9. Success message displayed to user

### 5.2 Create Event Flow

Refer to Add Contact Sequence Diagram above.

**Steps:**

1. User inputs `event add` command with event details
2. UI passes input to Logic component
3. AppParser routes to EventAddCommandParser
4. Parser validates dates, times, and other parameters
5. EventAddCommand created and executed
6. Model constructor validates event and adds to UniqueEventList
7. Storage persists updated event data
8. UI refreshes event list display
9. Confirmation message shown to user

### 5.3 Link Contact to Event Flow

<puml src="diagrams/LinkSequenceDiagram.puml" alt="Event Link Sequence Diagram"/>

**Steps:**

1. User inputs `event link` command with contact and event identifiers
2. Logic parses command and validates contact/event existence
3. Participant object created with appropriate status
4. Model updates ParticipantMap and triggers invalidationToggle mechanism for both contact and event displays
5. Storage persists relationship changes
6. UI updates both contact and event displays
7. Success confirmation displayed

### 5.4 Search/Find Flow

<puml src="diagrams/SearchFindSequenceDiagram.puml" alt="Search Find Sequence Diagram"/>

**Steps:**

1. User inputs `contact find` or `event find` command with search criteria
2. UI passes input to Logic component
3. AppParser routes to appropriate FindCommandParser (ContactFindCommandParser)
4. Parser validates search keywords and field prefixes
5. Predicate objects created based on search criteria (name, email, phone, address, tag)
6. FindCommand created and executed with predicate
7. Model updates filtered contact/event list using predicate
8. Observable lists trigger UI updates to show filtered results
9. Success message with result count displayed to user

### 5.5 Error Handling Sequence

<puml src="diagrams/ErrorHandlingSequenceDiagram.puml" alt="Error Handling Sequence Diagram"/>

**Steps:**

1. Exception occurs in any component (parsing, validation, execution, storage)
2. Exception caught and wrapped in appropriate exception type
3. Error message formatted with actionable guidance
4. CommandResult created with error information
5. UI displays error message in ResultDisplay
6. Model state remains unchanged (transactional behavior)
7. User can retry command with corrected input

### 5.6 Application Startup/Initialization Flow

**Steps:**

1. Main class starts JavaFX application and calls MainApp
2. MainApp initializes Config with default fallbacks
3. Storage components created (JsonAppDataStorage, JsonUserPrefsStorage)
4. ModelManager created with empty AppData and UserPrefs
5. LogicManager initialized with Model and Storage dependencies
6. UiManager initialized with Logic and Model references
7. Existing data loaded from storage into Model through Storage
8. Data corruption handling creates sample data if loading fails
9. Main window displayed with loaded or sample data
10. Application ready for user input

### 5.7 Edit/Update Flow

<puml src="diagrams/EventEditSequenceDiagram.puml" alt="Event Edit/Update Sequence Diagram"/>

**Steps:**

1. User inputs `contact edit` or `event edit` command with index and new values
2. UI passes input to Logic component
3. AppParser routes to appropriate EditCommandParser
4. Parser validates index exists and new field values
5. EditCommand created with index and updated field data
6. Command executes against Model to locate original entity
7. New entity object created with updated fields (immutable pattern)
8. Model replaces original entity in UniqueContactList/UniqueEventList
9. Storage persists updated entity data
10. Observable lists trigger UI updates to reflect changes
11. Success message displayed with updated entity details

### 5.8 RSVP Status Update Flow

Refer to Edit/Update Sequence Diagram above.

**Steps:**

1. User inputs `event rsvp --event EVENT_INDEX --contact CONTACT_INDEX --status STATUS` command
2. UI passes input to Logic component
3. AppParser routes to EventRsvpCommandParser using CommandType enum
4. Parser validates event and contact indices exist and status is valid (UNAVAILABLE, AVAILABLE, UNKNOWN)
5. EventRsvpCommand created with validated parameters
6. Command executes against Model to locate event and contact in their respective lists
7. Model uses ParticipantMap to find existing Participant
8. Participant status updated to new RSVP status
9. ModelManager automatically triggers Storage save operation
10. Storage persists updated participant relationship data to JSON files
11. Observable lists update both event and contact displays
12. Success message displayed with RSVP confirmation

### 5.9 Data Persistence Flow

<puml src="diagrams/StorageSequenceDiagram.puml" width="600" alt="Storage sequence diagram"/>

**Steps:**

1. Command execution completes successfully in Model
2. ModelManager automatically calls storage save method
3. JsonSerializableAppData serializes Model data to JSON format with participant relationships stored as email references
4. JSON data written to primary storage files
5. Backup files created before overwriting existing data
6. Storage validates file integrity after writing
7. Success/failure status logged and reported back to Model
8. Storage corruption handling provides recovery options if needed

---

## **6. Advanced Topics**

### 6.1 Observable Pattern Implementation

The application uses JavaFX's observable pattern for real-time UI updates:

```java
// In ModelManager
private final ObservableList<Contact> filteredContacts = FXCollections.observableArrayList();

// UI components bind to this list
contactListPanel.setItems(model.getFilteredContactList());
```

**Key Features:**

- **Automatic UI updates**: When Model data changes, UI updates automatically
- **Invalidation mechanism**: Contacts and Events use invalidationToggle field with immutable pattern to force observable list refreshes through object replacement
- **Filtered views**: Separate observable lists for filtered and all data
- **Decoupled architecture**: UI doesn't need to know when data changes
- **Efficient updates**: Only UI components that display changed data are updated

### 6.2 Performance Considerations

#### 6.2.1 Data Scaling

- Application supports up to 10,000 contacts and 1,000 events
- Observable lists use efficient change detection
- JSON serialization optimized for large datasets
- UI virtualization for large lists (if implemented)

#### 6.2.2 Memory Management

- Immutable objects prevent memory leaks
- Weak references in event listeners
- Proper cleanup in component disposal

### 6.3 Common Debugging Scenarios

#### 6.3.1 Command Not Found

1. Check command registration in AppParser
2. Verify parser class exists and is accessible
3. Validate command word spelling

#### 6.3.2 Data Not Persisting

1. Check file permissions
2. Verify JSON serialization works
3. Check storage file paths
4. Look for corruption handling in logs

#### 6.3.3 UI Not Updating

1. Verify observable list binding
2. Check for invalidation toggle usage
3. Ensure proper list change notifications

### 6.4 Extension Points

#### 6.4.1 Custom Commands

- Extend `Command` base class
- Follow existing naming conventions
- Implement proper error handling

#### 6.4.2 New Entity Types

- Create entity classes following Contact/Event patterns
- Add to Model component interfaces
- Implement storage serialization
- Create appropriate UI components

#### 6.4.3 Storage Backends

- Implement `Storage` interface
- Handle serialization format
- Maintain existing API contracts

---

## **7. Development Guidelines**

### 7.1 Adding New Commands

**Step-by-step process:**

1. **Create Command Class**

   ```java
   public class NewCommand extends Command {
       @Override
       public CommandResult execute(Model model) throws CommandException {
           // Implementation
       }
   }
   ```

2. **Create Parser Class**

   ```java
   public class NewCommandParser implements Parser<NewCommand> {
       public NewCommand parse(String args) throws ParseException {
           // Parsing logic
       }
   }
   ```

3. **Update AppParser**

   - Add command to CommandType enum if needed
   - Add routing logic in parseCommand method
   - Add regex pattern matching for new command

4. **Add Tests**

   - Unit tests for command execution
   - Parser tests for input validation
   - Integration tests for end-to-end flow

5. **Update Documentation**
   - Add command to User Guide
   - Update Developer Guide if needed

### 7.2 Adding New Data Fields

**For Contact fields:**

1. Add field to `Contact` class
2. Update constructor and validation logic
3. Modify storage JSON adapters
4. Update UI components and FXML
5. Add parser support for new field
6. Update test cases

**For Event fields:**

1. Add field to `Event` class
2. Follow similar steps as Contact fields
3. Consider impact on participant relationships

### 7.3 Testing Best Practices

#### 7.3.1 Unit Tests

- Test each public method individually
- Mock external dependencies
- Cover both success and failure scenarios
- Use descriptive test method names

#### 7.3.2 Integration Tests

- Test component interactions
- Use actual file system for storage tests
- Validate end-to-end command flows
- Test UI updates through observable lists

#### 7.3.3 Test Structure

```java
@Test
void execute_contactAddCommand_success() {
    // Arrange
    Model expectedModel = new ModelManager();
    Contact validContact = new Contact(...);
    expectedModel.addContact(validContact);

    // Act
    CommandResult result = new ContactAddCommand(validContact).execute(model);

    // Assert
    assertEquals(expectedModel, model);
    assertEquals(String.format(MESSAGE_ADD_CONTACT_SUCCESS, validContact),
                 result.getFeedbackToUser());
}
```

### 7.4 Code Style Guidelines

- Follow [Java coding standards](https://se-education.org/guides/conventions/java/index.html)
- Use meaningful variable and method names
- Keep methods short and focused
- Write self-documenting code
- Use assertions for internal assumptions
- Handle exceptions gracefully

---

## **8. Supporting Documentation**

- [Documentation guide](Documentation.md)
- [Testing guide](Testing.md)
- [Logging guide](Logging.md)
- [Configuration guide](Configuration.md)
- [DevOps guide](DevOps.md)

---

## **Appendix A: Requirements**

### A.1 Product Scope

#### A.1.1 Target User Profile

Event organizers at NUS who manage large contact databases, send bulk emails regularly, prefer keyboard-driven CLI interfaces for efficiency, and need automated mailing recommendations for targeted communication with event participants and stakeholders.

#### A.1.2 Value Proposition

Streamlines event communication workflow by integrating contact management with intelligent mailing recommendations and bulk email capabilities in a fast, keyboard-centric CLI interface, enabling event organizers to efficiently manage contacts and execute targeted email campaigns without switching between multiple tools.

### A.2 User Stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

| Priority | As a …          | I want to …                                                               | So that I can…                                         |
| -------- | --------------- | ------------------------------------------------------------------------- | ------------------------------------------------------ |
| `* * *`  | event organizer | add a contact with standard fields (Name, Phone, Email, Address)          | build my core contact database                         |
| `* * *`  | event organizer | associate a specific Role (e.g., 'Speaker', 'Attendee', 'VIP', 'Sponsor') | categorize and filter my contacts effectively          |
| `* * *`  | event organizer | delete a contact from the address book                                    | remove outdated or irrelevant entries                  |
| `* * *`  | event organizer | create a new event with name, date, time, and venue                       | start organizing my contacts around it                 |
| `* * *`  | event organizer | view a list of all my events, showing key details and their status        | get an overview of upcoming, past, or cancelled events |
| `* * *`  | event organizer | associate contacts from my address book with a specific event             | build an attendee list for the event                   |
| `* * *`  | event organizer | get a list of contacts defined by tags, or event association              | target communications and manage groups efficiently    |
| `* *`    | event organizer | set RSVP status for a contact for a specific event                        | track attendance commitments                           |
| `* *`    | event organizer | edit any field of an existing contact, including role and RSVP status     | keep contact information up-to-date                    |
| `* *`    | event organizer | view all details of a contact, including tags, and associated events      | see a clean, readable summary                          |
| `* *`    | event organizer | view all details of an event, including tags, and associated contacts     | see a clean, readable summary                          |
| `* *`    | event organizer | find contacts by searching any field (Name, Role, Tag, Email)             | quickly locate specific individuals                    |
| `* *`    | event organizer | add multiple tags to a contact                                            | perform complex filtering                              |
| `* *`    | event organizer | filter contact list by tags                                               | create highly specific lists                           |
| `* *`    | event organizer | list all contacts, with option to sort by Name or Role                    | get a general overview                                 |
| `* *`    | event organizer | remove a contact from an event without deleting from address book         | manage event participation flexibly                    |
| `* *`    | event organizer | archive a past event                                                      | keep main view uncluttered but retain data             |
| `* *`    | event organizer | set event status to 'Cancelled'                                           | exclude it from mailings                               |
| `* *`    | event organizer | import contacts from a CSV file                                           | quickly populate my address book                       |
| `* *`    | event organizer | export contacts or filtered subset to CSV                                 | backup or use in another application                   |
| `* *`    | user            | create a complete backup of application data                              | safeguard my information                               |
| `* *`    | user            | restore application data from a backup file                               | recover from data loss                                 |
| `* *`    | user            | specify storage location for data and backups                             | organize my files as needed                            |
| `*`      | user            | sort contacts by name                                                     | locate a person easily                                 |
| `*`      | user            | manage additional entity types related to contacts (tasks, loans, grades) | extend functionality as needed                         |

### A.3 Use Cases

For all use cases below, the **System** is NUS Event Mailer Pro (NUS EMP) and the **Actor** is the user, unless specified otherwise.

| Use Case ID | Description                    | Actor | Preconditions                      | Main Success Scenario                                                                                                                                                             | Extensions                                                                                                                                                                                                                                                                                                                                                                                                                                     | Postconditions                                     | Priority |
| ----------- | ------------------------------ | ----- | ---------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | -------------------------------------------------- | -------- |
| UC01        | Add a contact                  | User  | System is running                  | 1. User inputs contact details with name and email<br>2. System validates required fields<br>3. Contact added to the system<br>4. Success message displayed<br>Use case ends.     | 1a. Required fields missing or invalid<br> - System shows error message with missing fields<br> - User can retry with correct input<br> - Use case ends.<br>2a. Contact already exists<br> - System shows error about duplicate contact<br> - User can add with different email<br> - Use case ends.                                                                                                                                           | Contact added to system                            | High     |
| UC02        | Delete a contact               | User  | Contact exists in displayed list   | 1. User selects contact by index<br>2. User confirms deletion<br>3. Contact removed from system and all associated events<br>4. Confirmation message displayed<br>Use case ends.  | 1a. Invalid contact index<br> - System shows error about invalid index<br> - User can try with correct index<br> - Use case ends.                                                                                                                                                                                                                                                                                                              | Contact removed from system and all events         | High     |
| UC03        | Create an event                | User  | System is running                  | 1. User inputs event details with name and date<br>2. System validates date format<br>3. Event created with STARTING status<br>4. Success message displayed<br>Use case ends.     | 1a. Required fields missing<br> - System shows error about missing name or date<br> - User can provide missing information<br> - Use case ends.<br>2a. Invalid date format<br> - System shows specific date format error (DD-MM-YYYY HH:mm)<br> - User can correct date format<br> - Use case ends.                                                                                                                                            | Event added to system                              | High     |
| UC04        | Associate contacts with event  | User  | Event and contacts exist in system | 1. User selects event and contact by indices<br>2. System links contact to event<br>3. Contact marked as UNKNOWN for event<br>4. Confirmation message displayed<br>Use case ends. | 1a. Invalid event or contact index<br> - System shows error about non-existent event/contact<br> - User can select valid indices<br> - Use case ends.<br>2a. Contact already linked to event<br> - System shows that association already exists<br> - User can select different contact<br> - Use case ends.                                                                                                                                   | Contact-event association created                  | High     |
| UC05        | List contacts                  | User  | System is running                  | 1. User requests to list contacts<br>2. System displays all contacts<br>3. Each contact shown with index and details<br>Use case ends.                                            | 1a. No contacts exist<br> - System shows "No contacts found"<br> - User can add contacts using add command<br> - Use case ends.                                                                                                                                                                                                                                                                                                                | Contact list displayed with indices                | Medium   |
| UC06        | List events                    | User  | System is running                  | 1. User requests to list events<br>2. System displays all events<br>3. Each event shown with index, date, and status<br>Use case ends.                            | 1a. No events exist<br> - System shows "No events found"<br> - User can create events using add command<br> - Use case ends.                                                                                                                                                                                                                                                                                                                   | Event list displayed               | Medium   |
| UC07        | Filter contacts by criteria    | User  | Contacts exist in system           | 1. User inputs search keywords or field criteria<br>2. System filters contacts matching criteria<br>3. Filtered list displayed with new indices<br>Use case ends.                 | 1a. No contacts match criteria<br> - System shows "No contacts found"<br> - User can try different search terms<br> - Use case ends.<br>2a. Invalid search syntax<br> - System shows error about search format<br> - User can correct search syntax<br> - Use case ends.                                                                                                                                                                       | Filtered contact list displayed                    | Medium   |
| UC08        | Update RSVP status for contact | User  | Contact already linked to event    | 1. User selects event, contact, and RSVP status<br>2. System updates contact's status for event<br>3. Confirmation message displayed with new status<br>Use case ends.            | 1a. Invalid event or contact index<br> - System shows error about non-existent selection<br> - User can select valid indices<br> - Use case ends.<br>2a. Invalid RSVP status<br> - System shows valid status options (available/unavailable/unknown)<br> - User can select valid status<br> - Use case ends.<br>3a. Contact not linked to event<br> - System shows contact not in event<br> - User can link contact first<br> - Use case ends. | RSVP status updated for contact-event relationship | High     |

### A.4 Non-Functional Requirements

| NFR ID   | Category        | Description                           | Metric/Target                                 | Priority |
| -------- | --------------- | ------------------------------------- | --------------------------------------------- | -------- |
| NFR-T01  | Technical       | Runtime environment compatibility     | Java 17, Windows/macOS/Linux                  | High     |
| NFR-T02  | Technical       | Deployment method                     | Single JAR, no installer                      | High     |
| NFR-T03  | Technical       | Network dependency                    | Offline functionality                         | High     |
| NFR-P01  | Performance     | Command response time                 | < 500ms for typical commands                  | High     |
| NFR-P02  | Performance     | Application startup time              | < 3 seconds on standard hardware              | High     |
| NFR-P03  | Performance     | Memory usage with 10,000 contacts     | < 5000MB                                      | Medium   |
| NFR-P04  | Performance     | Data storage size for 10,000 contacts | < 50MB                                        | Medium   |
| NFR-P05  | Performance     | Supported data capacity               | 10,000 contacts, 1,000 events                 | High     |
| NFR-UX01 | User Experience | Interface design                      | Keyboard-driven, clean and simple             | High     |
| NFR-UX02 | User Experience | Command syntax consistency            | Consistent format across all operations       | High     |
| NFR-UX03 | User Experience | Error message quality                 | Clear, actionable guidance                    | High     |
| NFR-UX04 | User Experience | Command response usefulness           | Clear success/failure indication              | High     |
| NFR-F01  | Features        | Data file format                      | Human-editable (JSON)                         | Medium   |
| NFR-F02  | Features        | Data corruption handling              | Recovery warnings and manual recovery options | Medium   |
| NFR-D01  | Development     | Code coverage requirement             | ≥ 75% code coverage                           | Medium   |
| NFR-D02  | Development     | Testing framework                     | JUnit 5, comprehensive test suite             | Medium   |

### A.5 Requirements Priority Matrix

| Requirement Category      | Must Have (High)             | Should Have (Medium)        | Could Have (Low)         | Won't Have |
| ------------------------- | ---------------------------- | --------------------------- | ------------------------ | ---------- |
| **Contact Management**    | Add, edit, delete contacts   | Advanced search/filtering   | Custom fields            | N/A        |
| **Event Management**      | Create, view events          | Event archiving             | Recurring events         | N/A        |
| **Contact-Event Linking** | Link/unlink contacts         | RSVP status tracking        | Bulk operations          | N/A        |
| **Data Management**       | Data persistence             | Import/Export functionality | Custom storage locations | N/A        |
| **Performance**           | < 500ms response time        | < 3GB memory usage          | Batch optimization       | N/A        |
| **User Experience**       | Keyboard interface           | Consistent syntax           | Advanced UI features     | N/A        |
| **Technical**             | Cross-platform compatibility | Human-readable files        | Cloud integration        | N/A        |

### A.6 Glossary

- **Event organizer**: Target user who manages events and contacts
- **Contact**: Person entity with contact information and event associations
- **Event**: Event entity with participants and status tracking
- **Participant**: Junction entity linking contacts to events with status
- **Command**: User instruction that modifies or queries application state
- **Observable List**: JavaFX data structure for automatic UI updates
- **NFR**: Non-Functional Requirement
- **UC**: Use Case
- **MoSCoW**: Method of prioritizing requirements (Must have, Should have, Could have, Won't have)

---

## **Appendix B: Manual Testing Instructions**

<box type="info" seamless>

**Note:** These instructions provide a starting point for testing; testers should perform additional exploratory testing.

</box>

### B.1 Launch and Shutdown Testing

1. **Initial Launch**

   - Download JAR file to empty folder
   - Double-click JAR file
   - Expected: GUI displays with sample contacts

2. **Window Preference Persistence**
   - Resize and move window
   - Close application
   - Relaunch application
   - Expected: Previous size and position retained

### B.2 Contact Management Testing

1. **Add Contact**

   - Command: `contact add --name John Doe --email john@example.com --phone 12345678 --address "123 Main St"`
   - Short form: `contact add -n John Doe -e john@example.com -p 12345678 -a "123 Main St"`
   - Expected: Contact added, success message displayed

2. **Delete Contact**

   - Command: `contact delete 1` (where 1 is valid index)
   - Expected: Contact removed, confirmation shown

3. **Find Contact**

   - Command: `contact find John`
   - Field-specific: `contact find --email gmail --tag friend`
   - Expected: Contacts matching criteria displayed

4. **Edit Contact**

   - Command: `contact edit 1 --phone 98765432 --email newemail@gmail.com`
   - Expected: Contact updated, confirmation shown

5. **Invalid Commands**
   - Command: `contact add` (missing required fields)
   - Expected: Error message with missing fields indicated

### B.3 Event Management Testing

1. **Create Event**

   - Command: `event add --name Team Meeting --date 15-01-2024 14:00 --address Meeting Room 1`
   - Expected: Event created, success message shown

2. **Link Contact to Event**

   - Command: `event link --event 1 --contact 1`
   - Expected: Association created, confirmation displayed

3. **Update RSVP Status**

   - Command: `event rsvp --event 1 --contact 1 --status available`
   - Expected: RSVP status updated, confirmation shown

4. **Export Event Contacts**

   - Command: `event export 1`
   - Expected: Contacts copied to clipboard with contact details

5. **Unlink Contact from Event**

   - Command: `event unlink --event 1 --contact 1`
   - Expected: Association removed, confirmation shown

6. **List Events**
   - Command: `event list`
   - Expected: All events displayed with details

### B.4 Data Persistence Testing

1. **Data Recovery**

   - Add contacts and events
   - Close application
   - Verify data files created
   - Relaunch application
   - Expected: All data preserved

2. **Corruption Handling**
   - Manually corrupt data file
   - Launch application
   - Expected: Error message with recovery instructions

### B.5 Error Handling Testing

1. **Invalid Index**

   - Command: `contact delete 999`
   - Expected: Error message indicating invalid index

2. **Duplicate Data**

   - Add contact with existing email
   - Expected: Error message about duplicate contact

3. **Invalid Formats**

   - Command: `event add --name Test --date invalid-date`
   - Expected: Specific error about date format (DD-MM-YYYY HH:mm)

4. **Missing Required Fields**

   - Command: `contact add --name "John"` (missing email)
   - Expected: Error message about missing required fields

5. **Invalid RSVP Status**

   - Command: `event rsvp --event 1 --contact 1 --status invalid`
   - Expected: Error message about invalid status value

6. **Invalid Event/Contact Combination**
   - Command: `event link --event 999 --contact 1`
   - Expected: Error message about non-existent event
