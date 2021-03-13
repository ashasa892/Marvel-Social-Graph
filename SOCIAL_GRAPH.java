import java.io.*;
import java.util.ArrayList;
import java.util.Vector;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Iterator;
import java.util.Map;



public class SOCIAL_GRAPH {

	private static int V; // total number of nodes

	// To be used for sorting vector of vector of integers
	static class vector_integer implements Comparable{
		public Vector<Integer> v;

		public vector_integer(Vector<Integer> v) {
			this.v = v;
		}

		public vector_integer() {
			v = new Vector<Integer>();
		}

		public int compareTo(Object item) {
			Vector<Integer> v1 = ((vector_integer) item).v;
			int n = v.size(), n1 = v1.size();
			if (n > n1) return 1;
			if (n < n1) return -1;
			for (int i=0; i<n; i++) {
				if (v.get(i) > v1.get(i)) return 1;
				if (v.get(i) < v1.get(i)) return -1;
			}
			return 0;
		}
	}
	
	// Edge between 2 characters.. value denotes the number of co-occurences of both the characters
	static class Edge{
		public int target, value;

		public Edge(int target, int value) {
			this.target = target;
			this.value = value;
		}
	}


	/**
		@param line - Input line
		@return vector of values read from line
	 */
	private static Vector<String> read_line(String line) {

		String cur = "";
		boolean is_inside_quotes = false;
		Vector<String> values = new Vector<String>();

		for (int i=0; i<line.length(); i++) {
			char charac = line.charAt(i);
			if (charac == ',' && !is_inside_quotes) {
				values.add(cur);
				cur = "";
				continue;
			}
			if (charac == '\"') {
				is_inside_quotes ^= true;
			} else {
				cur += charac;
			}
			
		}

		values.add(cur);

		return values;
	}

	// Generic Merge Sort (To be used for sorting vector<Integer> and vector<vector<Integer> >)
	private static <T extends Comparable<T>> void mergeSort(Vector<T> v) {
		int n = v.size();

		if (n <= 1) {
			return;
		}

		Vector<T> v1 = new Vector<T>();
		Vector<T> v2 = new Vector<T>();

		for (int i=0; i<n/2; i++) v1.add(v.get(i));
		for (int i=n/2; i<n; i++) v2.add(v.get(i));

		mergeSort(v1);
		mergeSort(v2);

		// Merge Step
		v.clear();

		int i=0, j=0, n1=v1.size(), n2=v2.size();
		while (i<n1 && j<n2) {
			
			if (compare(v1.get(i), v2.get(j))) {
				v.add(v1.get(i));
				i += 1;
			} else {
				v.add(v2.get(j));
				j += 1;
			}
		}
		while (i<n1) {
			v.add(v1.get(i));
			i += 1;
		}
		while (j<n2) {
			v.add(v2.get(j));
			j += 1;
		}
	}


	private static boolean compare(Comparable o1, Object o2) {

		return o1.compareTo(o2)>0 ;

	}

	// Function to find and print the average of number of times a character appeared with another character
	private static void average(Vector<Edge> adj[]) {
		double avg = 0;
		for (int i=0; i<V; i++) avg += adj[i].size();
		avg /= V;
		avg = Math.round(avg*100.0)/100.0;
		System.out.println(String.format("%.2f", avg));
	}


	// Function to sort the characters according to number of co-occurrences with other characters (Most popular character should come first)
	private static Vector<vector_integer > rank(Vector<Edge> adj[]) {
		Vector<vector_integer> rank_p = new Vector<vector_integer>();
		for (int i=0; i<V; i++) {
			int occurs = 0;
			for (Edge e : adj[i]) occurs += e.value;
			Vector<Integer> v = new Vector<Integer>();

			v.add(occurs);
			v.add(i);
			rank_p.add(new vector_integer(v));
		}
		mergeSort(rank_p);
		return rank_p;
	}

	// Dfs to find the connected component (A connected component defines independent storylines)
	private static void dfs(Vector<Edge> adj[], boolean vis[], int cur, vector_integer comp) {
		vis[cur] = true;
		comp.v.add(cur);
		for (Edge nxt : adj[cur]) {
			if (!vis[nxt.target]) {
				dfs(adj, vis, nxt.target, comp);
			}
		}
	}

	private static void print_util(String s) {
        System.out.print(s);
	}

	public static void main(String[] args) throws Exception {

		long start = System.currentTimeMillis();

		if (args.length < 3) {
			System.out.println("Insufficient arguments");
			return;
		}

		String node_path = args[0], edge_path = args[1], operation = args[2];

		Scanner sc1 = new Scanner(new File(node_path));
		Scanner sc2 = new Scanner(new File(edge_path));

		sc1.useDelimiter("\n");
		sc2.useDelimiter("\n");

		// Input nodes
		Vector<String> nodes = new Vector<String>();
		if (sc1.hasNext()) sc1.nextLine();
		int count = 0;
		while (sc1.hasNext()) {
			Vector<String> values = read_line(sc1.nextLine());
			nodes.add(values.get(1));
		}

		mergeSort(nodes);
	    V = nodes.size(); 
	    // System.out.println(nodes);
		
		HashMap<String, Integer> node_map = new HashMap<>();
		for (int i=0; i<V; i++) {
			node_map.put(nodes.get(i), V-i-1);
		}


		// Input Edges
		Vector<Edge> [] adj;
		adj = new Vector[V];
		for (int i=0; i<V; i++) adj[i] = new Vector<Edge>();

		if (sc2.hasNext()) sc2.nextLine();
		while (sc2.hasNext()) {
			String temp = sc2.nextLine();
			Vector<String> values = read_line(temp);
			// System.out.println(values.get(0));
			int u = node_map.get(values.get(0)), 
				v = node_map.get(values.get(1)), 
				val = Integer.parseInt(values.get(2));

			adj[u].add(new Edge(v, val));
			adj[v].add(new Edge(u, val));
		}

		// average(adj);

		if (operation.equals("average")) {
			average(adj);
		}
		else if (operation.equals("rank")) {
			Vector<vector_integer> rank_p = rank(adj);

			for (int i=0; i<rank_p.size(); i++) {
				print_util(nodes.get(V-1-rank_p.get(i).v.get(1)));
				if (i != rank_p.size()-1) System.out.print(",");
			}
			// System.out.println();
		}
		else if (operation.equals("independent_storylines_dfs")) {
			Vector<vector_integer> components = new Vector<vector_integer>();
			boolean vis[] = new boolean[V];
			for (int i=0; i<V; i++) vis[i] = false;
			for (int i=0; i<V; i++) {
				if (!vis[i]) {
					vector_integer comp = new vector_integer();
					dfs(adj, vis, i, comp);
					mergeSort(comp.v);
					components.add(comp);
				}
			}
			mergeSort(components);
			for (vector_integer comp : components) {
				for (int i=0; i<comp.v.size(); i++) {
					print_util(nodes.get(V-1-comp.v.get(i)));
					if (i != comp.v.size()-1) System.out.print(",");
					else System.out.print("\n");
					
				}
				// System.out.println();
			}
		}

		long end = System.currentTimeMillis();
		long elapsedTime = end - start;
		// System.out.println(elapsedTime);



	}

}