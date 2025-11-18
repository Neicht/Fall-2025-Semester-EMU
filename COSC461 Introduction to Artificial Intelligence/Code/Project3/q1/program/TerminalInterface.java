package Project3.q1.program;

import java.io.*;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The Ultimate Terminal Interface (UTI for short)
 * @author Nicholas Gawenda
 * @version 3.1 Swiss Army Knife
 */
public class TerminalInterface {

    // --- Components ---
    /**
     * Handles formatting and printing to the console.
     */
    private final ConsolePrinter printer;
    /**
     * Handles user input parsing and validation.
     */
    private final InputHandler input;
    /**
     * Handles file system operations and navigation.
     */
    private final FileTools files;
    /**
     * Handles reflection, timing, and debugging tasks.
     */
    private final DebugTools debug;

    /**
     * The root node of the menu tree.
     */
    private final MenuNode rootMenu;

    public enum Alignment {LEFT, CENTER, RIGHT}

    ;
    /**
     * The currently active menu node.
     */
    private MenuNode currentMenu;
    /**
     * Flag controlling the main application loop.
     */
    private boolean isRunning;

    /**
     * Public access to Automata tools for direct usage.
     */
    public final AutomataTools automata;

    /**
     * Constructor. Initializes all helper components and the shared Scanner.
     */
    public TerminalInterface() {
        this.printer = new ConsolePrinter();
        // One Scanner to rule them all
        Scanner scanner = new Scanner(System.in);
        this.input = new InputHandler(scanner, printer);
        this.automata = new AutomataTools(printer);
        this.files = new FileTools(printer, input);
        this.debug = new DebugTools(printer);

        this.rootMenu = new MenuNode("Main Menu", "The root menu");
        this.currentMenu = this.rootMenu;
        this.isRunning = false;
    }

    // ==================================================================================
    // Plug-and-Play API)
    // ==================================================================================

    // --- Printing & State ---

    public TerminalInterface setIndent(int spaces) {
        printer.setIndent(spaces);
        return this;
    }

    public TerminalInterface setAlign(Alignment align) {
        printer.setAlign(align);
        return this;
    }

    public TerminalInterface setPrefix(String prefix) {
        printer.setPrefix(prefix);
        return this;
    }

    public TerminalInterface setLineWidth(int w) {
        printer.setLineWidth(w);
        return this;
    }

    public TerminalInterface setIncrementStep(int s) {
        printer.setIncrementStep(s);
        return this;
    }

    public void setStatLabelWidth(int w) {
        printer.setStatLabelWidth(w);
    }

    /**
     * Prints a line of text to the console.
     *
     * @param s The string to print.
     */
    public void out(String s) {
        printer.out(s);
    }

    /**
     * Prints a major title block, centered and enclosed in lines.
     *
     * @param title The text to display in the title.
     */
    public void printTitle(String title) {
        printer.printTitle(title);
    }

    /**
     * Prints a section header, centered and padded with the current line style.
     *
     * @param header The text to display.
     */
    public void printHeader(String header) {
        printer.printHeader(header);
    }

    /**
     * Prints a full-width horizontal line using the current style.
     */
    public void printLine() {
        printer.printLine();
    }

    /**
     * Prints a custom horizontal splitter.
     *
     * @param style The character to repeat.
     * @param width The width of the line.
     */
    public void printSplitter(char style, int width) {
        printer.printSplitter(style, width);
    }

    /**
     * Prints a key-value pair formatted like a spreadsheet row.
     *
     * @param label  The label (key).
     * @param values One or more values to display next to the label.
     */
    public void printStat(String label, String... values) {
        printer.printStat(label, values);
    }

    /**
     * Prints a block of text, automatically wrapping lines to fit the current width.
     *
     * @param text The text to wrap and print.
     */
    public void printBody(String text) {
        printer.printBody(text);
    }

    /**
     * Prints a message enclosed in a decorative box.
     *
     * @param message The message to emphasize.
     */
    public void printBox(String message) {
        printer.printBox(message);
    }

    /**
     * Prints a 2D array of data as a formatted ASCII table.
     *
     * @param headers Array of column headers.
     * @param data    2D array of string data.
     */
    public void printTable(String[] headers, String[][] data) {
        printer.printTable(headers, data);
    }

    /**
     * Prints an ASCII progress bar (e.g., [====....] 50%).
     *
     * @param current The current progress value.
     * @param total   The target total value.
     */
    public void printProgress(int current, int total) {
        printer.printProgress(current, total);
    }

    /**
     * Prints a horizontal histogram for an array of data.
     *
     * @param data Array of numerical values to plot.
     */
    public void printHistogram(double[] data) {
        printer.printHistogram(data);
    }

    /**
     * Prints a standardized success message (prefixed with [OK]).
     *
     * @param msg The message.
     */
    public void printSuccess(String msg) {
        printer.out("[OK] " + msg);
    }

