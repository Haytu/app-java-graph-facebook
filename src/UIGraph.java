import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.graphstream.ui.fx_viewer.FxViewPanel;
import org.graphstream.ui.fx_viewer.FxViewer;
import org.graphstream.ui.javafx.FxGraphRenderer;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
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
		GridPane grid = new GridPane();
		
		FileChooser fileChooser = new FileChooser();
        Button openButton = new Button("Open an edges file from Snap", new ImageView(new Image(new FileInputStream("resources/folder.png"))));
        openButton.setOnAction(
            new EventHandler<ActionEvent>() {
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
        
        Button run = new Button("Run", new ImageView(new Image(new FileInputStream("resources/play.png"))));
		run.setOnAction(new EventHandler<ActionEvent>() {
		    public void handle(ActionEvent event) {
		    	/* Le graphe est bien chargé */
		    	if(currentGraph != null && viewer != null) {
		    		for(int i = 3; i < grid.getChildren().size() && i > 2; i++) {
			    		grid.getChildren().remove(i);
		    		}
		    				    		
		    		FxViewPanel panel = (FxViewPanel)viewer.addDefaultView(false, new FxGraphRenderer());
		    		grid.add(panel, 2, 2);
		    		
		    		/* Connexité du graphe */
		    		Text nombre_composantes_connexes;
		    		if(currentGraph.isConnected()) {
		    			nombre_composantes_connexes = new Text("Graphe connexe");
		    		}
		    		else {
		    			nombre_composantes_connexes = new Text("Graphe non connexe à " + currentGraph.getConnectedComponentsCount() + " composantes connexes.");
		    		}
		            
		            Text diametre_graphe = new Text("Diamètre : " + currentGraph.getDiameter());
		            Text densite_graphe = new Text("Densité : " + currentGraph.getDensity());
		            
		            grid.add(nombre_composantes_connexes, 0, 2);
		            grid.add(diametre_graphe, 0, 3);
		            grid.add(densite_graphe, 0, 4);
		            
		            /*Button enregistre_matrice = new Button("Save adjacency matrix");
		    		enregistre_matrice.setOnAction(new EventHandler<ActionEvent>() {
		    		    public void handle(ActionEvent event) {
		    		    	
		    		    }
		    		});
		    		grid.add(enregistre_matrice, 0, 2);*/
		    	}
		    	else {
		    		/* On affiche un Warning si le fichier des arêtes n'est pas chargé */
		    		Alert alert = new Alert(AlertType.WARNING, "You should select an edges file before running it!", ButtonType.OK);
		    		alert.showAndWait();
		    	}
		    }
		});

        /* On ajoute tous les éléments à notre GridPane */
		grid.add(openButton, 0, 0);
		grid.add(run, 0, 1);
						
		/* Création de la scène */
		StackPane root = new StackPane();
		root.getChildren().addAll(grid);
		Scene scene = new Scene(root, 520, 350);
		return scene;
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
