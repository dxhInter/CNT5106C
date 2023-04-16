# CNT5106-Implementation of FTP client and server

Programming language: Java\
Project time: Feb 2023\

### How to run it
- open a terminal in src directory to run ```javac Ftpserver.java``` and choose a port such as 8080 to run```java Ftpserver 8080```
- open a new terminal in src directory to run ``` javac Client.java``` and choose the same port as the Ftpserver such as 8080 to run```java Client 8080```
- under client terminal, use commands:
    - ```$ upload:filename```
        - Upload one file from the client to the server via FTP, and the file name uploaded to the server will have the prefix "new"
    - ```$ download:filename```
        - Download one file from the server via FTP, and the file download from the server will have the prefix "new"

### Wrong command control
#### Wrong command for upload and download
The program would loop back and ask for different input, for example, the user input ```uploa:filename```, the program would ask the user to input the right command.
#### Wrong filename
The program would also loop back and ask for the right filename, for example, the client will upload a file and the filename isn't correct, so the program will ask the user to update the filename.
#### Wrong port number
The program would terminate and the console will print the error information about the wrong port.