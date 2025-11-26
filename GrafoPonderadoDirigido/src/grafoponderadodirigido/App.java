package grafoponderadodirigido;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application 
{

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        Parent root = FXMLLoader.load(getClass().getResource("ProyectoFinal.fxml"));
        Scene scene = new Scene(root);
        
        primaryStage.setTitle("Ordenamiento de climas");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    
    public static void main(String[] args) 
    {
        launch(args);
    }
}
