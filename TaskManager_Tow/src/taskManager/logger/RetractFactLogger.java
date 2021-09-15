package taskManager.logger;

import kr.ac.uos.ai.arbi.agent.logger.ActionBody;
import taskManager.logger.argument.RelationArgument;
import taskManager.utility.GLMessageManager;

public class RetractFactLogger implements ActionBody{
	private GLMessageManager msgManager;
	
	public RetractFactLogger(GLMessageManager manager){
		msgManager = manager;
	}
	
	@Override
	public Object execute(Object o) {
		
		RelationArgument argument = (RelationArgument)o;
		
		msgManager.retractFact(argument.getName());
		return null;
	}

}
