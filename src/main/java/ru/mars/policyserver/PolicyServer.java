package ru.mars.policyserver;

import org.apache.log4j.Logger;
import ru.mars.gameserver.Parameters;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Class PolicyServer
 * Starts a PolicyServer on the specified PORT_OF_GAME_SERVER.
 * Can be started as main class, passing the PORT_OF_GAME_SERVER number as the first command line argument
 *
 * @author Thomas Meyer, Less Rain (thomas@lessrain.com)
 */
public class PolicyServer extends Thread {
    /**
     * If no argument is passed the seawar will listen on this PORT_OF_GAME_SERVER for connections
     */
    public static final int DEFAULT_PORT = 1008;
    public static final String[] DEFAULT_POLICY = new String[]{"*"};

    /**
     * The character sequence sent by the Flash Player to request a _policy file
     */
    public static final String POLICY_REQUEST = "<policy-file-request/>";


    /*
     * PolicyServer class variables
     */
    private int _port;
    private boolean _listening;
    private ServerSocket _socketServer;
    private String _policy;


    /**
     * PolicyServer constructor
     *
     * @param port_ Sets the PORT_OF_GAME_SERVER that the PolicyServer listens on
     */
    public PolicyServer(int port_, String[] allowedHosts_) {
        _port = port_;
        _listening = true;
        if (allowedHosts_ == null) allowedHosts_ = DEFAULT_POLICY;
        _policy = buildPolicy(allowedHosts_);
    }

    private String buildPolicy(String[] allowedHosts_) {
        StringBuffer policyBuffer = new StringBuffer();

        policyBuffer.append("<?xml version=\"1.0\"?><cross-domain-policy>");
        for (int i = 0; i < allowedHosts_.length; i++) {
            String[] hostInfo = allowedHosts_[i].split(":");
            String hostname = hostInfo[0];
            String ports;
            if (hostInfo.length > 1) ports = hostInfo[1];
            else ports = "*";

            policyBuffer.append("<allow-access-from domain=\"" + hostname + "\" to-ports=\"" + ports + "\"  secure=\"false\"/>");
        }
        policyBuffer.append("</cross-domain-policy>");

        return policyBuffer.toString();
    }

    /**
     * Thread run method, accepts incoming connections and creates SocketConnection objects to handle requests
     */
    public void run() {
        try {
            _listening = true;

            // Start listening for connections
            _socketServer = new ServerSocket(_port, 50);
            if (Parameters.getInstance().isDebug())
                Logger.getLogger(this.getClass()).info("PolicyServer listening on PORT_OF_GAME_SERVER " + _port);

            while (_listening) {
                // Wait for a connection and accept it
                Socket socket = _socketServer.accept();

                try {
                    if (Parameters.getInstance().isDebug())
                        System.out.println("PolicyServer got a connection on PORT_OF_GAME_SERVER " + _port);
                    // Start a new connection thread
                    (new SocketConnection(socket)).start();
                } catch (Exception e) {
                    if (Parameters.getInstance().isDebug())
                        Logger.getLogger(this.getClass()).error("Exception", e);
                }
                try {
                    // Wait for a sec until a new connection is accepted to avoid flooding
                    sleep(1000);
                } catch (InterruptedException e) {
                }
            }
        } catch (IOException e) {
            if (Parameters.getInstance().isDebug())
                Logger.getLogger(this.getClass()).error("Exception", e);
        }
        if (Parameters.getInstance().isDebug())
            Logger.getLogger(this.getClass()).info("PolicyServer is out");
    }

    /**
     * Local class SocketConnection
     * For every accepted connection one SocketConnection is created.
     * It waits for the _policy file request, returns the _policy file and closes the connection immediately
     *
     * @author Thomas Meyer, Less Rain (thomas@lessrain.com)
     */
    class SocketConnection extends Thread {
        private Socket _socket;
        private BufferedReader _socketIn;
        private PrintWriter _socketOut;

        /**
         * Constructor takes the Socket object for this connection
         *
         * @param socket_ Socket connection to a client created by the PolicyServer main thread
         */
        public SocketConnection(Socket socket_) {
            _socket = socket_;
        }

        /**
         * Thread run method waits for the _policy request, returns the poilcy file and closes the connection
         */
        public void run() {
            try {
                // initialize socket and readers/writers
                _socket.setSoTimeout(10000);
                _socketIn = new BufferedReader(new InputStreamReader(_socket.getInputStream()));
                _socketOut = new PrintWriter(_socket.getOutputStream(), true);
            } catch (IOException e) {
                if (Parameters.getInstance().isDebug()) System.out.println("IO Exception " + e.getMessage());
                return;
            }

            readPolicyRequest();
        }

        /**
         * Wait for and read the _policy request sent by the Flash Player
         * Return the _policy file and close the Socket connection
         */
        private void readPolicyRequest() {
            try {
                // Read the request and compare it to the request string defined in the constants.
                // If the proper _policy request has been sent write out the _policy file
                if (POLICY_REQUEST.equals(read())) write(_policy);
            } catch (Exception e) {
                if (Parameters.getInstance().isDebug())
                    System.out.println("Exception " + e.getMessage());
            }
            close();
        }

        /**
         * Read until a zero character is sent or a maximum of 100 character
         *
         * @return The character sequence read
         * @throws IOException
         * @throws EOFException
         * @throws InterruptedIOException
         */
        private String read() throws IOException, EOFException, InterruptedIOException {
            StringBuffer buffer = new StringBuffer();
            int codePoint;
            boolean zeroByteRead = false;

            if (Parameters.getInstance().isDebug())
                System.out.println("Reading...");
            do {
                codePoint = _socketIn.read();
                if (codePoint == 0) zeroByteRead = true;
                else buffer.appendCodePoint(codePoint);
            }
            while (!zeroByteRead && buffer.length() < 100);
            if (Parameters.getInstance().isDebug())
                System.out.println("Read: " + buffer.toString());

            return buffer.toString();
        }

        /**
         * Writes a String to the client
         *
         * @param msg Text to be sent to the client (_policy file)
         */
        public void write(String msg) {
            _socketOut.println(msg + "\u0000");
            _socketOut.flush();
            if (Parameters.getInstance().isDebug())
                System.out.println("Wrote: " + msg);
        }

        /**
         * Close the Socket connection an set everything to null. Prepared for garbage collection
         */
        public void close() {
            try {
                if (_socket != null) _socket.close();
                if (_socketOut != null) _socketOut.close();
                if (_socketIn != null) _socketIn.close();
            } catch (IOException e) {
            }

            _socketIn = null;
            _socketOut = null;
            _socket = null;
        }

    }

}
