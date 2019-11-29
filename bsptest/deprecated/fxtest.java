import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.Scene;

public class fxtest extends Application
{
    public static void main(String[] args) {launch(args);}
    
    public void start(Stage stage)
    {
        Group items = new Group();
        Scene scene = new Scene(items,500,500);
        
        bspMap m = new bspMap(items);
        
        stage.setScene(scene);
        stage.setTitle("bsp_test");
        stage.setResizable(false);
        stage.show();
    }
}