    /**
     * Prints a standardized error message (prefixed with [ERROR]).
     *
     * @param msg The message.
     */
    public void printError(String msg) {
        printer.out("[ERROR] " + msg);
    }

    /**
     * Prints a standardized warning message (prefixed with [WARN]).
     *
     * @param msg The message.
     */
    public void printWarning(String msg) {
        printer.out("[WARN] " + msg);
    }


    /**
     * Sets the line character style for subsequent print operations.
     *
     * @param c The character (e.g., '-', '=', '*').
     * @return This object for chaining.
     */
    public TerminalInterface setLineStyle(char c) {
        printer.setLineStyle(c);
        return this;
    }

    /**
     * Pushes the current styling state (width, style, etc.) onto a stack.
     * Useful before temporarily changing settings for a specific output.
     */
    public void pushState() {
        printer.pushState();
    }

    /**
     * Restores the previously pushed styling state.
     */
    public void popState() {
        printer.popState();
    }

    // --- Input ---

    /**
     * Prompts the user for a string.
     *
     * @param prompt The text to display.
     * @return The user's input.
     */
    public String inString(String prompt) {
        return input.inString(prompt);
    }

    /**
     * Prompts the user for a string, accepting a default value if empty.
     *
     * @param prompt     The text to display.
     * @param defaultVal The value to return if the user presses Enter.
     * @return The user's input or the default value.
     */
    public String inString(String prompt, String defaultVal) {
        return input.inString(prompt, defaultVal);
    }

    /**
     * Prompts the user for an integer. loops until valid.
     *
     * @param prompt The text to display.
     * @return The valid integer.
     */
    public int inInt(String prompt) {
        return input.inInt(prompt);
    }

    /**
     * Prompts the user for an integer within a specific range.
     *
     * @param prompt The text to display.
     * @param min    The minimum acceptable value.
     * @param max    The maximum acceptable value.
     * @return A valid integer within [min, max].
     */
    public int inInt(String prompt, int min, int max) {
        return input.inInt(prompt, min, max);
    }

    /**
     * Prompts the user for a double. loops until valid.
     *
     * @param prompt The text to display.
     * @return The valid double.
     */
    public double inDouble(String prompt) {
        return input.inDouble(prompt);
    }

    /**
     * Prompts the user for a boolean (y/n, true/false, 1/0).
     *
     * @param prompt The text to display.
     * @return True or False.
     */
    public boolean inBoolean(String prompt) {
        return input.inBoolean(prompt);
    }

    /**
     * Presents a list of options and forces the user to select one.
     *
     * @param prompt  The text to display.
     * @param options Variable arguments of option strings.
     * @return The String value of the selected option.
     */
    public String inChoice(String prompt, String... options) {
        return input.inChoice(prompt, options);
    }

    /**
     * Pre-loads the input queue with values. Subsequent calls to input methods
     * will consume these values instead of blocking for keyboard input.
     *
     * @param inputs The strings to queue up.
     */
    public void scriptInput(String... inputs) {
        input.scriptInput(inputs);
    }

    /**
     * Pauses execution until the user presses Enter.
     */
    public void pause() {
        input.pause();
    }

    // --- Files & Directory ---

    /**
     * Reads the entire content of a file into a String.
     *
     * @param path The file path.
     * @return The file content, or null if error.
     */
    public String readFile(String path) {
        return files.readFile(path);
    }

    /**
     * Reads a file line by line into a List.
     *
     * @param path The file path.
     * @return List of lines, or empty list if error.
     */
    public List<String> readLines(String path) {
        return files.readLines(path);
    }

    /**
     * Writes a string to a file, creating parent directories if needed.
     *
     * @param path    The file path.
     * @param content The content to write.
     */
    public void writeFile(String path, String content) {
        files.writeFile(path, content);
    }

    /**
     * Ensures that the directory structure for the given path exists.
     *
     * @param path A file path or directory path.
     */
    public void ensureDirectory(String path) {
        files.ensureDirectory(path);
    }

    /**
     * Recursively searches for a file with the given name in the project.
     *
     * @param filename The name of the file to find.
     * @return The absolute path, or null if not found.
     */
    public String findFile(String filename) {
        return files.findFile(filename);
    }

    /**
     * Opens an interactive menu to browse and select a file.
     *
     * @param prompt    The text to display.
     * @param extension Filter files by extension (e.g., ".txt").
     * @return The selected file path, or null if canceled.
     */
    public String chooseFile(String prompt, String extension) {
        return files.chooseFile(prompt, extension);
    }

    /**
     * Starts an interactive file browser at the specified path.
     *
     * @param startPath The path to start browsing from.
     * @return The absolute path of the selected file.
     */
    public String browseFile(String startPath) {
        return files.browseFile(startPath);
    }

