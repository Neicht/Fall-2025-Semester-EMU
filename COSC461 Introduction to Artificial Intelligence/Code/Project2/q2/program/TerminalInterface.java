package Project2.q2.program;

import java.util.ArrayList;
import java.util.function.Function;


public class TerminalInterface {
    private ArrayList<Option> options;

    public static class Option {
        private String name;
        private String description;

        public Option() {
            this.name = null;
            this.description = null;
        }

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public void getDescription() {
            System.out.println(this.description);
        }


    }
    private <T, R> R fun(T value, Function<T, R> function) {
        return function.apply(value);
    }

    private void out(String s) {
        System.out.println(s);
    }

    private void in(String s) {
        int input = Integer.parseInt(s);
        if (input < 0 || input >= options.size()) {
            out("Invalid input");
        } else {
            options.get(input).getDescription();
        }

    }
}
