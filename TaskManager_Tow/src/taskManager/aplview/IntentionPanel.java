package taskManager.aplview;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;

import uos.ai.jam.Goal;
import uos.ai.jam.IntentionStructureChangeListener;
import uos.ai.jam.plan.APLElement;

public class IntentionPanel extends JPanel implements IntentionStructureChangeListener{
	private JTextArea textPanel;
	private JScrollPane scrollPane;
	
	public IntentionPanel(){
		this.setLayout(null);
		textPanel = new JTextArea(400,380);
		scrollPane = new JScrollPane(textPanel,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBounds(0,0,470,440);
		this.add(scrollPane);
	}
	
	@Override
	public void goalAdded(Goal goal) {
		System.out.println("Goal added : " + goal);
		// TODO Auto-generated method stub
		
	}

	@Override
	public void goalRemoved(Goal goal) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void intended(APLElement goal) {
		System.out.println("goal intended");
		String a = null;
		StringBuilder sb = new StringBuilder(textPanel.getText());
		sb.append("\n-------------------------\n");
		sb.append(goal.toString());
		textPanel.setText(sb.toString());
	}
	
}
