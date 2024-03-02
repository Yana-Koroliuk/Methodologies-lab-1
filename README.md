# Methodologies-lab-1

This console application converts Markdown files into HTML fragments. The generated HTML markup can be output to standard output (stdout) or written to an output file if provided with the `--out` argument. Additionally, the application performs checks for common Markdown errors, such as nested or unbalanced markup tags. If it encounters incorrect Markdown due to nesting issues or unbalanced tags, it will output an error to the standard error (stderr) and terminate with a non-zero exit code.

## Prerequisites
> **NOTE:** You need to have Java installed on your system to run this application. Visit [Java's official website](https://www.java.com/download/) to download and install Java.

## Installation
1. Clone the repository from GitHub:
```bash
git clone https://github.com/Yana-Koroliuk/Methodologies-lab-1.git
cd Methodologies-lab-1
```
## Usage
1. Compile the Java files:
```bash
javac src/main/java/com/koroliuk/app/*.java
```
2. Run the compiled Main class:
> **NOTE:** If you omit the --out argument, the output will be directed to standard output (stdout).
```bash
java -cp src/main/java com.koroliuk.app.Main /path/to/markdownfile.txt --out /path/to/output.html
```
## Revert commit

### [Revert commit](https://github.com/Yana-Koroliuk/Methodologies-lab-1/commit/f39ee4ea8f239e36bb06840274cdcb0d85d56f80)