import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.*;
import java.util.Date;
import java.util.Map;


public class Sharding extends GridPane {

    Label info;
    TextArea text;

    public Sharding() {
        //==========GRID PANE PROPERTIES===================
        this.setAlignment(Pos.CENTER);  // Override default
        this.setHgap(10);
        this.setVgap(12);
        this.setStyle("-fx-background-color: #F9F9F9;");


        //===================NODES=========================
        Label title = new Label("SHARDING");
        title.setAlignment(Pos.CENTER);

        Label cluster = new Label("Enter cluster (folder) name:");
        cluster.setAlignment(Pos.CENTER);

        TextField cn = new TextField();
        cn.getText();

        Button build = new Button("build");
        build.setStyle("-fx-font-size: 10pt;");
        build.setAlignment(Pos.CENTER);

        Button delete = new Button("delete");
        delete.setStyle("-fx-font-size: 10pt;");
        delete.setAlignment(Pos.CENTER);

        Button close = new Button("close");
        close.setStyle("-fx-font-size: 10pt;");
        close.setAlignment(Pos.CENTER);

        //Checkboxes
        CheckBox live = new CheckBox("live");
        //CheckBox box2 = new CheckBox("current");
        live.setSelected(false);


        //View logfile Button
        Button view = new Button("View LogFile");

        //Delete logfile Button
        Button dLog = new Button("delete LogFile");

        //import, enable sharding, create index & shard key Button
        Button importDB = new Button("Import JSON");//("Delete LogFile");

        //kill mongos process Button
        Button killMongos = new Button("Kill mongos process");

        HBox mainOp = new HBox();
        mainOp.setSpacing(10.0);
        mainOp.setAlignment(Pos.CENTER);
        mainOp.getChildren().addAll(killMongos,build,importDB,delete,close);

        HBox logView = new HBox();
        logView.setSpacing(10.0);
        logView.setAlignment(Pos.CENTER);
        logView.getChildren().addAll(view, live, dLog);


        text = new TextArea("");
        text.setPrefSize(670, 500);

        //text.setStyle("-fx-font-color:#29f972;");
        //text.setStyle("-fx-text-fill: lawngreen;")
        //text.setStyle("-fx-text-fill: #19ff0b;");
//        text.setStyle("-fx-font-size: 32px;");//0.9em;");

        text.setStyle("-fx-control-inner-background:#000000; -fx-font-family: Consolas; -fx-highlight-fill: #00ff00; -fx-highlight-text-fill: #000000; -fx-text-fill: #00ff00; ");


//        text.setStyle("-fx-control-inner-background:black;");


//        Region region = ( Region ) text.lookup( ".content" );
//        region.setBackground( new Background( new BackgroundFill( Color.BROWN, CornerRadii.EMPTY, Insets.EMPTY ) ) );



        //text.setStyle("");
        //text.setStyle("-fx-color-label-visible: Fffffff");
        //region.setStyle("-fx-background-color: yellow");



        //================EVENTS=====================
        //build cluster
        build.setOnAction(e -> {
            if(!cn.getText().isEmpty()){

                //display log file in text area
                try{ read_file(text,info);
                }catch(NullPointerException ex){

                }
                String[] command = {"/Users/sk_mpro/Documents/sharding/scripts/script_cluster.sh", cn.getText(), ""};
                ProcessBuilder pb = new ProcessBuilder(command);
                Map<String, String> env = pb.environment();
                env.put("VAR1", "myValue");
                pb.directory(new File("/Users/sk_mpro/Documents/sharding/scripts"));

                try {
                    Process p = pb.start();
                } catch (IOException e1) {
                    System.out.println("****error******");
                    e1.printStackTrace();
                }
            }

        });

        //delete cluster
        delete.setOnAction((ActionEvent event) -> {
            if(!cn.getText().isEmpty()){

                //display log file in text area
                try{read_file(text,info);
                }catch(NullPointerException ex){

                }

                String[] command = {"/Users/sk_mpro/Documents/sharding/scripts/bash/delete.sh", cn.getText(), ""};
                ProcessBuilder pb = new ProcessBuilder(command);
                Map<String, String> env = pb.environment();
                env.put("VAR1", "myValue");
                //env.remove("OTHERVAR");
                //env.put("VAR2", env.get("VAR1") + "suffix");
                pb.directory(new File("/Users/sk_mpro/Documents/sharding/scripts"));

                try {
                    Process p = pb.start();
                } catch (IOException e1) {
                    System.out.println("****error******");
                    e1.printStackTrace();
                }
                text.clear();

            }
        });

        //close app
        close.setOnAction(e -> {
            System.exit(0);
        });

        //show logfile
        view.setOnAction(e -> {

            try{
                read_file(text,info);
            }catch(NullPointerException ex){

            }



                });

        //delete logfile
        dLog.setOnAction(e->{
            String[] command = {"/Users/sk_mpro/Documents/sharding/scripts/bash/deleteLog.sh", "", ""};
            ProcessBuilder pb = new ProcessBuilder(command);
            Map<String, String> env = pb.environment();
            env.put("VAR1", "myValue");
            pb.directory(new File("/Users/sk_mpro/Documents/sharding"));

            try {
                Process p = pb.start();
            } catch (IOException e1) {
                System.out.println("****error******");
                e1.printStackTrace();
            }
        });

        live.setOnAction((ActionEvent e) ->{

            try{
                Stage stage = new Stage();
                GridPane gp = new GridPane();
                //set grid pane properties
                gp.setAlignment(Pos.CENTER);  // Override default
                gp.setHgap(10);
                gp.setVgap(12);
                gp.setStyle("-fx-background-color: #f9f4a5;");
                TextArea text1 = new TextArea("");
                text1.setPrefSize(460, 500);
                text1.setStyle("-fx-font-size: 0.9em;");
                text1.textProperty().addListener((ChangeListener<Object>) (observable, oldValue, newValue) -> {
                    text1.setScrollTop(Double.MAX_VALUE); //this will scroll to the bottom
                    //use Double.MIN_VALUE to scroll to the top
                });
                Label info1=new Label("LIVE");
                info1.setAlignment(Pos.CENTER);
                info=new Label();
                //update  text area to show cluster-build progression
                try{
                    Timeline updater = new Timeline(new KeyFrame(Duration.seconds(1), event -> read_file(text1,info) ));
                    updater.setCycleCount(5000);
                    updater.play();
                }catch (NullPointerException ex){

                }


                //group all nodes together in a vertical box for display
                VBox vb = new VBox();
                vb.setSpacing(7.0);
                vb.setAlignment(Pos.CENTER);  // Aligns VBox
                vb.getChildren().addAll(info1,info,text1);
                gp.add(vb, 0, 0);


//                    //live.setSelected(false);
//                    if(stage.isShowing())//unselect checkbox if stage is not showing
//                        live.setSelected(false);
                Scene scene;
                scene = new Scene(gp, 520, 570);
                stage.setScene(scene);//set scene
                stage.show();

                    if(stage.isShowing())//unselect checkbox if stage is not showing
                        live.setSelected(false);


//                if (live.isSelected()){
//                    stage.show();//display to screen
//                    //stage.toFront();
//                    //Controller.getInstance().setStage(stage);
//                }else{
//
//                    stage.close();
//                }

            }catch(NullPointerException ex){

            }


        });
        //show live logfile
//        live.setOnAction((ActionEvent e) -> {
//
//            try{
//
//                Scene scene;
//                GridPane gp = new GridPane();
//                //set grid pane properties
//                gp.setAlignment(Pos.CENTER);  // Override default
//                gp.setHgap(10);
//                gp.setVgap(12);
//                gp.setStyle("-fx-background-color: #f9f4a5;");
//                TextArea text1 = new TextArea("");
//                text1.setPrefSize(460, 500);
//                text1.setStyle("-fx-font-size: 0.9em;");
//                text1.textProperty().addListener((ChangeListener<Object>) (observable, oldValue, newValue) -> {
//                    text1.setScrollTop(Double.MAX_VALUE); //this will scroll to the bottom
//                    //use Double.MIN_VALUE to scroll to the top
//                });
//                Label info1=new Label("LIVE");
//                info1.setAlignment(Pos.CENTER);
//                info=new Label();
//                //update  text area to show cluster-build progression
//                try{
//                    Timeline updater = new Timeline(new KeyFrame(Duration.seconds(1), event -> read_file(text1,info) ));
//                    updater.setCycleCount(5000);
//                    updater.play();
//                }catch (NullPointerException ex){
//
//                }
//
//
//                //group all nodes together in a vertical box for display
//                VBox vb = new VBox();
//                vb.setSpacing(7.0);
//                vb.setAlignment(Pos.CENTER);  // Aligns VBox
//                vb.getChildren().addAll(info1,info,text1);
//                gp.add(vb, 0, 0);
//                scene = new Scene(gp, 520, 570);
//                Stage stage = new Stage();
//                stage.setScene(scene);//set scene
//                stage.show();//display to screen
//
//                //live.setSelected(false);
//                if(stage.isShowing())//unselect checkbox if stage is not showing
//                    live.setSelected(false);
//
//                Controller.getInstance().setStage(stage);
//            }catch(NullPointerException ex){
//
//            }
//
//
//
//
//                });

        //delete log file
        importDB.setOnAction(e -> {
            //display log file in text area
            try{
                read_file(text,info);
            }catch(NullPointerException ex){

            }

            String[] command = {"/Users/sk_mpro/Documents/sharding/scripts/bash/import.sh", "", ""};
            ProcessBuilder pb = new ProcessBuilder(command);
            Map<String, String> env = pb.environment();
            env.put("VAR1", "myValue");
            pb.directory(new File("/Users/sk_mpro/Documents/sharding/scripts/bash"));

            try {
                Process p = pb.start();
            } catch (IOException e1) {
                System.out.println("****error******");
                e1.printStackTrace();
            }

//            String[] command = {"/Users/sk_mpro/Documents/sharding/scripts/bash/deleteLog.sh","",""};
//            ProcessBuilder pb = new ProcessBuilder(command);
//            Map<String, String> env = pb.environment();
//            env.put("VAR1", "myValue");
//            pb.directory(new File("/Users/sk_mpro/Documents/sharding"));
//
//            try {
//                pb.start();//Process p =
//            } catch (IOException e1) {
//                System.out.println("****error******");
//                e1.printStackTrace();
//            }
        } );

        //kill mongos
        killMongos.setOnAction(e -> {
            String[] command = {"/Users/sk_mpro/Documents/sharding/scripts/bash/killMongos.sh","",""};
            ProcessBuilder pb = new ProcessBuilder(command);
            Map<String, String> env = pb.environment();
            env.put("VAR1", "myValue");
            pb.directory(new File("/Users/sk_mpro/Documents/sharding"));

            try {
                Process p = pb.start();
            } catch (IOException e1) {
                System.out.println("****error******");
                e1.printStackTrace();
            }

        } );

        //=========================================================

        //group all nodes together in a vertical box for display
        VBox vb = new VBox();
        vb.setSpacing(7.0);
        vb.setAlignment(Pos.CENTER);  // Aligns VBox
        vb.getChildren().addAll(title, cluster, cn, mainOp,  text, logView);
        this.add(vb, 0, 0);
        //this.add(close,0,2);
        Label cr = new Label("© Simone Rodigari all rights reserved.");
        cr.setAlignment(Pos.CENTER);
        HBox end = new HBox();
        end.setSpacing(10);
        end.setAlignment(Pos.CENTER);
        end.getChildren().addAll(cr,close);
        this.add(end,0,4);

    }



