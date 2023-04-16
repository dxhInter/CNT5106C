import java.net.*;
import java.io.*;
import java.util.*;

public class Ftpclient {
	Socket requestSocket;           //socket connect to the server
	ObjectOutputStream out;         //stream write to the socket
	ObjectInputStream in;          //stream read from the socket
	String message;                //message send to the server
	String MESSAGE;                //capitalized message read from the server
	FileInputStream fileIn;
	FileOutputStream fileout;
	public void Client() {}
	Boolean aBoolean=false;
	void run(int port)
	{
		try{
			//create a socket to connect to the server
			requestSocket = new Socket("localhost", port);
			System.out.println("Connected to localhost in port "+port);
			//initialize inputStream and outputStream
			out = new ObjectOutputStream(requestSocket.getOutputStream());
			out.flush();
			in = new ObjectInputStream(requestSocket.getInputStream());

			//get Input from standard input
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
			String upload="upload";
			String download="download";
			while(true){
				System.out.print("Hello, please input a sentence, such as upload:uploadTestFile.pptx,upload:uploadTestFile1.pptx,upload:uploadTestFile2.pptx or download:downloadTestFile.pptx,download:downloadTestFile1.pptx,download:downloadTestFile2.pptx or quit or disconnect: ");
				String content=bufferedReader.readLine();
				String [] command=content.split(":");
				if(Objects.equals(command[0], upload)){
					File file1=new File(command[1]);
					boolean tmp=isFileExits(file1);
					if (tmp){
						sendMessage(content);
						sendFile(command[1]);
					}else {
						System.out.println("file not exits, please input new file name");
					}
				}else if(Objects.equals(command[0], download)){
					sendMessage(content);
					String tmp=(String)in.readObject();
					System.out.println("received massage from server: "+tmp);
					if(Objects.equals(tmp, "no download file exits")){
						System.out.println("file not exits, please input new file name");
					}else{
						Long filelength=Long.parseLong(tmp.trim());
						receiveFile(command[1],filelength);
					}
//						aBoolean=true;
				} else if (Objects.equals(command[0],"quit" )) {
					sendMessage(content);
					aBoolean=true;
					stop();
					break;
				}else if (Objects.equals(command[0],"disconnect" )) {
					sendMessage(content);
					aBoolean=true;
					stop();
					break;
				}
				else{
					System.out.println("Wrong command! Please input again!");
				}
			}
		}
		catch (ConnectException e) {
			System.err.println("Connection refused. You need to initiate a server first.");
		}
		catch ( ClassNotFoundException e ) {
			System.err.println("Class not found");
		}
		catch(UnknownHostException unknownHost){
			System.err.println("You are trying to connect to an unknown host!");
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
//		finally{
//			//Close connections
//			try{
//				if(fileIn!=null)
//					fileIn.close();
//				in.close();
//				if(out!=null)
//					out.close();
//				requestSocket.close();
//			}
//			catch(IOException ioException){
//				ioException.printStackTrace();
//			}
//		}
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
	void sendFile(String filename) throws Exception{
		try {
//			File file=new File("src"+File.separatorChar+filename);
			filename = filename.substring(0, 1).toUpperCase() + filename.substring(1);
			File file=new File(filename);
			if (file.exists()){
				fileIn = new FileInputStream(file);
				System.out.println("start upload...");
				byte[] bytes = new byte[1024];
				int length = 0;
				while((length = fileIn.read(bytes, 0, bytes.length)) != -1) {
					out.write(bytes, 0, length);
					out.flush();
				}
				System.out.println("...wait");
				System.out.println("upload successfully!!!");
			}else{
				System.out.println("no file exists!!!!");
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	void receiveFile(String filename,Long le) throws Exception{
		try {
//			File filepath = new File("src");
//			if(!filepath.exists()){
//				filepath.mkdir();
//			}
//			File file=new File(filepath.getAbsolutePath() + File.separatorChar + "new"+filename);
//			File file2=new File(filename);
//			Long filelength=file2.length();
			filename = filename.substring(0, 1).toUpperCase() + filename.substring(1);
			File file=new File("new"+filename);
			fileout=new FileOutputStream(file);
			byte[] bytes = new byte[1024];
			int flag = 0;
			while((flag = in.read(bytes, 0, bytes.length)) != -1) {
				fileout.write(bytes, 0, flag);
				fileout.flush();
				if(file.length()==le){
					break;
				}
			}
			System.out.println("Download the file successfully: "+filename);
			fileout.close();
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	boolean isFileExits(File file){
		boolean tmp=file.exists();
		return  tmp;
	}
	void stop() {
		//Close connections
		try{
			if(fileIn!=null)
				fileIn.close();
			in.close();
			if(out!=null)
				out.close();
			requestSocket.close();
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
	}

	//main method
	public static void main(String args[])
	{
		Ftpclient client = new Ftpclient();
		int port=Integer.parseInt(args[0]);
		client.run(port);
	}
}
