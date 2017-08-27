package com.paytm.fulfilment.stateTransition;

import lombok.Data;

@Data
public class States {
	
	private State authorize;
	private State acknowledgeOrder;
	private State shipmentCreated;
	private State readytoShip;
	private State manifestRequired;
	private State shipped;
	private State delivered;
	private State cancel;
	
	

}
