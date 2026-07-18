# Loan Items Tracker

A command-line tool for managing loans.

## Features

- An interactive command-line interface.
- Saves results in JSON.
- Let's you add, remove, and list all loan items.
- Shows upcoming and overdue loan items.

## Prerequisites

- JDK 21

## Compile

Run this command to compile.

```sh
javac -d bin src/ca/richasf/**/*.java
```

## Run

Run this command to run.

```sh
java -cp lib/*:bin ca.richasf.loanitemstracker.LoanItemsTracker
```

Run this command to create Javadoc.

```sh
javadoc -d ./docs -cp ./lib/gson-2.13.2.jar:./lib/LGoodDatePicker-11.2.1.jar -sourcepath ./src -subpackages ca.richasf.loanitemstracker
```