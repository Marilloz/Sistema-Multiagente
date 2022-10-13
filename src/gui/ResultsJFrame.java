package gui;


import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import agenteGUI.AgenteGUI;
import javax.swing.JLabel;
import java.awt.Font;

public class ResultsJFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	/**
	 * Create the frame.
	 */
	public ResultsJFrame(final AgenteGUI resultsAgent, String ecc, String res) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(300, 200, 1200, 600);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		
		JLabel sinResolver = new JLabel("LA ECUACION A RESOLVER ES: ");
		sinResolver.setFont(new Font("Tahoma", Font.PLAIN, 40));
		sinResolver.setBounds(300, -20, 800, 300);
		contentPane.add(sinResolver);
		
		JLabel EcuacionsinResolver = new JLabel(ecc);
		EcuacionsinResolver.setFont(new Font("Tahoma", Font.PLAIN, 40));
		EcuacionsinResolver.setBounds(300, 80, 800, 300);
		contentPane.add(EcuacionsinResolver);
		
		
		JLabel resuelto = new JLabel("LA ECUACION RESUELTA ES: \n");
		resuelto.setFont(new Font("Tahoma", Font.PLAIN, 40));
		resuelto.setBounds(300, 210, 800, 300);
		contentPane.add(resuelto);
		
		JLabel EcuacionResuelta = new JLabel(res);
		EcuacionResuelta.setFont(new Font("Tahoma", Font.PLAIN, 40));
		EcuacionResuelta.setBounds(300, 310, 800, 300);
		contentPane.add(EcuacionResuelta);
		}
}