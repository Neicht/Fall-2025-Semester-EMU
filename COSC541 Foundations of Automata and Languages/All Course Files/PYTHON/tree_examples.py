from Common.diagonal_tree import print_diagonal_tree


# Tree node class
class Node:
    def __init__(self, label, children=None):
        self.label = label
        self.children = children or []


def printregex(node):
    if not node.children:
        print(node.label, end="")
        return
    elif not node.children[0]:
        print(node.label, end="")
    elif not node.children[1]:
        print(node.label, end="")
        return
    else:
        printregex(node.children[0])
        node.children[0] = []
        printregex(node)
        printregex(node.children[1])
        node.children[1] = []


# e1: (a+b)*ab
tree_e1 = Node("·", [
    Node("*", [
        Node("+", [Node("a"), Node("b")])
    ]),
    Node("·", [Node("a"), Node("b")])
])

# e2: (a + b)*
tree_e2 = Node("*", [
    Node("+", [Node("a"), Node("b")])
])

# e3: (a + b)*abb
tree_e3 = Node("·", [
    Node("*", [
        Node("+", [Node("a"), Node("b")])
    ]),
    Node("·", [
        Node("a"),
        Node("·", [
            Node("b"),
            Node("b")
        ])
    ])
])

tree_e3_2 = Node("·", [
    Node("·", [
        Node("·", [
            Node("*", [Node("+", [Node("a"), Node("b")])]),
            Node("a")
        ]),
        Node("b")
    ]),
    Node("b")
])

tree_e3_3 = Node("·", [
    Node("·", [
        Node("*", [Node("+", [Node("a"), Node("b")])]),
        Node("a")
    ]),
    Node("·", [
        Node("b"),
        Node("b")
    ])
])

tree_e4_1 = Node(".", [
    Node("*", [
        Node("+", [Node("a"), Node("b")])

    ]), Node("c")
])

tree_e4_2 = Node(".", [
    Node("+", [Node("a"), Node("b")]),
    Node("+", [Node("a"), Node("b")])
])

tree_e4_3 = Node(".", [
    Node("a"),
    Node("*", [
        Node("+", [
            Node("b"), Node("c")
        ])]
         )])

# print the tree
print_diagonal_tree(tree_e4_1)
print("----------------")
print_diagonal_tree(tree_e4_2)
print("----------------")
print_diagonal_tree(tree_e4_3)
printregex(tree_e4_2)

