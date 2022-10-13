package agenteGUI;
import auxiliar.TriPair;
import gui.ResultsJFrame;
import jade.core.Agent;

import jade.core.behaviours.*;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class AgenteGUI extends Agent{

	private static final long serialVersionUID = 1L;

	protected void setup() {
        OneShotBehaviour recive = new CyclicBehaviourRecieveMessage();
        addBehaviour(recive);
    }
    public class CyclicBehaviourRecieveMessage extends OneShotBehaviour{
    private static final long serialVersionUID = 1L;
        public void action() {
            try {
                ACLMessage msg = this.myAgent.blockingReceive(MessageTemplate.MatchPerformative(ACLMessage.REQUEST));
                if(msg != null) {
                   	TriPair t =(TriPair) msg.getContentObject();
                   	ResultsJFrame resJFrame = new ResultsJFrame((AgenteGUI) myAgent, t.getEcuacion(), t.getRes());
                   	resJFrame.setVisible(true);
                }else {
                    this.block(); 
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        public int onEnd(){
            reset();
            return super.onEnd();
        }
    }


    protected void onGuiEvent(GuiEvent arg0) {
        // TODO Auto-generated method stub

    }

    public char[] getPrediction() {
        // TODO Auto-generated method stub
        return null;
    }

}