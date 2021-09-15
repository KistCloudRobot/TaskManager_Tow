package taskManager.aplview;

import javax.swing.JFrame;
import javax.swing.table.DefaultTableModel;

import uos.ai.jam.Interpreter;

public class APLViewer extends JFrame{
	private Interpreter interpreter;
	private MessageReceivedPanel msgReceivedPanel;
	private WorldModelPanel worldModelPanel;
	private APLListPanel aplPanel;
	private IntentionPanel intentionPanel;
	
	public APLViewer(Interpreter interpreter){
		this.interpreter = interpreter;
		msgReceivedPanel = new MessageReceivedPanel();
		worldModelPanel = new WorldModelPanel();
		aplPanel = new APLListPanel();
		intentionPanel = new IntentionPanel();
	}
	
	public void msgReceived(String content, String sender){
		msgReceivedPanel.addMessage(content, sender);
	}
	
	public void init(){
		this.setSize(1000, 1000);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLayout(null);
		
		msgReceivedPanel.setBounds(10, 10, 480, 480);
		worldModelPanel.setBounds(510, 10, 480, 480);
		aplPanel.setBounds(10, 510, 480, 480);
		intentionPanel.setBounds(510, 510, 480, 480);
		
		interpreter.getWorldModelManager().getWorldModel().addChangeListener(worldModelPanel);
		interpreter.addAPLListener(aplPanel);
		interpreter.getIntentionStructure().addChangeListener(intentionPanel);
		this.add(msgReceivedPanel);
		this.add(worldModelPanel);
		this.add(aplPanel);
		this.add(intentionPanel);		
	}
	
	
}
