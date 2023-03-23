import java.io.* ;
import java.net.* ;
import java.util.* ;

public final class WebServer
{
	public static void main(String argv[]) throws Exception
	{
		// Set the port number.
		int port = 8080;

		// Establish the listen socket.
      		ServerSocket ss = new ServerSocket(port);

		// Process HTTP service requests in an infinite loop.
		while (true) {

			// Listen for a TCP connection request.
			Socket s = ss.accept();
			//Construct an object to process the HTTP request message
			HttpRequest request = new HttpRequest(s);
			//Create a new thread to process the request
			Thread thread = new Thread(request);
			//Start the thread
			thread.start();

		}

	}
}


final class HttpRequest implements Runnable
{
	final static String CRLF = "\r\n";
	Socket socket;

	// Constructor
	public HttpRequest(Socket socket) throws Exception 
	{
		this.socket = socket;
	}

	private static void sendBytes(FileInputStream fis, OutputStream os) throws Exception {
		//Construct a 1k buffer to hold bytes on their own way to the socket
		byte[] buffer = new byte[1024];
		int bytes = 0;

		//Copy requested file into the socket's output stream
		while ((bytes = fis.read(buffer)) != -1) {
			os.write(buffer, 0, bytes);
		}

		return;

	}

	private static String contentType(String fileName) {
		
		if (fileName.endsWith(".htm") || fileName.endsWith(".html")) {
			return "text/html";
		}
		if (fileName.endsWith(".gif")) {
			return "image/gif";
		}
		if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
			return "image/jpeg";
		}

		return "application/octet-stream";

	}

	// Implement the run() method of the Runnable interface.
	public void run()
	{
		try {
			processRequest();
		} 
		catch (Exception e) {
			System.out.println(e);
		}

		return;
	}

	private void processRequest() throws Exception
	{

		InputStreamReader in = new InputStreamReader(socket.getInputStream());
		DataOutputStream os = new DataOutputStream(socket.getOutputStream());
		
		BufferedReader br = new BufferedReader(in);
		String requestLine = br.readLine();

		System.out.println();
		System.out.println(requestLine);
		
		//Get and display header lines
		String headerLine = null;
		while ((headerLine = br.readLine()).length() != 0) {
			System.out.println(headerLine);
		}
		
		//Extract the filename from the request line
		StringTokenizer tokens = new StringTokenizer(requestLine);
		tokens.nextToken();
		String fileName = tokens.nextToken();
		fileName = "." + fileName;

		//Open the requested file
		FileInputStream fis = null;
		boolean fileExists = true;
		try {
			fis = new FileInputStream(fileName);
		}
		catch (FileNotFoundException e) {
			fileExists = false;
		}

		//Construct the response message
		String statusLine = null;
		String contentTypeLine = null;
		String entityBody = null;

		if (fileExists) {
			statusLine = "HTTP/1.1 200 OK" + CRLF;
			contentTypeLine = "Content-type: " + contentType(fileName) + CRLF;
		}
		else {
			statusLine = "HTTP/1.1 404 Not Found" + CRLF;
			contentTypeLine = "text/html" + CRLF;
			entityBody = "<HTML>" + "<HEAD><TITLE>Not Found</TITLE></HEAD>"
					+ "<BODY>Not Found</BODY></HTML>";
		}

		//Send the status line
		os.writeBytes(statusLine);
		//Send content type line
		os.writeBytes(contentTypeLine);
		//Send blank line
		os.writeBytes(CRLF);

		//Send the entity body
		if (fileExists) {
			sendBytes(fis, os);
			fis.close();
		}
		else {
			os.writeBytes(entityBody);
		}
		
		//Close streams and socket
		os.close();
		br.close();
		in.close();
		socket.close();


		System.out.println("Completed " + requestLine);

		return;

	}
}