    //Handle checkbox options
    private void handle(CheckBox box1){

        if(box1.isSelected()) {
            Scene scene;
            GridPane gp = new GridPane();
            //set grid pane properties
            gp.setAlignment(Pos.CENTER);  // Override default
            gp.setHgap(10);
            gp.setVgap(12);
            gp.setStyle("-fx-background-color: #f9be4e;");
            TextArea text1 = new TextArea("");
            text1.setPrefSize(460, 500);
            text1.setStyle("-fx-font-size: 0.9em;");
            text1.textProperty().addListener((ChangeListener<Object>) (observable, oldValue, newValue) -> {
                text1.setScrollTop(Double.MAX_VALUE); //this will scroll to the bottom
                //use Double.MIN_VALUE to scroll to the top
            });
            Label info1=new Label("LIVE");
            info1.setAlignment(Pos.CENTER);

            //update  text area to show cluster-build progression
            Timeline updater = new Timeline(new KeyFrame(Duration.seconds(1), event -> read_file(text1,info1) ));
            updater.setCycleCount(5000);
            updater.play();

            //group all nodes together in a vertical box for display
            VBox vb = new VBox();
            vb.setSpacing(7.0);
            vb.setAlignment(Pos.CENTER);  // Aligns VBox
            vb.getChildren().addAll(info1,text1);
            gp.add(vb, 0, 0);
            scene = new Scene(gp, 520, 570);
            Stage stage = new Stage();
            stage.setScene(scene);//set scene
            stage.show();//display to screen

            Controller.getInstance().setStage(stage);
        }
        else{
            //read_file(text,info);
        }
        //if(box1.isSelected()&&box2.isSelected()){}
    }
    //method to read a file & add content of it to textarea
    public void read_file(TextArea t, Label i){
        //create object file which is the link to our cluster-build progression log
        final File file= new File("/Users/sk_mpro/Documents/sharding/log.file");
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            StringBuffer stringBuffer = new StringBuffer();
            String line;
            t.clear();
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
                stringBuffer.append("\n");
            }
            fileReader.close();
            //System.out.println("Contents of file:");
            t.appendText(stringBuffer.toString());
            //System.out.println(stringBuffer.toString());
        } catch (IOException e) {
            //e.printStackTrace();
        }
        i.setText(new Date().toString());
    }
}
