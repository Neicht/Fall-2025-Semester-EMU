# tree printer using diagonal edges

def build_ascii_tree(node):
    if node is None:
        return [], 0, 0, 0

    label = str(node.label)

    # leaf node
    if not getattr(node, "children", None):
        width = len(label)
        return [label], width, 1, width // 2

    # build left and right subtrees
    left_lines, lw, lh, lm = build_ascii_tree(node.children[0])
    if len(node.children) > 1:
        right_lines, rw, rh, rm = build_ascii_tree(node.children[1])
    else:
        right_lines, rw, rh, rm = [], 0, 0, 0

    s = label
    s_len = len(s)

    # center label between children
    first_line = (" " * (lm + 1)) + s + (" " * (rw - rm))
    second_line = " " * lm + "/" + " " * s_len + " " * (rw - rm + 1) + "\\"

    # normalize heights
    height = max(lh, rh)
    left_lines += [" " * lw] * (height - lh)
    right_lines += [" " * rw] * (height - rh)

    # merge subtrees
    combined = [l + " " * (s_len + 2) + r for l, r in zip(left_lines, right_lines)]

    lines = [first_line, second_line] + combined
    total_width = lw + s_len + rw + 2
    middle = lw + s_len // 2
    return lines, total_width, height + 2, middle


def print_diagonal_tree(node):
    lines, *_ = build_ascii_tree(node)
    for line in lines:
        print(line.rstrip())