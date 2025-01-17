package duke;

import duke.exceptions.FormatException;
import duke.exceptions.InvalidCommandException;
import duke.exceptions.NoDescriptionException;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

/**
 * The command class handles different types of commands.
 * Including add todo, add deadline, add event, mark, unmark, and find task.
 */
public class Command {
    /**
     * The function will check for the command entered by the user and chexk whether it is valid.
     * It will then call the corresponding methods to execute the command.
     *
     * @param tasks The list containing the user's tasks.
     * @param userInput The command entered by the user.
     * @throws InvalidCommandException If the command entered is not found, the exception will be thrown.
     */
    public static void execute(ArrayList<Task> tasks, String userInput) throws InvalidCommandException {
        final String[] commandTypeAndParams = new Parser().parseCommand(userInput);
        final String commandType = commandTypeAndParams[0];
        final String commandArgs = commandTypeAndParams[1];
        switch (commandType) {
        case "list":
            Ui.printListOfTasks(tasks);
            break;
        case "todo":
            try {
                addTodo(tasks, commandArgs);
                Ui.showAddTaskMessage(tasks);
            } catch (NoDescriptionException e) {
                System.out.println("WOOFS!!! The description of a todo cannot be empty.");
                System.out.println("Please try to add todo again υ´• ﻌ •`υ");
                Ui.printLine();
            }
            break;
        case "deadline":
            try {
                addDeadline(tasks, commandArgs);
                Ui.showAddTaskMessage(tasks);
            } catch (NoDescriptionException e) {
                System.out.println("WOOFS!!! The description of a deadline cannot be empty.");
                System.out.println("Please try to add deadline again υ´• ﻌ •`υ");
                Ui.printLine();
            } catch (FormatException | ParseException e) {
                System.out.println("WOOFS!!! The format of entering deadline is incorrect.");
                System.out.println("Please try to add deadline again υ´• ﻌ •`υ");
                Ui.printLine();
            }
            break;
        case "event":
            try {
                addEvent(tasks, commandArgs);
                Ui.showAddTaskMessage(tasks);
            } catch (NoDescriptionException e) {
                System.out.println("WOOFS!!! The description of a event cannot be empty.");
                System.out.println("Please try to add event again υ´• ﻌ •`υ");
                Ui.printLine();
            } catch (FormatException | ParseException e) {
                System.out.println("WOOFS!!! The format of entering event is incorrect.");
                System.out.println("Please try to add event again υ´• ﻌ •`υ");
                Ui.printLine();
            }
            break;
        case "mark":
            try {
                markTask(tasks, commandArgs);
            } catch (NoDescriptionException e) {
                System.out.println("WOOFS!!! The index of entering task must be stated.");
                System.out.println("Please try to mark task again υ´• ﻌ •`υ");
                Ui.printLine();
            } catch (IndexOutOfBoundsException | FormatException e) {
                System.out.println("WOOFS!!! The index of entering task is not valid.");
                System.out.println("Please try to mark task again υ´• ﻌ •`υ");
                Ui.printLine();
            }
            break;
        case "unmark":
            try {
                unmarkTask(tasks, commandArgs);
            } catch (NoDescriptionException e) {
                System.out.println("WOOFS!!! The index of entering task must be stated.");
                System.out.println("Please try to mark task again υ´• ﻌ •`υ");
                Ui.printLine();
            } catch (IndexOutOfBoundsException | FormatException e) {
                System.out.println("WOOFS!!! The index of entering task is not valid.");
                System.out.println("Please try to mark task again υ´• ﻌ •`υ");
                Ui.printLine();
            }
            break;
        case "delete":
            try {
                TaskList.deleteTask(tasks, commandArgs);
            } catch (NoDescriptionException e) {
                System.out.println("WOOFS!!! The index of entering task must be stated.");
                System.out.println("Please try to delete task again υ´• ﻌ •`υ");
                Ui.printLine();
            } catch (IndexOutOfBoundsException | FormatException e) {
                System.out.println("WOOFS!!! The index of entering task is not valid.");
                System.out.println("Please try to delete task again υ´• ﻌ •`υ");
                Ui.printLine();
            }
            break;
        case "help":
            Ui.showHelpMessage();
            break;
        case "find":
            find(tasks, commandArgs);
            break;
        default:
            throw new InvalidCommandException();
        }
    }

    /**
     * Add a new todo to the tasks list.
     *
     * @param tasks The list containing the user's tasks
     * @param commandArgs The task description entered by the user
     * @throws NoDescriptionException If the description is empty, the exception will be thrown.
     */
    public static void addTodo(ArrayList<Task> tasks, String commandArgs) throws NoDescriptionException {
        final String taskDescription = commandArgs.trim();
        if (taskDescription.length() == 0) {
            throw new NoDescriptionException();
        }
        TaskList.addTask(tasks, new Todo(commandArgs));
    }

