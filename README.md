# About

This project will contain a java cli application tool
that will be used to automatically update `chromedriver`
to a version that matches the installed version of `Google Chrome`.

This is done as there's no native way to do this so ideally this 
tool can be used alongside a cron job to ensure that everytime 
chromedriver is used, it is the correct version.

## Building

To build the project, you can use the following command:

```bash
./mvnw clean package
```

This will create a JAR file in the `target` directory that
is executable and can be run with the following command:

```bash
java -jar target/chromedriver-updater-<version>.jar -h
```

## Usage

To use the tool, you can run the following command:

```bash
java -jar target/chromedriver-updater-<version>.jar -c <path-to-chrome> -d <path-to-chromedriver>
```

## Supported platforms

- Linux
- Windows


### Todo list
* Detect chrome version ✅
  * On windows ✅
  * On linux ✅
* Obtain the version from chromedriver
* Compare the version and detect if an update is needed
* Download and replace the chromedriver binary
* Add support for macOS
* Check if native compilation is possible and if so add it.