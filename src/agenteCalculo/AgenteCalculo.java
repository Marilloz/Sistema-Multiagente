package agenteCalculo;

import jade.core.AID;
import jade.core.Agent;

import java.util.ArrayList;
import java.util.List;
import auxiliar.*;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;



public class AgenteCalculo extends Agent {
	private static final long serialVersionUID = 1L;

	protected void setup() {	
		SequentialBehaviour sequentialBehaviour = new SequentialBehaviour(this);
		sequentialBehaviour.addSubBehaviour(new CyclicBehaviourRecieveMessage());
		sequentialBehaviour.addSubBehaviour(new BehaviourGenToken());
		sequentialBehaviour.addSubBehaviour(new BehaviourComprobarTokens());
		sequentialBehaviour.addSubBehaviour(new BehaviourComprobarTokensCalculo());
		sequentialBehaviour.addSubBehaviour(new Send());
		addBehaviour(sequentialBehaviour);
	}

	public class CyclicBehaviourRecieveMessage extends OneShotBehaviour{

		private static final long serialVersionUID = 1L;
		public void action() {
			try {
				ACLMessage msg = this.myAgent.blockingReceive(MessageTemplate.MatchPerformative(ACLMessage.REQUEST));
				if(msg != null) {
					String ecuacion = msg.getContent();
					System.out.println("J: ecuacion = " + ecuacion);

					ACLMessage mensaje = new ACLMessage(ACLMessage.INFORM);
					mensaje.addReceiver(new AID("AgenteCalculo",AID.ISLOCALNAME));
					mensaje.setContent(ecuacion);
					this.myAgent.send(mensaje);

				}		
			}
			catch (Exception e) {
				e.printStackTrace();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		public int onEnd(){           
			reset();
			return super.onEnd();
		}

	}

	public class BehaviourGenToken extends OneShotBehaviour{

		private static final long serialVersionUID = 1L;

		public void action() {
			try {
				System.out.println("J: GENTOKEN");
				ACLMessage msg = this.myAgent.blockingReceive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));
				if(msg != null) {
					String ecuacion = msg.getContent();
					List<Token> tokens = new ArrayList<Token>();
					tokens = genTokens(ecuacion);
					System.out.println("J: Tokens Generados = " + tokens);            	

					ACLMessage mensaje = new ACLMessage(ACLMessage.INFORM);
					mensaje.addReceiver(new AID("AgenteCalculo",AID.ISLOCALNAME));
					TriPair t = new TriPair(ecuacion,tokens,"OK");
					mensaje.setContentObject(t);
					this.myAgent.send(mensaje);			
				}
			}
			catch(Exception  e){
				e.printStackTrace();
			}

		} 
		public Token genTok(int key, double atrib){
			return new Token(key,atrib);
		}
		public List<Token> genTokens(String ecuacion){
			List<Token> res = new ArrayList<Token>();

			boolean leido = false;
			for(int i = 0; i<ecuacion.length();){
				Token t  = null;
				char c = ecuacion.charAt(i);
				switch(c){
				case ' ':
					i++;
					continue;
				case '	':
					i++;
					continue;
				case '+':
					t = genTok(2,0);
					break;
				case '-':
					t = genTok(3,0);
					break;
				case '*':
					t = genTok(4,0);
					break;
				case '/':
					t = genTok(5,0);
					break;				
				case '^':
					t = genTok(6,0);
					break;
				case 's':
					t = genTok(7,0);
					break;
				case '(':
					t = genTok(8,0);
					break;
				case ')':
					t = genTok(9,0);
					break;
				case 'x':
					t = genTok(10,0);
					break;
				case '=' :
					t = genTok(11,0);
					break;
				}
				if(c >= '0' && c <= '9'){
					double num = 0;
					int puntos = 0;
					int decimal = 0;
					while((c >= '0' && c <= '9') || c == '.'){
						decimal++;
						if(c == '.'){
							puntos++;
							decimal = 0;
						}
						else{
							int x =  (int) c - 48;
							if(puntos == 0){
								num = num * 10 + x;
							}
							else if(puntos == 1){
								num+= Math.pow(0.1,decimal) * x; 
							}
							else{
								System.err.println("ERROR varios puntos en el numero");
								return null;
							}
						}
						i++;
						if(i<ecuacion.length())
							c = ecuacion.charAt(i);
						else
							break;
					}
					num = Math.round(num*10000.0)/10000.0;  
					t = genTok(1,num);
					leido = true;
				}
				else{
					if(t== null){
						i++;
						continue;
					}
				}
				res.add(t);
				if(!leido){
					i++;
				}
				leido = false;
			}
			return res;
		}
	}

