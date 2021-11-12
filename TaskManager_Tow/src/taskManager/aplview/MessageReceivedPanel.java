package taskManager.aplview;

import java.util.LinkedList;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import taskManager.TaskManager_Tow2;

public class MessageReceivedPanel extends JPanel{

	private DefaultTableModel contextManagerMessage;
	private DefaultTableModel knowledgeManagerMessage;
	private DefaultTableModel behaviorManagerMessage;
	private DefaultTableModel otherManagerMessage;
	
	private JScrollPane contextPane;
	private JScrollPane knowledgePane;
	private JScrollPane behaviorPane;
	private JScrollPane otherPane;
	
	private JTable contextTable;
	private JTable knowledgeTable;
	private JTable behaviorTable;
	private JTable otherTable;
	private int selected;
	
	
	public MessageReceivedPanel(){
		contextManagerMessage = new DefaultTableModel();
		knowledgeManagerMessage = new DefaultTableModel();
		behaviorManagerMessage = new DefaultTableModel();
		otherManagerMessage = new DefaultTableModel();
		
		contextTable = new JTable();
		knowledgeTable = new JTable();
		behaviorTable = new JTable();
		otherTable = new JTable();
		
		contextPane = new JScrollPane(contextTable);
		knowledgePane = new JScrollPane(knowledgeTable);
		behaviorPane = new JScrollPane(behaviorTable);
		otherPane = new JScrollPane(otherTable);
			
		contextTable.setModel(contextManagerMessage);
		knowledgeTable.setModel(knowledgeManagerMessage);
		behaviorTable.setModel(behaviorManagerMessage);
		otherTable.setModel(otherManagerMessage);

		init();

	}
	
	public void init(){
		contextManagerMessage.addColumn("ContextManager");
		knowledgeManagerMessage.addColumn("KnowledgeManager");
		behaviorManagerMessage.addColumn("BehaviorManager");
		otherManagerMessage.addColumn("Other");
		
		this.setLayout(null);
		contextPane.setBounds(0, 0, 480, 110);
		knowledgePane.setBounds(0, 120, 480, 110);
		behaviorPane.setBounds(0, 240, 480, 110);
		otherPane.setBounds(0, 360, 480, 110);
		
		
		this.add(contextPane);
		this.add(knowledgePane);
		this.add(behaviorPane);
		this.add(otherPane);
		
		
	}
	
	public void addMessage(String message, String sender){
		Object[] msg = new Object[1];
		msg[0] = message;
		if(sender.equals(TaskManager_Tow2.CONTEXTMANAGER_ADRESS)){
			contextManagerMessage.addRow(msg);
			selected = 0;
		}else if(sender.equals(TaskManager_Tow2.KNOWLEDGEMANAGER_ADRESS)){
			knowledgeManagerMessage.addRow(msg);
			selected = 1;
		}else if(sender.equals(TaskManager_Tow2.BEHAVIOUR_INTERFACE_ADDRESS)){
			behaviorManagerMessage.addRow(msg);
			selected = 2;
		}else{
			otherManagerMessage.addRow(msg);
			selected = 3;
		}
	}
	
	
	
}
