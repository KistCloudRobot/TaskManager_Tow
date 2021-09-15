package taskManager.logger;

import kr.ac.uos.ai.arbi.agent.logger.ActionBody;
import taskManager.logger.argument.GoalArgument;
import taskManager.utility.GLMessageManager;

public class NewGoalLogger implements ActionBody{
	private GLMessageManager msgManager;
	
	public NewGoalLogger(GLMessageManager manager){
		msgManager = manager;
	}
	@Override
	public Object execute(Object o) {
		GoalArgument arg = (GoalArgument)o;
		
		String goal = "(" + arg.getName();
		for(int i = 0; i < arg.getExpresisonList().size();i++){
			goal += " " + arg.getExpresisonList().get(i).toString();
		}
		goal += ")";
		
		msgManager.postGoal(goal, arg.getUtility());
		return null;
	}

}
