package taskManager.logger;

import kr.ac.uos.ai.arbi.agent.logger.ActionBody;
import taskManager.utility.GLMessageManager;

public class IntendLogger implements ActionBody{
	private GLMessageManager msgManager;
	
	public IntendLogger(GLMessageManager manager){
		msgManager = manager;
	}
	@Override
	public Object execute(Object o) {
		
		return null;
	}

}