    /**
     * Gets the file system path of the directory containing the given object's class.
     *
     * @param context The object instance (usually 'this').
     * @return The absolute directory path.
     */
    public String getClassLocation(Object context) {
        return files.getClassLocation(context);
    }

    /**
     * Prints a visual tree of the directory structure.
     *
     * @param path The root path to visualize.
     */
    public void printDirectoryTree(String path) {
        files.printDirectoryTree(path);
    }

    /**
     * Starts recording all console output to a file.
     *
     * @param filename The file to save the log to.
     */
    public void startLog(String filename) {
        printer.startLog(filename);
    }

    /**
     * Stops recording console output.
     */
    public void stopLog() {
        printer.stopLog();
    }


    // --- Debug & Tools ---

    /**
     * Enables or disables debug mode.
     *
     * @param enabled True to enable debug logging.
     */
    public void setDebug(boolean enabled) {
        debug.setDebug(enabled);
    }

    /**
     * Prints a message only if debug mode is enabled.
     *
     * @param msg The message to print.
     */
    public void debug(String msg) {
        debug.log(msg);
    }

    /**
     * Prints the current state configuration (Width, Style, etc.) for debugging UI.
     */
    public void printState() {
        printer.printState();
    }

    /**
     * Uses reflection to print the private fields of any object.
     *
     * @param o The object to inspect.
     */
    public void inspect(Object o) {
        debug.inspect(o);
    }

    /**
     * Starts a performance timer.
     */
    public void tic() {
        debug.tic();
    }

    /**
     * Stops the timer and prints the elapsed time.
     *
     * @param label A label for the elapsed time.
     */
    public void toc(String label) {
        debug.toc(label);
    }

    public long getToc(){
        return debug.getToc();
    }

    /**
     * Wraps a runnable task in a try-catch block that prints a clean crash report
     * instead of crashing the application.
     *
     * @param task The code to execute.
     */
    public void guard(Runnable task) {
        debug.guard(task);
    }

    /**
     * Generates an array of random integers.
     *
     * @param size The size of the array.
     * @param min  Minimum value.
     * @param max  Maximum value.
     * @return The array of random ints.
     */
    public int[] generateRandomIntArray(int size, int min, int max) {
        return debug.generateRandomIntArray(size, min, max);
    }

    public int[] generateIntArray(int size, int step, int start){
        return debug.generateIntArray(size, step, start);
    }
    public double[] generateDoubleArray(int size, double step, double start){
        return debug.generateDoubleArray(size, step, start);
    }

    /**
     * Generates a random alphanumeric string.
     *
     * @param len The length of the string.
     * @return The random string.
     */
    public String generateRandomString(int len) {
        return debug.generateRandomString(len);
    }


    // ==================================================================================
    // MENU SYSTEM
    // ==================================================================================

    /**
     * Gets the root node of the menu system.
     *
     * @return The root menu node.
     */
    public MenuNode getRootMenu() {
        return this.rootMenu;
    }

    /**
     * Adds a new submenu category.
     *
     * @param name   The name of the category.
     * @param parent The parent node to attach to.
     * @return The new MenuNode.
     */
    public MenuNode addCategory(String name, MenuNode parent) {
        MenuNode category = new MenuNode(name, "");
        parent.addChild(category);
        return category;
    }

    /**
     * Adds an executable option to a menu.
     *
     * @param parent      The parent menu.
     * @param name        The name of the option.
     * @param description A description of the option.
     * @param action      The lambda to execute when selected.
     */
    public void addOption(MenuNode parent, String name, String description, Consumer<TerminalInterface> action) {
        MenuNode option = new MenuNode(name, description, action);
        parent.addChild(option);
    }

    /**
     * Starts the main application loop.
     * Continues until {@link #stop()} is called.
     */
    public void start() {
        this.isRunning = true;
        while (this.isRunning) {
            displayMenu();
            handleInput();
        }
    }

    /**
     * Stops the main application loop.
     */
    public void stop() {
        this.isRunning = false;
    }

    /**
     * Renders the current menu and breadcrumbs.
     */
    private void displayMenu() {
        // Build Breadcrumbs (e.g., "Main > Network > Settings")
        Deque<String> path = new ArrayDeque<>();
        MenuNode temp = currentMenu;
        while (temp != null) {
            path.push(temp.getName());
            temp = temp.getParent();
        }
        printer.printHeader(String.join(" > ", path));

        List<MenuNode> options = currentMenu.getChildren();
        for (int i = 0; i < options.size(); i++) {
            MenuNode node = options.get(i);
            String indicator = (node.getType() == MenuNode.NodeType.CATEGORY) ? " [...]" : "";
            out((i + 1) + ". " + node.getName() + indicator);
        }
        if (currentMenu.getParent() != null) out("0. Back");
        printer.printLine();
    }

