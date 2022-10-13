package auxiliar;

import  java.io.Serializable;
import 	java.util.List;

public class TriPair implements Serializable{ 

	private static final long serialVersionUID = 1L;

	private String res;
	
	private String ecuacion;
	
	private List<Token> tokenList;

	public TriPair(String ecuacion, List<Token> tokenList, String res){
		this.res = res;
		this.ecuacion = ecuacion;
		this.tokenList = tokenList;
	}

	public String getRes(){
		return res;
	}
	
	public void setRes(String res){
		this.res = res;
	}
	
	public String getEcuacion(){
		return ecuacion;
	}
	
	public void setEcuacion(String ecuacion){
		this.ecuacion = ecuacion;
	}
	
	public List<Token> getTokenList(){
		return tokenList;
	}
	
	public void setTokenList(List<Token> tokenList){
		this.tokenList = tokenList;
	}

}
