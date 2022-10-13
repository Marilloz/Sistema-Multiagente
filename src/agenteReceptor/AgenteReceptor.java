package agenteReceptor;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
// import java.io.BufferedReader;
// import java.io.File;
// import java.io.FileReader;
// import java.util.Scanner;
import gui.InsertJSFrame;

public class AgenteReceptor extends Agent {
	private static final long serialVersionUID = 1L;

	protected void setup() {
	SequentialBehaviour sequentialBehaviour = new SequentialBehaviour(this);
	sequentialBehaviour.addSubBehaviour(new Recive());
	sequentialBehaviour.addSubBehaviour(new Send());
	addBehaviour(sequentialBehaviour);
	}
	public class Recive extends OneShotBehaviour{
		private static final long serialVersionUID = 1L;
		public void action() {
				InsertJSFrame inJFrame = new InsertJSFrame((AgenteReceptor) myAgent);
				inJFrame.setVisible(true);
				String ecc = "";
				while("".equals(ecc)){
					ecc = inJFrame.getString();
					try{			
						Thread.sleep(100);
					} catch(Exception e){
						e.printStackTrace();
					}
				}
				inJFrame.resetString();
				
				System.out.println("F: Ecuacion leida: " + ecc);
				
				ACLMessage mensaje = new ACLMessage(ACLMessage.INFORM);
				mensaje.addReceiver(new AID("AgenteReceptor",AID.ISLOCALNAME));
				mensaje.setContent(ecc);
				this.myAgent.send(mensaje);
				
			}
	}

	public class Send extends OneShotBehaviour{	
		private static final long serialVersionUID = 1L;
		public void action(){
			try{
				ACLMessage msg = this.myAgent.blockingReceive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));
				if(msg!=null){
					String ecuacion = msg.getContent();
					
					ACLMessage mensaje = new ACLMessage(ACLMessage.REQUEST); 	
					mensaje.addReceiver(new AID("AgenteCalculo",AID.ISLOCALNAME));
					
					System.out.println("F: Ecuacion antes de mandarla " + ecuacion);
					
					mensaje.setContent(ecuacion);
					this.myAgent.send(mensaje);
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