    /**
     * Processes user input for the menu system.
     */
    private void handleInput() {
        int inputVal = input.inInt("Select option: ");
        if (inputVal == 0) {
            if (currentMenu.getParent() != null) currentMenu = currentMenu.getParent();
            else out("Already at root.");
            return;
        }
        int index = inputVal - 1;
        if (index >= 0 && index < currentMenu.getChildren().size()) {
            MenuNode selected = currentMenu.getChildren().get(index);
            if (selected.getType() == MenuNode.NodeType.CATEGORY) {
                currentMenu = selected;
            } else {
                try {
                    selected.getAction().accept(this);
                } catch (Exception e) {
                    printer.printBox("CRASH DURING ACTION");
                    e.printStackTrace();
                    input.pause();
                }
            }
        } else {
            out("Invalid selection.");
        }
    }

    // ==================================================================================
    // INNER CLASS: ConsolePrinter (Rendering & State)
    // ==================================================================================

    /**
     * Internal component responsible for all console output and state management.
     */
    private static class ConsolePrinter {

        private static class State implements Cloneable {
            int lineWidth = 46;
            char lineStyle = '-';
            int statLabelWidth = 25;
            int incrementStep = 1;
            int indent = 0;
            Alignment align = Alignment.CENTER;
            String prefix = "";

            @Override
            public State clone() {
                try {
                    return (State) super.clone();
                } catch (CloneNotSupportedException e) {
                    return null;
                }
            }
        }

        private State state = new State();
        private final Deque<State> stack = new ArrayDeque<>();
        private PrintWriter logWriter = null;

        // --- Core Output Method (Now handles Indent + Prefix) ---
        public void out(String s) {
            // 1. Handle multiline strings so indentation applies to all lines
            String[] lines = s.split("\n");
            for (String line : lines) {
                // Build the final string: Indent + Prefix + Content
                String finalLine = " ".repeat(Math.max(0, state.indent)) + state.prefix + line;
                System.out.println(finalLine);
                if (logWriter != null) logWriter.println(finalLine);
            }
            if (logWriter != null) logWriter.flush();
        }

        // --- State Management ---
        public void setStatLabelWidth(int w) {
            state.statLabelWidth = w;
        }

        public void setLineWidth(int w) {
            state.lineWidth = w;
        }

        public void setLineStyle(char c) {
            state.lineStyle = c;
        }

        public void setIndent(int i) {
            state.indent = Math.max(0, i);
        }

        public void setAlign(Alignment a) {
            state.align = a;
        }

        public void setPrefix(String p) {
            state.prefix = (p == null) ? "" : p;
        }

        public void setIncrementStep(int s) { state.incrementStep = s; }


        public void pushState() {
            stack.push(state.clone());
        }

        public void popState() {
            if (!stack.isEmpty()) state = stack.pop();
        }

        public void printState() {
            out("--- Printer State ---");
            out(" Width:  " + state.lineWidth);
            out(" Indent: " + state.indent);
            out(" Align:  " + state.align);
            out(" Prefix: " + state.prefix);
            out(" Line:   " + state.lineStyle);
            out(" Stat:   " + state.statLabelWidth);
            printLine();
        }

        // --- Drawing Methods ---
        public void printLine() {
            // Line takes up full width, but respects indent visually
            out(String.valueOf(state.lineStyle).repeat(Math.max(0, state.lineWidth)));
        }

        public void printSplitter(char c, int w) {
            out(String.valueOf(c).repeat(Math.max(0, w)));
        }

        public void printHeader(String h) {
            String title = " " + h + " ";
            int effectiveWidth = state.lineWidth;

            if (title.length() >= effectiveWidth) {
                out(title);
                return;
            }

            int padding = effectiveWidth - title.length();
            String padChar = String.valueOf(state.lineStyle);
            String left, right;

            switch (state.align) {
                case LEFT:
                    left = padChar.repeat(2); // Small fixed pad on left
                    right = padChar.repeat(Math.max(0, padding - 2));
                    break;
                case RIGHT:
                    right = padChar.repeat(2); // Small fixed pad on right
                    left = padChar.repeat(Math.max(0, padding - 2));
                    break;
                case CENTER:
                default:
                    int leftPad = padding / 2;
                    left = padChar.repeat(leftPad);
                    right = padChar.repeat(padding - leftPad);
                    break;
            }
            out(left + title + right);
        }

        public void printTitle(String t) {
            printLine();
            // Title text alignment
            int padding = state.lineWidth - t.length();
            String spaces = switch (state.align) {
                case LEFT -> "";
                case RIGHT -> " ".repeat(Math.max(0, padding));
                default -> " ".repeat(Math.max(0, padding / 2));
            };

            out(spaces + t);
            printLine();
        }

