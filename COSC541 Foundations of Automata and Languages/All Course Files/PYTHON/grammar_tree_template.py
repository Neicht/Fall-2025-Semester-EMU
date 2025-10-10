from Common.diagonal_tree import print_diagonal_tree

class Node:
    def __init__(self, label, children=None):
        self.label = label
        self.children = children or []

# Right-linear grammar
# S → aS | bA | cB | λ
# A → aA | b
# B → cB | b
grammar = {
    "S": [["a", "S"], ["b", "A"], ["c", "B"], ["λ"]],
    "A": [["a", "A"], ["b"]],
    "B": [["c", "B"], ["b"]],
}


# derivation function to complete
# return (parse_tree, remainder) if successful, else None
def derive(symbol, string):
    pass


if __name__ == "__main__":
    test_strings = ["b", "ab", "aab", "acb", "ccb", "abc", ""]
    for s in test_strings:
        result = derive("S", s)
        print(f"\nInput: '{s}'")
        if result and result[1] == "":
            print("accepted")
            print_diagonal_tree(result[0])
        else:
            print("rejected")