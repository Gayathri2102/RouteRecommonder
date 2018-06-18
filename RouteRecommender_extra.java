import java.awt.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Scanner;

/**
 * 
 */

/**
 * @author Gayathri
 *
 */
public class RouteRecommender implements Tester {

	private ArrayList<String> output = new ArrayList<String>();
	private HashMap<String,AdjacentList> graph = new HashMap<String,AdjacentList>();
	private HashMap<Integer,Integer> bus = new HashMap<Integer,Integer>();
	private class AdjacentList {
		public boolean visited = false;
		ArrayList<Adjacent> list = new ArrayList<Adjacent>();
	}
	private class Adjacent {
		public String stop;
		public int route, arr, cost;
		Adjacent (String stop, int route, int arr, int cost) {
			this.stop = stop;
			this.route = route;
			this.arr = arr;
			this.cost = cost;
		}
	}
	private class Node {
		public String stop;
		public Node parent;
		public int route, arr, cost;
		Node (String stop, Node parent, int route, int arr, int cost) {
			this.stop = stop;
			this.parent = parent;
			this.route = route;
			this.arr = arr;
			this.cost = cost;
		}
	}
	private class NodeCompare implements Comparator < Node > {
		@Override
		public int compare(Node n1, Node n2) {
			return n1.cost-n2.cost;
		}
	}
		
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Scanner objScanner = new Scanner(System.in);
		ArrayList<String> printOutput  = new RouteRecommender().compute(objScanner);
		
		for (int i = 0; i < printOutput.size(); i++) {
			System.out.println(printOutput.get(i));
		}
	}

	/* (non-Javadoc)
	 * @see Tester#compute(java.util.Scanner)
	 */
	@Override
	public ArrayList<String> compute(Scanner log) {
		String stop;
		String prev;
		int route, arr, cost, interval;
		ArrayList<String> result;
		String[] line = log.nextLine().split(" ");
		while (!line[0].equals("end")) {
			if (line[0].contains("route"))	{
				route = Integer.parseInt(line[1]);
				interval = Integer.parseInt(line[2]);
				bus.put(route, interval);
				prev = "";
				arr = 0;
				cost = 0;
				line = log.nextLine().split(" ");
				while (!line[0].equals("end")) {
					stop = line[0];
					arr += cost;
					cost = Integer.parseInt(line[1]);
					if (!graph.containsKey(stop)) {
						graph.put(stop,new AdjacentList());
					}
					if (!prev.isEmpty()) {
						graph.get(prev).list.add(new Adjacent(stop, route, arr%interval, cost));
					}
					prev = stop;
					line = log.nextLine().split(" ");
				}
				line = log.nextLine().split(" ");
			}
		}
		line = log.nextLine().split(" ");
		while (!line[0].equals("end")) {
			result = path(line[0],line[1],Integer.parseInt(line[2]));
			for(int i = result.size()-1; i >= 0; i--) {
				output.add(result.remove(i));
			}
			line = log.nextLine().split(" ");
			for (AdjacentList key : graph.values()) {
				key.visited = false;
			}
		}
		return output;
	}
	ArrayList<String> path(String src, String dst, int arr) {
		ArrayList<Node> nodes = new ArrayList<Node>();
		ArrayList<String> result = new ArrayList<String>();
		int route = 0 , wait, cur = arr;
		Node move = new Node(src, null, 0, 0, 0);
		while(!src.equals(dst)) {
			for (Adjacent next : graph.get(src).list) {
				wait = (int) Math.ceil(((double)cur - next.arr)/bus.get(next.route))*bus.get(next.route)+next.arr;
				nodes.add(new Node(next.stop, move, next.route, cur, wait+next.cost));
			}
			Collections.sort(nodes,new NodeCompare());
			graph.get(src).visited = true;
			while (graph.get(src).visited) {
				move = nodes.remove(0);
				src = move.stop;
			}
			cur = move.cost;
		}
		result.add("Get off at stop "+move.stop);
		route = move.route;
		while (move.parent.parent != null){
			move = move.parent;
			if (route == move.route) {
				continue;
			}
			result.add("At stop " + move.stop + " switch to bus #" + route);
			route = move.route;
		}
		result.add("At stop " + move.parent.stop + " take bus #" + route);
		return result;
	}
}
