
public class Main {
	public static void main(String args[]) {
		
		Graph g = new Graph("dv");
		g.init("data/xab");
		System.out.println(g.isConnected());
		System.out.println(g.getConnectedComponentsCount());
		
		/*byte[][] matrix = graph.getAdjacencyMatrix();
		for (int i = 0; i < matrix.length; i++) {
		    for (int j = 0; j < matrix[i].length; j++) {
		        System.out.print(matrix[i][j] + " ");
		    }
		    System.out.println();
		}*/
		
		/*Node node1 = graph.getNode(1);
		Node node2 = graph.getNode(3);
		Node node3 = graph.getNode("20536157");
		
		node1.addAttribute("ui.style", "fill-color: red;");
		node2.addAttribute("ui.style", "fill-color: green;");
		node3.addAttribute("ui.style", "fill-color: blue;");
		
		/*node.addAttribute("ui.style", "fill-color: red;");
		
		System.out.println(node1.getInDegree());
		System.out.println(node1.getOutDegree());
		
		System.out.println(graph.getNeighbourhood(node1));
		
		System.out.println(graph.getShortestPath(node1, node2));

		System.out.println(graph.getForce(graph.getNode(1), graph.getNode(4)));*/
			
		//System.out.println(graph.getAverageCentrality(node1));
		
		/*for(Node currentNode : graph.getEachNode()) {
			System.out.println(graph.getClosenessCentrality(currentNode));
			if(graph.getClosenessCentrality(currentNode)>0.4)
				currentNode.addAttribute("ui.style", "fill-color: red;");
		}*/
		
		/*Node node1 = graph.getNode(200);
		System.out.println(graph.getClosenessCentrality(node1));
		node1.addAttribute("ui.style", "fill-color: red;");*/
		
		//graph.display();	
	}
}