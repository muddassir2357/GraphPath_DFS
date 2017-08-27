package com.paytm.fulfilment.stateTransition;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class State {
	
	private String methodName;
	private String name;
	private List<State> connections;
	public boolean isVisited;
	
	public State(String name,String methodName){
		
		this.methodName=methodName;	
		this.name=name;
		isVisited=false;
		connections= new ArrayList<State>();
		
	}

	
}
