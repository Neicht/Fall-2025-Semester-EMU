package personal.io;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;


/**
 * Handles all console input and output, and manages a nested menu system.
 * This class provides a way to build interactive command-line menus
 * and handle user input validation.
 * <p>
 * -- NOTE: JAVADOC ANNOTATIONS WERE DONE AUTOMATICALLY, THEY MAY BE INCORRECT --
 *
 * @author Nicholas Gawenda
 * @version 1.0
 */
public class TerminalInterface {

    /**
     * Represents a single node in the menu structure. A node can be either
     * an executable {@link NodeType#OPTION} or a navigable {@link NodeType#CATEGORY}
     * that contains child nodes.
     */
    public static class MenuNode {

        /**
         * Defines the type of a {@link MenuNode}, distinguishing between
         * sub-menus (CATEGORY) and executable items (OPTION).
         */
        public enum NodeType {
            /**
             * Represents an executable menu item that performs an action.
             */
            OPTION,
            /**
             * Represents a menu item that opens a sub-menu.
             */
            CATEGORY
        }

        /**
         * The display name of the menu node.
         */
        private String name;
        /**
         * A brief description of the menu node (optional).
         */
        private String description;
        /**
         * The type of the node (OPTION or CATEGORY).
         */
        private NodeType type;
        /**
         * The action to execute if this node is an OPTION.
         */
        private Consumer<TerminalInterface> action;
        /**
         * The list of child nodes if this node is a CATEGORY.
         */
        private List<MenuNode> children;
        /**
         * A reference to the parent node for "Back" navigation.
         */
        private MenuNode parent;

        /**
         * Constructs a new {@link MenuNode} of type {@link NodeType#OPTION}.
         *
         * @param name        The display name for this option.
         * @param description A brief description of what this option does.
         * @param action      A {@link Consumer} lambda that executes when this option is selected.
         */
        public MenuNode(String name, String description, Consumer<TerminalInterface> action) {
            this.name = name;
            this.description = description;
            this.action = action;
            this.type = NodeType.OPTION;
            this.children = null;
            this.parent = null;
        }

        /**
         * Constructs a new {@link MenuNode} of type {@link NodeType#CATEGORY}.
         *
         * @param name        The display name for this category (sub-menu).
         * @param description A brief description of this category.
         */
        public MenuNode(String name, String description) {
            this.name = name;
            this.description = description;
            this.action = null;
            this.type = NodeType.CATEGORY;
            this.children = new ArrayList<>();
            this.parent = null;
        }

        // --- Getters and Setters ---

        /**
         * Gets the name of the menu node.
         *
         * @return The display name.
         */
        public String getName() {
            return name;
        }

        /**
         * Gets the description of the menu node.
         *
         * @return The description.
         */
        public String getDescription() {
            return description;
        }

        /**
         * Gets the type of the menu node.
         *
         * @return The {@link NodeType} (OPTION or CATEGORY).
         */
        public NodeType getType() {
            return type;
        }

        /**
         * Gets the action associated with this node.
         *
         * @return The {@link Consumer} action, or null if this is a CATEGORY.
         */
        public Consumer<TerminalInterface> getAction() {
            return action;
        }

        /**
         * Gets the list of child nodes.
         *
         * @return The list of children, or null if this is an OPTION.
         */
        public List<MenuNode> getChildren() {
            return children;
        }

        /**
         * Gets the parent of this menu node.
         *
         * @return The parent {@link MenuNode}, or null if this is the root.
         */
        public MenuNode getParent() {
            return parent;
        }

        /**
         * Sets the parent for this menu node.
         *
         * @param parent The {@link MenuNode} to set as the parent.
         */
        public void setParent(MenuNode parent) {
            this.parent = parent;
        }

        /**
         * Adds a child node to this node. This method only works if the
         * current node is of type {@link NodeType#CATEGORY}.
         *
         * @param child The {@link MenuNode} to add as a child.
         */
        public void addChild(MenuNode child) {
            if (this.type == NodeType.CATEGORY) {
                child.setParent(this);
                this.children.add(child);
            }
        }
    }

    // --- TerminalInterface Fields ---

    /**
     * Scanner used for all console input.
     */
    private Scanner scanner;
    /**
     * The root node of the menu hierarchy.
     */
    private MenuNode rootMenu;
    /**
     * The currently displayed menu node.
     */
    private MenuNode currentMenu;
    /**
     * Flag to control the main application loop.
     */
    private boolean isRunning;

    /**
     * Constructor for TerminalInterface. Initializes the scanner and
     * creates the root "Main Menu" node.
     */
    public TerminalInterface() {
        this.scanner = new Scanner(System.in);
        this.rootMenu = new MenuNode("Main Menu", "The root menu");
        this.currentMenu = this.rootMenu;
        this.isRunning = false;
    }

    /**
     * Gets the root node of the menu, allowing options to be added.
     *
     * @return The root {@link MenuNode}.
     */
    public MenuNode getRootMenu() {
        return this.rootMenu;
    }

    /**
     * Adds a new category (sub-menu) to a parent menu.
     *
     * @param name   The name of the category (e.g., "File").
     * @param parent The node to attach this category to (e.g., the root).
     * @return The newly created {@link MenuNode} for the category.
     */
    public MenuNode addCategory(String name, MenuNode parent) {
        MenuNode category = new MenuNode(name, ""); // Description is empty by default
        parent.addChild(category);
        return category;
    }

    /**
     * Adds a new action/option to a parent menu.
     *
     * @param parent      The menu to add this option to.
     * @param name        The name of the option (e.g., "Save File").
     * @param description A brief description.
     * @param action      The lambda function to execute when selected.
     */
    public void addOption(MenuNode parent, String name, String description, Consumer<TerminalInterface> action) {
        MenuNode option = new MenuNode(name, description, action);
        parent.addChild(option);
    }


    /**
     * Starts the main loop of the terminal interface. This loop will
     * continue running, displaying the current menu and handling input,
     * until {@link #stop()} is called. Closes the scanner on exit.
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
     * Stops the terminal interface loop. This will cause the {@link #start()}
     * method to exit after its current input cycle.
     */
    public void stop() {
        this.isRunning = false;
    }

    /**
     * Displays the options for the current menu to the console.
     * Automatically adds a "0. Back" option if not at the root menu.
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
     * Waits for user input and processes it. Navigates to a sub-menu,
     * executes an action, or navigates back based on the user's choice.
     * Handles invalid input gracefully.
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
                e.printStackTrace(); // Optional: for debugging
            }
        }
    }

    // --- I/O Helper Methods ---

    /**
     * Prints a string to the console with a newline.
     *
     * @param s The string to print.
     */
    public void out(String s) {
        System.out.println(s);
    }

    /**
     * Gets a raw string of input from the user (reads the entire line).
     *
     * @return The string entered by the user.
     */
    public String inString() {
        return scanner.nextLine();
    }

    /**
     * Prints a prompt and then gets a string from the user.
     *
     * @param prompt The prompt to display to the user.
     * @return The string entered by the user.
     */
    public String inString(String prompt) {
        out(prompt);
        return inString();
    }

    /**
     * Gets an integer from the user. Reprompts indefinitely
     * until a valid integer is entered.
     *
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
     * Gets a double from the user. Reprompts indefinitely
     * until a valid double is entered.
     *
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