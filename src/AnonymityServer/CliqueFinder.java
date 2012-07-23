package AnonymityServer;
import java.util.Collection;
import java.util.Iterator;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.BronKerboschCliqueFinder;

//STILL ISSUE: strong+basic clients in the same clique
public class CliqueFinder {

	Collection spatialGraphCliques ;
	AnonymityMessage message;
	public  CliqueFinder(UndirectedGraph spatialGraph,AnonymityMessage messageIn){
		
		message=messageIn;
		BronKerboschCliqueFinder cliqueFinder = new BronKerboschCliqueFinder(spatialGraph);
		spatialGraphCliques = cliqueFinder.getAllMaximalCliques();
		
	}
	public Collection getCliques() {
		
		String policy = message.getAnonymityPolicy();
		Iterator it =spatialGraphCliques.iterator();
		while (it.hasNext()){
			Collection<AnonymityMessage> clique = (Collection<AnonymityMessage>) it.next();
			if (clique.contains(message))
			{
				if(policy.equals("basic")&&(clique.size()>=AnonymityQueues.basicAnonymityk)){
					return clique;
				}
				else if (policy.equals("strong")&&(clique.size()>=AnonymityQueues.strongAnonymityk)){
					return clique;
				}
			}
		}
		return null;
		
		
			
	}
	
}
