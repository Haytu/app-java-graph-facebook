import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;

import org.graphstream.algorithm.*;
import org.graphstream.graph.Path;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.*;

public class Graph extends DefaultGraph implements GraphInterface {

	public Graph(String id) {
		super(id);
	}
	
	/* Parse et crée le graphe d'après le fichier .edges du dataset ego-twitter de Snap */
	public void init(String filename) {
		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
		    String line;
		    
		    /* Pour chaque ligne du fichier */
		    while ((line = br.readLine()) != null) {
		    	String[] data = line.split(" ");
		    	String Node1 = data[0];
		    	String Node2 = data[1];
		    	
		    	/* Si le noeud n'existe pas */
		    	if(this.getNode(Node1) == null) {
		    		this.addNode(Node1);
		    	}
		    	
		    	if(this.getNode(Node2) == null) {
					this.addNode(Node2);
	    		}
		    		
		    	/* Ajoute l'arête entre les deux noeuds */
				this.addEdge(Node1 + Node2, Node1, Node2);
				
		    }
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/* Retourne la matrice d'ajacence du graphe */
	public byte[][] getAdjacencyMatrix() {
		int n = this.getNodeCount();
		byte adjacencyMatrix[][] = new byte[n][n];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				adjacencyMatrix[i][j] = (byte)(this.getNode(i).hasEdgeBetween(j) ? 1 : 0);
			}
		}
		return adjacencyMatrix;
	}
	
	/* Retourne le nombre de composantes connexes */
	public int getConnectedComponentsCount() {
		ConnectedComponents cc = new ConnectedComponents();
		cc.init(this);
		return cc.getConnectedComponentsCount();
	}

	/* Retourne si oui ou non le graphe est connexe */
	public boolean isConnected() {
		return this.getConnectedComponentsCount() < 2;
	}

	/* Retourne le plus court chemin entre node1 et node2 selon l'algorithme A* */
	public Path getShortestPath(Node node1, Node node2) {
		AStar astar = new AStar(this);
 		astar.compute(node1.getId(), node2.getId());
 		return astar.getShortestPath();
	}

	/* Retourne les voisins du noeud node en respectant le sens des arcs */
	public HashSet<Node> getNeighbourhood(Node node) {
		HashSet<Node> neighbors = new HashSet<Node>();
		
		/* Pour tous les arcs "sortants" du noeud node */
		/*Iterator<Edge> ite = node.getLeavingEdgeIterator();
		while(ite.hasNext()) {
			/* Ajoute le noeud "à l'opposé" du noeud node */
			/*neighbors.add(ite.next().getOpposite(node));
		}*/
		
		return neighbors;
	}

	/* Retourne le diamètre du graphe : la plus grande distance géodésique possible entre 2 sommets */
	/** O(n*(n+m)) **/
	public int getDiameter() {
		return (int)Toolkit.diameter(this);
	}

	/* Retourne la densité du graphe : le rapport entre le nombre d'arêtes observées et le nombre maximal d'arêtes possibles */
	/** O(1) **/
	public double getDensity() {
		return Toolkit.density(this);
	}

	/* Retourne la force d'un lien entre node1 et node2 : voisinage commun / voisinage total - 2 */
	public float getForce(Node node1, Node node2) {
		HashSet<Node> node1Neighbors = this.getNeighbourhood(node1);
		HashSet<Node> node2Neighbors = this.getNeighbourhood(node2);

		/* Intersection */
		HashSet<Node> commonNeighbors = new HashSet<Node>(node1Neighbors);
		commonNeighbors.retainAll(node2Neighbors);

		/* Union */
		HashSet<Node> allNeighbors = new HashSet<Node>(node1Neighbors);
		allNeighbors.addAll(node2Neighbors);
		
		return (float)(commonNeighbors.size()/(allNeighbors.size() - 2.0));
	}

	/* Retourne le degré de centralité du noeud node en terme de voisinage : degré / n-1 */
	/** O(1) **/
	public float getCentralityByNeighborhood(Node node) {
		return (float)(node.getDegree()/(this.nodeCount-1.0));
	}
	
	/* Retourne la somme des distances des plus courts chemins entre tous les noeud vers le noeud node */
	public float getSumDistancesShortestPathTo(Node node) {
		float sumDistancesToNode = 0;
		/*for(Node currentNode : this.getEachNode()) {
			Path shortestPath = this.getShortestPath(currentNode, node);
			if(shortestPath != null) {
				sumDistancesToNode += shortestPath.size();
			}
		}*/
		return sumDistancesToNode;
	}
	
	/* Retourne le degré de centralité moyenne du noeud node en terme de distance : plus la valeur est faible, plus le noeud est central */
	public float getAverageCentrality(Node node) {
		float sumDistancesToNode = this.getSumDistancesShortestPathTo(node);
		return (float)((1.0/(this.nodeCount-1.0))*sumDistancesToNode);
	}

	/* Retourne le degré de centralité de proximité : plus la valeur est forte, plus le noeud est central */
	public float getClosenessCentrality(Node node) {
		float sumDistancesToNode = this.getSumDistancesShortestPathTo(node);
		return (float)((this.nodeCount-1.0)/sumDistancesToNode);
	}

	/*** TODO ***/
	public float getBetweennessCentrality(Node node) {
		return (float)0.0;
	}
}
