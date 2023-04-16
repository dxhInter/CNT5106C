import java.net.*;
import java.io.*;
import java.util.*;

public class Ftpserver {

//	private static final int sPort = 8000;   //The server will be listening on this port number
	private static ServerSocket listener;
	public static void main(String[] args) throws Exception {
		System.out.println("The server is running.");
		int sPort=Integer.parseInt(args[0]);
		listener = new ServerSocket(sPort);
		int clientNum = 1;
//        	try {
//				while(true) {
//					new Handler(listener.accept(),clientNum).start();
//					System.out.println("Client "  + clientNum + " is connected!");
//					clientNum++;
//				}
//        	}finally {
//				listener.close();
//        	}
		while(true) {
			new Handler(listener.accept(),clientNum).start();
			System.out.println("Client "  + clientNum + " is connected!");
			clientNum++;
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
        	private ObjectInputStream in;	//stream read from the socket
        	private ObjectOutputStream out;    //stream write to the socket
			FileOutputStream fileout;
			FileInputStream fileIn;
			Long filesize=0L;
			private int no;		//The index number of the client

        	public Handler(Socket connection, int no) {
            		this.connection = connection;
	    		this.no = no;
        	}
        public void run() {
 		try{
			//initialize Input and Output streams
			out = new ObjectOutputStream(connection.getOutputStream());
			out.flush();
			in = new ObjectInputStream(connection.getInputStream());
			String upload="upload";
			String download="download";
			try{
				while(true)
				{
					String content = (String)in.readObject();
					System.out.println("Receive content from the client: " + content);
					String [] command=content.split(":");
					if(Objects.equals(command[0], upload)){
						receiveFile(command[1]);
					}else if(Objects.equals(command[0], download)){
						File file1=new File(command[1]);
						boolean tmp=isFileExits(file1);
						filesize=file1.length();
						String size=Long.toString(filesize);
						if (tmp) {
							sendMessage(size);
							sendFile(command[1]);
//							aBoolean=true;
							if(fileIn!=null)
								fileIn.close();
						}else {
							System.out.println("file not exits, please input new file name in client");
							sendMessage("no download file exits");
						}
					} else if (Objects.equals(command[0],"quit" )) {
						try{
							if(fileout!=null)
								fileout.close();
							if(in!=null)
								in.close();
							out.close();
							connection.close();
						}catch(IOException ioException){
							System.out.println("Disconnect with Client " + no);
						}
						break;
					}else if (Objects.equals(command[0],"disconnect" )) {
						if(fileout!=null)
							fileout.close();
						if(in!=null)
							in.close();
						out.close();
						connection.close();
						listener.close();
						System.out.println("Server has been closed");
						break;
					}
				}
			}catch(ClassNotFoundException classnot){
					System.err.println("Data received in unknown format");
			}
		}catch(IOException ioException){
			System.out.println("Disconnect with Client " + no);
			ioException.printStackTrace();
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	//send a message to the output stream
		public void sendMessage(String msg){
			try{
				out.writeObject(msg);
				out.flush();
				System.out.println("Send message: " + msg + " to Client " + no);
			}catch(IOException ioException){
				ioException.printStackTrace();
			}
		}
		void receiveFile(String filename) throws Exception{
			try {
//			String filename="newuploadTestFile.pptx";
////				long length=in.readLong();
//			File filepath = new File("src");
//			if(!filepath.exists()){
//				filepath.mkdir();
//			}
//			File file=new File(filepath.getAbsolutePath() + File.separatorChar + "new"+filename);
				filename = filename.substring(0, 1).toUpperCase() + filename.substring(1);
				File file=new File("new"+filename);
				fileout=new FileOutputStream(file);
				byte[] bytes = new byte[1024];
				int flag = 0;
				while((flag = in.read(bytes, 0, bytes.length)) != -1) {
					fileout.write(bytes, 0, flag);
					fileout.flush();
				}
				System.out.println("Received file: "+filename);
			}catch (Exception e){
				e.printStackTrace();
			}
		}
		void sendFile(String filename) throws Exception{
			try {
//			File file=new File("src"+File.separatorChar+filename);
				filename = filename.substring(0, 1).toUpperCase() + filename.substring(1);
				File file=new File(filename);
				if (file.exists()){
					fileIn = new FileInputStream(file);
					System.out.println("start send the file...");
					byte[] bytes = new byte[1024];
					int length = 0;
					while((length = fileIn.read(bytes, 0, bytes.length)) != -1) {
						out.write(bytes, 0, length);
						out.flush();
					}
					System.out.println("...wait");
					System.out.println("send the file successfully!!!");
					fileIn.close();
				}else{
					System.out.println("no file!!!!");
				}
			}catch (Exception e){
				e.printStackTrace();
			}
		}
		boolean isFileExits(File file){
			boolean tmp=file.exists();
			return  tmp;
		}
    }
}
