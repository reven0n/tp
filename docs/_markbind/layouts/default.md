<head-bottom>
  <link rel="stylesheet" href="{{baseUrl}}/stylesheets/main.css">
</head-bottom>

<header sticky>
  <navbar type="dark">
    <a slot="brand" href="{{baseUrl}}/index.html" title="Home" class="navbar-brand">NUS Event Mailer Pro</a>
    <li><a href="{{baseUrl}}/index.html" class="nav-link">Home</a></li>
    <li><a href="{{baseUrl}}/UserGuide.html" class="nav-link">User Guide</a></li>
    <li><a href="{{baseUrl}}/DeveloperGuide.html" class="nav-link">Developer Guide</a></li>
    <li><a href="{{baseUrl}}/AboutUs.html" class="nav-link">About Us</a></li>
    <li><a href="https://github.com/AY2526S1-CS2103T-F15b-2/tp" target="_blank" class="nav-link"><md>:fab-github:</md></a>
    </li>
    <li slot="right">
      <form class="navbar-form">
        <searchbar :data="searchData" placeholder="Search" :on-hit="searchCallback" menu-align-right></searchbar>
      </form>
    </li>
  </navbar>
</header>

<div id="flex-body">
  <nav id="site-nav">
    <div class="site-nav-top">
      <div class="fw-bold mb-2" style="font-size: 1.25rem;">Site Map</div>
    </div>
    <div class="nav-component slim-scroll">
      <site-nav>
