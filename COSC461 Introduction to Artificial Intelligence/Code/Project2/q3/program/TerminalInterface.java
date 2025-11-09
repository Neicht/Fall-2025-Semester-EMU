package Project2.q3.program;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;

/**
 * Handles all console input and output, and manages a nested menu system.
 */
public class TerminalInterface {

    public static class MenuNode {
        public enum NodeType { OPTION, CATEGORY }

        private String name;
        private String description;
        private NodeType type;
        private Consumer<TerminalInterface> action;
        private List<MenuNode> children; // Used only for CATEGORY
        private MenuNode parent;         // Used for navigation

        // Constructor for OPTION
        public MenuNode(String name, String description, Consumer<TerminalInterface> action) {
            this.name = name;
            this.description = description;
            this.action = action;
            this.type = NodeType.OPTION;
            this.children = null;
            this.parent = null;
        }

        // Constructor for CATEGORY
        public MenuNode(String name, String description) {
            this.name = name;
            this.description = description;
            this.action = null;
            this.type = NodeType.CATEGORY;
            this.children = new ArrayList<>();
            this.parent = null;
        }

        // --- Getters and Setters ---
        public String getName() { return name; }
        public String getDescription() { return description; }
        public NodeType getType() { return type; }
        public Consumer<TerminalInterface> getAction() { return action; }
        public List<MenuNode> getChildren() { return children; }
        public MenuNode getParent() { return parent; }
        public void setParent(MenuNode parent) { this.parent = parent; }
        public void addChild(MenuNode child) {
            if (this.type == NodeType.CATEGORY) {
                child.setParent(this);
                this.children.add(child);
            }
        }
    }

    // --- TerminalInterface Fields ---
    private Scanner scanner;
    private MenuNode rootMenu;
    private MenuNode currentMenu;
    private boolean isRunning;

    /**
     * Constructor for TerminalInterface.
     */
    public TerminalInterface() {
        this.scanner = new Scanner(System.in);
        this.rootMenu = new MenuNode("Main Menu", "The root menu");
        this.currentMenu = this.rootMenu;
        this.isRunning = false;
    }

    /**
     * Gets the root node of the menu, allowing options to be added.
     * @return The root MenuNode.
     */
    public MenuNode getRootMenu() {
        return this.rootMenu;
    }

    /**
     * Adds a new category (sub-menu) to a parent menu.
     * @param name The name of the category (e.g., "File").
     * @param parent The node to attach this category to (e.g., the root).
     * @return The newly created MenuNode for the category.
     */
    public MenuNode addCategory(String name, MenuNode parent) {
        MenuNode category = new MenuNode(name, "");
        parent.addChild(category);
        return category;
    }

    /**
     * Adds a new action/option to a parent menu.
     * @param parent The menu to add this option to.
     * @param name The name of the option (e.g., "Save File").
     * @param description A brief description.
     * @param action The lambda function to execute when selected.
     */
    public void addOption(MenuNode parent, String name, String description, Consumer<TerminalInterface> action) {
        MenuNode option = new MenuNode(name, description, action);
        parent.addChild(option);
    }


    /**
     * Starts the main loop of the terminal interface.
     */
    public void start() {
        this.isRunning = true;
        while (this.isRunning) {
            displayMenu();
            handleInput();
        }
        scanner.close();
    }

    /**
     * Stops the terminal interface loop.
     */
    public void stop() {
        this.isRunning = false;
    }

    /**
     * Displays the options for the current menu.
     */
    private void displayMenu() {
        out("\n--- " + currentMenu.getName() + " ---");
        List<MenuNode> options = currentMenu.getChildren();
        for (int i = 0; i < options.size(); i++) {
            MenuNode node = options.get(i);
            String typeIndicator = (node.getType() == MenuNode.NodeType.CATEGORY) ? "[...]" : "";
            out((i + 1) + ". " + node.getName() + " " + typeIndicator);
        }
        if (currentMenu.getParent() != null) {
            out("0. Back");
        }
        out("--------------------");
    }

    /**
     * Waits for user input and processes it.
     */
    private void handleInput() {
        int input = inInt("Select an option:");

        // "Back" option
        if (input == 0) {
            if (currentMenu.getParent() != null) {
                currentMenu = currentMenu.getParent();
            } else {
                out("Already at the main menu.");
            }
            return;
        }

        int index = input - 1; // Adjust for 0-based list
        List<MenuNode> options = currentMenu.getChildren();

        if (index < 0 || index >= options.size()) {
            out("Invalid input. Please try again.");
            return;
        }

        MenuNode selected = options.get(index);
        if (selected.getType() == MenuNode.NodeType.CATEGORY) {
            // Navigate into sub-menu
            currentMenu = selected;
        } else {
            // Execute action
            try {
                selected.getAction().accept(this);
            } catch (Exception e) {
                out("An error occurred: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    // --- I/O Helper Methods ---

    /**
     * Prints a string to the console with a newline.
     * @param s The string to print.
     */
    public void out(String s) {
        System.out.println(s);
    }

    /**
     * Gets a string from the user.
     * @return The string entered by the user.
     */
    public String inString() {
        return scanner.nextLine();
    }

    /**
     * Prints a prompt and then gets a string from the user.
     * @param prompt The prompt to display to the user.
     * @return The string entered by the user.
     */
    public String inString(String prompt) {
        out(prompt);
        return inString();
    }

    /**
     * Gets an integer from the user.
     * @param prompt The prompt to display.
     * @return The integer entered by the user.
     */
    public int inInt(String prompt) {
        out(prompt);
        while (true) {
            try {
                String line = scanner.nextLine();
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                out("Invalid integer. Please try again:");
            }
        }
    }

    /**
     * Gets a double from the user.
     * @param prompt The prompt to display.
     * @return The double entered by the user.
     */
    public double inDouble(String prompt) {
        out(prompt);
        while (true) {
            try {
                String line = scanner.nextLine();
                return Double.parseDouble(line);
            } catch (NumberFormatException e) {
                out("Invalid double. Please try again:");
            }
        }
    }
}
