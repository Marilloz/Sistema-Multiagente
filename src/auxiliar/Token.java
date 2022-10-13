 package auxiliar;

import  java.io.Serializable;

public class Token implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private int clave;
	private double atributo; // SOLO NUMEROS
	
	public Token(int c, double v) {
		clave = c;
		atributo = v;
	}
	
	public int getClave() {
		return clave;
	}
	
	public void setClave (int c) {
		clave = c;
	}
	
	public double getAtributo() {
		return atributo;
	}
	
	public void setAtributo (double a) {
		atributo = a;
	}
	
	String[] nombres = {"vacio","Numero","+","-","*","/","^","s","(",")","X","="};
	public String toString(){
		if(this.clave == 1){
			return "{" + atributo + "}";
		}
		else{
			return "{" + nombres[this.clave] +"}";
		}
	}
	// 1 = Numero
	// 2 = +
	// 3 = -
	// 4 = *
	// 5 = /
	// 6 = ^
	// 7 = s (Raiz cuadrada)
	// 8 = (
	// 9 = )
	//10 = X
	//11 = =
	

}
