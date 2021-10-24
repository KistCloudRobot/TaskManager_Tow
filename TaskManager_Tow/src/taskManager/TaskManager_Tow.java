package taskManager;

import java.io.BufferedWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import kr.ac.uos.ai.arbi.agent.ArbiAgent;
import kr.ac.uos.ai.arbi.agent.ArbiAgentExecutor;
import kr.ac.uos.ai.arbi.ltm.DataSource;
import kr.ac.uos.ai.arbi.model.GLFactory;
import kr.ac.uos.ai.arbi.model.GeneralizedList;
import kr.ac.uos.ai.arbi.model.Value;
import kr.ac.uos.ai.arbi.model.parser.ParseException;
import taskManager.aplview.APLViewer;
import taskManager.logger.TaskManagerLogger;
import taskManager.utility.CommunicationUtility;
import taskManager.utility.GLMessageManager;
import taskManager.utility.JAMUtilityManager;
import taskManager.utility.RecievedMessage;
import uos.ai.jam.Interpreter;
import uos.ai.jam.JAM;
import uos.ai.jam.parser.JAMParser;

public class TaskManager_Tow extends ArbiAgent {
	private Interpreter interpreter;
	private GLMessageManager msgManager;
	private BlockingQueue<RecievedMessage> messageQueue;
	private TaskManagerLogger logger;
	private boolean isTriggered = false;
//	private APLViewer aplViewer;
	
	public static String ENV_JMS_BROKER;
	public static String ENV_AGENT_NAME;
	public static String ENV_ROBOT_NAME;
	public static final String ARBI_PREFIX = "www.arbi.com/";
	public static final String BASE_AGENT_NAME = "/TaskManager";
	
	public static final String JMS_BROKER_URL = "tcp://172.16.165.204:61112";
	//public static final String JMS_BROKER_URL = "tcp://localhost:61616";
	public static final String TASKMANAGER_ADRESS = "www.arbi.com/Tow2/TaskManager";
	public static  String CONTEXTMANAGER_ADRESS = "agent://www.arbi.com/Tow2/ContextManager";
	public static  String KNOWLEDGEMANAGER_ADRESS = "agent://www.arbi.com/Tow2/KnowledgeManager";
	public static  String BEHAVIOUR_INTERFACE_ADDRESS = "agent://www.arbi.com/Tow2/BehaviourInterface";
	public static final String PERCEPTION_ADRESS = "agent://www.arbi.com/perception";
	public static final String ACTION_ADRESS = "agent://www.arbi.com/Tow2/action";
	public static  String REASONER_ADRESS = "agent://www.arbi.com/Tow2/TaskReasoner";
	public static final String PREFIX = "http://www.arbi.com//ontologies#";
	

	public static final String AGENT_PREFIX = "agent://";
	public static final String DATASOURCE_PREFIX = "ds://";
	private TaskManagerDataSource dc;

	
	public TaskManager_Tow() {
		

		initAddress();
		
		ArbiAgentExecutor.execute(ENV_JMS_BROKER, AGENT_PREFIX + ARBI_PREFIX + ENV_AGENT_NAME + BASE_AGENT_NAME, this,2);
		interpreter = JAM.parse(new String[] { "./TaskManagerTowPlan/boot.jam" });
		messageQueue = new LinkedBlockingQueue<RecievedMessage>();
		
		msgManager = new GLMessageManager(interpreter);
		
//		aplViewer = new APLViewer(interpreter);
		//logger = new TaskManagerLogger(this,interpreter);
		init();
	}
	

	public void initAddress() {
		//ENV_JMS_BROKER = "tcp://" + System.getenv("JMS_BROKER");
		//ENV_AGENT_NAME = System.getenv("AGENT");
		//ENV_ROBOT_NAME = System.getenv("ROBOT");
		ENV_JMS_BROKER = "tcp://172.16.165.204"  + ":61112";
		
		ENV_AGENT_NAME = "Tow2";
		ENV_ROBOT_NAME = "AMR_TOW2";
		CONTEXTMANAGER_ADRESS =  AGENT_PREFIX + ARBI_PREFIX + ENV_AGENT_NAME + "/ContextManager"; 
		REASONER_ADRESS =  AGENT_PREFIX + ARBI_PREFIX + ENV_AGENT_NAME + "/TaskReasoner"; 
		BEHAVIOUR_INTERFACE_ADDRESS = AGENT_PREFIX + ARBI_PREFIX + ENV_AGENT_NAME + "/BehaviorInterface"; 
	}
	public void test(){
		
		if(isTriggered == false){

			messageQueue.add(new RecievedMessage("test","(WakeupService)"));
			isTriggered = true;
		}
		
	}
	