        public void printStat(String label, String... vals) {
            out(String.format("%-" + state.statLabelWidth + "s: %s", label, String.join(" | ", vals)));
        }

        public void printBody(String text) {
            if (text == null) return;
            StringBuilder line = new StringBuilder();
            for (String w : text.split("\\s+")) {
                if (line.length() + w.length() + 1 > state.lineWidth) {
                    out(line.toString());
                    line.setLength(0);
                }
                if (!line.isEmpty()) line.append(" ");
                line.append(w);
            }
            if (!line.isEmpty()) out(line.toString());
        }

        public void printBox(String msg) {
            printSplitter('=', state.lineWidth);
            out("  " + msg);
            printSplitter('=', state.lineWidth);
        }

        public void printTable(String[] headers, String[][] data) {
            if (headers == null || data == null) return;
            int[] widths = new int[headers.length];
            for (int i = 0; i < headers.length; i++) widths[i] = headers[i].length();
            for (String[] row : data) {
                for (int i = 0; i < row.length && i < widths.length; i++) {
                    if (row[i] != null) widths[i] = Math.max(widths[i], row[i].length());
                }
            }
            StringBuilder fmt = new StringBuilder();
            // Tables usually look best with explicit borders, ignoring generic alignment
            for (int w : widths) fmt.append("| %-").append(w).append("s ");
            fmt.append("|");

            String line = "+-" + Arrays.stream(widths).mapToObj("-"::repeat).collect(Collectors.joining("-+-")) + "-+";
            out(line);
            out(String.format(fmt.toString(), (Object[]) headers));
            out(line);
            for (String[] row : data) out(String.format(fmt.toString(), (Object[]) row));
            out(line);
        }

        public void printProgress(int current, int total) {
            int barWidth = state.lineWidth - 10;
            int progress = (int) ((double) current / total * barWidth);
            String bar = "[" + "=".repeat(progress) + " ".repeat(barWidth - progress) + "]";

            // NOTE: We use System.out.print here directly because \r logic conflicts with our new 'out' indent logic
            System.out.print("\r" + " ".repeat(state.indent) + state.prefix + bar + " " + (int) ((double) current / total * 100) + "%");
            if (current == total) System.out.println();
        }

        public void printHistogram(double[] data) {
            if (data == null || data.length == 0) return;
            double max = -Double.MAX_VALUE;
            for (double d : data) max = Math.max(max, d);

            int barMax = state.lineWidth - 15;
            for (double v : data) {
                int len = (int) ((v / max) * barMax);
                String label = String.format("%-10.4f |", v);
                out(label + "#".repeat(Math.max(0, len)));
            }
        }

        public void startLog(String filename) {
            try {
                logWriter = new PrintWriter(new FileWriter(filename));
                out("[Log Started: " + filename + "]");
            } catch (IOException e) {
                out("[Error creating log]");
            }
        }

        public void stopLog() {
            if (logWriter != null) {
                out("[Log Stopped]");
                logWriter.close();
                logWriter = null;
            }
        }
    }

    // ==================================================================================
    // INNER CLASS: InputHandler (Scanner & Scripting)
    // ==================================================================================

    /**
     * Internal component for handling Scanner input and scripting.
     */
    private static class InputHandler {
        private final Scanner scanner;
        private final ConsolePrinter printer;
        private final Queue<String> scriptQueue = new LinkedList<>();

        public InputHandler(Scanner s, ConsolePrinter p) {
            this.scanner = s;
            this.printer = p;
        }

        private String rawInput() {
            if (!scriptQueue.isEmpty()) {
                String val = scriptQueue.poll();
                printer.out(val + " (scripted)");
                return val;
            }
            return scanner.nextLine();
        }

        public void scriptInput(String... inputs) {
            Collections.addAll(scriptQueue, inputs);
        }

        public String inString(String prompt) {
            printer.out(prompt);
            return rawInput();
        }

        public String inString(String prompt, String def) {
            printer.out(prompt + " [" + def + "]: ");
            String val = rawInput();
            return val.isEmpty() ? def : val;
        }

        public int inInt(String prompt) {
            printer.out(prompt);
            while (true) {
                try {
                    return Integer.parseInt(rawInput());
                } catch (NumberFormatException e) {
                    printer.out("Invalid integer. Try again:");
                }
            }
        }

        public int inInt(String prompt, int min, int max) {
            while (true) {
                int val = inInt(prompt + " (" + min + "-" + max + "): ");
                if (val >= min && val <= max) return val;
                printer.out("Value out of range.");
            }
        }

        public double inDouble(String prompt) {
            printer.out(prompt);
            while (true) {
                try {
                    return Double.parseDouble(rawInput());
                } catch (NumberFormatException e) {
                    printer.out("Invalid double. Try again:");
                }
            }
        }

