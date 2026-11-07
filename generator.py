
import random

def randomize_case(text: str) -> str:
    """Randomize the casing of each character in text."""
    choices = [
        text.upper(),
        text.lower(),
        text.capitalize(),
        "".join(c.upper() if random.random() < 0.5 else c.lower() for c in text),
    ]
    return random.choice(choices)

def random_whitespace() -> str:
    """Generate various valid whitespace combinations (spaces, tabs, newlines)."""
    options = [
        " ",
        "  ",
        "   ",
        "\t",
        " \t",
        "\t ",
        "\n",
        "\r\n",
        "\n\n",
        "\t\n",
        "\n\t",
        " \n ",
    ]
    return random.choice(options)

def generate_process_file(
    filename: str,
    num_instructions: int = 20,
    page_range: tuple[int, int] = (1, 10),
    write_probability: float = 0.4
):
    """
    Generate an edge-case process file according to COMP2240 Section 2.6:
    - Minimum 1, maximum 50 page references per process (Section 2.2.a)
    - Begin / end markers (case-insensitive)
    - Operation R / W (case-insensitive)
    - Page number (integer)
    - Whitespace separating words: spaces, tabs, CR/LF line breaks, mixed
    - Tokens may be on a single line, across multiple lines, or heavily indented
    """
    tokens: list[str] = []

    # Leading whitespace before begin
    tokens.append(randomize_case("begin"))

    # Generate 1 to 50 instructions
    count = max(1, min(50, num_instructions))
    for _ in range(count):
        op = "W" if random.random() < write_probability else "R"
        op_token = randomize_case(op)
        page_token = str(random.randint(page_range[0], page_range[1]))
        tokens.append(op_token)
        tokens.append(page_token)

    # Closing marker
    tokens.append(randomize_case("end"))

    # Join tokens with diverse whitespace
    content_parts: list[str] = []
    # Optional leading whitespace
    if random.random() < 0.5:
        content_parts.append(random_whitespace())

    for i, token in enumerate(tokens):
        content_parts.append(token)
        if i < len(tokens) - 1:
            content_parts.append(random_whitespace())

    # Optional trailing whitespace
    if random.random() < 0.5:
        content_parts.append(random_whitespace())

    content = "".join(content_parts)

    with open(filename, "w", encoding="utf-8") as f:
        f.write(content)

    print(f"Generated '{filename}' with {count} instructions.")

if __name__ == "__main__":
    # 1. Single-line process (Section 2.6 Note 1: 'begin W 1 R 2 W 3 R 4 R 5 end')
    generate_process_file("gen_single_line.txt", num_instructions=5, page_range=(1, 5))

    # 2. Maximum instructions (50 instructions edge case, Section 2.2.a)
    generate_process_file("gen_max_50.txt", num_instructions=50, page_range=(1, 15))

    # 3. Minimum instructions (1 instruction edge case, Section 2.2.a)
    generate_process_file("gen_min_1.txt", num_instructions=1, page_range=(1, 3))

    # 4. Read-only workload (0 dirty pages, no write-backs)
    generate_process_file("gen_readonly.txt", num_instructions=15, page_range=(1, 8), write_probability=0.0)

    # 5. Write-heavy workload (stress-tests dirty page write-backs and Clock replacement)
    generate_process_file("gen_writeheavy.txt", num_instructions=25, page_range=(1, 12), write_probability=0.85)

    # 6. Default generated.txt with mixed case and varied whitespace
    generate_process_file("generated.txt", num_instructions=20, page_range=(1, 10))