	private void init() {
		msgManager.assertFact("GLUtility", msgManager);
		msgManager.assertFact("Communication", new CommunicationUtility(this, dc));
		msgManager.assertFact("ExtraUtility", new JAMUtilityManager(interpreter));
		msgManager.assertFact("TaskManager", this);
		
		msgManager.assertFact("isro:ReasonerAddress", REASONER_ADRESS);
		msgManager.assertFact("isro:ContextManagerAddress", CONTEXTMANAGER_ADRESS);
		msgManager.assertFact("isro:BehaviorAddress", BEHAVIOUR_INTERFACE_ADDRESS);

		msgManager.assertFact("isro:robot", ENV_ROBOT_NAME);
		msgManager.assertFact("isro:agent", ENV_AGENT_NAME);
		msgManager.assertFact("OnAgentTaskStatus", ENV_AGENT_NAME, "wait", "wait");
		msgManager.assertFact("RobotAt", ENV_ROBOT_NAME, 0, 0);
		msgManager.assertFact("RobotVelocity", ENV_ROBOT_NAME, 0);
		msgManager.assertFact("BatteryRemain", ENV_ROBOT_NAME, 50);
		msgManager.assertFact("OnRobotTaskStatus", ENV_ROBOT_NAME, "wait");
		
		//aplViewer.init();
		
		Thread t = new Thread() {
			public void run() {
				interpreter.run();
			}
		};
		
		t.start();
	}
	
	public String getConversationID() {
		return this.getConversationID();
	}

	@Override
	public void onNotify(String sender, String notification) {
	
//		aplViewer.msgReceived(notification, sender);
		RecievedMessage msg = new RecievedMessage(sender, notification);
		messageQueue.add(msg);	
	}

