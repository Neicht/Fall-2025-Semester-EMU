package Project3.q2.program;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The Ultimate Terminal Interface (UTI for short)
 * 
 * @author Nicholas Gawenda
 * @version 3.1 Swiss Army Knife
 */
public class TerminalInterface {

    // --- Components ---
    private final ConsolePrinter printer;
    private final InputHandler input;
    private final FileTools files;
    private final DebugTools debug;
    private final MenuNode rootMenu;

    public enum Alignment {
        LEFT, CENTER, RIGHT
    };

    private MenuNode currentMenu;
    private boolean isRunning;
    public final AutomataTools automata;

    public TerminalInterface() {
        this.printer = new ConsolePrinter();
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
     * Prints a block of text, automatically wrapping lines to fit the current
     * width.
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

    public void printFancyHistogram(double[] data, String[] labels) {
        printer.printFancyHistogram(data, labels);
    }

    /**
     * Prints a 2D array as a heatmap using ASCII characters.
     *
     * @param data The 2D array of values.
     */
    public void printHeatmap(double[][] data) {
        printer.printHeatmap(data);
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
     * Gets the file system path of the directory containing the given object's
     * class.
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

    public long getToc() {
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

    public int[] generateIntArray(int size, int step, int start) {
        return debug.generateIntArray(size, step, start);
    }

    public double[] generateDoubleArray(int size, double step, double start) {
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
        if (currentMenu.getParent() != null)
            out("0. Back");
        printer.printLine();
    }

    /**
     * Processes user input for the menu system.
     */
    private void handleInput() {
        int inputVal = input.inInt("Select option: ");
        if (inputVal == 0) {
            if (currentMenu.getParent() != null)
                currentMenu = currentMenu.getParent();
            else
                out("Already at root.");
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

    private static class ConsolePrinter {
        private static class State implements Cloneable {
            int width = 46, statWidth = 25, indent = 0;
            char style = '-';
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

        // --- State ---
        void setStatLabelWidth(int w) {
            state.statWidth = w;
        }

        void setLineWidth(int w) {
            state.width = w;
        }

        void setLineStyle(char c) {
            state.style = c;
        }

        void setIndent(int i) {
            state.indent = Math.max(0, i);
        }

        void setAlign(Alignment a) {
            state.align = a;
        }

        void setPrefix(String p) {
            state.prefix = (p == null) ? "" : p;
        }

        void pushState() {
            stack.push(state.clone());
        }

        void popState() {
            if (!stack.isEmpty())
                state = stack.pop();
        }

        void printState() {
            out("--- Printer State ---");
            out(String.format(" W:%d I:%d A:%s P:%s L:%c S:%d", state.width, state.indent, state.align, state.prefix,
                    state.style, state.statWidth));
            printLine();
        }

        // --- Output ---
        void out(String s) {
            String p = " ".repeat(state.indent) + state.prefix;
            for (String line : s.split("\n")) {
                String finalLine = p + line;
                System.out.println(finalLine);
                if (logWriter != null)
                    logWriter.println(finalLine);
            }
            if (logWriter != null)
                logWriter.flush();
        }

        void printLine() {
            out(String.valueOf(state.style).repeat(Math.max(0, state.width)));
        }

        void printSplitter(char c, int w) {
            out(String.valueOf(c).repeat(Math.max(0, w)));
        }

        void printHeader(String h) {
            String t = " " + h + " ";
            int pad = state.width - t.length();
            if (pad < 0) {
                out(t);
                return;
            }
            String s = String.valueOf(state.style);
            String l = s.repeat(2), r = s.repeat(2); // Min padding
            int rem = Math.max(0, pad - 4);
            switch (state.align) {
                case LEFT -> r += s.repeat(rem);
                case RIGHT -> l += s.repeat(rem);
                default -> {
                    l += s.repeat(rem / 2);
                    r += s.repeat(rem - (rem / 2));
                }
            }
            out(l + t + r);
        }

        void printTitle(String t) {
            printLine();
            int pad = Math.max(0, state.width - t.length());
            String sp = " "
                    .repeat(state.align == Alignment.LEFT ? 0 : (state.align == Alignment.RIGHT ? pad : pad / 2));
            out(sp + t);
            printLine();
        }

        void printStat(String k, String... v) {
            out(String.format("%-" + state.statWidth + "s: %s", k, String.join(" | ", v)));
        }

        void printBody(String text) {
            if (text == null)
                return;
            StringBuilder line = new StringBuilder();
            for (String w : text.split("\\s+")) {
                if (line.length() + w.length() + 1 > state.width) {
                    out(line.toString());
                    line.setLength(0);
                }
                if (!line.isEmpty())
                    line.append(" ");
                line.append(w);
            }
            if (!line.isEmpty())
                out(line.toString());
        }

        void printBox(String msg) {
            printSplitter('=', state.width);
            out("  " + msg);
            printSplitter('=', state.width);
        }

        void printTable(String[] headers, String[][] data) {
            if (headers == null || data == null)
                return;
            int[] w = new int[headers.length];
            for (int i = 0; i < headers.length; i++)
                w[i] = headers[i].length();
            for (String[] row : data)
                for (int i = 0; i < row.length && i < w.length; i++)
                    if (row[i] != null)
                        w[i] = Math.max(w[i], row[i].length());

            String fmt = Arrays.stream(w).mapToObj(i -> "| %-" + i + "s ").collect(Collectors.joining()) + "|";
            String sep = "+-" + Arrays.stream(w).mapToObj("-"::repeat).collect(Collectors.joining("-+-")) + "-+";

            out(sep);
            out(String.format(fmt, (Object[]) headers));
            out(sep);
            for (String[] row : data)
                out(String.format(fmt, (Object[]) row));
            out(sep);
        }

        void printProgress(int cur, int tot) {
            int w = state.width - 10;
            int p = (int) ((double) cur / tot * w);
            System.out.print("\r" + " ".repeat(state.indent) + state.prefix + "[" + "=".repeat(p) + " ".repeat(w - p)
                    + "] " + (int) ((double) cur / tot * 100) + "%");
            if (cur == tot)
                System.out.println();
        }

        void printHistogram(double[] data) {
            printFancyHistogram(data, new String[data.length]);
        }

        void printFancyHistogram(double[] data, String[] labels) {
            if (data == null || data.length == 0)
                return;
            double max = Arrays.stream(data).max().orElse(1);
            int barMax = state.width - 15;
            for (int i = 0; i < data.length; i++) {
                String lbl = (labels.length > i && labels[i] != null) ? labels[i] : "";
                out(String.format("%s %-10.4f |%s", lbl, data[i], "#".repeat((int) ((data[i] / max) * barMax))));
            }
        }

        void printHeatmap(double[][] data) {
            if (data == null || data.length == 0)
                return;
            String gradient = " .:-=+*#%@";
            double min = Double.POSITIVE_INFINITY, max = Double.NEGATIVE_INFINITY;
            for (double[] row : data) {
                for (double v : row) {
                    if (v < min)
                        min = v;
                    if (v > max)
                        max = v;
                }
            }
            double range = max - min;
            if (range == 0)
                range = 1;

            out("Heatmap (Range: " + String.format("%.2f", min) + " - " + String.format("%.2f", max) + ")");
            int cols = Arrays.stream(data).mapToInt(r -> r.length).max().orElse(0);
            printSplitter('-', cols * 2 + 2);
            for (double[] row : data) {
                StringBuilder sb = new StringBuilder("|");
                for (double v : row) {
                    int idx = (int) ((v - min) / range * (gradient.length() - 1));
                    sb.append(gradient.charAt(Math.max(0, Math.min(gradient.length() - 1, idx)))).append(" ");
                }
                while (sb.length() < cols * 2 + 1)
                    sb.append("  "); // Pad if jagged
                sb.append("|");
                out(sb.toString());
            }
            printSplitter('-', cols * 2 + 2);
        }

        void startLog(String f) {
            try {
                logWriter = new PrintWriter(new FileWriter(f));
                out("[Log: " + f + "]");
            } catch (IOException e) {
                out("[Log Error]");
            }
        }

        void stopLog() {
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
        private final Queue<String> script = new LinkedList<>();

        InputHandler(Scanner s, ConsolePrinter p) {
            scanner = s;
            printer = p;
        }

        private String raw() {
            if (!script.isEmpty()) {
                String v = script.poll();
                printer.out(v + " (scripted)");
                return v;
            }
            return scanner.nextLine();
        }

        void scriptInput(String... s) {
            Collections.addAll(script, s);
        }

        String inString(String p) {
            printer.out(p);
            return raw();
        }

        String inString(String p, String d) {
            printer.out(p + " [" + d + "]: ");
            String v = raw();
            return v.isEmpty() ? d : v;
        }

        int inInt(String p) {
            printer.out(p);
            while (true) {
                try {
                    return Integer.parseInt(raw());
                } catch (Exception e) {
                    printer.out("Invalid int.");
                }
            }
        }

        int inInt(String p, int min, int max) {
            while (true) {
                int v = inInt(p + " (" + min + "-" + max + "): ");
                if (v >= min && v <= max)
                    return v;
                printer.out("Range error.");
            }
        }

        double inDouble(String p) {
            printer.out(p);
            while (true) {
                try {
                    return Double.parseDouble(raw());
                } catch (Exception e) {
                    printer.out("Invalid double.");
                }
            }
        }

        boolean inBoolean(String p) {
            while (true) {
                String s = inString(p + " (y/n): ").toLowerCase();
                if (s.startsWith("y") || s.equals("1") || s.equals("true"))
                    return true;
                if (s.startsWith("n") || s.equals("0") || s.equals("false"))
                    return false;
            }
        }

        String inChoice(String p, String... opts) {
            printer.out(p);
            for (int i = 0; i < opts.length; i++)
                printer.out(" " + (i + 1) + ". " + opts[i]);
            return opts[inInt("Select: ", 1, opts.length) - 1];
        }

        void pause() {
            inString("Press Enter...");
        }
    }

    // ==================================================================================
    // INNER CLASS: FileTools (IO & Directory)
    // ==================================================================================

    private record FileTools(ConsolePrinter printer, InputHandler input) {
        String readFile(String p) {
            try {
                return Files.readString(Path.of(p));
            } catch (IOException e) {
                return null;
            }
        }

        List<String> readLines(String p) {
            try {
                return Files.readAllLines(Path.of(p));
            } catch (IOException e) {
                return new ArrayList<>();
            }
        }

        void writeFile(String p, String c) {
            try {
                ensureDirectory(p);
                Files.writeString(Path.of(p), c);
                printer.out("[Saved] " + p);
            } catch (IOException e) {
                printer.out("Error: " + e);
            }
        }

        void ensureDirectory(String p) {
            try {
                Path path = Path.of(p);
                if (p.contains(".")) {
                    if (path.getParent() != null)
                        Files.createDirectories(path.getParent());
                } else
                    Files.createDirectories(path);
            } catch (IOException e) {
                printer.out("Dir Error: " + e);
            }
        }

        String findFile(String n) {
            try (Stream<Path> s = Files.walk(Path.of("."))) {
                return s.filter(p -> p.getFileName().toString().equals(n)).findFirst().map(Path::toString).orElse(null);
            } catch (IOException e) {
                return null;
            }
        }

        String chooseFile(String prompt, String ext) {
            try (Stream<Path> s = Files.walk(Path.of("."), 2)) {
                List<String> f = s.filter(p -> Files.isRegularFile(p) && p.toString().endsWith(ext)).map(Path::toString)
                        .toList();
                return f.isEmpty() ? null : input.inChoice(prompt, f.toArray(new String[0]));
            } catch (IOException e) {
                return null;
            }
        }

        String browseFile(String start) {
            Path cur = Path.of(start).toAbsolutePath().normalize();
            if (!Files.exists(cur))
                cur = Path.of(".").toAbsolutePath().normalize();

            while (true) {
                printer.pushState();
                printer.setLineStyle('.');
                printer.printHeader("Browsing: " + cur.getFileName());
                printer.popState();
                File[] files = cur.toFile().listFiles();
                if (files == null) {
                    printer.out("Access Denied.");
                    cur = cur.getParent();
                    continue;
                }
                Arrays.sort(files, (a, b) -> a.isDirectory() != b.isDirectory() ? (a.isDirectory() ? -1 : 1)
                        : a.getName().compareToIgnoreCase(b.getName()));

                List<File> opts = new ArrayList<>();
                printer.out(cur.getParent() != null ? " 1. [UP] .." : " 1. [ROOT]");
                int i = 2;
                for (File f : files) {
                    printer.out(" " + i++ + ". " + (f.isDirectory() ? "[+]" : " - ") + " " + f.getName());
                    opts.add(f);
                }
                printer.out(" 0. Cancel");
                printer.printLine();

                int c = input.inInt("Select: ");
                if (c == 0)
                    return null;
                if (c == 1) {
                    if (cur.getParent() != null)
                        cur = cur.getParent();
                    continue;
                }
                if (c - 2 < opts.size()) {
                    File sel = opts.get(c - 2);
                    if (sel.isDirectory())
                        cur = sel.toPath();
                    else
                        return sel.getAbsolutePath();
                }
            }
        }

        String getClassLocation(Object ctx) {
            try {
                return Paths.get(ctx.getClass().getResource("").toURI()).toString();
            } catch (Exception e) {
                return null;
            }
        }

        void printDirectoryTree(String root) {
            try {
                Files.walkFileTree(Path.of(root), new SimpleFileVisitor<>() {
                    int d = 0;

                    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes a) {
                        printer.out("  ".repeat(d++) + "+ " + dir.getFileName());
                        return FileVisitResult.CONTINUE;
                    }

                    public FileVisitResult visitFile(Path file, BasicFileAttributes a) {
                        printer.out("  ".repeat(d) + "- " + file.getFileName());
                        return FileVisitResult.CONTINUE;
                    }

                    public FileVisitResult postVisitDirectory(Path dir, IOException e) {
                        d--;
                        return FileVisitResult.CONTINUE;
                    }
                });
            } catch (IOException e) {
                printer.out("Tree Error.");
            }
        }
    }
    // ==================================================================================
    // INNER CLASS: DebugTools (Reflect, Timer, Mock)
    // ==================================================================================

    private static class DebugTools {
        private final ConsolePrinter printer;
        private long start;
        private boolean debug = false;

        DebugTools(ConsolePrinter p) {
            printer = p;
        }

        void setDebug(boolean b) {
            debug = b;
            printer.out("[Debug: " + b + "]");
        }

        void log(String m) {
            if (debug)
                printer.out("[DEBUG] " + m);
        }

        void inspect(Object o) {
            if (o == null) {
                printer.out("Null Object");
                return;
            }
            printer.printHeader("Inspect: " + o.getClass().getSimpleName());
            for (Field f : o.getClass().getDeclaredFields()) {
                f.setAccessible(true);
                try {
                    printer.printStat(f.getName(), String.valueOf(f.get(o)));
                } catch (Exception e) {
                    printer.printStat(f.getName(), "[Locked]");
                }
            }
            printer.printLine();
        }

        void tic() {
            start = System.currentTimeMillis();
        }

        long getToc() {
            return System.currentTimeMillis() - start;
        }

        void toc(String l) {
            printer.out(l + getToc() + "ms");
        }

        void guard(Runnable t) {
            try {
                t.run();
            } catch (Exception e) {
                printer.printBox("CRASH: " + e.getClass().getSimpleName());
                printer.out(e.getMessage());
                e.printStackTrace();
            }
        }

        int[] generateRandomIntArray(int s, int min, int max) {
            return new Random().ints(s, min, max + 1).toArray();
        }

        String generateRandomString(int len) {
            String c = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
            StringBuilder sb = new StringBuilder();
            Random r = new Random();
            for (int i = 0; i < len; i++)
                sb.append(c.charAt(r.nextInt(c.length())));
            return sb.toString();
        }

        int[] generateIntArray(int s, int step, int start) {
            int[] a = new int[s];
            for (int i = 0; i < s; i++)
                a[i] = start + (step * i);
            return a;
        }

        double[] generateDoubleArray(int s, double step, double start) {
            double[] a = new double[s];
            for (int i = 0; i < s; i++)
                a[i] = start + (step * i);
            return a;
        }
    }

    // ==================================================================================
    // MENU NODE STRUCTURE
    // ==================================================================================

    public static class MenuNode {
        enum NodeType {
            OPTION, CATEGORY
        }

        final String name, description;
        final NodeType type;
        final Consumer<TerminalInterface> action;
        final List<MenuNode> children;
        MenuNode parent;

        MenuNode(String n, String d, Consumer<TerminalInterface> a) {
            name = n;
            description = d;
            action = a;
            type = NodeType.OPTION;
            children = null;
        }

        MenuNode(String n, String d) {
            name = n;
            description = d;
            action = null;
            type = NodeType.CATEGORY;
            children = new ArrayList<>();
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
            parent = p;
        }

        public void addChild(MenuNode c) {
            if (type == NodeType.CATEGORY) {
                c.setParent(this);
                children.add(c);
            }
        }
    }

    // ==================================================================================
    // INNER CLASS: AutomataTools (DFA/NFA/TM Visualization, DOT File Export)
    // ==================================================================================

    public record AutomataTools(ConsolePrinter printer) {
        public void printTransitionTable(String[] states, String[] alpha, String[][] trans) {
            if (states == null || alpha == null || trans == null)
                return;
            String[] h = new String[alpha.length + 1];
            h[0] = "State";
            System.arraycopy(alpha, 0, h, 1, alpha.length);
            String[][] data = new String[states.length][h.length];
            for (int i = 0; i < states.length; i++) {
                data[i][0] = states[i];
                for (int j = 0; j < alpha.length; j++)
                    data[i][j + 1] = (j < trans[i].length && trans[i][j] != null) ? trans[i][j] : "Ø";
            }
            printer.printTitle("Transition Function (δ)");
            printer.printTable(h, data);
        }

        public void printTape(List<String> tape, int head, String state) {
            StringBuilder t = new StringBuilder("|"), p = new StringBuilder(" ");
            for (String s : tape)
                t.append(String.format(" %-2s|", s));
            p.append(" ".repeat(head * 4 + 2)).append("^");
            if (state != null)
                printer.out("\n(State: " + state + ")");
            printer.out(t.toString());
            printer.out(p.toString());
        }

        public void exportDotFile(String f, Map<String, Map<String, String>> trans, String start, Set<String> acc) {
            StringBuilder dot = new StringBuilder("digraph G {\n  rankdir=LR;\n  node [shape = circle];\n");
            dot.append("  __start [shape = none, label = \"\"];\n  __start -> \"").append(start).append("\";\n");
            for (String s : acc)
                dot.append("  \"").append(s).append("\" [shape = doublecircle];\n");
            trans.forEach((src, m) -> m.forEach((sym, dst) -> dot.append("  \"").append(src).append("\" -> \"")
                    .append(dst).append("\" [label = \"").append(sym).append("\"];\n")));
            dot.append("}");
            try {
                Files.writeString(Path.of(f), dot.toString());
                printer.out("[DOT Exported] " + f);
            } catch (IOException e) {
                printer.out("[Export Error] " + e);
            }
        }
    }
}