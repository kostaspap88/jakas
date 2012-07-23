package AnonymityServer;
//Some day perhaps we should switch to an Executor instead of locks and threads
import java.util.concurrent.Executor;

public class MessagePerturbationEngineExecutor implements Executor {

	

	@Override
	public void execute(Runnable r) {
		new Thread(r).start();
		
	}

}
