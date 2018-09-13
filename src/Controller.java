//import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class Controller {
    private static Controller instance;
    private Stage myStage;

    public Controller() {
        instance = this;
    }

    public static Controller getInstance() {
        if (instance == null) {
            instance = new Controller();
        }
        return instance;
    }

    public void setStage(Stage s){this.myStage = s;}

    //public Stage getStage(){return this.myStage;}
}