    /**
     * Add a new event item to the task list.
     *
     * @param tasks The list containing the user's tasks.
     * @param commandArgs The event description entered by the user.
     * @throws NoDescriptionException If the description is empty, the exception will be thrown.
     * @throws FormatException If the format of entering event description is wrong, the exception will be thrown.
     * @throws ParseException If the event date couldn't be parse into date type, the exceptino will be thrown.
     */
    public static void addEvent(ArrayList<Task> tasks, String commandArgs)
            throws NoDescriptionException, FormatException, ParseException {
        final int indexOfFrom = commandArgs.indexOf("from:");
        final int indexOfTo = commandArgs.indexOf("to:");
        if (indexOfTo == -1 || indexOfFrom == -1) {
            throw new FormatException();
        }
        String eventDescription = commandArgs.substring(0, indexOfFrom).trim();
        String from = commandArgs.substring(indexOfFrom, indexOfTo).trim().replace("from:", "").trim();
        String to = commandArgs.substring(indexOfTo).trim().replace("to:", "").trim();
        if (eventDescription.trim().length() == 0 || from.length() == 0 || to.length() == 0) {
            throw new NoDescriptionException();
        }
        Date formattedFrom = Parser.parseDate(from);
        Date formattedTo = Parser.parseDate(to);
        if (formattedTo.before(formattedFrom)) {
            throw new FormatException();
        }
        TaskList.addTask(tasks, new Event(eventDescription, formattedFrom, formattedTo));
    }

    /**
     * Add a new deadline item to the user's task list.
     *
     * @param tasks The list containing the user's tasks.
     * @param commandArgs The deadline description entered by the user.
     * @throws NoDescriptionException If the description is empty, the exception will be thrown.
     * @throws FormatException If format of entering deadline description is wrong, the exception will be thrown.
     * @throws ParseException If the event date couldn't be parse into date type, the exception will be thrown.
     */
    public static void addDeadline(ArrayList<Task> tasks, String commandArgs)
            throws NoDescriptionException, FormatException, ParseException {
        final int indexOfDeadline = commandArgs.indexOf("by:");
        if (indexOfDeadline == -1) {
            throw new FormatException();
        }
        String deadlineDescription = commandArgs.substring(0, indexOfDeadline).trim();
        String deadline = commandArgs.substring(indexOfDeadline).replace("by:", "").trim();
        if (deadlineDescription.trim().length() == 0) {
            throw new NoDescriptionException();
        }
        if (deadline.trim().length() == 0) {
            throw new NoDescriptionException();
        }
        Date formattedDeadline = Parser.parseDate(deadline);
        TaskList.addTask(tasks, new Deadline(deadlineDescription, formattedDeadline));
    }

    /**
     * The function will go through all the tasks to find
     * and print out the task description containing keyword.
     *
     * @param tasks The list containing the user's tasks.
     * @param keyword The keyword that the user want to search for.
     */
    public static void find(ArrayList<Task> tasks, String keyword) {
        System.out.println("Below are the tasks that contains " + keyword + ": ");
        for (int i = 0; i < tasks.size(); i += 1) {
            if (tasks.get(i).description.contains(keyword)) {
                System.out.print(i+1);
                System.out.print(". ");
                System.out.println(tasks.get(i).toString());
            }
        }
        Ui.printLine();
    }

    /**
     * Unmark the task index entered by the user.
     * If the task have not been marked, it will prompt the user about it.
     *
     * @param tasks The list containing the user's task.
     * @param commandArgs The index description entered by the user.
     * @throws NoDescriptionException If the index description is empty, the exception will be thrown
     * @throws IndexOutOfBoundsException If the index is not within the size of the list, the exception will be thrown.
     * @throws FormatException If the index is not an integer, the exception will be thrown.
     */
    public static void unmarkTask(ArrayList<Task> tasks, String commandArgs)
            throws NoDescriptionException, IndexOutOfBoundsException, FormatException {
        if (commandArgs.trim().length() == 0) {
            throw new NoDescriptionException();
        }
        final int unmarkId = Parser.parseIndex(commandArgs) - 1;
        if (unmarkId < 0 || unmarkId >= tasks.size()) {
            throw new IndexOutOfBoundsException();
        }
        if (!tasks.get(unmarkId).isDone) {
            System.out.println("This task hasn't been marked as done yet ∪･ω･∪");
        } else {
            tasks.get(unmarkId).markAsNotDone();
            System.out.println("I've unmarked this task ∪･ω･∪:");
            System.out.println(tasks.get(unmarkId));
        }
        Ui.printLine();
    }

    /**
     * Mark the task index entered by the user.
     * If the task have been marked, it will prompt the user about it.
     * @param tasks The list containing the user's tasks.
     * @param commandArgs The index description entered by the user.
     * @throws NoDescriptionException If the index description is empty, the exception will be thrown
     * @throws IndexOutOfBoundsException If the index is not within the size of the list, the exception will be thrown
     * @throws FormatException If the index is not an integer, the exception will be thrown.
     */
    public static void markTask(ArrayList<Task> tasks, String commandArgs)
            throws NoDescriptionException, IndexOutOfBoundsException, FormatException {
        if (commandArgs.trim().length() == 0) {
            throw new NoDescriptionException();
        }
        final int markId = Parser.parseIndex(commandArgs) - 1;
        if (markId < 0 || markId >= tasks.size()) {
            throw new IndexOutOfBoundsException();
        }
        if (tasks.get(markId).isDone) {
            System.out.println("This task has already been marked as done ੯•໒꒱❤︎");
        } else {
            tasks.get(markId).markAsDone();
            System.out.println("I've marked this task as done ੯•໒꒱❤︎:");
            System.out.println(tasks.get(markId));
        }
        Ui.printLine();
    }
}
