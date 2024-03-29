# Gradle Datalyzer

Gradle Datalyzer is a tool for gathering an overview of a Gradle project.

Datalyzer is only intended to be used by JetBrains research participants.

### Usage

##### Quickstart

*Linux/Mac*

```shell
# cd into the project directory to analyze
cd /path/to/gradle-project/

# download and unzip the latest datalyzer release
curl -LO https://github.com/adam-enko/gradle-datalyzer/releases/latest/download/datalyzer.zip
unzip datalyzer.zip

# run datalyzer
./datalyzer/bin/datalyzer

# After datalyzer has finished, please share the zip file in ./datalyzer-reports/
```

### Instructions

> [!IMPORTANT]  
> Datalyzer must be run with the same Java version used to run Gradle,
> otherwise the project's build scripts might not be run correctly.
>
> The easiest way to do this is to run Datalyzer in the same directory
> as the Gradle project being analyzed.

1. Download the latest `datalyzer.zip` from
   [the GitHub releases page](https://github.com/adam-enko/gradle-datalyzer/releases).

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

### Options

<!-- Do not edit datalyzer-options - they are automatically generated -->

```shell datalyzer-options
Usage: datalyzer [<options>]

Options:
  --projectDir=<path>  Location of the Gradle Project
  --reportsDir=<path>  Output reports directory
  -h, --help           Show this message and exit
```

### Captured data

Datalyzer uses the Gradle Tooling API to capture

* build scripts
* basic task output
* Gradle events

An example report is available in [./docs/example-report/](./docs/example-report/).
