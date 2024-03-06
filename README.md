# Gradle Project Data Extractor

Gradle Project Data Extractor (gpde) is a tool for gathering an overview about a Gradle project.

gpde is only intended to be used by JetBrains research participants.

### Usage

1. Download gpde from the GitHub releases page.
2. Unzip gpde into the project directory to be analyzed.
3. Run gpde:

   > [!IMPORTANT]  
   > gpde must be run with the same Java version used to run Gradle

   *Linux/Mac*

    ```shell
    ./gpde/bin/gpde
    ```

   *Windows*

    ```shell
    .\gpde\bin\gpde.bat
    ```                      
4. Wait for the report to finish.
5. Share the zipped report.

### Options

<!-- Do not edit gpde-options - they are automatically generated -->

```shell gpde-options
Usage: gpde [<options>]

Options:
  --projectDir=<path>  Location of the Gradle Project
  --reportsDir=<path>  Output reports directory
  -h, --help           Show this message and exit
```
