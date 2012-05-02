package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;

public class ClientTester {

    /*
     * Send a fortune request to server.
     */
    public static void client1() throws Exception{
    	System.out.println("hello");
    	Socket sock = new Socket("localhost", 4444);
        BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
        PrintWriter out = new PrintWriter(new OutputStreamWriter(sock.getOutputStream()));        
        //SEND REQUEST AND PRINT RESPONSE
        
        out.println("Aries\n");
        out.flush();
        System.out.println(in.readLine());
        sock.close();
    }
    
	public static void main(String[] args) {
		try {
			client1();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
