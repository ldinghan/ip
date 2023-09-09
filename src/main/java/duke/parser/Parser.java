package duke.parser;

import duke.commands.*;
import duke.exceptions.*;
import duke.tasks.*;
import duke.storage.Storage;
import duke.ui.Ui;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

/**
 * The duke.parser.Parser class is responsible for parsing user input and
 * converting it into meaningful commands and tasks.
 */
public class Parser {

    /**
     * Parses an input from user to get duke.commands.Command.
     *
     * @param str The user input string.
     * @return The parsed command.
     */
    public static Command getCommand(String str, Storage storage, TaskList taskList, Ui ui) {
        String command_word = str.split(" ")[0];
        switch (command_word) {
        case "list":
            return new ListCommand();
        case "mark":
            return new MarkCommand(str);
        case "unmark":
            return new UnmarkCommand(str);
        case "todo":
            return new ToDoCommand(str);
        case "deadline":
            return new DeadlineCommand(str);
        case "event":
            return new EventCommand(str);
        case "delete":
            return new DeleteCommand(str);
        case "find":
            return new FindCommand(str);
        case "bye":
            return new ByeCommand();
        default:
            return new InvalidCommand();
        }
    }

    /**
     * Extracts the task index from a user input string and marks the task as done or undone.
     *
     * @param str    The user input string.
     * @param tasks  The list of tasks to operate on.
     * @return The index of the task that was marked.
     * @throws InvalidTaskIndexException   If the task index is invalid.
     * @throws MissingTaskIndexException  If the task index is missing.
     */
    public static int taskToMark(String str, TaskList tasks)
            throws InvalidTaskIndexException, MissingTaskIndexException {
        if (str.split(" ").length == 2) {
            int taskIndex = Integer.parseInt(str.split(" ")[1]) - 1;
            if (taskIndex + 1 > tasks.getSize() || taskIndex < 0) {
                throw new InvalidTaskIndexException("Invalid Task Index.");
            }
            tasks.markTaskAsDone(taskIndex);
            return taskIndex;
        } else {
            throw new MissingTaskIndexException("Task Index Missing.");
        }
    }

    /**
     * Parses a user input string to get the index of a task to unmark.
     *
     * @param str    The user input string.
     * @param tasks  The list of tasks.
     * @return The index of the task that was marked as undone.
     * @throws InvalidTaskIndexException   If the task index is invalid.
     * @throws MissingTaskIndexException  If the task index is missing.
     */
    public static int taskToUnmark(String str, TaskList tasks)
            throws InvalidTaskIndexException, MissingTaskIndexException {
        if (str.split(" ").length == 2) {
            int taskIndex = Integer.parseInt(str.split(" ")[1]) - 1;
            if (taskIndex + 1 > tasks.getSize() || taskIndex < 0) {
                throw new InvalidTaskIndexException("Invalid Task Index.");
            }
            tasks.markTaskAsUndone(taskIndex);
            return taskIndex;
        } else {
            throw new MissingTaskIndexException("Task Index Missing.");
        }
    }

    /**
     * Extracts the keyword from user input and finds tasks with the specified keyword.
     *
     * @param str The user input string.
     * @param tasks The list of tasks to operate on.
     * @return A TaskList of tasks containing the keyword.
     * @throws InvalidKeywordException If the keyword is missing, or if there is more than 1 keyword.
     */
    public static TaskList findKeyword(String str, TaskList tasks) throws InvalidKeywordException {
        if (str.split(" ").length == 2) {
            String keyword = str.split(" ")[1];
            return tasks.findTask(keyword);
        } else {
            throw new InvalidKeywordException("Keyword given is not a single word.");
        }
    }

    /**
     * Extracts the task index from a user input string and deletes the task.
     *
     * @param str    The user input string.
     * @param tasks  The list of tasks to operate on.
     * @return The task that was deleted.
     * @throws InvalidTaskIndexException   If the task index is invalid.
     * @throws MissingTaskIndexException  If the task index is missing.
     */
    public static Task taskToDelete(String str, TaskList tasks)
            throws InvalidTaskIndexException, MissingTaskIndexException{
        if (str.split(" ").length == 2) {
            int taskIndex = Integer.parseInt(str.split(" ")[1]) - 1;
            if (taskIndex + 1 > tasks.getSize() || taskIndex < 0) {
                throw new InvalidTaskIndexException("Invalid Task Index.");
            }
            Task toRemove = tasks.getTask(taskIndex);
            tasks.deleteTask(taskIndex);
            return toRemove;

        } else {
            throw new MissingTaskIndexException("Task Index Missing.");
        }

    }

    /**
     * Parses a user input string into a task.
     *
     * @param str The user input string.
     * @return The parsed task.
     * @throws InvalidDescriptionException If the task description is invalid.
     * @throws InvalidDateTimeException    If the task date and time are invalid.
     */
    public static Task parseStringToTask(String str, String commandWord)
            throws InvalidDescriptionException, InvalidDateTimeException {
        switch(commandWord) {
        case "todo":
            if (str.split(" ").length > 1) {
                ToDo todo = new ToDo(str.split(" ")[1]);
                return todo;
            } else {
                throw new InvalidDescriptionException("Invalid description.");
            }
        case "deadline":
            if (str.split(" ").length > 3) {
                String fullTaskDescription = str.split(" ", 2)[1];
                String description = fullTaskDescription.split(" /by ")[0];
                String by = fullTaskDescription.split(" /by ")[1];
                String[] datetime = by.split(" ");
                try {
                    LocalDate date = LocalDate.parse(datetime[0]);
                    LocalTime time = LocalTime.parse(datetime[1]);
                    Deadline deadline = new Deadline(description, date, time);
                    return deadline;
                } catch (DateTimeParseException e) {
                    throw new InvalidDateTimeException("Invalid Datetime.");
                }
            } else {
                throw new InvalidDescriptionException("Invalid description.");
            }
        case "event":
            if (str.split(" ").length > 4) {
                String fullTaskDescription = str.split(" ", 2)[1];
                String description = fullTaskDescription.split(" /from ")[0];
                String from = String.join("", fullTaskDescription.split(" /from ")[1]).split(" /to ")[0];
                String to = fullTaskDescription.split(" /to ")[1];
                try {
                    String[] fromDatetime = from.split(" ");
                    String[] toDatetime = to.split(" ");
                    LocalDate fromDate = LocalDate.parse(fromDatetime[0]);
                    LocalTime fromTime = LocalTime.parse(fromDatetime[1]);
                    LocalDate toDate = LocalDate.parse(toDatetime[0]);
                    LocalTime toTime = LocalTime.parse(toDatetime[1]);
                    Event event = new Event(description, fromDate, fromTime, toDate, toTime);
                    return event;
                } catch (DateTimeParseException e) {
                    throw new InvalidDateTimeException("Invalid Datetime");
                }
            } else {
                throw new InvalidDescriptionException("Invalid description.");
            }
        default:
            return null;
        }
    }
}
