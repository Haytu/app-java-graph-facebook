import java.util.HashSet;
import org.graphstream.graph.*;

public interface GraphInterface {
	public void init(String filename);
	public byte[][] getAdjacencyMatrix();
	public Path getShortestPath(Node node1, Node node2);
	public HashSet<Node> getNeighbourhood(Node node);
	
	/* Connexité du graphe */
	public boolean isConnected();
	public int getConnectedComponentsCount();
	
	/* Diamètre, densité et force */
	public int getDiameter();
	public double getDensity();
	public float getForce(Node node1, Node node2);
	
	/* Degrés de centralité */
	public float getSumDistancesShortestPathTo(Node node);
	public float getCentralityByNeighborhood(Node node);
	public float getAverageCentrality(Node node);
	public float getClosenessCentrality(Node node);
	public float getBetweennessCentrality(Node node);
}
