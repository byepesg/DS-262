# DS-262 — Data Structures

Official repository for the Data Structures course.

This repository contains instructor reference implementations, student implementations, exercises, and supporting material used during the semester.

## Repository Structure

```text
DS-262/
├── implementations/
│   ├── instructor/
│   └── students/
│       ├── group-02/
│       └── group-04/
├── exercises/
├── docs/
├── README.md
├── CONTRIBUTING.md
└── .gitignore
```

## Instructor Implementations

Reference implementations developed during the course are stored under:

```text
implementations/instructor/
```

These implementations are shared by both course groups.

Typical topics include:

* Linked Lists
* Stacks
* Queues
* Circular Arrays
* Dynamic Arrays
* Trees
* Heaps
* Hash Tables
* Graphs

Instructor implementations may be published after the corresponding topic or assignment has been completed.

## Student Implementations

Student contributions are separated by course group.

### Group 02

```text
implementations/students/group-02/
```

### Group 04

```text
implementations/students/group-04/
```

Each team must create its own directory.

Example:

```text
implementations/students/group-02/team-01/
implementations/students/group-02/team-02/
```

or:

```text
implementations/students/group-04/team-01/
implementations/students/group-04/team-02/
```

Inside each team folder, implementations should be organized by topic.

Example:

```text
team-03/
├── linked-lists/
├── stacks/
├── queues/
├── circular-arrays/
└── dynamic-arrays/
```

## Branch Strategy

The `main` branch represents the official state of the repository.

Students must not work directly on `main`.

Each contribution must be developed in a dedicated branch using this convention:

```text
gXX/team-YY/topic
```

Examples:

```text
g02/team-03/linked-list
g02/team-05/stack
g04/team-02/queue
g04/team-07/dynamic-array
```

Each contribution must be submitted through a Pull Request into `main`.

## Programming Language

The primary programming language for the course is Java.

Other programming languages may occasionally be used for comparison or demonstration.

## Academic Integrity

Students may collaborate within their assigned teams.

Each team is responsible for the originality of its implementations.

Students must not:

* copy code from another team;
* submit instructor implementations as their own;
* modify another team's directory;
* modify instructor code unless explicitly instructed.

## Contribution Guide

Before contributing, read:

```text
CONTRIBUTING.md
```

## Instructor

Sebastián Yepes García
Data Structures
Universidad Nacional de Colombia