	@Override
	public void onStart() {
		dc = new TaskManagerDataSource(this);

		dc.connect(ENV_JMS_BROKER, DATASOURCE_PREFIX + ARBI_PREFIX + ENV_AGENT_NAME + BASE_AGENT_NAME,2);

		System.out.println("======Start Test Agent======");
		System.out.println("??");
		this.send("tester", "wtf");
		// subscribe
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//String subscribeStatement = "(rule (fact (VoicePerceived $user $input)) --> (notify (VoicePerceived $user $input)))";
		//dc.subscribe(subscribeStatement);
		//System.out.println("??");
		String subscribeStatement = "(rule (fact (context $context)) --> (notify (context $context)))";
		System.out.println("before subscribe");
		String subID = dc.subscribe(subscribeStatement);
		
		System.out.println("after subscribe");
		
		//subscribeStatement = "(rule (fact (UserIntention $person $intention)) --> (notify (UserIntention $person $intention)))";
		//dc.subscribe(subscribeStatement);
		
	}

	
	public boolean dequeueMessage() {

		if (messageQueue.isEmpty())
			return false;
		else {
			try {
				RecievedMessage message = messageQueue.take();
				GeneralizedList gl = null;
				String data = message.getMessage();
				String sender = message.getSender();

//				aplViewer.msgReceived(data, sender);

				gl = GLFactory.newGLFromGLString(data);

				if (gl.getName().equals("PostGoal")) {
					//System.out.println("test");
					gl = gl.getExpression(0).asGeneralizedList();

					System.out.println("goal to post " + gl.toString());
					
					msgManager.assertGoal(gl.toString());
					
				} else if (gl.getName().equals("InitiateServicePackage")) {
					String packageName = gl.getExpression(0).toString();
					packageName = packageName.substring(1, packageName.length() - 1);
					initServicePackage(packageName);
				} else if(gl.getName().equals("context")){
					String recievedContext = "(ContextRecieved "+ gl.getExpression(0).toString() + ")";
					msgManager.assertGL(recievedContext);
				} else if(gl.getName().equals("relationChanged")) {
					String relationChanged = "(relationChanged " + gl.getExpression(0).toString() + ")";
					msgManager.assertGL(relationChanged);
				}  else if (gl.getName().equals("RobotPath")) {
					msgManager.assertGL(gl.toString());
					msgManager.assertGL("(RobotPathUpdated)");
				} else if (gl.getName().equals("GoalReport")) {
					msgManager.assertGL(gl.toString());
				} else if (gl.getName().equals("GoalRequest")) {
					GeneralizedList goalGL = gl.getExpression(0).asGeneralizedList();
					msgManager.assertFact(goalGL.getName() + "RequestedFrom", sender, goalGL.getExpression(1), goalGL.getExpression(2));
				} else if(gl.getExpression(0).isGeneralizedList()) { 
					if (gl.getExpression(0).asGeneralizedList().getName().equals("actionID") &&
							gl.getExpression(1).asValue().stringValue().equals("success")) {
						String actionResult = "(actionCompleted "+ gl.getExpression(0).asGeneralizedList().getExpression(0) + ")";
						msgManager.assertGL(actionResult);	
					}
				}
				
				else {
					msgManager.assertGL(data);
				}

			} catch (InterruptedException | ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return true;
		}
	}

	@Override
	public String onQuery(String sender, String query) {
		System.out.println("recieved query from " + sender + " : " + query);

//		aplViewer.msgReceived(query, sender);

		String result = handleQuery(query);

		return "(result \"" + result + "\")";
	}

	private String handleQuery(String query) {
		String result = "success";
		try {
			GeneralizedList gl = GLFactory.newGLFromGLString(query);
			String name = gl.getName();
			if (name.equals("retractFact")) {
				System.out.println("retractFact called");
				msgManager.retractFact(gl.getExpression(0).toString());
			} else if (name.equals("updateFact")) {
				System.out.println("updateFact called");
				msgManager.updateFact(gl.getExpression(0).toString(), gl.getExpression(1).toString());
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}


	@Override
	public void onData(String sender, String data) {
		System.out.println("recieved data from " + sender + " : " + data);
		RecievedMessage msg = new RecievedMessage(sender, data);
		messageQueue.add(msg);
	}

	@Override
	public String onRequest(String sender, String request) {
		System.out.println("received data from " + sender + " : " + request);
		RecievedMessage msg = new RecievedMessage(sender, request);
		messageQueue.add(msg);

		return "(ok)";
	}
	

	public void initServicePackage(String packageName) {
		String retrieve = "";
		String plan = "";
		System.out.println("retrieving service package start");

		GeneralizedList gl = null;
		int i = 0;
		while (true) {
			i++;
			String data = "(ServicePackage \"" + packageName + "\" \"plan\" \"" + i + "\" $a)";

			retrieve = dc.retrieveFact(data);
			System.out.println("plan" + i + " retrieval");

			if (retrieve.equals("(fail)")) {
				break;
			}

			try {
				gl = GLFactory.newGLFromGLString(retrieve);
				plan = GLFactory.unescape(gl.getExpression(3).toString());
				plan = plan.substring(1, plan.length() - 1);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			JAMParser.parseString(interpreter, plan);
		}
		i = 0;

		while (true) {
			i++;

			String data = "(Context \"" + i + "\" $a)";
			retrieve = dc.retrieveFact(data);
			System.out.println("context retrieval : " + retrieve);

			if (retrieve.equals("(fail)")) {
				break;
			}

			try {
				gl = GLFactory.newGLFromGLString(retrieve);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String context = GLFactory.unescape(gl.getExpression(1).toString());
			context = context.substring(1, context.length() - 1);
			GeneralizedList contextGL = null;
			try {
				contextGL = GLFactory.newGLFromGLString(context);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String contextStatement = "(" + contextGL.getName();
			String tempText = "";
			
			for (int j = 0; j < contextGL.getExpressionsSize(); j++) {
				tempText = contextGL.getExpression(j).asGeneralizedList().getExpression(0).toString();
				System.out.println(tempText);
				tempText = tempText.substring(1, tempText.length() - 1);
				contextStatement += " " + tempText;
			}
			
			contextStatement += ")";
			String subscribeStatement = "(rule (fact (context " + contextStatement + ")) --> (notify " + contextStatement + "))";

			System.out.println("subscribe statement : " + subscribeStatement);

			String id = dc.subscribe(subscribeStatement);
			System.out.println("context : " + context);
		}
		
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.addMessage("dispatcher", "(PostGoal (initiation))");
		
		System.out.println("****ServicePackage Parse Completed****");
		System.out.println("****Initiated Service : " + packageName + " ****");
	}

	public void addMessage(String sender,String data){
		RecievedMessage msg = new RecievedMessage(sender,data);
		messageQueue.add(msg);
	}
	
	public GLMessageManager getMsgManager() {
		return msgManager;
	}
	
	public String toString() {
		return "TaskManager";
	}

	public static void main(String[] args) {

		new TaskManager_Tow();
	}
}
