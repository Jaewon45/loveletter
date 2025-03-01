#!/bin/bash

# Define the output file
OUTPUT_FILE="temp.txt"

# Clear the file if it exists
> "$OUTPUT_FILE"

# Find all .java files in subdirectories and append them to temp.txt
find . -type f -name "*.java" | while read -r file; do
    echo "===== CONTENTS OF: $file =====" >> "$OUTPUT_FILE"
    cat "$file" >> "$OUTPUT_FILE"
    echo -e "\n\n" >> "$OUTPUT_FILE"
done

echo "All .java files have been collected into $OUTPUT_FILE."
