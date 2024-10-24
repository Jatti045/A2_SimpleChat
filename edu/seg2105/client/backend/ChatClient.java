// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com

package edu.seg2105.client.backend;

import ocsf.client.*;

import java.io.*;
import java.util.Arrays;

import edu.seg2105.client.common.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI; 

  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  
  public ChatClient(String host, int port, ChatIF clientUI) 
    throws IOException 
  {
    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
    openConnection();
  }

  
  //Instance methods ************************************************
    
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
    clientUI.display(msg.toString());
    
    
  }

  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromClientUI(String message) throws IOException {
    if (message.startsWith("#")) {
      handleUserInput(message);
    } else {
      try {
        sendToServer(message);
      } catch (IOException e) {
        clientUI.display("Could not send message to server. Terminating client.");
        quit();
      }
    }
  }

  private void handleUserInput(String message) throws IOException {
    String command = message.trim();
    if (command.equals("#quit")) {
      quit();
    } else if (command.equals("#logoff")) {
      if(isConnected()) {
        closeConnection();
        clientUI.display("Logged off.");
      } else {
        clientUI.display("Already disconnected.");
      }
    } else if (command.startsWith("#sethost ")) {
      if (!isConnected()) {
        String newHost = command.substring(9).trim();
        setHost(newHost);
        clientUI.display("Host set to " + getHost());
      } else {
        clientUI.display("Cannot set host while connected.");
      }
    } else if (command.startsWith("#setport ")) {
      if (!isConnected()) {
        String newPort = command.substring(9).trim();
        int port = Integer.parseInt(newPort);
        setPort(port);
        clientUI.display("Port set to " + getPort());
      } else {
        clientUI.display("Cannot set port while connected.");
      }
    } else if (command.equals("#login")) {
      if (!isConnected()) {
        openConnection();
        clientUI.display("Logged in.");
      } else {
        clientUI.display("Already connected.");
      }
    } else if (command.equals("#gethost")) {
      clientUI.display("Host: " + getHost());
    } else if (command.equals("#getport")) {
      clientUI.display("Port: " + getPort());
    } else {
      clientUI.display("Invalid command.");
    }
  }




  /**
   * This method terminates the client.
   */
  public void quit() {
    try {
      if (isConnected()) {
        closeConnection();
      }
    } catch (IOException e) {
      clientUI.display("Error while closing the connection.");
    } finally {
      System.exit(0);
    }
  }


  /**
   * Implements the hook method called each time an exception is thrown by the client's
   * thread that is waiting for messages from the server. The method may be
   * overridden by subclasses.
   *
   * @param exception
   *            the exception raised.
   */
  @Override
  protected void connectionException(Exception exception) {
    clientUI.display("The server is down.");
    quit();
  }

  /**
   * Implements the hook method called after the connection has been closed. The default
   * implementation does nothing. The method may be overriden by subclasses to
   * perform special processing such as cleaning up and terminating, or
   * attempting to reconnect.
   */
  @Override
  protected void connectionClosed() {
    clientUI.display("Connection closed.");
  }
}
//End of ChatClient class
