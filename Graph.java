package com.paytm.fulfilment.stateTransition;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.paytm.fulfilment.steps.FFStates;

public class Graph {

	private Graph() {

	}

	public States states;

	public static Graph initialize() {

		Graph graph = new Graph();
		graph.states = new States();

		graph.states.setAuthorize(graph.createState(FFStates.AUTHORIZE.toString(), "authorize"));
		graph.states.setAcknowledgeOrder(graph.createState(FFStates.ACKNOLEDGE_ORDER.toString(), "acknowledgeOrder"));
		graph.states.setShipmentCreated(graph.createState(FFStates.SHIPMENT_CREATED.toString(), "shipmentCreation"));
		graph.states.setReadytoShip(graph.createState(FFStates.READY_TO_SHIP.toString(), "markReadyToShip"));
		graph.states.setManifestRequired(graph.createState(FFStates.MANIFEST_REQUIRED.toString(), "createManifest"));
		graph.states.setShipped(graph.createState(FFStates.SHIPPED.toString(), "moveToShipped"));
		graph.states.setDelivered(graph.createState(FFStates.DELIVERED.toString(),"moveToDelievered"));
		graph.states.setCancel(graph.createState(FFStates.CANCEL.toString(), "cancellation"));
		
		
		
		
		graph.addConnection(graph.states.getAuthorize(), graph.states.getAcknowledgeOrder());
		graph.addConnection(graph.states.getAcknowledgeOrder(), graph.states.getShipmentCreated());
		graph.addConnection(graph.states.getShipmentCreated(), graph.states.getReadytoShip());
		graph.addConnection(graph.states.getReadytoShip(), graph.states.getManifestRequired());
		graph.addConnection(graph.states.getManifestRequired(), graph.states.getShipped());
		graph.addConnection(graph.states.getShipped(), graph.states.getDelivered());
		
		graph.addConnection(graph.states.getAuthorize(), graph.states.getCancel());
		graph.addConnection(graph.states.getAcknowledgeOrder(), graph.states.getCancel());
		graph.addConnection(graph.states.getShipmentCreated(), graph.states.getCancel());
		graph.addConnection(graph.states.getReadytoShip(), graph.states.getCancel());
		graph.addConnection(graph.states.getManifestRequired(), graph.states.getCancel());

		

		return graph;
	}

	public State createState(String name, String methodName) {

		return new State(name, methodName);
	}

	public void addConnection(State forState, State ofState) {

		forState.getConnections().add(ofState);
	}

	public List<ArrayList<State>> findPath(State fromState, State toState) {
		Stack<State> stack = new Stack<State>();
		State node = fromState;
		
		List<ArrayList<State>> paths = new ArrayList<ArrayList<State>>();
		ArrayList<State> path = null;
		boolean found = false;

		while (node != null || !stack.empty()) {

			if (found) {
				node = null;
				if (!stack.empty()) {
					node = stack.pop();
					for (ArrayList<State> p : paths) {
						p.add(node);
					}
					
					if (getNextState(node) != null) {
						paths.addAll(findPath(node, toState));
					}
					toState.isVisited = false;
				}

			} else {
				node.isVisited = true;
				if (node.getName().equals(toState.getName())) {
					
					path = new ArrayList<State>();
					paths.add(path);
					path.add(node);
					found = true;
				} else if (node != null && getNextState(node) != null) {
					
					stack.push(node);
					node = getNextState(node);
					toState.isVisited = false;

				} else {
					node=null;
					if(!stack.isEmpty())
						node = stack.pop();

				}

			}
		}

		return paths;

	}

	private State getNextState(State state) {

		List<State> list = state.getConnections();

		for (State s : list) {
			if (!s.isVisited) {
				return s;
			}
		}
		return null;
	}

	public static void main(String[] args) {

		Graph graph = Graph.initialize();
		List<ArrayList<State>> l = graph.findPath(graph.states.getAuthorize(), graph.states.getCancel());

		for (ArrayList<State> al : l) {
			System.out.println();
			for (State s : al) {
				System.out.print(s.getName()+" ,");
			}
		}
	}

}
