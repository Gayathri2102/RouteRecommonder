/** */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Route recommender that takes individual bus routes as inputs 
 * and transit points. Outputs the shortest route between it.
 * The Summarizer provides a methods:
 *  ArrayList<String> compute(Scanner log) : Takes a scanner stream of text and
 *  											provides shortest route as output
 * @author  Gayathri Balasubramanian
 */
public class RouteRecommender implements Tester {

	/**
	 * Edge in the graph. Contains a single stop to stop information. 
	 */
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
	
	/**
	 * List of edges for graph node and the visited flag. 
	 * Contains all the stops that can be reached from current stop. 
	 */
	private class AdjacentList {
		public boolean visited = false;
		ArrayList<Adjacent> list = new ArrayList<Adjacent>();
	}
	
	/**
	 * Single node of the priority queue used in path generation. 
	 */
	private class Q_Node {
		public String stop;
		public Q_Node parent;
		public int route, cost;
		Q_Node (String stop, Q_Node parent, int route, int cost) {
			this.stop = stop;
			this.parent = parent;
			this.route = route;
			this.cost = cost;
		}
	}
	
	//Graph to store the routes.
	private HashMap<String,AdjacentList> graph = new HashMap<String,AdjacentList>();
	private HashMap<Integer,Integer> bus = new HashMap<Integer,Integer>();
	//Output list of shortest path.
	private ArrayList<String> output = new ArrayList<String>();
	
	/**
	 * Main method to start the execution using input from console.
	 * Creates an object and calls the compute method and prints output.
	 * @param args
	 */
	public static void main(String[] args) {
		Scanner objScanner = new Scanner(System.in);
		ArrayList<String> printOutput  = new RouteRecommender().compute(objScanner);
		
		for (int i = 0; i < printOutput.size(); i++) {
			System.out.println(printOutput.get(i));
		}
	}

	/** (non-Javadoc)
	 * @see Tester#compute(java.util.Scanner)
	 */
	@Override
	public ArrayList<String> compute(Scanner log) {
		BuildGraph (log);
		return Recommend (log);
	}
	
	/**
	 * Build graph of the routes from the bus details provided.
	 * @param log
	 */
	private void BuildGraph (Scanner log)
	{
		String stop;
		String prev;
		int route, arr, cost, interval;
		
		String[] line = log.nextLine().split(" ");
		//Loop for each bus route
		while (!line[0].equals("end")) {
			if (line[0].contains("route"))	{
				route = Integer.parseInt(line[1]);
				interval = Integer.parseInt(line[2]);
				bus.put(route, interval);
				prev = "";
				arr = 0;
				cost = 0;
				line = log.nextLine().split(" ");
				//Loop for each stop in a bus route
				while (!line[0].equals("end")) {
					stop = line[0];
					arr += cost;
					cost = Integer.parseInt(line[1]);
					// Create a node for each new stop
					if (!graph.containsKey(stop)) {
						graph.put(stop,new AdjacentList());
					}
					//Non-first stop in a route, add edge in graph
					if (!prev.isEmpty()) {
						graph.get(prev).list.add(new Adjacent(stop, route, arr%interval, cost));
					}
					prev = stop;
					line = log.nextLine().split(" ");
				}
				line = log.nextLine().split(" ");
			}
		}
	}
	
	/**
	 * Recommends the shortest path between transit locations provided.
	 * @param log
	 */
	private ArrayList<String> Recommend (Scanner log)
	{
		ArrayList<String> path;
		Q_Node dst_node;
		String[] line = log.nextLine().split(" ");
		
		//For each pair of transit locations 
		while (!line[0].equals("end")) {
			dst_node = ShortestPath(line[0],line[1],Integer.parseInt(line[2]));
			path = TracePath (dst_node);
			//Reverse the path identified after reaching destination
			for(int i = path.size()-1; i >= 0; i--) {
				output.add(path.remove(i));
			}
			line = log.nextLine().split(" ");
			//Reset visited flag to use it for next recommendation
			for (AdjacentList key : graph.values()) {
				key.visited = false;
			}
		}
		return output;
	}
	
	/**
	 * Reach the destination using shortest path, starting from the time given.
	 * Uses min-heap based implementation of priority queue. 
	 * @param src: starting/orgination/source stop
	 * 		  dst: ending/terminating/destination stop
	 * 		  arr: arrival time/ staring time of the journey
	 */
	Q_Node ShortestPath(String src, String dst, int arr) {
		Q_Node []queue = new Q_Node[(int) Math.pow(graph.size(),2)];
		int cost, cur = arr, size = 0, i, tmp;
		Q_Node move = new Q_Node(src, null, 0, 0);
		while(!src.equals(dst)) {
			for (Adjacent next : graph.get(src).list) {
				//Finding the cost of travel based on the wait time by interval 
				//the distance to next stop. 
				cost = (int) Math.ceil(((double)cur - next.arr) / bus.get(next.route))
						* bus.get(next.route) + next.arr + next.cost;
				//Add new node to queue and re-arrange min-heap
				InsertShiftUp(queue, ++size, 
						new Q_Node(next.stop, move, next.route, cost));
			}
			graph.get(src).visited = true;
			//Remove shorted unvisited stop from min-heap and rearrange
			while (graph.get(src).visited) {
				move = queue[1];
				DeleteShiftDown(queue, size--);
				src = move.stop;
			}
			cur = move.cost;
		}
		return move;
	}
	
	/**
	 * Trace the path back from the destination reached by shortest route. 
	 * @param dst: Destination node containing parent links to previous stops.
	 */
	private ArrayList<String> TracePath (Q_Node dst_node)
	{
		ArrayList<String> result = new ArrayList<String>();
		int route;
		
		//Print Destination to output
		result.add("Get off at stop "+dst_node.stop);
		route = dst_node.route;
		while (dst_node.parent.parent != null){
			dst_node = dst_node.parent;
			//Print every bus route change along the travel
			if (route == dst_node.route) {
				continue;
			}
			result.add("At stop " + dst_node.stop + " switch to bus #" + route);
			route = dst_node.route;
		}
		//Print the starting point information.
		result.add("At stop " + dst_node.parent.stop + " take bus #" + route);
		
		return result;
	}
	
	/**
	 * Insert node at the end and rearrange in min-heap. 
	 * @param queue: Priority queue formed on min-heap.
	 * 		  size: Size of the priority queue. 
	 * 		  Node: New node to be inserted.
	 */
	private void InsertShiftUp(Q_Node[] queue,int size, Q_Node node) {
		int i;
		for (i = size; (i)/2>0; i=(i)/2) {
			if (queue[(i)/2].cost < node.cost) {
				break;
			}
			queue[i]=queue[(i)/2];
		}
		queue[i] =  node;
	}
	
	/**
	 * Delete node at the top and rearrange in min-heap
	 * by moving last element to fill the gap. 
	 * @param queue: Priority queue formed on min-heap.
	 * 		  size: Size of the priority queue.
	 */
	private void DeleteShiftDown(Q_Node[] queue,int size) {
		int i, tmp;
		for (i = 1; i*2<size;i=tmp) {
			tmp = queue[i*2].cost<queue[i*2+1].cost?i*2:i*2+1;
			queue[i]=queue[tmp];
		}
		queue[i] = queue[size];
		queue[size--]=null;
	}
}
