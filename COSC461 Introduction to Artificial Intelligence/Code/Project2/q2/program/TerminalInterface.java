package Project2.q2.program;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.function.Consumer;

/**
 * A terminal interface that supports nested, category-based menus.
 */
public class TerminalInterface {

    /**
     * Represents a node in the menu tree. Can be a clickable option
     * or a category that leads to a sub-menu.
     */
    public static class MenuNode {
        public enum NodeType { CATEGORY, OPTION }

        private String name;
        private String description;
        private NodeType type;
        private Consumer<TerminalInterface> action; // Null for categories
        private ArrayList<MenuNode> children;     // Null for options
        private MenuNode parent;                  // Null for root

        /**
         * Constructor for an OPTION.
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
         * Constructor for a CATEGORY.
         */
        public MenuNode(String name) {
            this.name = name;
            this.description = "Open the " + name + " menu";
            this.action = null;
            this.type = NodeType.CATEGORY;
            this.children = new ArrayList<>();
            this.parent = null;
        }

        /**
         * Adds a child node (option or category) to this category.
         * @param node The node to add.
         */
        public void addChild(MenuNode node) {
            if (this.type == NodeType.CATEGORY) {
                node.parent = this;
                this.children.add(node);
            }
        }

        // --- Getters ---
        public String getName() { return name; }
        public String getDescription() { return description; }
        public NodeType getType() { return type; }
        public Consumer<TerminalInterface> getAction() { return action; }
        public ArrayList<MenuNode> getChildren() { return children; }
        public MenuNode getParent() { return parent; }
    }

    // --- TerminalInterface Fields ---

    private MenuNode rootMenu;
    private MenuNode currentMenu;
    private Scanner scanner;
    private boolean isRunning;

    /**
     * Constructor for TerminalInterface.
     */
    public TerminalInterface() {
        this.rootMenu = new MenuNode("Main Menu");
        this.currentMenu = rootMenu;
        this.scanner = new Scanner(System.in);
        this.isRunning = true;
    }

    /**
     * Gets the root menu node, which all other categories are added to.
     * @return The root MenuNode.
     */
    public MenuNode getRootMenu() {
        return this.rootMenu;
    }

    /**
     * Helper method to add a new category (sub-menu) to a parent.
     * @param name The name of the new category.
     * @param parent The parent node (e.g., getRootMenu()).
     * @return The newly created MenuNode for the category.
     */
    public MenuNode addCategory(String name, MenuNode parent) {
        MenuNode category = new MenuNode(name);
        parent.addChild(category);
        return category;
    }

    /**
     * Helper method to add a new option to a parent category.
     * @param parent The parent category node.
     * @param name The name of the option.
     * @param description The description.
     * @param action The Consumer action to run.
     */
    public void addOption(MenuNode parent, String name, String description, Consumer<TerminalInterface> action) {
        MenuNode option = new MenuNode(name, description, action);
        parent.addChild(option);
    }

    /**
     * Displays a prompt and gets a string input from the user.
     * @return The string entered by the user.
     */
    public String inString() {
        return scanner.nextLine();
    }

    /**
     * Displays a prompt and safely gets an integer input.
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
                out("Invalid input. Please enter a number.");
            }
        }
    }

    /**
     * Displays a prompt and safely gets a double input.
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
                out("Invalid input. Please enter a decimal number (e.g., 0.5).");
            }
        }
    }


    /**
     * Prints a string to the console.
     * @param s The string to print.
     */
    public void out(String s) {
        System.out.println(s);
    }

    /**
     * Stops the main loop.
     */
    public void stop() {
        this.isRunning = false;
    }

    /**
     * Starts the main terminal loop.
     */
    public void start() {
        this.isRunning = true;
        while (isRunning) {
            displayMenu();
            int choice = inInt("Select an option:");

            if (choice == 0) {
                // Go Back
                if (currentMenu.getParent() != null) {
                    currentMenu = currentMenu.getParent();
                } else {
                    out("Already at Main Menu.");
                }
                continue;
            }

            // Adjust for 0-based index
            choice--;

            if (choice >= 0 && choice < currentMenu.getChildren().size()) {
                MenuNode selectedNode = currentMenu.getChildren().get(choice);

                if (selectedNode.getType() == MenuNode.NodeType.CATEGORY) {
                    // Navigate into sub-menu
                    currentMenu = selectedNode;
                } else {
                    // Execute option
                    selectedNode.getAction().accept(this);
                }
            } else {
                out("Invalid option.");
            }
        }
        out("Closing scanner.");
        scanner.close();
    }

    /**
     * Displays the current menu.
     */
    private void displayMenu() {
        out("\n--- [ " + currentMenu.getName() + " ] ---");
        ArrayList<MenuNode> children = currentMenu.getChildren();
        for (int i = 0; i < children.size(); i++) {
            MenuNode node = children.get(i);
            String typeIndicator = (node.getType() == MenuNode.NodeType.CATEGORY) ? " (Menu)" : "";
            out((i + 1) + ". " + node.getName() + typeIndicator + " (" + node.getDescription() + ")");
        }
        if (currentMenu.getParent() != null) {
            out("0. Go Back");
        }
        out("---------------------");
    }
}

