package Project2.q2.program;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.function.Consumer; // Import Consumer
import java.util.function.Function;


public class TerminalInterface {
    public ArrayList<Option> options;
    private Scanner scanner; // Add a scanner for input

    public void out(String s) {
        System.out.println(s);
    }

    /**
     * Validates if the input string is a valid option index.
     */
    public boolean in(String s) {
        try {
            int input = Integer.parseInt(s);
            if (input < 0 || input >= options.size()) {
                out("Invalid input. Please try again.");
                return false;
            } else {
                return true;
            }
        } catch (NumberFormatException e) {
            out("Invalid input. Please enter a number.");
            return false;
        }
    }


    public TerminalInterface() {
        this.options = new ArrayList<>();
        this.scanner = new Scanner(System.in); // Initialize scanner
    }

    /**
     * Add a new option with a name, description, and an action to execute.
     *
     * @param name        The name of the option (e.g., "Exit")
     * @param description The help description
     * @param action      A lambda function (Consumer) that
     * takes this TerminalInterface as an argument.
     */
    public void addOption(String name, String description, Consumer<TerminalInterface> action) {
        // Create the new option with the action
        Option newOption = new Option(this.options.size() + ". " + name, description, action);
        // Use your 'fun' method to add it to the list
        fun(newOption, this.options::add);
    }

    public void addOptionList(ArrayList<Option> optionList) {
        this.options.addAll(optionList);
    }

    // ... other methods like setOptionList, getOptionList ...
    public void setOptionList(ArrayList<Option> optionList) {
        this.options = optionList;
    }

    public ArrayList<Option> getOptionList() {
        return this.options;
    }


    /**
     * Displays all current options to the console.
     */
    public void displayOptions() {
        out("\nPlease select an option:");
        for (Option option : options) {
            out(String.format("%-20s | %s", option.getName(), option.getDescription()));
        }
        System.out.print("> ");
    }

    /**
     * Starts the main loop of the terminal interface.
     */
    public void start() {
        while (true) {
            displayOptions();
            String input = scanner.nextLine();

            if (in(input)) { // If input is valid
                int index = Integer.parseInt(input);
                // Get the selected option and execute its action
                options.get(index).execute(this);
            }
        }
    }

    /**
     * Gets a line of string input from the user.
     * This uses the same scanner as the main loop.
     */
    public String inString() {
        return this.scanner.nextLine();
    }


    /**
     * Represents a single menu option with an associated action.
     */
    public static class Option {
        private String name;
        private String description;
        // The action to perform, represented as a Consumer function
        private Consumer<TerminalInterface> action;

        public Option() {
            this.name = null;
            this.description = null;
            this.action = null;
        }

        public Option(String name, String description, Consumer<TerminalInterface> action) {
            this.name = name;
            this.description = description;
            this.action = action; // Store the action
        }

        /**
         * Executes the action associated with this option.
         *
         * @param iface The TerminalInterface instance to pass to the action.
         */
        public void execute(TerminalInterface iface) {
            if (this.action != null) {
                this.action.accept(iface); // Run the lambda function
            } else {
                iface.out("This option has no action defined.");
            }
        }

        // ... Getters and Setters ...
        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getDescription() {
            return this.description;
        }
    }

    public <T, R> R fun(T value, Function<T, R> function) {
        return function.apply(value);
    }
}