	public class BehaviourComprobarTokens extends OneShotBehaviour{

		private static final long serialVersionUID = 1L;
		public void action() {

			try {
				ACLMessage msg = this.myAgent.blockingReceive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));
				if(msg != null) {

					TriPair t = (TriPair) msg.getContentObject();
					List<Token> tokens = t.getTokenList();
					String ecuacion = t.getEcuacion();
					String res = t.getRes();
					System.out.println("J: CT Llega Tokens = " + tokens);
					System.out.println("J: CT Llega Ecuacion = " + t.getEcuacion());
					if(tokens == null){
						res = "ERROR 1.1";
					}
					else{
						int cp1 = comprobarTokens1(tokens);
						if(cp1 != 0){
							res = "ERROR 2."+ cp1;
						}
						else{
							int cp2 =  comprobarTokens2(tokens);
							if( cp2 != 0){
								res = "ERROR 3." + cp2;
							}
						}

					}

					ACLMessage mensaje = new ACLMessage(ACLMessage.INFORM);
					mensaje.addReceiver(new AID("AgenteCalculo",AID.ISLOCALNAME));
					t = new TriPair(ecuacion,tokens,res);
					mensaje.setContentObject(t);
					this.myAgent.send(mensaje);
					System.out.println("J: RES = " + res);
				}		
			}
			catch (Exception e) {
				e.printStackTrace();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

	public int comprobarTokens1(List<Token> tokens){
		int variables = 0;
		int parentesis = 0;
		int iguales = 0;
		if(tokens.size()<3){
			System.err.println("ERROR pocos tokens");
			return 1; //Se introducen menos de 3 tokens
		}
		if(tokens.get(tokens.size() - 2).getClave() != 11){
			System.err.println("ERROR penultimo no es un igual");
			return 2; //El penultimo token no es el "="
		}
		if(tokens.get(tokens.size() - 1).getClave() != 1 && 
				tokens.get(tokens.size() - 1).getClave() != 10){
			System.err.println("ERROR ultimo no es un numero o una x");
			return 3; //El ultimo token no es un numero o una X
		}
		for(Token tok: tokens){
			if(tok.getClave() == 8){
				parentesis++;
			}
			else if(tok.getClave() == 9){
				parentesis--;
			}
			else if(tok.getClave() == 10){
				variables++;
			}
			else if(tok.getClave() == 11){
				iguales++;
			}

			if(parentesis > 1 || parentesis < -1){
				System.err.println("ERROR nº parentesis incorrecto");
				return 4; //Se han introducido mal el numero de parentesis
			}
			if(iguales == 0 && tok.getClave() == 10){
				System.err.println("ERROR la X esta a la izquierda del Igual");
				return 5; //Existe una X a la izquierda del igual
			}			
		}
		if(iguales != 1){
			System.err.println("ERROR nº iguales incorrecto");
			return 6; //Existen 0 o >1 iguales
		}
		if(variables > 1 ){
			System.err.println("ERROR varias variables");
			return 7; //Existe >1 variable
		}
		if(parentesis != 0){
			System.err.println("ERROR nº parentesis incorrecto");
			return 4; //Se han introducido mal el numero de parentesis
		}
		return 0;		
	}

	public int comprobarTokens2(List<Token> tokens){
		boolean numero  = true;
		for(int i = 0; i<tokens.size()- 2;){
			Token t = tokens.get(i);
			if(numero){
				if(t.getClave() != 1 && t.getClave() != 3 && t.getClave() != 7 && t.getClave() != 8){
					System.err.println("0: Token no esperado, se esperaba algo que no sea una operacion");	
					return 1;
				}
				else if(t.getClave() == 3){
					Token t2 = tokens.get(i + 1);
					int clave = t2.getClave();
					if(clave!=1 && clave!=7 && clave!=8){
						System.err.println("1: Token no esperado, Se esperaba un Numero, una Raiz cuadrada o un Parentesis");	
						return 2;
					}
					if(clave == 1){
						t2.setAtributo(-1*t2.getAtributo());
						tokens.remove(i);
					}
					if(clave == 7){
						Token t3 = tokens.get(i + 2);
						int clave2 = t3.getClave();
						if(clave2!=1 && clave2!=8){
							System.err.println("2: Token no esperado, se esperaba un Numero o un parentesis");	
							return 3;
						}
						if(clave2 == 1){
							t3.setAtributo(-1*Math.sqrt(t3.getAtributo()));
							tokens.remove(i);
							tokens.remove(i);
						}
						if(clave == 8){
							List<Token> sublista = new ArrayList<Token>();
							tokens.remove(i);
							while (tokens.get(i).getClave() != 9) {
								sublista.add(tokens.get(i));
								tokens.remove(i);
							}
							tokens.remove(i);
							tokens.add(i, funcionParentesis(sublista));
							//funcion parentesis
						}
					}


				}
				else if(t.getClave() == 7){
					Token t2 = tokens.get(i + 1);
					int clave = t2.getClave();
					if(clave!=1 && clave!=8){
						System.err.println("3: Token no esperado, se esperaba un Numero o un parentesis");	
						return 3;
					}
					if(clave == 1){
						t2.setAtributo(Math.sqrt(t2.getAtributo()));
						tokens.remove(i);
					}
					if(clave == 8){
						List<Token> sublista = new ArrayList<Token>();
						tokens.remove(i);
						while (tokens.get(i).getClave() != 9) {
							sublista.add(tokens.get(i));
							tokens.remove(i);
						}
						tokens.remove(i);
						tokens.add(i, funcionParentesis(sublista));
					}

				}
				else if(t.getClave() == 8){
					List<Token> sublista = new ArrayList<Token>();
					tokens.remove(i);
					while (tokens.get(i).getClave() != 9) {
						sublista.add(tokens.get(i));
						tokens.remove(i);
					}
					tokens.remove(i);
					tokens.add(i, funcionParentesis(sublista));
				}

				numero = !numero;
			}
			else{
				if(t.getClave() == 1 || t.getClave() == 7 || t.getClave() == 8){
					System.err.println("X: Token no esperado, Se esperaba una operacion");	
					return 4;
				}

				numero = !numero;	
			}

			i++;
		}
		return 0; 
	}

	public Token funcionParentesis (List<Token> sublista) {
		Token res = new Token(1,0);
		comprobarTokens2(sublista);

		int i = 0;
		// ^

		for(i = 0; i<sublista.size();){
			Token t = sublista.get(i);
			if(t.getClave() == 6){
				Token base = sublista.get(i - 1);
				Token exponente = sublista.get(i + 1);
				double potencia = Math.pow(base.getAtributo(),exponente.getAtributo()); 
				base.setAtributo(potencia);
				sublista.remove(i);
				sublista.remove(i);
				i--;
			}			
			i++;
		}
		// * /


		for(i = 0; i<sublista.size();){
			Token t = sublista.get(i);
			if(t.getClave() == 4){
				Token n1 = sublista.get(i - 1);
				Token n2 = sublista.get(i + 1);
				double producto = n1.getAtributo() * n2.getAtributo(); 
				n1.setAtributo(producto);
				sublista.remove(i);
				sublista.remove(i);
				i--;
			}
			else if(t.getClave() == 5){
				Token dividendo = sublista.get(i - 1);
				Token divisor = sublista.get(i + 1);
				double cociente = dividendo.getAtributo() / divisor.getAtributo();
				dividendo.setAtributo(cociente); 
				sublista.remove(i);
				sublista.remove(i);
				i--;
			}			
			i++;
		}
		// + -



		for(i = 0; i<sublista.size();){
			Token t = sublista.get(i);
			if(t.getClave() == 2){
				Token n1 = sublista.get(i - 1);
				Token n2 = sublista.get(i + 1);
				double resultado = n1.getAtributo() + n2.getAtributo(); 
				n1.setAtributo(resultado);
				sublista.remove(i);
				sublista.remove(i);
				i--;
			}
			else if(t.getClave() == 3){
				Token n1 = sublista.get(i - 1);
				Token n2 = sublista.get(i + 1);
				double resultado = n1.getAtributo() - n2.getAtributo();  
				n1.setAtributo(resultado);
				sublista.remove(i);
				sublista.remove(i);
				i--;
			}			
			i++;
		}


		if(sublista.size() == 1){
			res = sublista.get(0);
		}
		else{
			System.err.println("ERROR NS QUE");
			return null;
		}
		return res;
	}




	public class BehaviourComprobarTokensCalculo extends OneShotBehaviour{
		private static final long serialVersionUID = 1L;
		public void action(){
			try{
				ACLMessage msg = this.myAgent.blockingReceive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));
				if(msg != null) {
					TriPair t =	(TriPair) msg.getContentObject();
					List<Token> tokens = t.getTokenList();
					String res = t.getRes() ;
					String ecuacion = t.getEcuacion() ;
					if(res.length() != 0){
						if(funFin(tokens) != 0){
							res = "ERROR 4.1";
						}
						else{
							res = "" + tokens.get(0).getAtributo() + " = ";
							if(tokens.get(2).getClave() == 1){
								res+= tokens.get(2).getAtributo();
							} 
							else{
								res+= "x";
							}
						}
					}
					ACLMessage mensaje = new ACLMessage(ACLMessage.INFORM);
					mensaje.addReceiver(new AID("AgenteCalculo",AID.ISLOCALNAME));
					t = new TriPair(ecuacion,tokens,res);
					mensaje.setContentObject(t);
					this.myAgent.send(mensaje);

				}
			}catch (Exception e) {
				e.printStackTrace();
			}
		}

