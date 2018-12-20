import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashSet;
import java.util.TreeSet;

import org.graphstream.algorithm.*;
import org.graphstream.graph.Path;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.*;

public class Graph extends DefaultGraph implements GraphInterface {
	/* Attributs */
	private HashSet<Node> nodes;
	
	/* Constructeur */
	public Graph(String id) {
		super(id);
		this.nodes = new HashSet<Node>();
	}
	
	/* Getters */
	public HashSet<Node> getNodes() {
		return nodes;
	}

	/* Setters */
	public void setNodes(HashSet<Node> nodes) {
		this.nodes = nodes;
	}

	/* Parse et crée le graphe d'après les fichiers du dataset ego-facebook de Snap (ici dans data) */
	public void init(String filename) {
		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
		    String line;
		    
		    /* Pour chaque ligne du fichier */
		    while ((line = br.readLine()) != null) {
		    	String[] data = line.split(" ");
		    	String node1_str = data[0];
		    	String node2_str = data[1];
		    	
		    	/* Si le noeud n'existe pas */
		    	if(this.getNode(node1_str) == null) {
		    		/* Ajoute le noeud au graphe */
		    		Node node1 = this.addNode(node1_str);
		    		/* Ajoute le noeud à la liste des noeuds */
					this.nodes.add(node1);
		    	}
		    	
		    	if(this.getNode(node2_str) == null) {
					Node node2 = this.addNode(node2_str);
					this.nodes.add(node2);
	    		}
		    		
		    	/* Ajoute l'arête entre les deux noeuds */
				this.addEdge(node1_str + node2_str, node1_str, node2_str);
				
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

	/* Retourne le plus court chemin entre node1 et node2 selon l'algorithme A* */
	public Path getShortestPath(Node node1, Node node2) {
		AStar astar = new AStar(this);
 		astar.compute(node1.getId(), node2.getId());
 		return astar.getShortestPath();
	}

	/* Retourne les voisins du noeud node */
	public HashSet<Node> getNeighbourhood(Node node) {
		HashSet<Node> neighbors = new HashSet<Node>();

		/* Pour toutes les arêtes du noeud node */
		for(int i = 0; i < node.getDegree(); i++) {
			neighbors.add(node.getEdge(i).getOpposite(node));
		}
		return neighbors;
	}
	
	/* Retourne un noeud au hasard du graphe */
	public Node getRandomNode() {
		return Toolkit.randomNode(this);
	}
	
	/* Retourne si oui ou non le graphe est connexe */
	public boolean isConnected() {
		return this.getConnectedComponentsCount() < 2;
	}
	
	/* Retourne le nombre de composantes connexes */
	public int getConnectedComponentsCount() {
		ConnectedComponents cc = new ConnectedComponents();
		cc.init(this);
		return cc.getConnectedComponentsCount();
	}
	
	/* Retourne le degré moyen des noeuds */
	public float getAverageDegree() {
		float sumDegree = 0;
		for(Node node : this) {
			sumDegree += node.getDegree();
		}
		return sumDegree/this.nodeCount;
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
	
	/* Retourne la somme des distances des plus courts chemins entre tous les noeuds vers le noeud node */
	public float getSumDistancesShortestPathTo(Node node) {
		float sumDistancesToNode = 0;

		for (Node currentNode : this) {
			Path shortestPath = this.getShortestPath(currentNode, node);
			if(shortestPath != null) {
				sumDistancesToNode += shortestPath.size();
			}
		}
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

	/* Retourne le noeud ayant le plus grand score de centralité par voisinage */
	/** O(n) **/
	public Node getNodeWithGreaterCentralityByNeighborhood() {
		/* Comparator TreeSet sur les scores de centralité par voisinage */
		Comparator<Node> comp = (Node n1, Node n2) -> (Float.compare(this.getCentralityByNeighborhood(n1), (this.getCentralityByNeighborhood(n2))));
		
		/* On crée un TreeSet à partir de la liste des noeuds de notre graphe */
		TreeSet<Node> nodesSortedByCbyN = new TreeSet<Node>(comp);
		nodesSortedByCbyN.addAll(this.nodes);
		
		/* On retourne le dernier : celui qui a la + grande centralité par voisinage */
		return nodesSortedByCbyN.last();
	}

	/* Retourne le noeud ayant le plus grand score de centralité moyenne */
	/** Trop grande complexité **/
	public Node getNodeWithGreaterAverageCentrality() {
		/* Comparator TreeSet sur les scores de centralité moyenne */
		Comparator<Node> comp = (Node n1, Node n2) -> (Float.compare(this.getAverageCentrality(n1), (this.getAverageCentrality(n2))));
		TreeSet<Node> nodesSortedByAC = new TreeSet<Node>(comp);
		nodesSortedByAC.addAll(this.nodes);
		
		return nodesSortedByAC.first();
	}

	/* Retourne le noeud ayant le plus grand score de centralité de proximité */
	/** Trop grande complexité **/
	public Node getNodeWithGreaterClosenessCentrality() {
		/* Comparator TreeSet sur les scores de centralité de proximité */
		Comparator<Node> comp = (Node n1, Node n2) -> (Float.compare(this.getClosenessCentrality(n1), (this.getClosenessCentrality(n2))));
		TreeSet<Node> nodesSortedByCC = new TreeSet<Node>(comp);
		nodesSortedByCC.addAll(this.nodes);
		
		return nodesSortedByCC.last();
	}
	
	/* Retourne le noeud ayant le plus grand score de centralité intermédiaire */
	/** Trop grande complexité **/
	public Node getNodeWithGreaterBetweennessCentrality() {
		Comparator<Node> comp = (Node n1, Node n2) -> (Double.compare((double)n1.getAttribute("Cb"), ((double)n2.getAttribute("Cb"))));
		TreeSet<Node> nodesSortedByBC = new TreeSet<Node>(comp);
		
		/* Execute l'algorithme de centralité intermédiaire */
		BetweennessCentrality bcb = new BetweennessCentrality();
		bcb.init(this);
		bcb.compute();
				
		nodesSortedByBC.addAll(this.nodes);
		
		return nodesSortedByBC.last();
	}
	
	/* Colore les noeuds en fonction de s'il sont influenceurs ou influencés */
	public void showInfluencers() {
		float averageCentrality = 0;
		
		/* Pour tous les noeuds */
		for(Node node : this) {
			if(node != null) {
				averageCentrality += this.getCentralityByNeighborhood(node);
			}
		}

		averageCentrality /= this.nodeCount;
	     
		/* Pour tous les noeuds */
	    for(Node node : this) {
	    	String color = "";
	    	Node currentNode = node;
	    	float centralityDegree = this.getCentralityByNeighborhood(currentNode);
	    	
	    	/* Code couleur : 
	    	 * les sommets verts foncés : les très influencés
	    	 * 			 		 clairs : les influencés
	    	 * 			   jaunes		: influenceurs / influencés
	    	 * 			   oranges	    : les influenceurs
	    	 * 			   rouges       : les + gros influenceurs */
	    	if(centralityDegree <= averageCentrality*0.25) {
	    		color = "darkgreen";
	    	}
	    	else if(centralityDegree <= averageCentrality*0.5) {
	    		color = "green";
	    	}
	    	else if(centralityDegree <= averageCentrality*1.25) {
	    		color = "yellow";
	    	}
	    	else if(centralityDegree <= averageCentrality*1.5) {
	    		color = "orange";
	    	}
	    	else {
	    		color = "red";
	    	}
	    	
	    	currentNode.setAttribute("ui.style", "fill-color: " + color + ";");
	    }
	}
}