* [Home]({{ baseUrl }}/index.html)
* [User Guide]({{ baseUrl }}/UserGuide.html) :expanded:
  * [1. Getting Started]({{ baseUrl }}/UserGuide.html#1-getting-started)
    * [1.1 Installation]({{ baseUrl }}/UserGuide.html#1-1-installation)
    * [1.2 Command Formats]({{ baseUrl }}/UserGuide.html#1-2-command-formats)
    * [1.3 User Interface Overview]({{ baseUrl }}/UserGuide.html#1-3-user-interface-overview)
  * [2. Managing Contacts]({{ baseUrl }}/UserGuide.html#2-managing-your-contacts)
    * [2.1 `contact add`]({{ baseUrl }}/UserGuide.html#2-1-contact-add)
    * [2.2 `contact list`]({{ baseUrl }}/UserGuide.html#2-2-contact-list)
    * [2.3 `contact edit`]({{ baseUrl }}/UserGuide.html#2-3-contact-edit)
    * [2.4 `contact find`]({{ baseUrl }}/UserGuide.html#2-4-contact-find)
    * [2.5 `contact delete`]({{ baseUrl }}/UserGuide.html#2-5-contact-delete)
    * [2.6 `contact show`]({{ baseUrl }}/UserGuide.html#2-6-contact-show)
  * [3. Managing Events]({{ baseUrl }}/UserGuide.html#3-managing-your-events)
    * [3.1 `event add`]({{ baseUrl }}/UserGuide.html#3-1-event-add)
    * [3.2 `event list`]({{ baseUrl }}/UserGuide.html#3-2-event-list)
    * [3.3 `event edit`]({{ baseUrl }}/UserGuide.html#3-3-event-edit)
    * [3.4 `event find`]({{ baseUrl }}/UserGuide.html#3-4-event-find)
    * [3.5 `event delete`]({{ baseUrl }}/UserGuide.html#3-5-event-delete)
    * [3.6 `event link`]({{ baseUrl }}/UserGuide.html#3-6-event-link)
    * [3.7 `event unlink`]({{ baseUrl }}/UserGuide.html#3-7-event-unlink)
    * [3.8 `event show`]({{ baseUrl }}/UserGuide.html#3-8-event-show)
    * [3.9 `event export`]({{ baseUrl }}/UserGuide.html#3-9-event-export)
    * [3.10 `event rsvp`]({{ baseUrl }}/UserGuide.html#3-10-event-rsvp)
  * [4. Your Data is Safe]({{ baseUrl }}/UserGuide.html#4-your-data-is-safe)
    * [4.1 Automatic Saving]({{ baseUrl }}/UserGuide.html#4-1-automatic-saving)
    * [4.2 Advanced: Editing Data Directly]({{ baseUrl }}/UserGuide.html#4-2-advanced-editing-data-directly)
  * [5. FAQ]({{ baseUrl }}/UserGuide.html#5-frequently-asked-questions)
  * [6. Known Issues]({{ baseUrl }}/UserGuide.html#6-known-issues)
  * [7. Quick Command Reference]({{ baseUrl }}/UserGuide.html#7-quick-command-reference)
    * [7.1 Basic Commands]({{ baseUrl }}/UserGuide.html#7-1-basic-commands)
    * [7.2 Contact Commands]({{ baseUrl }}/UserGuide.html#7-2-contact-commands)
    * [7.3 Event Commands]({{ baseUrl }}/UserGuide.html#7-3-event-commands)
* [Developer Guide]({{ baseUrl }}/DeveloperGuide.html) :expanded:
  * [1. Getting Started]({{ baseUrl }}/DeveloperGuide.html#1-getting-started)
    * [1.1 Project Overview]({{ baseUrl }}/DeveloperGuide.html#1-1-project-overview)
    * [1.2 Quick Setup]({{ baseUrl }}/DeveloperGuide.html#1-2-quick-setup)
    * [1.3 Development Workflow]({{ baseUrl }}/DeveloperGuide.html#1-3-development-workflow)
  * [2. System Architecture]({{ baseUrl }}/DeveloperGuide.html#2-system-architecture)
    * [2.1 High-Level Architecture]({{ baseUrl }}/DeveloperGuide.html#2-1-high-level-architecture)
    * [2.2 Component Overview]({{ baseUrl }}/DeveloperGuide.html#2-2-component-overview)
    * [2.3 Component Interactions]({{ baseUrl }}/DeveloperGuide.html#2-3-component-interactions)
  * [3. Core Domain Model]({{ baseUrl }}/DeveloperGuide.html#3-core-domain-model)
    * [3.1 Data Model Overview]({{ baseUrl }}/DeveloperGuide.html#3-1-data-model-overview)
    * [3.2 Contact and Event]({{ baseUrl }}/DeveloperGuide.html#3-2-contact-and-event)
    * [3.3 Participant Handling]({{ baseUrl }}/DeveloperGuide.html#3-3-participant-handling)
  * [4. Command System]({{ baseUrl }}/DeveloperGuide.html#4-command-system)
    * [4.1 Command Processing Flow]({{ baseUrl }}/DeveloperGuide.html#4-1-command-processing-flow)
    * [4.2 Command Categories]({{ baseUrl }}/DeveloperGuide.html#4-2-command-categories)
    * [4.3 Parser Architecture]({{ baseUrl }}/DeveloperGuide.html#4-3-parser-architecture)
    * [4.4 Error Handling Flow]({{ baseUrl }}/DeveloperGuide.html#4-4-error-handling-flow)
  * [5. Component Interactions]({{ baseUrl }}/DeveloperGuide.html#5-component-interactions)
    * [5.1 Add Contact Flow]({{ baseUrl }}/DeveloperGuide.html#5-1-add-contact-flow)
    * [5.2 Create Event Flow]({{ baseUrl }}/DeveloperGuide.html#5-2-create-event-flow)
    * [5.3 Link Contact to Event Flow]({{ baseUrl }}/DeveloperGuide.html#5-3-link-contact-to-event-flow)
    * [5.4 Search/Find Flow]({{ baseUrl }}/DeveloperGuide.html#5-4-search-find-flow)
    * [5.5 Error Handling Sequence]({{ baseUrl }}/DeveloperGuide.html#5-5-error-handling-sequence)
    * [5.6 Application Startup/Initialization Flow]({{ baseUrl }}/DeveloperGuide.html#5-6-application-startup-initialization-flow)
    * [5.7 Edit/Update Flow]({{ baseUrl }}/DeveloperGuide.html#5-7-edit-update-flow)
    * [5.8 RSVP Status Update Flow]({{ baseUrl }}/DeveloperGuide.html#5-8-rsvp-status-update-flow)
    * [5.9 Data Persistence Flow]({{ baseUrl }}/DeveloperGuide.html#5-9-data-persistence-flow)
  * [6. Advanced Topics]({{ baseUrl }}/DeveloperGuide.html#6-advanced-topics)
    * [6.1 Observable Pattern Implementation]({{ baseUrl }}/DeveloperGuide.html#6-1-observable-pattern-implementation)
    * [6.2 Performance Considerations]({{ baseUrl }}/DeveloperGuide.html#6-2-performance-considerations)
    * [6.3 Common Debugging Scenarios]({{ baseUrl }}/DeveloperGuide.html#6-3-common-debugging-scenarios)
    * [6.4 Extension Points]({{ baseUrl }}/DeveloperGuide.html#6-4-extension-points)
  * [7. Development Guidelines]({{ baseUrl }}/DeveloperGuide.html#7-development-guidelines)
    * [7.1 Adding New Commands]({{ baseUrl }}/DeveloperGuide.html#7-1-adding-new-commands)
    * [7.2 Adding New Data Fields]({{ baseUrl }}/DeveloperGuide.html#7-2-adding-new-data-fields)
    * [7.3 Testing Best Practices]({{ baseUrl }}/DeveloperGuide.html#7-3-testing-best-practices)
    * [7.4 Code Style Guidelines]({{ baseUrl }}/DeveloperGuide.html#7-4-code-style-guidelines)
  * [8. Supporting Documentation]({{ baseUrl }}/DeveloperGuide.html#8-supporting-documentation)
  * [Appendix A: Requirements]({{ baseUrl }}/DeveloperGuide.html#appendix-a-requirements)
    * [A.1 Product Scope]({{ baseUrl }}/DeveloperGuide.html#a-1-product-scope)
    * [A.2 User Stories]({{ baseUrl }}/DeveloperGuide.html#a-2-user-stories)
    * [A.3 Use Cases]({{ baseUrl }}/DeveloperGuide.html#a-3-use-cases)
    * [A.4 Non-Functional Requirements]({{ baseUrl }}/DeveloperGuide.html#a-4-non-functional-requirements)
    * [A.5 Requirements Priority Matrix]({{ baseUrl }}/DeveloperGuide.html#a-5-requirements-priority-matrix)
    * [A.6 Glossary]({{ baseUrl }}/DeveloperGuide.html#a-6-glossary)
  * [Appendix B: Manual Testing Instructions]({{ baseUrl }}/DeveloperGuide.html#appendix-b-manual-testing-instructions)
    * [B.1 Launch and Shutdown Testing]({{ baseUrl }}/DeveloperGuide.html#b-1-launch-and-shutdown-testing)
    * [B.2 Contact Management Testing]({{ baseUrl }}/DeveloperGuide.html#b-2-contact-management-testing)
    * [B.3 Event Management Testing]({{ baseUrl }}/DeveloperGuide.html#b-3-event-management-testing)
    * [B.4 Data Persistence Testing]({{ baseUrl }}/DeveloperGuide.html#b-4-data-persistence-testing)
    * [B.5 Error Handling Testing]({{ baseUrl }}/DeveloperGuide.html#b-5-error-handling-testing)
* [About Us]({{ baseUrl }}/AboutUs.html)
      </site-nav>
    </div>
  </nav>
  <div id="content-wrapper">
    {{ content }}
  </div>
  <nav id="page-nav">
    <div class="nav-component slim-scroll">
      <page-nav />
    </div>
  </nav>
  <scroll-top-button></scroll-top-button>
</div>

<footer>
  <!-- Support MarkBind by including a link to us on your landing page! -->
  <div class="text-center">
    <small>[<md>**Powered by**</md> <img src="https://markbind.org/favicon.ico" width="30"> {{MarkBind}}, generated on {{timestamp}}]</small>
  </div>
</footer>