        public boolean inBoolean(String prompt) {
            while (true) {
                String s = inString(prompt + " (y/n): ").toLowerCase();
                if (s.startsWith("y") || s.equals("true") || s.equals("1")) return true;
                if (s.startsWith("n") || s.equals("false") || s.equals("0")) return false;
            }
        }

        public String inChoice(String prompt, String... options) {
            printer.out(prompt);
            for (int i = 0; i < options.length; i++) printer.out(" " + (i + 1) + ". " + options[i]);
            int idx = inInt("Select: ", 1, options.length);
            return options[idx - 1];
        }

        public void pause() {
            inString("Press Enter to continue...");
        }
    }

    // ==================================================================================
    // INNER CLASS: FileTools (IO & Directory)
    // ==================================================================================

    /**
         * Internal component for File I/O, path finding, and directory browsing.
         */
        private record FileTools(ConsolePrinter printer, InputHandler input) {

        public String readFile(String path) {
                try {
                    return Files.readString(Path.of(path));
                } catch (IOException e) {
                    printer.out("[Read Error] " + e.getMessage());
                    return null;
                }
            }

            public List<String> readLines(String path) {
                try {
                    return Files.readAllLines(Path.of(path));
                } catch (IOException e) {
                    printer.out("[Read Error] " + e.getMessage());
                    return new ArrayList<>();
                }
            }

            public void writeFile(String path, String content) {
                try {
                    ensureDirectory(path);
                    Files.writeString(Path.of(path), content);
                    printer.out("[Saved] " + path);
                } catch (IOException e) {
                    printer.out("[Write Error] " + e.getMessage());
                }
            }

            public void ensureDirectory(String path) {
                try {
                    Path p = Path.of(path);
                    if (path.contains(".")) {
                        if (p.getParent() != null) Files.createDirectories(p.getParent());
                    } else {
                        Files.createDirectories(p);
                    }
                } catch (IOException e) {
                    printer.out("[Dir Error] " + e.getMessage());
                }
            }

            public String findFile(String name) {
                try (Stream<Path> walk = Files.walk(Path.of("."))) {
                    Optional<Path> match = walk.filter(p -> p.getFileName().toString().equals(name)).findFirst();
                    return match.map(Path::toString).orElse(null);
                } catch (IOException e) {
                    return null;
                }
            }

            public String chooseFile(String prompt, String extension) {
                try (Stream<Path> list = Files.walk(Path.of("."), 2)) {
                    List<String> files = list
                            .filter(p -> Files.isRegularFile(p) && p.toString().endsWith(extension))
                            .map(Path::toString)
                            .toList();
                    if (files.isEmpty()) {
                        printer.out("No " + extension + " files found.");
                        return null;
                    }
                    return input.inChoice(prompt, files.toArray(new String[0]));
                } catch (IOException e) {
                    printer.out("Error scanning files.");
                    return null;
                }
            }

            public String browseFile(String startPath) {
                Path current = Path.of(startPath).toAbsolutePath().normalize();
                // Safety check: if startPath is invalid, fallback to current dir
                if (!Files.exists(current)) current = Path.of(".").toAbsolutePath().normalize();

                while (true) {
                    printer.pushState();
                    printer.setLineStyle('.'); // Different style for browser
                    printer.printHeader("Browsing: " + current.getFileName());
                    printer.popState();

                    File dir = current.toFile();
                    File[] files = dir.listFiles();

                    if (files == null) {
                        printer.out("Access Denied.");
                        current = current.getParent();
                        continue;
                    }

                    // Sort: Directories first, then Files. Alphabetical.
                    Arrays.sort(files, (a, b) -> {
                        if (a.isDirectory() && !b.isDirectory()) return -1;
                        if (!a.isDirectory() && b.isDirectory()) return 1;
                        return a.getName().compareToIgnoreCase(b.getName());
                    });

                    List<File> options = new ArrayList<>();
                    // Option 1: Go Up (if not at root)
                    if (current.getParent() != null) {
                        printer.out(" 1. [UP] ..");
                    } else {
                        printer.out(" 1. [ROOT] (Cannot go up)");
                    }

                    // List contents
                    int counter = 2;
                    for (File f : files) {
                        String tag = f.isDirectory() ? "[+]" : " - ";
                        printer.out(" " + counter + ". " + tag + " " + f.getName());
                        options.add(f);
                        counter++;
                    }
                    printer.out(" 0. Cancel");
                    printer.printLine();

                    int choice = input.inInt("Select: ");
                    if (choice == 0) return null; // Cancel
                    if (choice == 1) {
                        if (current.getParent() != null) current = current.getParent();
                        continue;
                    }

                    // Map selection to file (adjust index because 1 is Up)
                    int fileIndex = choice - 2;
                    if (fileIndex >= 0 && fileIndex < options.size()) {
                        File selected = options.get(fileIndex);
                        if (selected.isDirectory()) {
                            current = selected.toPath(); // Enter directory
                        } else {
                            return selected.getAbsolutePath(); // Return file path
                        }
                    } else {
                        printer.out("Invalid selection.");
                    }
                }
            }