		public int funFin(List<Token> tokens){

			int i = 0;
			// ^

			for(i = 0; i<tokens.size();){
				Token t = tokens.get(i);
				if(t.getClave() == 6){
					Token base = tokens.get(i - 1);
					Token exponente = tokens.get(i + 1);
					double potencia = Math.pow(base.getAtributo(),exponente.getAtributo()); 
					base.setAtributo(potencia);
					tokens.remove(i);
					tokens.remove(i);
					i--;
				}			
				i++;
			}
			// * /


			for(i = 0; i<tokens.size();){
				Token t = tokens.get(i);
				if(t.getClave() == 4){
					Token n1 = tokens.get(i - 1);
					Token n2 = tokens.get(i + 1);
					double producto = n1.getAtributo() * n2.getAtributo(); 
					n1.setAtributo(producto);
					tokens.remove(i);
					tokens.remove(i);
					i--;
				}
				else if(t.getClave() == 5){
					Token dividendo = tokens.get(i - 1);
					Token divisor = tokens.get(i + 1);
					double cociente = dividendo.getAtributo() / divisor.getAtributo();
					dividendo.setAtributo(cociente); 
					tokens.remove(i);
					tokens.remove(i);
					i--;
				}			
				i++;
			}
			// + -



			for(i = 0; i<tokens.size();){
				Token t = tokens.get(i);
				if(t.getClave() == 2){
					Token n1 = tokens.get(i - 1);
					Token n2 = tokens.get(i + 1);
					double resultado = n1.getAtributo() + n2.getAtributo(); 
					n1.setAtributo(resultado);
					tokens.remove(i);
					tokens.remove(i);
					i--;
				}
				else if(t.getClave() == 3){
					Token n1 = tokens.get(i - 1);
					Token n2 = tokens.get(i + 1);
					double resultado = n1.getAtributo() - n2.getAtributo();  
					n1.setAtributo(resultado);
					tokens.remove(i);
					tokens.remove(i);
					i--;
				}			
				i++;
			}


			if(!(tokens.size() == 3)){
				System.err.println("ERROR NS QUE");
				return 1;
			}
			return 0;
		}
	}

	public class Send extends OneShotBehaviour{	
		private static final long serialVersionUID = 1L;

		public void action(){
			try{
				ACLMessage msg = this.myAgent.blockingReceive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));
				if(msg != null) {
					TriPair t =	(TriPair) msg.getContentObject();
					List<Token> tokens = t.getTokenList();
					String res = t.getRes() ;
					String ecuacion = t.getEcuacion();
					System.out.println("J: FJ Llega Tokens = " + tokens);
					System.out.println("J: FJ Llega Ecuacion = " + ecuacion);
					System.out.println("J: FJ Llega Res = "+ res);
					
					ACLMessage mensaje = new ACLMessage(ACLMessage.REQUEST); 	
					mensaje.addReceiver(new AID("AgenteGUI",AID.ISLOCALNAME));
					mensaje.setContentObject(t);
					this.myAgent.send(mensaje);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}



}
