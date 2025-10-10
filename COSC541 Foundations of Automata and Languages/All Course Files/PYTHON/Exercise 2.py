class Node:
    def __init__(self, label, children=None):
        self.label = label
        self.children = children or []


tree_e1 = Node(".", [Node("*"), [Node("+"), Node("b")], Node(".")])
tree_e2 = Node("*", [Node("+", [Node("a", Node("b"))])])
print(tree_e2.children)
