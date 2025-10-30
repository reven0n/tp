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

Refer to the guide [_Setting up and getting started_](https://github.com/AY2526S1-CS2103T-F15b-2/tp/blob/master/docs/SettingUp.md) for detailed installation and setup instructions.

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

[Enhanced Architecture Diagram placeholder]

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

The UI component consists of:
- `MainWindow`: Primary application window
- `CommandBox`: User input interface
- `ResultDisplay`: Command feedback display
- `ContactListPanel`/`EventListPanel`: Data presentation panels
- `StatusBarFooter`: Status information display

All UI components inherit from `UiPart` abstract class and use FXML for layout definitions.

#### 2.2.3 Logic Component
**API**: [`Logic.java`](https://github.com/AY2526S1-CS2103T-F15b-2/tp/blob/master/src/main/java/nusemp/logic/Logic.java)

The Logic component manages:
- **Command parsing**: Translates user input into executable commands
- **Command execution**: Executes commands against the model
- **Result formatting**: Returns formatted results to the UI

Key classes:
- `LogicManager`: Main logic coordinator
- `AppParser`: Routes commands to appropriate parsers
- `Command` hierarchy: Individual command implementations

#### 2.2.4 Model Component
**API**: [`Model.java`](https://github.com/AY2526S1-CS2103T-F15b-2/tp/blob/master/src/main/java/nusemp/model/Model.java)

The Model component maintains application state:
- **Data storage**: `UniqueContactList`, `UniqueEventList`
- **Filtered views**: Observable lists for UI binding
- **User preferences**: Application settings and preferences
- **Data relationships**: Contact-event associations

#### 2.2.5 Storage Component
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

[Component Interaction Sequence Diagram placeholder]

The components interact through well-defined interfaces:
- UI calls Logic to execute commands
- Logic updates Model state
- Model notifies UI of changes through observable lists
- Storage persists Model data on changes

---

## **3. Core Domain Model**

### 3.1 Data Model Overview

[Data Model Relationships Diagram placeholder]

The application manages three core entity types:
- **Contact**: Individuals with contact information and roles
- **Event**: Events with dates, venues, and participant relationships managed through ParticipantMap
- **Participant**: Many-to-many relationship between contacts and events

### 3.2 Contact Entity

[Contact Class Diagram placeholder]

**Core Fields:**
- `Name`: Person's name (required)
- `Email`: Email address (required, unique, case-insensitive for primary key)
- `Phone`: Phone number (optional, supports empty values)
- `Address`: Physical address (optional, supports empty values)
- `Tag`: Categorization labels (optional, multiple)
- `ContactKey`: Primary key based on lowercase email
- `invalidationToggle`: UI update mechanism (internal)

**Key Characteristics:**
- Immutable data structure with primary key system
- Field validation on construction with empty value support
- Multi-level equality: primary key equality vs. full object equality
- Invalidation mechanism for observable list updates
- Utility methods: hasPhone(), hasAddress(), hasTags(), hasSameFields()
- Located in `nusemp.model.contact` package

### 3.3 Event Entity

[Event Class Diagram placeholder]

**Core Fields:**
- `Name`: Event title (required, primary key)
- `Date`: Event date and time (required, format: DD-MM-YYYY HH:mm)
- `Address`: Event venue (optional, supports empty values)
- `EventStatus`: Current event state (STARTING, ONGOING, CLOSED)
- `Tag`: Event categories (optional, multiple)
- `EventKey`: Primary key based on event name
- `invalidationToggle`: UI update mechanism (internal)

**Key Characteristics:**
- Immutable data structure with name-based primary key
- Multi-level equality system (isSameEvent(), hasSameFields(), equals())
- Status-based filtering and management
- Invalidation mechanism for observable list updates
- Convenience constructor with default STARTING status and empty tags
- Located in `nusemp.model.event` package

### 3.4 Participant Entity

[Participant Class Diagram placeholder]

**Core Fields:**
- `Contact`: Associated contact reference
- `Event`: Associated event reference
- `ParticipantStatus`: Attendance status (UNAVAILABLE, AVAILABLE, UNKNOWN)

**Key Characteristics:**
- Located in `nusemp.model.participant` package
- Links Contact and Event entities with full context
- Default constructor with AVAILABLE status
- Multi-level equality methods for contact comparisons (equalsContact(), hasSameContact())
- Used by ParticipantMap for relationship management
- Supports status updates for RSVP functionality

### 3.5 Data Relationships

**Architecture:**
- **Contact ↔ Event**: Many-to-many relationship through ParticipantMap with dual indexing for efficient lookups
- **Contact → Tag**: One-to-many relationship
- **Event → Tag**: One-to-many relationship
- **ParticipantMap**: Central relationship manager with dual indexing

**ParticipantMap Features:**
- Dual indexing: byContact and byEvent HashMaps for efficient lookups in both directions
- Automatic relationship consistency maintenance
- Support for contact/event updates across all relationships
- Participant status management with full CRUD operations
- Located in AppData for centralized relationship management
- Participants stored as email references with status during serialization

---

## **4. Command System**

### 4.1 Command Processing Flow

[Command Processing Sequence Diagram placeholder]

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

[Parser Architecture Diagram placeholder]

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

### 4.4 Error Handling

[Error Handling Flow Diagram placeholder]

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

[Add Contact Sequence Diagram placeholder]

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

[Create Event Sequence Diagram placeholder]

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

[Link Contact to Event Sequence Diagram placeholder]

**Steps:**
1. User inputs `event link` command with contact and event identifiers
2. Logic parses command and validates contact/event existence
3. Participant object created with appropriate status
4. Model updates ParticipantMap and triggers invalidationToggle mechanism for both contact and event displays
5. Storage persists relationship changes
6. UI updates both contact and event displays
7. Success confirmation displayed

### 5.4 Search/Find Flow

[Search Find Sequence Diagram placeholder]

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

### 5.5 Error Handling Flow

[Error Handling Sequence Diagram placeholder]

**Steps:**
1. Exception occurs in any component (parsing, validation, execution, storage)
2. Exception caught and wrapped in appropriate exception type
3. Error message formatted with actionable guidance
4. CommandResult created with error information
5. UI displays error message in ResultDisplay
6. Model state remains unchanged (transactional behavior)
7. User can retry command with corrected input

### 5.6 Application Startup/Initialization Flow

[Application Startup Sequence Diagram placeholder]

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

[Edit Update Sequence Diagram placeholder]

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

[RSVP Status Update Sequence Diagram placeholder]

**Steps:**
1. User inputs `event rsvp --event EVENT_INDEX --contact CONTACT_INDEX --status STATUS` command
2. UI passes input to Logic component
3. AppParser routes to EventRsvpCommandParser using CommandType enum
4. Parser validates event and contact indices exist and status is valid (UNAVAILABLE, AVAILABLE, UNKNOWN)
5. EventRsvpCommand created with validated parameters
6. Command executes against Model to locate event and contact in their respective lists
7. Model uses ParticipantMap to find existing Participant or creates new one
8. Participant status updated to new RSVP status
9. ModelManager automatically triggers Storage save operation
10. Storage persists updated participant relationship data to JSON files
11. Observable lists update both event and contact displays
12. Success message displayed with RSVP confirmation

### 5.9 Data Persistence Flow

[Data Persistence Sequence Diagram placeholder]

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

## **6. Development Guidelines**

### 6.1 Adding New Commands

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

### 6.2 Adding New Data Fields

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

### 6.3 Testing Best Practices

#### 6.3.1 Unit Tests
- Test each public method individually
- Mock external dependencies
- Cover both success and failure scenarios
- Use descriptive test method names

#### 6.3.2 Integration Tests
- Test component interactions
- Use actual file system for storage tests
- Validate end-to-end command flows
- Test UI updates through observable lists

#### 6.3.3 Test Structure
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

### 6.4 Code Style Guidelines

- Follow [Java coding standards](https://se-education.org/guides/conventions/java/index.html)
- Use meaningful variable and method names
- Keep methods short and focused
- Write self-documenting code
- Use assertions for internal assumptions
- Handle exceptions gracefully

---

## **7. Advanced Topics**

### 7.1 Observable Pattern Implementation

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

### 7.2 Performance Considerations

#### 7.2.1 Data Scaling
- Application supports up to 10,000 contacts and 1,000 events
- Observable lists use efficient change detection
- JSON serialization optimized for large datasets
- UI virtualization for large lists (if implemented)

#### 7.2.2 Memory Management
- Immutable objects prevent memory leaks
- Weak references in event listeners
- Proper cleanup in component disposal

### 7.3 Common Debugging Scenarios

#### 7.3.1 Command Not Found
1. Check command registration in AppParser
2. Verify parser class exists and is accessible
3. Validate command word spelling

#### 7.3.2 Data Not Persisting
1. Check file permissions
2. Verify JSON serialization works
3. Check storage file paths
4. Look for corruption handling in logs

#### 7.3.3 UI Not Updating
1. Verify observable list binding
2. Check for invalidation toggle usage
3. Ensure proper list change notifications

### 7.4 Extension Points

#### 7.4.1 Custom Commands
- Extend `Command` base class
- Follow existing naming conventions
- Implement proper error handling

#### 7.4.2 New Entity Types
- Create entity classes following Contact/Event patterns
- Add to Model component interfaces
- Implement storage serialization
- Create appropriate UI components

#### 7.4.3 Storage Backends
- Implement `Storage` interface
- Handle serialization format
- Maintain existing API contracts

---

## **8. Supporting Documentation**

- [Documentation guide](https://github.com/AY2526S1-CS2103T-F15b-2/tp/blob/master/docs/Documentation.md)
- [Testing guide](https://github.com/AY2526S1-CS2103T-F15b-2/tp/blob/master/docs/Testing.md)
- [Logging guide](https://github.com/AY2526S1-CS2103T-F15b-2/tp/blob/master/docs/Logging.md)
- [Configuration guide](https://github.com/AY2526S1-CS2103T-F15b-2/tp/blob/master/docs/Configuration.md)
- [DevOps guide](https://github.com/AY2526S1-CS2103T-F15b-2/tp/blob/master/docs/DevOps.md)

---

## **Appendix A: Requirements**

### A.1 Product Scope

#### A.1.1 Target User Profile

Event organizers at NUS who manage large contact databases, send bulk emails regularly, prefer keyboard-driven CLI interfaces for efficiency, and need automated mailing recommendations for targeted communication with event participants and stakeholders.

#### A.1.2 Value Proposition

Streamlines event communication workflow by integrating contact management with intelligent mailing recommendations and bulk email capabilities in a fast, keyboard-centric CLI interface, enabling event organizers to efficiently manage contacts and execute targeted email campaigns without switching between multiple tools.

### A.2 Non-Functional Requirements

#### A.2.1 Technical Requirements
- **Java 17** runtime environment
- **Cross-platform** compatibility (Windows, macOS, Linux)
- **No installer** required - single JAR deployment
- **Offline functionality** - no network dependencies

#### A.2.2 Performance Requirements
- **Response time**: < 2-3 seconds for typical user commands
- **Startup time**: < 30 seconds on standard hardware
- **Memory usage**: < 3GB with 10,000 contacts (requires appropriate JVM configuration)
- **Storage efficiency**: < 30MB for 10,000 contacts
- **Batch operations**: May take longer for large datasets (10,000+ records)

#### A.2.3 User Experience Requirements
- **Keyboard-driven** interface for efficiency
- **Consistent command syntax** across all operations
- **Clear error messages** with actionable guidance
- **Human-readable** data files for manual recovery

### A.3 Glossary

- **Event organizer**: Target user who manages events and contacts
- **Contact**: Person entity with contact information and event associations
- **Event**: Event entity with participants and status tracking
- **Participant**: Junction entity linking contacts to events with status
- **Command**: User instruction that modifies or queries application state
- **Observable List**: JavaFX data structure for automatic UI updates

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
   - Command: `event add --name "Team Meeting" --date "15-01-2024 14:00" --address "Meeting Room 1"`
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
   - Command: `event add --name "Test" --date "invalid-date"`
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