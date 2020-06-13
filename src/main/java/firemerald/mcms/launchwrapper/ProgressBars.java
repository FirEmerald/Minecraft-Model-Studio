package firemerald.mcms.launchwrapper;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.JLabel;

public class ProgressBars extends JFrame
{
	private static final long serialVersionUID = -8116468364934043217L;
	private JProgressBar progressBar;
	private JLabel mainText;
	private JProgressBar secondaryProgressBar;
	private JLabel subText;
	
	public ProgressBars(String title, int numItems)
	{
		this.setResizable(false);
		this.setTitle(title);
		this.setBounds(0, 0, 480, 94);
		this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		this.getContentPane().setLayout(null);
		this.getContentPane().setPreferredSize(new Dimension(480, 64));
		mainText = new JLabel("Waiting...");
		mainText.setBounds(10, 10, 460, 22);
		mainText.setHorizontalAlignment(SwingConstants.CENTER);
		mainText.setVerticalAlignment(SwingConstants.CENTER);
		subText = new JLabel("");
		subText.setBounds(10, 32, 460, 22);
		subText.setHorizontalAlignment(SwingConstants.CENTER);
		subText.setVerticalAlignment(SwingConstants.CENTER);
		progressBar = new JProgressBar(0, 0, numItems);
		progressBar.setBounds(10, 10, 460, 22);
		secondaryProgressBar = new JProgressBar(0, 0, 1);
		secondaryProgressBar.setBounds(10, 32, 460, 22);
		//this.secondaryProgressBar.setVisible(false);
		this.getContentPane().add(mainText);
		this.getContentPane().add(progressBar);
		this.getContentPane().add(subText);
		this.getContentPane().add(secondaryProgressBar);
		this.pack();
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}
	
	public void setProgress(String label, int item)
	{
		this.mainText.setText(label);
		this.progressBar.setValue(item);
		this.subText.setVisible(false);
		this.secondaryProgressBar.setVisible(false);
		this.secondaryProgressBar.setMaximum(1);
		this.secondaryProgressBar.setValue(0);
	}
	
	public void setSecondaryMax(int max)
	{
		this.secondaryProgressBar.setMaximum(max > 0 ? max : 1);
		this.secondaryProgressBar.setValue(0);
		this.secondaryProgressBar.setVisible(max > 0);
	}
	
	public void setSubProgress(String subProgress, int item)
	{
		this.subText.setVisible(true);
		this.subText.setText(subProgress);
		this.secondaryProgressBar.setValue(item);
	}
}