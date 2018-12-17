import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.graph.Path;
import org.graphstream.ui.fx_viewer.FxViewPanel;
import org.graphstream.ui.fx_viewer.FxViewer;
import org.graphstream.ui.javafx.FxGraphRenderer;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class UIGraph extends Application implements EventHandler<ActionEvent> {
	
	private static Graph currentGraph = null;
	private static FxViewer viewer = null;
	
	/* Construit la fenêtre */
	public Scene construitScene(Stage primaryStage) throws FileNotFoundException {
		/* GridPane principale */
		GridPane grid = new GridPane();
		/* GridPane résultats */
		GridPane grid_results = new GridPane();
		
		/* Barre de navigation */
		MenuBar menu_bar = new MenuBar();
		menu_bar.prefWidthProperty().bind(primaryStage.widthProperty());

		/** Menu "File" **/
		Menu menu_fichier = new Menu("File");
		
		/*** Open ***/
		/* On instancie le graphe avec notre fichier d'arêtes ego-facebook de Snap (Stanford) */
		Menu menu_open = new Menu("Open", new ImageView(new Image(new FileInputStream("resources/folder.png"))));
		MenuItem menu_open_edges = new MenuItem("Edges file from Snap");
		menu_open.getItems().addAll(menu_open_edges);
		
		FileChooser fileChooser = new FileChooser();
		menu_open_edges.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(final ActionEvent e) {
                File file = fileChooser.showOpenDialog(primaryStage);
                if (file != null) {
                	currentGraph = new Graph(file.getName());
                	viewer = new FxViewer(currentGraph, FxViewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
		    		viewer.enableAutoLayout();
                    currentGraph.init(file.getAbsolutePath());
                }
            }
		});
		
		menu_fichier.getItems().add(menu_open);
		
		/*** Run ***/        
        /* On démarre l'étude basique du graphe */
		Menu menu_run = new Menu("Run", new ImageView(new Image(new FileInputStream("resources/play.png"))));
		
		MenuItem menu_run_basic = new MenuItem("Basic study of the graph");
		menu_run_basic.setOnAction(new EventHandler<ActionEvent>() {
		    public void handle(ActionEvent event) {
		    	/* Le graphe est bien chargé */
		    	if(currentGraph != null && viewer != null) {
		    		/* Nettoie les résultats pour en accueillir de nouveaux */
		    		grid_results.getChildren().clear();
		    				    		
		    		/* Fais apparaître le graphe dans l'interface utilisateur */
		    		FxViewPanel panel = (FxViewPanel)viewer.addDefaultView(false, new FxGraphRenderer());
		    		grid_results.add(panel, 0, 0);
		    		
		    		/* Connexité du graphe */
		    		Text nombre_composantes_connexes;
		    		if(currentGraph.isConnected()) {
		    			nombre_composantes_connexes = new Text("Graphe connexe");
		    		}
		    		else {
		    			nombre_composantes_connexes = new Text("Graphe non connexe à " + currentGraph.getConnectedComponentsCount() + " composantes connexes");
		    		}
		            
		    		/* Informations sur le graphe */
		    		Text moyenneDegre_graphe = new Text("Moyenne des degrés des noeuds : " + currentGraph.getAverageDegree());
		            Text diametre_graphe = new Text("Diamètre : " + currentGraph.getDiameter());
		            Text densite_graphe = new Text("Densité : " + currentGraph.getDensity());
		            
		            /* Récupère le noeud "central" par voisinage */
		            Node node_greaterCbyN = currentGraph.getNodeWithGreaterCentralityByNeighborhood();
		            Text node_greaterCbyN_text = new Text("Degré de centralité max : " + currentGraph.getCentralityByNeighborhood(node_greaterCbyN) + " (noeud rouge)");
		            node_greaterCbyN.setAttribute("ui.style", "fill-color: red;");
		            
		            Text node_neighborsCount_text = new Text("Noeud central a " + currentGraph.getNeighbourhood(node_greaterCbyN).size() + " noeuds voisins");
		            
		            /* Bouton affichage de la matrice d'adjacence du graphe */
		            Button affiche_matrice = new Button("Display adjacency matrix in console");
		            affiche_matrice.setOnAction(new EventHandler<ActionEvent>() {
		    		    public void handle(ActionEvent event) {
		    		    	byte[][] adjacencyMatrix = currentGraph.getAdjacencyMatrix();
		    				for (int i = 0; i < adjacencyMatrix.length; i++) {
		    				    for (int j = 0; j < adjacencyMatrix[i].length; j++) {
		    				        System.out.print(adjacencyMatrix[i][j] + " ");
		    				    }
		    				    System.out.println();
		    				}
		    			}
		    		});
		            
		            /* 2 noeuds choisis au hasard */
		            Node node1 = currentGraph.getRandomNode();
		            Node node2 = currentGraph.getRandomNode();
		            
		            node1.setAttribute("ui.style", "fill-color: green;");
		            node2.setAttribute("ui.style", "fill-color: green;");
		            
		            /* Met en vert le plus court chemin entre node1 et node2 */
		            Path shortestPath = currentGraph.getShortestPath(node1, node2);
		            
		            /* Si les 2 noeuds sont dans la même composante connexe */
		            if(shortestPath != null) {
		            	for(Edge edge : shortestPath.getEdgePath()) {
			            	edge.setAttribute("ui.style", "fill-color: green;");
			            }
		            }
		            
		            /* Calcule la force entre les deux noeuds */
		            Text force_text = new Text("Force entre les 2 noeuds verts : " + currentGraph.getForce(node1, node2) + " (plus court chemin en vert)");
		             
		            /* Tous nos résultats sont attachés à une GridPane commune qu'on peut recréer au besoin */
		    		grid_results.add(nombre_composantes_connexes, 0, 1);
		            grid_results.add(diametre_graphe, 0, 2);
		            grid_results.add(densite_graphe, 0, 3);
		            grid_results.add(node_greaterCbyN_text, 0, 4);
		            grid_results.add(node_neighborsCount_text, 0, 5);
		            grid_results.add(moyenneDegre_graphe, 0, 6);
		            grid_results.add(force_text, 0, 7);
		    		grid_results.add(affiche_matrice, 0, 8);
		    	}
		    	else {
		    		displayWarningGraphMissing();
		    	}
		    }
		});
		
		MenuItem menu_run_influencers = new MenuItem("Looking for influencers");
		menu_run_influencers.setOnAction(new EventHandler<ActionEvent>() {
		    public void handle(ActionEvent event) {
		    	/* Le graphe est bien chargé */
		    	if(currentGraph != null && viewer != null) {
		    		/* Nettoie les résultats pour en accueillir de nouveaux */
		    		grid_results.getChildren().clear();
		    		
		    		/* Fais apparaître le graphe dans l'interface utilisateur */
		    		FxViewPanel panel = (FxViewPanel)viewer.addDefaultView(false, new FxGraphRenderer());
		    		grid_results.add(panel, 1, 1);
		    		
		    		/* Recherche les + influenceurs */
		            currentGraph.showInfluencers();
		    	}
		    	else {
		    		displayWarningGraphMissing();
		    	}
		    }
		});
		
		/*** Quitter ***/
	    MenuItem item_quitter = new MenuItem("Quitter", null);
	    item_quitter.setOnAction(new EventHandler<ActionEvent>() {
	      public void handle(ActionEvent event) {
	    	  System.exit(0);
	    	  Platform.exit();
	      }
	    });
		
		menu_run.getItems().addAll(menu_run_basic);
		menu_run.getItems().addAll(menu_run_influencers);

		menu_fichier.getItems().add(menu_run);
	    menu_fichier.getItems().add(new SeparatorMenuItem());
	    menu_fichier.getItems().add(item_quitter);
		menu_bar.getMenus().add(menu_fichier);

        /* On ajoute tous les éléments à notre GridPane */
		grid.addRow(2, grid_results);
		grid.getChildren().add(menu_bar);
						
		/* Création de la scène */
		StackPane root = new StackPane();
		root.getChildren().addAll(grid);
		Scene scene = new Scene(root, 780, 525);
		return scene;
	}
	
	public void displayWarningGraphMissing() {
		/* On affiche un Warning si le fichier des arêtes n'est pas chargé */
		Alert alert = new Alert(AlertType.WARNING, "You should select an edges file before running it!", ButtonType.OK);
		alert.showAndWait();
	}
	
    public void start(Stage primaryStage) {
		primaryStage.setTitle("GraphFriends");
		
        try {
			primaryStage.setScene(construitScene(primaryStage));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
        
        primaryStage.sizeToScene();
        primaryStage.show();
        primaryStage.setOnCloseRequest(e -> System.exit(0));
	}

	public void handle(ActionEvent arg0) {}
}
