# JPrinter - Java File Printing Application

## Introduction
JPrinter is a Java application that monitors a specified folder for the creation of text files and automatically sends them for printing using a selected printer. This project is built with JavaFX and leverages Java's WatchService for monitoring file changes and Java Print Service for printing.

## Features
- Monitors a specified folder for new text files.
- Automatically prints the contents of the text files.
- Allows users to choose a printer from the available list.
- Provides a simple user interface for configuration and control.
- Displays status indicators for monitoring state.

## Getting Started
To use JPrinter, follow these steps:

1. Clone the repository or download the project source code.

2. Open the project in your Java development environment (e.g., Eclipse, IntelliJ IDEA).

3. Build and run the project.

4. Configure your printer by selecting it from the available printers in the dropdown.

5. Choose the folder you want to monitor by clicking the "Choose Folder" button.

6. Click the "Start Monitoring" button to begin monitoring the folder for new text files.

7. When a new text file is created in the monitored folder, its contents will be automatically sent to the selected printer for printing.

8. You can stop the monitoring process by clicking the "Stop Monitoring" button.

## Dependencies
JPrinter uses the following dependencies:

- JavaFX: The JavaFX library for creating the user interface.
- Java Print Service: Used for printer discovery and print job management.

## Usage
- The main class is `MainScreenController`, which contains the application logic.
- The application provides a simple user interface with buttons and a choice box for printer selection.

## Contributing
Contributions to JPrinter are welcome! If you have any suggestions, improvements, or bug fixes, please feel free to open an issue or submit a pull request on the project's GitHub repository.

## License
This project is licensed under the MIT License. See the [LICENSE](https://github.com/your-username/your-project/blob/main/LICENSE) file for details.

## Acknowledgments
- This project was inspired by the need for a simple file-to-print solution.
- Thanks to the Java community for providing the necessary libraries and tools.

---
