import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.ForkJoinPool;

public class Controller implements Initializable {
    @FXML
    private GridPane grid;

    @FXML
    private Text score;

    @FXML
    JFXButton refresh;

    @FXML
    JFXButton solve;


    private Grid blockGrid;


    public void initialize(URL location, ResourceBundle resources) {
        createGrid(true);

        refresh.setOnAction(event -> {
            grid.getColumnConstraints().clear();
            grid.getRowConstraints().clear();
            createGrid(true);
        });

        solve.setOnAction(event -> {
            Solutions solutions = new Solutions(blockGrid);

            ForkJoinPool childBoardSolver = new ForkJoinPool();
            childBoardSolver.invoke(solutions);
        });
    }

    private void createGrid(boolean firstRun) {

        if (firstRun) {
            //blockGrid = Main.blockGrid;
            blockGrid = new Grid();
            blockGrid.setBlockNeighbors();
            score.setText(blockGrid.getScore().toString());
        }

        int numCols = 5;
        int numRows = 5;

        for (int i = 0; i < numCols; i++) {
            ColumnConstraints colConstraints = new ColumnConstraints();
            colConstraints.setHgrow(Priority.SOMETIMES);
            grid.getColumnConstraints().add(colConstraints);
        }

        for (int i = 0; i < numRows; i++) {
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setVgrow(Priority.SOMETIMES);
            grid.getRowConstraints().add(rowConstraints);
        }

        for (int i = 0; i < numCols; i++) {
            for (int j = 0; j < numRows; j++) {
                addPane(i, j);
            }
        }
    }

    private void addPane(int colIndex, int rowIndex) {
        for (int i = 0; i < 25; i++) {
            if (blockGrid.getBlocks().getList()[i].getX() == colIndex && blockGrid.getBlocks().getList()[i].getY() == rowIndex) {

                Pane pane = new Pane();
                if (blockGrid.getBlocks().getList()[i].getColor() == 0) {
                    pane.setStyle("-fx-background-color: #e74c3c;");
                } else if (blockGrid.getBlocks().getList()[i].getColor() == 1) {
                    pane.setStyle("-fx-background-color: #3498db;");
                } else if (blockGrid.getBlocks().getList()[i].getColor() == 2) {
                    pane.setStyle("-fx-background-color: #2ecc71;");
                } else if (blockGrid.getBlocks().getList()[i].getColor() == 3) {
                    pane.setStyle("-fx-background-color: #ecf0f1;");
                }

                int color = blockGrid.getBlocks().getList()[i].getColor();

                if (color != 3) {
                    Block temp = blockGrid.getBlocks().getList()[i];
                    pane.setOnMouseClicked(e -> {
                        System.out.printf("Mouse clicked cell [%d, %d]%n", colIndex, rowIndex);
                        ArrayList<Block> blocksList = blockGrid.getBlockNeighbors(temp, new ArrayList<>());
                        for (Block c : blocksList) {
                            System.out.printf("Neighbor found at: [%d, %d]%n", c.getX(), c.getY());
                        }

                        Task<Void> task = new Task<Void>() {
                            @Override
                            protected Void call() {

                                blockGrid.removeBlocks(blocksList);

                                Platform.runLater(new Runnable() {
                                    public void run() {
                                        score.setText(blockGrid.getScore().toString());
                                        grid.getColumnConstraints().clear();
                                        grid.getRowConstraints().clear();
                                    }
                                });

                                return null;
                            }
                        };

                        task.stateProperty().addListener(new ChangeListener<Worker.State>() {
                            @Override
                            public void changed(ObservableValue<? extends Worker.State> observable,
                                                Worker.State oldValue, Worker.State newState) {
                                if (newState == Worker.State.SUCCEEDED) {

                                    Platform.runLater(new Runnable() {
                                        public void run() {
                                            createGrid(false);
                                        }
                                    });

                                    blockGrid.getBlocks().resetNeighbors();
                                    blockGrid.setBlockNeighbors();

                                }
                            }
                        });
                        new Thread(task).start();
                    });
                }
                grid.add(pane, colIndex, rowIndex);
            }

        }
    }
}
