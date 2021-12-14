# Flaming Arrows #
# AKA - Game of Amazons #

This project is a JavaFX application that allows the user to play the <a href="https://en.wikipedia.org/wiki/Game_of_the_Amazons">Game of Amazons</a>.
It can be played against another user on the same computer or against an AI.

### Prerequisites

* Any OpenJDK 17 Installation ([download from AdoptOpenJDK](https://adoptopenjdk.net)) 
* To create the Windows installer, you need to have the WIX toolset installed (https://wixtoolset.org)

### Environment

The environment variable `JAVA_HOME` must be set so the build script knows where to find the java installation
for the jpackage tool.

### Building the Project

Call `mvn clean install` on the root project to create the installer.

Call `mvn javafx:run` on the main-ui project to run the app in your IDE.
  
