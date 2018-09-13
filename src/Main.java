import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("-Sharding-");

        Sharding s = new Sharding();
        ShardingScene ss = new ShardingScene(s);

        primaryStage.setScene(ss);//set scene
        primaryStage.show();//display to screen

        Controller.getInstance().setStage(primaryStage);//set current stage
    }

    public static void main(String[] args) {
        launch(args);
    }
}
