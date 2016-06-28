package Server_v3;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.Channel;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {
    int port;
	CharsetEncoder encoder;
    SocketAddress localport;
    ServerSocketChannel tcpserver;
    DatagramChannel udpserver;
    Selector selector;
    ByteBuffer receiveBuffer;
    
    /*
     * Constructor:
     * Initialize the server with port of choice.
     * @params port		Port of choice
     */
    Server(int port) {
    	this.port = port;
    }
    
    /*
     * Set up the Server so that it can take TCP and UDP messages 
     * for the chosen port.
     */
    public void setupServer() {
    	this.encoder = Charset.forName("US-ASCII").newEncoder();
    	try {
	    	configurePort();
	    	configureSelector();
    	} catch (Exception e) {
    	      System.err.println(e);
    	      System.exit(1);
    	}
    	this.receiveBuffer = ByteBuffer.allocate(0);
    	System.out.println("(Server) set to Port: " + this.port);
    }
    
    /*
     * Set up port, TCP and UDP channels.
     */
    private void configurePort() throws IOException {
    	this.localport = new InetSocketAddress(this.port);
    	
    	this.tcpserver = ServerSocketChannel.open();
        this.tcpserver.socket().bind(this.localport);
        
        this.udpserver = DatagramChannel.open();
        this.udpserver.socket().bind(this.localport);
        
        this.tcpserver.configureBlocking(false);
        this.udpserver.configureBlocking(false);
    }
    
    /*
     * Set up the selector so that it can dynamically choose
     * between TCP and UDP channels on one port.
     */
    private void configureSelector() throws IOException {
    	this.selector = Selector.open();
    	this.tcpserver.register(selector, SelectionKey.OP_ACCEPT);
        this.udpserver.register(selector, SelectionKey.OP_READ);
    }
}