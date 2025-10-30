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
  * [Getting Started]({{ baseUrl }}/UserGuide.html#getting-started)
    * [Installation]({{ baseUrl }}/UserGuide.html#installation)
    * [Command Formats]({{ baseUrl }}/UserGuide.html#command-formats)
    * [User Interface Overview]({{ baseUrl }}/UserGuide.html#user-interface-overview)
  * [Managing Contacts]({{ baseUrl }}/UserGuide.html#managing-your-contacts)
    * [`contact add`]({{ baseUrl }}/UserGuide.html#contact-add)
    * [`contact list`]({{ baseUrl }}/UserGuide.html#contact-list)
    * [`contact edit`]({{ baseUrl }}/UserGuide.html#contact-edit)
    * [`contact find`]({{ baseUrl }}/UserGuide.html#contact-find)
    * [`contact delete`]({{ baseUrl }}/UserGuide.html#contact-delete)
    * [`contact show`]({{ baseUrl }}/UserGuide.html#contact-show)
  * [Managing Events]({{ baseUrl }}/UserGuide.html#managing-your-events)
    * [`event add`]({{ baseUrl }}/UserGuide.html#event-add)
    * [`event list`]({{ baseUrl }}/UserGuide.html#event-list)
    * [`event edit`]({{ baseUrl }}/UserGuide.html#event-edit)
    * [`event delete`]({{ baseUrl }}/UserGuide.html#event-delete)
    * [`event link`]({{ baseUrl }}/UserGuide.html#event-link)
    * [`event unlink`]({{ baseUrl }}/UserGuide.html#event-unlink)
    * [`event show`]({{ baseUrl }}/UserGuide.html#event-show)
    * [`event export`]({{ baseUrl }}/UserGuide.html#event-export)
    * [`event rsvp`]({{ baseUrl }}/UserGuide.html#event-rsvp)
  * [Your Data is Safe]({{ baseUrl }}/UserGuide.html#your-data-is-safe)
  * [FAQ]({{ baseUrl }}/UserGuide.html#frequently-asked-questions)
  * [Known Issues]({{ baseUrl }}/UserGuide.html#known-issues)
  * [Quick Command Reference]({{ baseUrl }}/UserGuide.html#quick-command-reference)
* [Developer Guide]({{ baseUrl }}/DeveloperGuide.html) :expanded:
  * [1. Getting Started]({{ baseUrl }}/DeveloperGuide.html#1-getting-started) :expanded:
    * [1.1 Project Overview]({{ baseUrl }}/DeveloperGuide.html#1-1-project-overview)
    * [1.2 Quick Setup]({{ baseUrl }}/DeveloperGuide.html#1-2-quick-setup)
    * [1.3 Development Workflow]({{ baseUrl }}/DeveloperGuide.html#1-3-development-workflow)
  * [2. System Architecture]({{ baseUrl }}/DeveloperGuide.html#2-system-architecture) :expanded:
    * [2.1 High-Level Architecture]({{ baseUrl }}/DeveloperGuide.html#2-1-high-level-architecture)
    * [2.2 Component Overview]({{ baseUrl }}/DeveloperGuide.html#2-2-component-overview)
    * [2.3 Component Interactions]({{ baseUrl }}/DeveloperGuide.html#2-3-component-interactions)
  * [3. Core Domain Model]({{ baseUrl }}/DeveloperGuide.html#3-core-domain-model) :expanded:
    * [3.1 Data Model Overview]({{ baseUrl }}/DeveloperGuide.html#3-1-data-model-overview)
    * [3.2 Contact Entity]({{ baseUrl }}/DeveloperGuide.html#3-2-contact-entity)
    * [3.3 Event Entity]({{ baseUrl }}/DeveloperGuide.html#3-3-event-entity)
    * [3.4 Participant Entity]({{ baseUrl }}/DeveloperGuide.html#3-4-participant-entity)
    * [3.5 Data Relationships]({{ baseUrl }}/DeveloperGuide.html#3-5-data-relationships)
  * [4. Command System]({{ baseUrl }}/DeveloperGuide.html#4-command-system) :expanded:
    * [4.1 Command Processing Flow]({{ baseUrl }}/DeveloperGuide.html#4-1-command-processing-flow)
    * [4.2 Command Categories]({{ baseUrl }}/DeveloperGuide.html#4-2-command-categories)
    * [4.3 Parser Architecture]({{ baseUrl }}/DeveloperGuide.html#4-3-parser-architecture)
    * [4.4 Error Handling]({{ baseUrl }}/DeveloperGuide.html#4-4-error-handling)
  * [5. Component Interactions]({{ baseUrl }}/DeveloperGuide.html#5-component-interactions) :expanded:
    * [5.1 Add Contact Flow]({{ baseUrl }}/DeveloperGuide.html#5-1-add-contact-flow)
    * [5.2 Create Event Flow]({{ baseUrl }}/DeveloperGuide.html#5-2-create-event-flow)
    * [5.3 Link Contact to Event Flow]({{ baseUrl }}/DeveloperGuide.html#5-3-link-contact-to-event-flow)
    * [5.4 Search/Find Flow]({{ baseUrl }}/DeveloperGuide.html#5-4-searchfind-flow)
    * [5.5 Error Handling Flow]({{ baseUrl }}/DeveloperGuide.html#5-5-error-handling-flow)
    * [5.6 Application Startup Flow]({{ baseUrl }}/DeveloperGuide.html#5-6-application-startupinitialization-flow)
    * [5.7 Edit/Update Flow]({{ baseUrl }}/DeveloperGuide.html#5-7-editupdate-flow)
    * [5.8 RSVP Status Update Flow]({{ baseUrl }}/DeveloperGuide.html#5-8-rsvp-status-update-flow)
    * [5.9 Data Persistence Flow]({{ baseUrl }}/DeveloperGuide.html#5-9-data-persistence-flow)
  * [6. Development Guidelines]({{ baseUrl }}/DeveloperGuide.html#6-development-guidelines) :expanded:
    * [6.1 Adding New Commands]({{ baseUrl }}/DeveloperGuide.html#6-1-adding-new-commands)
    * [6.2 Adding New Data Fields]({{ baseUrl }}/DeveloperGuide.html#6-2-adding-new-data-fields)
    * [6.3 Testing Best Practices]({{ baseUrl }}/DeveloperGuide.html#6-3-testing-best-practices)
    * [6.4 Code Style Guidelines]({{ baseUrl }}/DeveloperGuide.html#6-4-code-style-guidelines)
  * [7. Advanced Topics]({{ baseUrl }}/DeveloperGuide.html#7-advanced-topics) :expanded:
    * [7.1 Observable Pattern Implementation]({{ baseUrl }}/DeveloperGuide.html#7-1-observable-pattern-implementation)
    * [7.2 Performance Considerations]({{ baseUrl }}/DeveloperGuide.html#7-2-performance-considerations)
    * [7.3 Common Debugging Scenarios]({{ baseUrl }}/DeveloperGuide.html#7-3-common-debugging-scenarios)
    * [7.4 Extension Points]({{ baseUrl }}/DeveloperGuide.html#7-4-extension-points)
  * [8. Supporting Documentation]({{ baseUrl }}/DeveloperGuide.html#8-supporting-documentation)
  * [Appendix A: Requirements]({{ baseUrl }}/DeveloperGuide.html#appendix-a-requirements) :expanded:
    * [A.1 Product Scope]({{ baseUrl }}/DeveloperGuide.html#a-1-product-scope)
    * [A.2 Non-Functional Requirements]({{ baseUrl }}/DeveloperGuide.html#a-2-non-functional-requirements)
    * [A.3 Glossary]({{ baseUrl }}/DeveloperGuide.html#a-3-glossary)
  * [Appendix B: Manual Testing Instructions]({{ baseUrl }}/DeveloperGuide.html#appendix-b-manual-testing-instructions) :expanded:
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
