package AnonymityServer;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.jgraph.graph.DefaultEdge;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.SimpleGraph;

public class AnonymityQueues {
	
	public static boolean engineRunning = true; //better define it in a server config file	
	public static int queueCapacity = 1000; //better define it in a server config file
	public static int basicAnonymityk =2;//better define it in a server config file
	public static int strongAnonymityk =4;//better define it in a server config file - NOT FULLY IMPLEMENTED
	public static double safetyDistance=1250.0;//better define it in a server config file
	public static Queue<AnonymizedMessage> outgoingQueue = new ConcurrentLinkedQueue<AnonymizedMessage>();//deprecated
	public static Queue<AnonymityMessage> incomingQueue = new ConcurrentLinkedQueue<AnonymityMessage>();
	public static Hashtable<Integer, Lock> lockTable = new Hashtable<Integer, Lock>();
	public static Hashtable<Integer, AnonymizedMessage> outgoingTable = new Hashtable<Integer, AnonymizedMessage>();
	public static int id=0;
	public static Lock serverLock = new Lock(AnonymityQueues.queueCapacity+10);//not necessary so far
}
