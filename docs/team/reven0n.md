---
  layout: default.md
  title: "Darien's Project Portfolio Page"
---

### Project: NUS Event Mailer Pro

NUS Event Mailer Pro (NUS EMP) is a desktop application used to manage events and contacts for NUS event organizers. The user interacts with it using a CLI, and it has a GUI created with JavaFX. It is written in Java, and has about 10 kLoC.

Given below are my contributions to the project.

- **New Features**
    - Added Commands `event add`, `event delete` and `event list` for `Event` and it's relevant parsers [\#49](https://github.com/AY2526S1-CS2103T-F15b-2/tp/pull/49), [\#68](https://github.com/AY2526S1-CS2103T-F15b-2/tp/pull/68), [\#106](https://github.com/AY2526S1-CS2103T-F15b-2/tp/pull/106)
    - Implemented initial bidirectional link feature between `Contact` and `Event` and the commands `event link` and `event unlink` [\#60](https://github.com/AY2526S1-CS2103T-F15b-2/tp/pull/60), [\#105](https://github.com/AY2526S1-CS2103T-F15b-2/tp/pull/105), [\#114](https://github.com/AY2526S1-CS2103T-F15b-2/tp/pull/114)
    - Added `Tags` for `Events` which work similarly to `Tags` in `Contact` [\#111](https://github.com/AY2526S1-CS2103T-F15b-2/tp/pull/111)

- **Code contributed**: [RepoSense link](https://nus-cs2103-ay2526s1.github.io/tp-dashboard/?search=f15b-2&sort=totalCommits&sortWithin=title&timeframe=commit&mergegroup=&groupSelect=groupByRepos&breakdown=true&checkedFileTypes=docs~functional-code~test-code~other&since=2025-09-19T00%3A00%3A00&filteredFileName=&tabOpen=true&tabType=authorship&tabAuthor=reven0n&tabRepo=AY2526S1-CS2103T-F15b-2%2Ftp%5Bmaster%5D&authorshipIsMergeGroup=false&authorshipFileTypes=docs~functional-code~test-code&authorshipIsBinaryFileTypeChecked=false&authorshipIsIgnoredFilesChecked=false)

- **Project management**:

    - Managed weekly meetings and took minutes throughout
    - Update status board on notion

- **Enhancements to existing features**:

    - Wrote test cases for newly added features [\#171](https://github.com/AY2526S1-CS2103T-F15b-2/tp/pull/171)
    - Re-implemented `Event` Commands with new `ParticipantMap` association class [\#187](https://github.com/AY2526S1-CS2103T-F15b-2/tp/pull/187), [\#196](https://github.com/AY2526S1-CS2103T-F15b-2/tp/pull/196), [\#304](https://github.com/AY2526S1-CS2103T-F15b-2/tp/pull/304)
    - Fixed bugs found in PE-D testing related to `Event` features [\#304](https://github.com/AY2526S1-CS2103T-F15b-2/tp/pull/304)

- **Documentation**:

    - User Guide:
        - Added documentation for the features `event add`, `event delete`, `event link`, `event unlink` and `event list` [\#72](https://github.com/AY2526S1-CS2103T-F15b-2/tp/pull/228/files)
        - Fixed various bugs mentioned in PE-D feedback [\#309](https://github.com/AY2526S1-CS2103T-F15b-2/tp/pull/309)
    - Developer Guide:
        - Updated UML class diagrams for `Logic` (Pull request [\#146](https://github.com/AY2526S1-CS2103T-F15b-2/tp/pull/146))
        - Added sequence diagrams for `ContactAddCommand`, `EventEditCommand` and `EventLinkCommand` [\#221](https://github.com/AY2526S1-CS2103T-F15b-2/tp/pull/221)
        - Fixed various bugs mentioned in PE-D feedback [\#309](https://github.com/AY2526S1-CS2103T-F15b-2/tp/pull/309)
    - Team Pages:
        - Added personal about page [\#21](https://github.com/AY2526S1-CS2103T-F15b-2/tp/pull/24)
        - Added profile photo to documentation [\#13](https://github.com/AY2526S1-CS2103T-F15b-2/tp/pull/13)

- **Community**:

    - PRs reviewed (with non-trivial review comments): [\#57](https://github.com/AY2526S1-CS2103T-F15b-2/tp/pull/57), [\#62](https://github.com/AY2526S1-CS2103T-F15b-2/tp/pull/62), [\#75](https://github.com/AY2526S1-CS2103T-F15b-2/tp/pull/75), [\#78](https://github.com/AY2526S1-CS2103T-F15b-2/tp/pull/78), [\#110](https://github.com/AY2526S1-CS2103T-F15b-2/tp/pull/110), [\#158](https://github.com/AY2526S1-CS2103T-F15b-2/tp/pull/158), [\#169](https://github.com/AY2526S1-CS2103T-F15b-2/tp/pull/169), [\#191](https://github.com/AY2526S1-CS2103T-F15b-2/tp/pull/191), [\#218](https://github.com/AY2526S1-CS2103T-F15b-2/tp/pull/218)
    - Helped smoke test IP [\#1](https://github.com/nus-cs2103-AY2526S1/forum/issues/190)

- **Tools**:

    - Utilized SourceTree extensively for repository management and PR operations
    - Integrated Claude Code AI assistant for documentation and code review workflows

