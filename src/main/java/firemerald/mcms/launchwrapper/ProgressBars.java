package firemerald.mcms.launchwrapper;

import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.WindowConstants;
import javax.swing.JLabel;

public class ProgressBars extends JFrame
{
	private static final long serialVersionUID = -8116468364934043217L;
	private JProgressBar progressBar;
	private JLabel mainText;
	private JLabel subText;
	
	public ProgressBars(int numItems)
	{
		this.setResizable(false);
		this.setTitle("Progress Bar");
		this.setBounds(100, 100, 407, 132);
		this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		this.getContentPane().setLayout(null);
		mainText = new JLabel("Waiting...");
		mainText.setBounds(10, 11, 371, 13);
		this.getContentPane().add(mainText);
		subText = new JLabel("");
		subText.setBounds(10, 24, 371, 23);
		this.getContentPane().add(subText);
		progressBar = new JProgressBar(0, 0, numItems);
		progressBar.setBounds(10, 58, 371, 22);
		this.getContentPane().add(progressBar);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}
	
	public void setProgress(String label, int item)
	{
		this.mainText.setText(label);
		this.progressBar.setValue(item);
		this.subText.setText("");
	}
	
	public void setSubProgress(String subProgress)
	{
		this.subText.setText(subProgress);
	}
}