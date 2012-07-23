package AnonymityServer;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
/** At the point the server starts, the message perturbation engine needs to function, so the
 * the thread starts when the servlet is initialized, i.e. onContextInitialized()
 * If the server goes down, so does the engine thread, as seen on contextDestroyed()
 * @author airwizard
 *
 */
public class StartEngineServletListener  implements ServletContextListener{
	
	MessagePerturbationEngineThread engine;
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		/** Stop the engine */
		AnonymityQueues.engineRunning=false;
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
	
		System.out.println("StartEngineServletListener:\n   -Ready to start Engine Thread");
		/** Start the engine thread */
		engine = new MessagePerturbationEngineThread(); 
		

		
		engine.start();
				
		
		
		
		
	}

	
		
	
}
