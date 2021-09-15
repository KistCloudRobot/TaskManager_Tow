package taskManager.logger;

import kr.ac.uos.ai.arbi.agent.logger.ActionBody;
import taskManager.logger.argument.RelationArgument;
import taskManager.utility.GLMessageManager;

public class AssertFactLogger implements ActionBody{
	private GLMessageManager msgManager;
	
	public AssertFactLogger(GLMessageManager manager){
		msgManager = manager;
	}
	
	
	@Override
	public Object execute(Object o) {
		RelationArgument argument = (RelationArgument)o;
		System.out.println("fact asserted!");
		msgManager.assertFact(argument.getName(), argument.getExpresisonList());
		
		return null;
	}

}