            public String getClassLocation(Object context) {
                if (context == null) return null;
                try {
                    URL url = context.getClass().getResource("");
                    if (url == null) return "Unknown";
                    return Paths.get(url.toURI()).toString();
                } catch (Exception e) {
                    return null;
                }
            }

            public void printDirectoryTree(String rootPath) {
                try {
                    Files.walkFileTree(Path.of(rootPath), new SimpleFileVisitor<Path>() {
                        int depth = 0;

                        @Override
                        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                            printer.out("  ".repeat(depth++) + "+ " + dir.getFileName());
                            return FileVisitResult.CONTINUE;
                        }

                        @Override
                        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                            printer.out("  ".repeat(depth) + "- " + file.getFileName());
                            return FileVisitResult.CONTINUE;
                        }

                        @Override
                        public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
                            depth--;
                            return FileVisitResult.CONTINUE;
                        }
                    });
                } catch (IOException e) {
                    printer.out("Error listing tree.");
                }
            }
        }

    // ==================================================================================
    // INNER CLASS: DebugTools (Reflect, Timer, Mock)
    // ==================================================================================

    /**
     * Internal component for debugging, performance timing, and mocking.
     */
    private static class DebugTools {
        private final ConsolePrinter printer;
        private long startTime;
        private boolean debugMode = false;

        public DebugTools(ConsolePrinter p) {
            this.printer = p;
        }

        public void setDebug(boolean b) {
            this.debugMode = b;
            printer.out("[Debug Mode: " + b + "]");
        }

        public void log(String msg) {
            if (debugMode) printer.out("[DEBUG] " + msg);
        }

        public void inspect(Object o) {
            if (o == null) {
                printer.out("Object is null");
                return;
            }
            printer.printHeader("Inspect: " + o.getClass().getSimpleName());
            for (Field f : o.getClass().getDeclaredFields()) {
                f.setAccessible(true);
                try {
                    printer.printStat(f.getName(), String.valueOf(f.get(o)));
                } catch (Exception e) {
                    printer.printStat(f.getName(), "[Access Denied]");
                }
            }
            printer.printLine();
        }

        public void tic() {
            startTime = System.currentTimeMillis();
        }

        public long getToc() {
            return System.currentTimeMillis() - startTime;
        }

        public void toc(String label) {
            long timeTaken = System.currentTimeMillis() - startTime;
                printer.out(label + timeTaken + "ms");
        }


        public void guard(Runnable task) {
            try {
                task.run();
            } catch (Exception e) {
                printer.printBox("CRASH REPORT");
                printer.out("Ex: " + e.getClass().getSimpleName());
                printer.out("Msg: " + e.getMessage());
                printer.printLine();
                e.printStackTrace();
            }
        }

        public int[] generateRandomIntArray(int size, int min, int max) {
            Random r = new Random();
            return r.ints(size, min, max + 1).toArray();
        }

        public String generateRandomString(int len) {
            String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
            StringBuilder sb = new StringBuilder();
            Random r = new Random();
            for (int i = 0; i < len; i++) sb.append(chars.charAt(r.nextInt(chars.length())));
            return sb.toString();
        }

        public int[] generateIntArray(int size, int step, int start) {
            int[] arr = new int[size];
            for (int i = 0; i < arr.length; i++) arr[i] = start + (step * i);
            return arr;
        }

        public double[] generateDoubleArray(int size, double step, double start) {
            double[] arr = new double[size];
            for (int i = 0; i < arr.length; i++) arr[i] = start + (step * i);
            return arr;
        }
    }

    // ==================================================================================
    // MENU NODE STRUCTURE
    // ==================================================================================

    /**
     * Represents a single node in the interactive menu tree.
     */
    public static class MenuNode {
        /**
         * Enumeration for node types.
         */
        public enum NodeType {OPTION, CATEGORY}

        private final String name;
        private final String description;
        private final NodeType type;
        private final Consumer<TerminalInterface> action;
        private final List<MenuNode> children;
        private MenuNode parent;

        public MenuNode(String name, String desc, Consumer<TerminalInterface> action) {
            this.name = name;
            this.description = desc;
            this.action = action;
            this.type = NodeType.OPTION;
            this.children = null;
        }

        public MenuNode(String name, String desc) {
            this.name = name;
            this.description = desc;
            this.action = null;
            this.type = NodeType.CATEGORY;
            this.children = new ArrayList<>();
        }

        public String getName() {
            return name;
        }

        public NodeType getType() {
            return type;
        }

        public List<MenuNode> getChildren() {
            return children;
        }

        public MenuNode getParent() {
            return parent;
        }

        public Consumer<TerminalInterface> getAction() {
            return action;
        }

        public void setParent(MenuNode p) {
            this.parent = p;
        }

        public void addChild(MenuNode c) {
            if (type == NodeType.CATEGORY) {
                c.setParent(this);
                assert children != null;
                children.add(c);
            }
        }
    }

    // ==================================================================================
    // INNER CLASS: AutomataTools (DFA/NFA/TM Visualization)
    // ==================================================================================

    /**
         * Internal component for Automata Theory visualization (DFA, NFA, Turing Machines).
         */
        public record AutomataTools(ConsolePrinter printer) {

        /**
             * Prints a formatted transition table for a Finite Automaton.
             *
             * @param states      Array of state names (e.g. ["q0", "q1", "q2"])
             * @param alphabet    Array of symbols (e.g. ["0", "1"])
             * @param transitions 2D array where row=state, col=symbol. Contains destination state.
             *                    Use "Ø" or null for no transition.
             */
            public void printTransitionTable(String[] states, String[] alphabet, String[][] transitions) {
                if (states == null || alphabet == null || transitions == null) return;

                // Build headers: "State" + alphabet symbols
                String[] headers = new String[alphabet.length + 1];
                headers[0] = "State";
                System.arraycopy(alphabet, 0, headers, 1, alphabet.length);

                // Build data rows
                String[][] tableData = new String[states.length][headers.length];
                for (int i = 0; i < states.length; i++) {
                    tableData[i][0] = states[i]; // First col is State Name
                    for (int j = 0; j < alphabet.length; j++) {
                        String dest = (j < transitions[i].length) ? transitions[i][j] : null;
                        tableData[i][j + 1] = (dest == null) ? "Ø" : dest;
                    }
                }

                printer.printTitle("Transition Function (δ)");
                printer.printTable(headers, tableData);
            }

            /**
             * Visualizes a Turing Machine tape at a specific step.
             *
             * @param tape  A list or array of symbols on the tape.
             * @param head  The current index of the read/write head.
             * @param state The current state name (optional, can be null).
             */
            public void printTape(List<String> tape, int head, String state) {
                StringBuilder tapeStr = new StringBuilder("|");
                StringBuilder ptrStr = new StringBuilder(" ");

                // Define a fixed width for each cell for alignment
                int cellWidth = 3;
                String fmt = " %-" + (cellWidth - 1) + "s|";

                for (String s : tape) {
                    tapeStr.append(String.format(fmt, s));
                }

                // Calculate head position visually
                // Each cell is cellWidth + 1 (border) chars wide
                // The center of a cell is at offset + 1 + (cellWidth/2)
                int pointerPos = (head * (cellWidth + 1)) + 1 + (cellWidth / 2);

                ptrStr.append(" ".repeat(Math.max(0, pointerPos)));
                ptrStr.append("^"); // The Head Pointer

                if (state != null) {
                    printer.out("\n(State: " + state + ")");
                }
                printer.out(tapeStr.toString());
                printer.out(ptrStr.toString());
            }

            /**
             * Generates a Graphviz DOT file for visualizing automaton.
             * Use <a href="https://dreampuf.github.io/GraphvizOnline">...</a>
             *
             * @param filename     Output filename (e.g., "dfa.dot")
             * @param transitions  Map of Source -> (Symbol -> Destination)
             * @param startState   Name of the start state
             * @param acceptStates Set of accepted state names
             */
            public void exportDotFile(String filename, Map<String, Map<String, String>> transitions, String startState, Set<String> acceptStates) {
                StringBuilder dot = new StringBuilder();
                dot.append("digraph G {\n");
                dot.append("  rankdir=LR;\n");
                dot.append("  node [shape = circle];\n");

                // Mark start state
                dot.append("  __start [shape = none, label = \"\"];\n");
                dot.append("  __start -> \"").append(startState).append("\";\n");

                // Mark accept states
                for (String s : acceptStates) {
                    dot.append("  \"").append(s).append("\" [shape = doublecircle];\n");
                }

                // Transitions
                for (var entry : transitions.entrySet()) {
                    String src = entry.getKey();
                    for (var trans : entry.getValue().entrySet()) {
                        String sym = trans.getKey();
                        String dest = trans.getValue();
                        dot.append("  \"").append(src).append("\" -> \"").append(dest)
                                .append("\" [label = \"").append(sym).append("\"];\n");
                    }
                }
                dot.append("}");

                try {
                    Files.writeString(Path.of(filename), dot.toString());
                    printer.out("[DOT Exported] " + filename);
                } catch (IOException e) {
                    printer.out("[Export Error] " + e.getMessage());
                }
            }
        }
}