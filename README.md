# Gradle Datalyzer

Gradle Datalyzer is a tool for gathering an overview about a Gradle project.

Datalyzer is only intended to be used by JetBrains research participants.

### Usage

1. Download Datalyzer from the GitHub releases page.
2. Unzip Datalyzer into the project directory to be analyzed.
3. Run Datalyzer:

   *Linux/Mac*

    ```shell
    ./datalyzer/bin/datalyzer
    ```

   *Windows*

    ```shell
    .\datalyzer\bin\datalyzer.bat
    ```                      
4. Wait for the report to finish.
5. Share the zipped report.

> [!IMPORTANT]  
> datalyzer must be run with the same Java version used to run Gradle.

### Options

<!-- Do not edit datalyzer-options - they are automatically generated -->

```shell datalyzer-options
Usage: datalyzer [<options>]

Options:
  --projectDir=<path>  Location of the Gradle Project
  --reportsDir=<path>  Output reports directory
  -h, --help           Show this message and exit
```
