 package AnonymityServer;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;

import javax.servlet.ServletContextAttributeEvent;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jgraph.graph.DefaultEdge;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.SimpleGraph;

/**
 * Servlet implementation class, AnonymityServlet.  The main purpose of this class is to 
 * implement a server interface for the Anonymity Server.  The clients perform HTTP POST requests
 * and send their latitude, longitude, timestamp and anonymization policy (no policy, basic anonymity,
 * strong anonymity)
 * NOTE: Although not explicitly invoked, the http requests are handled by the library by many threads.
 */
@WebServlet("/AnonymityServlet")
public class AnonymityServlet extends HttpServlet  {
	private static final long serialVersionUID = 1L;
	private int id;
	Random generator = new Random(); 
	 
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AnonymityServlet() {
        super();
        // TODO Auto-generated constructor stub
    }
 
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		/** No need for a GET request, we prefer POST */
	}

	/**
	 * We get from a potential client the latitude, longitude, timestamp and anonymity policy
	 * lat and lon will be used for spatial cloaking, timestamp for temporal cloaking and 
	 * policy will help us define the k-level of anonymity.
	 * Then the parameters are combined into an AnonymityMessage and then loaded to the Incoming Queue
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	
		
		
	    /** Parameters of request */
	    String latitude = request.getParameter("latitude").toString();
	    String longitude = request.getParameter("longitude").toString();
	    String timeStamp = request.getParameter("timestamp").toString();
	    String anonymityPolicy = request.getParameter("anonymity_policy").toString();
	    
	    
	    /** Create message from parameters */
	    id = generator.nextInt();
	    AnonymityMessage message = new AnonymityMessage(AnonymityQueues.id,latitude,longitude,timeStamp,anonymityPolicy,response);
	    AnonymityQueues.id++;
	  
	   
	    /** Put message into queue */
		AnonymityQueues.incomingQueue.offer(message);
	  
	    //getServletContext().setAttribute("incoming_queue", incomingQueue);
		/** Notify about request the logs */
	    System.out.println("Received POST HTTP request:\n   Coordinates lat/lon =" +
	    		" "+latitude+" / "+longitude+"\n   Timestamp (in seconds UnixEpoch) = "+timeStamp+
	    		"\n   Anonymity Policy = "+anonymityPolicy+"\n  " +
	    				" Incoming Queue Size ="+ AnonymityQueues.incomingQueue.size()+"\n"+"ID assigned= "+message.getID()); 
	
	    
	    
	    Lock lockObject = new Lock(message.getID());
	    AnonymityQueues.lockTable.put(message.getID(), lockObject);
	    
	    synchronized (lockObject){
	    	try {
				lockObject.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    
	    //Critical response section
	  
	 
	   response.addHeader("latitude", AnonymityQueues.outgoingTable.get(lockObject.getID()).getLatitude()+"");
	   response.addHeader("longitude", AnonymityQueues.outgoingTable.get(lockObject.getID()).getLongitude()+"");
		
	}

	
	

}
