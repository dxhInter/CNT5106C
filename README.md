#CNT5106C

2023 SPRING\
Based on Java\
Using IDEA\
3 Projects
### How to run it
- open a terminal in src directory to run ```javac chatServer.java``` and run```java chatServer```, then input the name for client such as Alice, and input the port to start the socket server such as 8000.
- open a new terminal in src directory to run ``` javac chatServer.java ``` and and run```java chatServer```, then input the name for client such as Bob, and input a different port to start the socket server such as 8001. Enter the 8000 at Bob to connect with Alice's server, also input 8001 at Alice to connect with Bob's server. 
- under the terminal, use commands or user can send any messages:
    - ```$ upload:filename```
        - Upload one file to other server via FTP, and the file name uploaded to the server will have the prefix "new"
    - ```$ download:filename```
        - Download one file from the server via FTP, and the file download from the server will have the prefix "new"
    - ```$ quit```
        - Stop the socket and quit the program
    - ```$ disconnect```
        - Stop the socket connection.

### Wrong command control
#### Wrong command for upload and download
The program would loop back and ask for different input, for example, the user input ```up:filename```, the program would ask the user to input the right command.
#### Wrong filename
The program would also loop back and ask for the right filename, for example, the client will upload a file and the filename isn't correct, so the program will ask the user to update the filename.
#### Wrong port number
The program would terminate and the console will print the error information about the wrong port.
