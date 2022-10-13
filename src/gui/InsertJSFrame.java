package gui;

import java.awt.EventQueue;
import java.awt.*;

import javax.swing.JFrame;
import javax.swing.JTextField;
import agenteReceptor.AgenteReceptor;

import javax.swing.JLabel;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


import javax.swing.JButton;

public class InsertJSFrame extends JFrame implements ActionListener{
	
	
	private static final long serialVersionUID = 1L;
	private String ecuacion="";


	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					InsertJSFrame frame = new InsertJSFrame(null);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	 
	public void jTextField1ActionPerformed(java.awt.event.ActionEvent evt){
		String texto = jTextField1.getText();
		System.out.println("RECIBIDO:"+texto);
	}
	
	JLabel jlabMsg;
	JTextField jTextField1;
	JButton btnResolver;
	AgenteReceptor agent;
	public InsertJSFrame(final AgenteReceptor insertAgent) {
		agent = insertAgent;
		
		jlabMsg = new JLabel("Introduce la Ecuacion");
		jlabMsg.setFont(new Font("Tahoma", Font.PLAIN, 40));
		jTextField1 = new javax.swing.JTextField(50);
		
		btnResolver = new JButton("Resolve");
		btnResolver.setFont(new Font("Tahoma", Font.PLAIN, 30));
		btnResolver.setSize(1000, 1000);

		setLayout(new FlowLayout());
		setTitle("Frame");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jTextField1.setFont(new Font("Tahoma", Font.PLAIN, 20));
		jTextField1.setToolTipText("Ecuacion debe ir aquí");
		
		btnResolver.addActionListener(this);
		add(jlabMsg);
		add(jTextField1);
		add(btnResolver);
		
		setSize(1000,400);
		setLocationRelativeTo(null);
		setVisible(true);
		
	
		
		}
		
		public void actionPerformed(ActionEvent e){
			this.ecuacion = jTextField1.getText();
			System.out.println("RECIBIDO = " + this.ecuacion);
			setVisible(false);
			dispose();
		}
		
		public String getString() {
			return this.ecuacion;
		}
		public void resetString(){
			this.ecuacion="";
		}
}
