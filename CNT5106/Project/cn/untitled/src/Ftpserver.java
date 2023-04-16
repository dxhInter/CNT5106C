import java.awt.*;
import java.net.*;
import java.io.*;
import java.util.*;

import java.awt.Robot;

public class Ftpserver {
	ServerSocket sSocket;   //serversocket used to lisen on port number 8000
	Socket connection = null; //socket for the connection with the client
	String message;    //message received from the client
	String MESSAGE;    //uppercase message send to the client
	ObjectOutputStream out;  //stream write to the socket
	ObjectInputStream in;    //stream read from the socket
	FileOutputStream fileout;
	FileInputStream fileIn;
	Long filesize=0L;
	public void Server() {}
	void run(int port)
	{
		try{
			//create a serversocket
			sSocket = new ServerSocket(port, 10);
			//Wait for connection
			System.out.println("Waiting for connection");
			//accept a connection from the client
			connection = sSocket.accept();
			System.out.println("Connection received from " + connection.getInetAddress().getHostName()+":"+port);
			//initialize Input and Output streams
			out = new ObjectOutputStream(connection.getOutputStream());
			out.flush();
			in = new ObjectInputStream(connection.getInputStream());
			String upload="upload";
			String download="download";
			try {
//				String content = (String)in.readObject();
//				System.out.println("Receive content from the client: " + content);
//				String [] command=content.split(":");
				while (true){
					String content = (String)in.readObject();
					System.out.println("Receive content from the client: " + content);
					String [] command=content.split(":");
					if(Objects.equals(command[0], upload)){
						receiveFile(command[1]);
//						if(fileIn!=null)
//							fileIn.close();
//						aBoolean=true;
//						command[0]=null;
//						delay();
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
//						command[0]=null;
//						delay();
					} else if (Objects.equals(command[0],"quit" )) {
						stop();
//						command[0]=null;
						break;
					}
//					else{
//						System.out.println("wrong command!!!");
//					}
				}
//				if(Objects.equals(command[0], upload)){
//					receiveFile(command[1]);
//					aBoolean=true;
//				}else if(Objects.equals(command[0], download)){
//					File file1=new File(command[1]);
//					boolean tmp=isFileExits(file1);
//					if (tmp) {
//						sendMessage("download file exits");
//						sendFile(command[1]);
//						aBoolean=true;
//					}else {
//						System.out.println("file not exits, please input new file name in client");
//						sendMessage("no download file exits");
//					}
//				} else if (Objects.equals(command[0],"quit" )) {
//					stop();
//				} else{
//					System.out.println("wrong command!!!");
//				}
			}catch (Exception e){
				e.printStackTrace();
			}
		} catch(IOException ioException){
			ioException.printStackTrace();
		}
//		finally{
//			try{
//				if(fileout!=null)
//					fileout.close();
//				if(in!=null)
//					in.close();
//				out.close();
//				sSocket.close();
//			}
//			catch(IOException ioException){
//				ioException.printStackTrace();
//			}
//		}
	}
	void delay() throws Exception{

		Robot r   =   new   Robot();
		r.delay(2000   );
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
	void sendMessage(String msg)
	{
		try{
			out.writeObject(msg);
			out.flush();
			System.out.println("Send message: " + msg);
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
	}
	boolean isFileExits(File file){
		boolean tmp=file.exists();
		return  tmp;
	}
	void stop(){
		try{
			if(fileout!=null)
				fileout.close();
			if(in!=null)
				in.close();
			out.close();
			sSocket.close();
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
	}

	public static void main(String args[]) {
		try {
			Ftpserver s = new Ftpserver();
			int port=Integer.parseInt(args[0]);
//			while(true){
//				s.run(port);
//			}
			s.run(port);
		}catch (Exception e){
			e.printStackTrace();
		}
	}
}
