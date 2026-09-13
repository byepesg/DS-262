# Contributing to DS-262

All student contributions must follow the workflow described below.

## 1. Clone the Repository

Clone the repository using SSH:

```bash
git clone git@github.com:byepesg/DS-262.git
```

Enter the repository:

```bash
cd DS-262
```

## 2. Start from `main`

Before starting a new contribution:

```bash
git checkout main
git pull origin main
```

This ensures that your local repository contains the most recent changes.

## 3. Create a Branch

Do not work directly on `main`.

Create a branch using:

```text
gXX/team-YY/topic
```

Where:

* `g02` corresponds to Group 02.
* `g04` corresponds to Group 04.
* `team-YY` corresponds to your assigned team.
* `topic` describes the implementation.

Examples:

```text
g02/team-03/linked-list
g02/team-03/stack
g04/team-07/queue
g04/team-07/dynamic-array
```

Example command:

```bash
git checkout -b g04/team-07/dynamic-array
```

## 4. Work Only Inside Your Team Folder

Students from Group 02 must work under:

```text
implementations/students/group-02/
```

Students from Group 04 must work under:

```text
implementations/students/group-04/
```

For example:

```text
implementations/students/group-04/team-07/
```

A recommended structure is:

```text
team-07/
├── linked-lists/
├── stacks/
├── queues/
├── circular-arrays/
└── dynamic-arrays/
```

Students must not modify:

```text
implementations/instructor/
```

unless explicitly instructed.

Students must not modify another team's directory.

## 5. Add Your Implementation

For example:

```text
implementations/students/group-04/team-07/linked-lists/
├── SinglyLinkedList.java
├── Main.java
└── README.md
```

When appropriate, the topic `README.md` should include:

* names of team members;
* data structure implemented;
* supported operations;
* time complexity;
* implementation notes.

## 6. Review Your Changes

Before committing:

```bash
git status
```

Review differences:

```bash
git diff
```

## 7. Stage Your Files

Add only your team's contribution.

Example:

```bash
git add implementations/students/group-04/team-07/
```

Avoid staging unrelated files.

## 8. Commit

Use descriptive commits.

Recommended convention:

```text
feat(gXX-teamYY): description
```

Example:

```bash
git commit -m "feat(g04-team07): implement singly linked list"
```

Other examples:

```text
feat(g02-team03): implement stack
fix(g04-team07): correct dequeue operation
docs(g02-team05): document dynamic array complexity
```

## 9. Push Your Branch

Example:

```bash
git push -u origin g04/team-07/dynamic-array
```

## 10. Create a Pull Request

Open the repository on GitHub.

Create a Pull Request from your branch into:

```text
main
```

For example:

```text
g04/team-07/dynamic-array
            ↓
       Pull Request
            ↓
           main
```

The Pull Request should include:

* course group;
* team number;
* topic implemented;
* brief description of the implementation;
* known limitations, if any.

## 11. Review Process

The instructor may:

* approve the Pull Request;
* request changes;
* leave comments;
* reject the Pull Request if repository rules are not followed.

If changes are requested, continue working on the same branch.

Then:

```bash
git add ...
git commit -m "fix(g04-team07): address review comments"
git push
```

The existing Pull Request will update automatically.

## Repository Rules

Do not:

* push directly to `main`;
* modify another team's folder;
* modify instructor implementations;
* delete unrelated files;
* commit compiled Java files;
* upload credentials, passwords, API keys, or tokens.

## Before Submitting

Always run:

```bash
git status
```

Make sure the contribution contains only the intended files before pushing.

