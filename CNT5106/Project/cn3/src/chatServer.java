import java.net.*;
import java.io.*;
import java.util.Objects;
import java.util.Scanner;

public class chatServer {


    private static ServerSocket listener;
    private static Socket requestSocket;

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        //System.out.println("please input name");
        System.out.println("please input your name");
        String content = bufferedReader.readLine();
        System.out.println("please input your port");
        int sPortA = scanner.nextInt();
        listener = new ServerSocket(sPortA);
        System.out.println("The server is running" + sPortA);
        //System.out.println("Client " + clientNum + " is connected!");
        System.out.println("please print the port you want to connect with");
        int port = scanner.nextInt();
        requestSocket = new Socket("localhost", port);
        new HandlerWrite(requestSocket, port, content).start();

        new Handler(listener.accept(), port, content).start();
    }

    private static class HandlerWrite extends Thread {
        private static Socket requestSocket;
        ObjectOutputStream out;         //stream write to the socket
        ObjectInputStream in;          //stream read from the socket
        String name;                //message send to the server
        String MESSAGE;                //capitalized message read from the server
        int portWrite;
        FileInputStream fileIn;
        FileOutputStream fileout;
        Boolean aBoolean = false;

        public HandlerWrite(Socket requestSocket, int port ,String name) {
            this.requestSocket = requestSocket;
            this.portWrite = port;
            this.name = name;
        }

        public void run() {
            try {
                //create a socket to connect to the server
                System.out.println("Connected to localhost in port " + portWrite);
                //initialize inputStream and outputStream
                out = new ObjectOutputStream(requestSocket.getOutputStream());
                out.flush();
                in = new ObjectInputStream(requestSocket.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
                String upload = "upload";
                String download = "download";
                while (true) {
                    System.out.println("Hello, please send a message, or print some command like \n" +
                            "upload:uploadTestFile.pptx, download:downloadTestFile.pptx, quit, disconnect: ");
                    String content = bufferedReader.readLine();
                    String[] command = content.split(":");
                    if (Objects.equals(command[0], upload)) {
                        File file1 = new File(command[1]);
                        boolean tmp = isFileExits(file1);
                        if (tmp) {
                            sendMessage(content);
                            sendFile(command[1]);
                        } else {
                            System.out.println("file not exits, please input new file name");
                        }
                    } else if (Objects.equals(command[0], download)) {
                        sendMessage(content);
                        String tmp = (String) in.readObject();
                        System.out.println("received massage from server: " + tmp);
                        if (Objects.equals(tmp, "no download file exits")) {
                            System.out.println("file not exits, please input new file name");
                        } else {
                            Long filelength = Long.parseLong(tmp.trim());
                            receiveFile(command[1], filelength);
                        }
//						aBoolean=true;
                    } else if (Objects.equals(command[0], "quit")) {
                        sendMessage(content);
                        aBoolean = true;
                        try {
                            if (fileIn != null)
                                fileIn.close();
                            in.close();
                            if (out != null)
                                out.close();
                            requestSocket.close();
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                        break;
                    } else if (Objects.equals(command[0], "disconnect")) {
                        sendMessage(content);
                        aBoolean = true;
                        try {
                            if (fileIn != null)
                                fileIn.close();
                            in.close();
                            if (out != null)
                                out.close();
                            requestSocket.close();
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                        break;
                    } else {
//                        System.out.println("Wrong command! Please input again!");
                        sendMessage(name + "： " +content);
                    }
                }
            } catch (ConnectException e) {
                System.err.println("Connection refused. You need to initiate a server first.");
            } catch (ClassNotFoundException e) {
                System.err.println("Class not found");
            } catch (UnknownHostException unknownHost) {
                System.err.println("You are trying to connect to an unknown host!");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        void sendMessage(String msg) {
            try {
                out.writeObject(msg);
                out.flush();
                System.out.println("Send message: " + msg);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

        void sendFile(String filename) throws Exception {
            try {
//			File file=new File("src"+File.separatorChar+filename);
                filename = filename.substring(0, 1).toUpperCase() + filename.substring(1);
                File file = new File(filename);
                if (file.exists()) {

                    fileIn = new FileInputStream(file);
                    System.out.println("start upload...");
                    byte[] bytes = new byte[1024];
                    int length = 0;
                    while ((length = fileIn.read(bytes, 0, bytes.length)) != -1) {
                        out.write(bytes, 0, length);
                        out.flush();
                    }
                    System.out.println("...wait");
                    System.out.println("upload successfully!!!");
                    sendMessage(name + " just sent " + filename);
                } else {
                    System.out.println("no file exists!!!!");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        void receiveFile(String filename, Long le) throws Exception {
            try {
                filename = filename.substring(0, 1).toUpperCase() + filename.substring(1);
                File file = new File("new" + filename);
                fileout = new FileOutputStream(file);
                byte[] bytes = new byte[1024];
                int flag = 0;
                while ((flag = in.read(bytes, 0, bytes.length)) != -1) {
                    fileout.write(bytes, 0, flag);
                    fileout.flush();
                    if (file.length() == le) {
                        break;
                    }
                }
                System.out.println("Download the file successfully: " + filename);
                fileout.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        boolean isFileExits(File file) {
            boolean tmp = file.exists();
            return tmp;
        }
    }


    /**
     * A handler thread class.  Handlers are spawned from the listening
     * loop and are responsible for dealing with a single client's requests.
     */
    private static class Handler extends Thread {
        private String message;    //message received from the client
        private String MESSAGE;    //uppercase message send to the client
        private Socket connection;
        private ObjectInputStream in;    //stream read from the socket
        private ObjectOutputStream out;    //stream write to the socket
        FileOutputStream fileout;
        FileInputStream fileIn;

        String name;
        Long filesize = 0L;
        private int no;        //The index number of the client

        public Handler(Socket connection, int no, String name) {
            this.connection = connection;
            this.no = no;
            this.name = name;
        }

        public void run() {
            try {
                //initialize Input and Output streams
                out = new ObjectOutputStream(connection.getOutputStream());
                out.flush();
                in = new ObjectInputStream(connection.getInputStream());
                String upload = "upload";
                String download = "download";
                try {
                    while (true) {
                        String content = (String) in.readObject();
                        //System.out.println("Receive content from the client: " + content);
                        String[] command = content.split(":");
                        if (Objects.equals(command[0], upload)) {
                            receiveFile(command[1]);
                        } else if (Objects.equals(command[0], download)) {
                            File file1 = new File(command[1]);
                            boolean tmp = isFileExits(file1);
                            filesize = file1.length();
                            String size = Long.toString(filesize);
                            if (tmp) {
                                sendMessage(size);
                                sendFile(command[1]);
//							aBoolean=true;
                                if (fileIn != null)
                                    fileIn.close();
                            } else {
                                System.out.println("file not exits, please input new file name in client");
                                sendMessage("no download file exits");
                            }
                        } else if (Objects.equals(command[0], "quit")) {
                            try {
                                if (fileout != null)
                                    fileout.close();
                                if (in != null)
                                    in.close();
                                out.close();
                                connection.close();
                            } catch (IOException ioException) {
                                System.out.println("Disconnect with Client " + no);
                            }
                            break;
                        } else if (Objects.equals(command[0], "disconnect")) {
                            if (fileout != null)
                                fileout.close();
                            if (in != null)
                                in.close();
                            out.close();
                            connection.close();
                            listener.close();
                            System.out.println("Server has been closed");
                            break;
                        } else {
                            System.out.println(("Receive content \n " + content));

                            System.out.println("Hello, please input a sentence, or print some command like \t" +
                                    "upload:uploadTestFile.pptx, download:downloadTestFile.pptx, quit, disconnect: ");
                        }
                    }
                } catch (ClassNotFoundException classnot) {
                    System.err.println("Data received in unknown format");
                }
            } catch (IOException ioException) {
                System.out.println("Disconnect with Client " + no);
                ioException.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //send a message to the output stream
        public void sendMessage(String msg) {
            try {
                out.writeObject(msg);
                out.flush();
                System.out.println("Send message: " + msg + " to Client " + no);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

        void receiveFile(String filename) throws Exception {
            try {
//			String filename="newuploadTestFile.pptx";
////				long length=in.readLong();
//			File filepath = new File("src");
//			if(!filepath.exists()){
//				filepath.mkdir();
//			}
//			File file=new File(filepath.getAbsolutePath() + File.separatorChar + "new"+filename);
                filename = filename.substring(0, 1).toUpperCase() + filename.substring(1);
                File file = new File("new" + filename);
                fileout = new FileOutputStream(file);
                byte[] bytes = new byte[1024];
                int flag = 0;
                while ((flag = in.read(bytes, 0, bytes.length)) != -1) {
                    fileout.write(bytes, 0, flag);
                    fileout.flush();
                }
                System.out.println("Received file: " + filename);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        void sendFile(String filename) throws Exception {
            try {
//			File file=new File("src"+File.separatorChar+filename);
                filename = filename.substring(0, 1).toUpperCase() + filename.substring(1);
                File file = new File(filename);
                if (file.exists()) {
                    fileIn = new FileInputStream(file);
                    System.out.println("start send the file...");
                    byte[] bytes = new byte[1024];
                    int length = 0;
                    while ((length = fileIn.read(bytes, 0, bytes.length)) != -1) {
                        out.write(bytes, 0, length);
                        out.flush();
                    }
                    System.out.println("...wait");
                    System.out.println("send the file successfully!!!");
                    fileIn.close();
                } else {
                    System.out.println("no file!!!!");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        boolean isFileExits(File file) {
            boolean tmp = file.exists();
            return tmp;
        }
    }
}

