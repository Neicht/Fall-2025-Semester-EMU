package automata.treedemo;

import java.util.Arrays;
import java.util.List;


public class TreeExamples {
	
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

    public static void main(String[] args) {
        // e1: (a+b)*ab
        Node tree_e1 = new Node("·",
            new Node("*",
                new Node("+", new Node("a"), new Node("b"))
            ),
            new Node("·", new Node("a"), new Node("b"))
        );

        // e2: (a + b)*
        Node tree_e2 = new Node("*",
            new Node("+", new Node("a"), new Node("b"))
        );

        // e3: (a + b)*abb
        Node tree_e3 = new Node("·",
            new Node("*",
                new Node("+", new Node("a"), new Node("b"))
            ),
            new Node("·",
                new Node("a"),
                new Node("·",
                    new Node("b"),
                    new Node("b")
                )
            )
        );

        // e3_2: (a + b)*abb
        Node tree_e3_2 = new Node("·",
            new Node("·",
                new Node("·",
                    new Node("*", new Node("+", new Node("a"), new Node("b"))),
                    new Node("a")
                ),
                new Node("b")
            ),
            new Node("b")
        );

        // e3_3: (a + b)*abb
        Node tree_e3_3 = new Node("·",
            new Node("·",
                new Node("*", new Node("+", new Node("a"), new Node("b"))),
                new Node("a")
            ),
            new Node("·",
                new Node("b"),
                new Node("b")
            )
        );

        // print tree
        DiagonalTree.printTree(tree_e1);
    }

}
