package automata.treedemo;

import java.util.*;

/**
 * Template: Build a regular grammar derivation tree
 * -----------------------------------------------
 * Right-linear grammar:
 *   S → aS | bA | cB | λ
 *   A → aA | b
 *   B → cB | b
 *
 * Your task:
 *   - Implement the derive() method below.
 *   - For each input string, determine if it can be derived from S.
 *   - If accepted, print the parse tree using DiagonalTree.printTree().
 */

public class GrammarTreeTemplate {
	// -------------------- Tree Node --------------------
    public static class Node implements DiagonalTree.TreeNode {
        private final String label;
        private final List<Node> children;

        public Node(String label, Node... children) {
            this.label = label;
            this.children = Arrays.asList(children);
        }

        @Override
        public String getLabel() {
            return label;
        }

        @Override
        public List<? extends DiagonalTree.TreeNode> getChildren() {
            return children;
        }
    }

    private static final Map<String, List<List<String>>> GRAMMAR = new HashMap<>();

    static {
        GRAMMAR.put("S", Arrays.asList(
                Arrays.asList("a", "S"),
                Arrays.asList("b", "A"),
                Arrays.asList("c", "B"),
                Arrays.asList("λ")
        ));
        GRAMMAR.put("A", Arrays.asList(
                Arrays.asList("a", "A"),
                Arrays.asList("b")
        ));
        GRAMMAR.put("B", Arrays.asList(
                Arrays.asList("c", "B"),
                Arrays.asList("b")
        ));
    }

    // helper class to store results (tree + remaining string)
    private static class Result {
        Node tree;
        String remainder;
        Result(Node tree, String remainder) {
            this.tree = tree;
            this.remainder = remainder;
        }
    }

    // derive() to be completed
    // return (parse_tree, remainder) if successful, else null
    private static Result derive(String symbol, String string) {
        return null;
    }

    public static void main(String[] args) {
        String[] testStrings = {"b", "ab", "aab", "acb", "ccb", "abc", ""};

        for (String s : testStrings) {
            Result result = derive("S", s);
            System.out.println("\nInput: '" + s + "'");
            if (result != null && result.remainder.isEmpty()) {
                System.out.println("accepted");
                DiagonalTree.printTree(result.tree);
            } else {
                System.out.println("rejected");
            }
        }
    }

}
