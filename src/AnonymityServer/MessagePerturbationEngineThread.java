package AnonymityServer;

import java.security.SecureRandom;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.http.HttpServletResponse;

import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.referencing.GeodeticCalculator;
import org.jgraph.graph.DefaultEdge;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.graph.UndirectedSubgraph;

import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Point;

/** We denote with ** comments the explanations */
//  We denote with line comments the parts of the algorithm (in pseudo-code) that we have implemented
//  Full algorithm (in pseudo-code): Protecting location privacy with personalized k-anonymity:
//    Architecture and Algorithms, B.Gedik, page: 8

public class MessagePerturbationEngineThread extends Thread {
	
	
	LinkedList<AnonymityMessage> processingQueue = new LinkedList<AnonymityMessage>();
	// serious consideration: processingQueue should be implemented more
	// efficiently with R* trees
	// another consideration: UNIQUE identifier for each message
	UndirectedGraph<AnonymityMessage, DefaultEdge> graph = new SimpleGraph<AnonymityMessage, DefaultEdge>(
			DefaultEdge.class);

	SecureRandom ranGen = new SecureRandom();
	byte[] aesKey = new byte[16]; // 16 bytes = 128 bits

	public void run() {
		System.out
				.println("MessagePerturbationEngineThread:\n   -Engine is ON");
		/** While the servlet is up the boolean engineRunning is true. When it stops the servlet context
		 * makes it false and the engine stops by getting to the end of the run() thread function.
		 */
		while (AnonymityQueues.engineRunning == true) { //while engine_running=true
			AnonymityMessage message;
			try {
				Thread.sleep(3000L);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			/** We are constantly polling the asynchronous incoming queue to check for new http requests */
			if ((message = AnonymityQueues.incomingQueue.poll()) != null) {//if Qm <> {} then msc <- Pop the first item in Qm

				processingQueue.add(message); 
				graph.addVertex(message); //Add the message msc into Gm as a node
										  //NOT IMPLEMENTED: Add msc to Im with L(msc), i.e. use geo-spatial index
										  //NOT IMPLEMENTED: Add msc to Hm with (msc*t+msc*dt), i.e. temporal cloaking

				Set<AnonymityMessage> subset = null;
				try {
					subset = searchForNeighbors(message); //N <- Range search of the processing queue using simple Search
														  //NOT IMPLEMENTED: N <- Range search in Im using Bcn(msc), i.e. using the spatial index and geo-Bounds
				} catch (TransformException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				/** At this point we have found the candidates for anomymization, i.e. we have formed the subset */
				System.out
						.println("MessagePerturbationEngineThread:\n   -VertexSet:"
								+ graph.vertexSet().toString());
				System.out
						.println("MessagePerturbationEngineThread:\n   -SubsetOfNeighbors:"
								+ subset);

				UndirectedSubgraph<AnonymityMessage, DefaultEdge> subGraph = new UndirectedSubgraph<AnonymityMessage, DefaultEdge>(
						graph, subset, null); //Gm' is created here based on subset

				System.out
						.println("MessagePerturbationEngineThread:\n   -SubGraph:"
								+ subGraph);

				CliqueFinder cliqueFinder = new CliqueFinder(graph, message); //M <- LOCAL-k_SEARCH(ms*k,msc,Gm')
				Collection resultCliques = cliqueFinder.getCliques();
				if (resultCliques != null) {//if M <> {}
					System.out
							.println("MessagePerturbationEngineThread:\n   -Cliques:"
									+ resultCliques.toString());
				} else {
					System.out
							.println("MessagePerturbationEngineThread:\n   -Not Suitable Clique found");
				}

				if (resultCliques != null) {
									// NOT IMPLEMENTED YET: Randomize the order of messages in M
					// System.out.println(ranGen.nextInt(resultCliques.size()));
					// ranGen.nextBytes(aesKey);
					Iterator it = resultCliques.iterator();
					LinkedList<AnonymityMessage> cloakingQueue = new LinkedList<AnonymityMessage>();
					while (it.hasNext()) {//foreach ms in M
						message = (AnonymityMessage) it.next();
						cloakingQueue.add(message);
						graph.removeVertex(message);//Remove ms from Gm
						processingQueue.removeFirstOccurrence(message);//Remove ms Processing Queue
						//NOT IMPLEMENTED: remove ms from Hm
						//NOT IMPLEMENTED: remove ms from Im

					}
					//NOT IMPLEMENTED: temporal cloaking
					//NOT IMPLEMENTED: ms <- Topmost item in Hm
					//NOT IMPLEMENTED: if ms*t+ms*dt<now, remove ms from Gm,Im; Pop the topmost element in Hm else break 
					LinkedList<AnonymizedMessage> out = obfuscateMessages(cloakingQueue);//Perturbed messages, mt
					for (int i=0;i<cloakingQueue.size();i++){
					AnonymityQueues.outgoingQueue.offer(out.get(i));
					
					 
					
					System.out.println("MessagePerturbationEngineThread:\n"+"   -Message id Out = "+out.get(i).getID());
					}
					System.out.println("MessagePerturbationEngineThread:\n"+"   -Messages outputted to Queue, #messages = "+cloakingQueue.size());

				}
				
				

				System.out
						.println("MessagePerturbationEngineThreadEND:\n   -VertexSet:"
								+ graph.vertexSet().toString());
				System.out
						.println("MessagePerturbationEngineThreadEND:\n   -processingQueue:"
								+ processingQueue.toString());

			}
		}
		System.out
				.println("MessagePerturbationEngineThread\n   -Engine is OFF");

	}

	/** This function calculates the central positioning of all the group, so as 
	 * to create the anonymous group location. We find the centroid of the geometrical point collection
	 * The suggested way is calculation of MBR  but I do not have a clear opinion about optimality so far
	 * @param queue
	 */
	private LinkedList<AnonymizedMessage> obfuscateMessages(LinkedList<AnonymityMessage> queue) {
		
		LinkedList<AnonymizedMessage> result = new LinkedList<AnonymizedMessage>();
	
		GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory( null );

		Coordinate[] coords;
	
		coords = new Coordinate[queue.size()];
		for (int i=0;i<queue.size();i++){
			double lat=queue.get(i).getLatitude();
			double lon=queue.get(i).getLongitude();
			coords[i]= new Coordinate(lat,lon);
		
			
				}
		
        /** Geometry Collection */	
		MultiPoint points = geometryFactory.createMultiPoint(coords);
	
		/** Get the anonymous location */
		Point center= points.getCentroid();
		
		System.out.println("MessagePerturbationEngineThread:\n "+"   -Perturbed location: "+center.getX()+"/"+center.getY()+"\n");
		for (int i=0;i<queue.size();i++){
			System.out.println("ID = "+queue.get(i).getID());
			AnonymizedMessage m = new AnonymizedMessage(queue.get(i).getID(), center.getX(), center.getY(), queue.get(i).getResponse());
			result.add(new AnonymizedMessage(queue.get(i).getID(), center.getX(), center.getY(), queue.get(i).getResponse()));
			Lock lockObject =AnonymityQueues.lockTable.get(queue.get(i).getID());
		    AnonymityQueues.outgoingTable.put(m.getID(), m);
			synchronized  (lockObject){
			lockObject.notify();
			AnonymityQueues.lockTable.remove(queue.get(i).getID());
			}
		}
		
		return result;



	}

	/** This function will search all current clients in order to determine which can be grouped 
	 * together for anonymity purposes
	 * @param message
	 * @return
	 * @throws TransformException
	 */
	private Set<AnonymityMessage> searchForNeighbors(AnonymityMessage message)
			throws TransformException {

		Set<AnonymityMessage> subset = new HashSet<AnonymityMessage>();
		subset.add(message);
		//Foreach ms in N(processingQueue), ms<>msc
		for (int i = 0; i < processingQueue.size(); i++) {
			System.out.println("MessagePerturbationEngineThread:\n"
					+ "   -Message Processing started\n   -lat/lon= "
					+ processingQueue.get(i).getLatitude() + "/"
					+ processingQueue.get(i).getLongitude());
			if (distanceIsCloseEnough(message, processingQueue.get(i))// if L(ms) in inside B(msc)
					&& (notSameID(message, processingQueue.get(i)))) {
				System.out.println("The message is: " + message.toString());
				System.out.println("The messageCand is: "
						+ processingQueue.get(i).toString());
				graph.addEdge(message, processingQueue.get(i)); //add the edge (msc,ms) into Gm
				subset.add(processingQueue.get(i));
			}
		}
		return subset; //Gm' <- subgraph of Gm consisting of messages in N

	}

	/** Just checking that we don't use the same node as a neighbour of itself */
	private boolean notSameID(AnonymityMessage message,
			AnonymityMessage messageCand) {
		boolean result = true;
		if (message == messageCand) {
			result = false;
		}

		return result;
	}

	/** This function will check if two clients are close enough s.t. they can be grouped together
	 * for anonymity
	 * @param message
	 * @param messageCand
	 * @return
	 * @throws TransformException
	 */
	private boolean distanceIsCloseEnough(AnonymityMessage message,
			AnonymityMessage messageCand) throws TransformException {
		/** CLOSE ENOUGH - FUCK YEAH */
		boolean closeEnough;

		DirectPosition2D pos = new DirectPosition2D(message.getLongitude(),
				message.getLatitude());

		DirectPosition2D posCand = new DirectPosition2D(
				messageCand.getLongitude(), messageCand.getLatitude());

		GeodeticCalculator calc = new GeodeticCalculator();
		calc.setStartingPosition(pos);
		calc.setDestinationPosition(posCand);

		System.out
				.println("MessagePerturbationEngineThread:\n   -Distance is: "
						+ calc.getOrthodromicDistance());

		if (calc.getOrthodromicDistance() <= AnonymityQueues.safetyDistance) {
			closeEnough = true;
		} else {
			closeEnough = false;
		}

		return closeEnough;
	}

}
