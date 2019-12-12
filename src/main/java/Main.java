import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ForkJoinPool;
import org.apache.commons.collections4.ListUtils;



public class Main extends Application {

    static final int portNumber = 13188;
    static String hostName;
    public static Grid blockGrid;
    static int role;
    private static ArrayList<Solutions> clientGrids;
    private static ArrayList<Grid> gridsToSolve;
    private static ArrayList<Solutions> solutionsToSolve = new ArrayList<>();

    public static void main(String[] args) throws CloneNotSupportedException {
        Scanner kb = new Scanner(System.in);
        System.out.println("Please type 1 to accept connections (server role)\nPress 2 to connect to another computer");
        role = Integer.parseInt(kb.nextLine());
        int requiredComputers = 0;
        int numConnections = 0;


        if (role == 1) {
            blockGrid = new Grid();
            blockGrid.setBlockNeighbors();
            
            new Thread(() -> launch(args)).start();

            System.out.println("Please enter the number of computers you wish to connect");
            requiredComputers = Integer.parseInt(kb.nextLine());


            ServerSocket ss;
            try {
                ss = new ServerSocket(portNumber);
                
                Solutions newSolutions = new Solutions(blockGrid);

                clientGrids = newSolutions.getNextGrids(new Solutions(blockGrid));
               

                while (numConnections < requiredComputers) {
                    Socket socket = ss.accept();

                    ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());

                    int gridsSize = clientGrids.size();
                    int sizeToSplit = gridsSize/requiredComputers;
                    
                    List clientGridsList = clientGrids;

                    List<List<Grid>> output = ListUtils.partition(clientGridsList, sizeToSplit);

                    outputStream.writeObject(new ArrayList(output.get(numConnections)));

                    numConnections++;
                    System.out.println("Client connected: " + numConnections + " / " + requiredComputers);

                    socket.close();

                }

                while (true) {
                    Socket solutionSocket = ss.accept();
                    ObjectInputStream inputStream = new ObjectInputStream(solutionSocket.getInputStream());
                    //System.out.println("\nFound a Solution!");

                    String solutionFromClient = (String) inputStream.readObject();

                    System.out.println(solutionFromClient);
                    
                    solutionSocket.close();
                }

            } catch (IOException io) {
                io.printStackTrace();
                System.exit(-1);


            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        } else if (role == 2) {
            //do the "client" stuff
            System.out.println("Type in host IP:");

            hostName = kb.nextLine();
            boolean connected = false;
            try {
                while (!connected) {
                    Socket socket = new Socket(hostName, portNumber);
                    connected = true;
                    ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());
                    solutionsToSolve = (ArrayList<Solutions>) inStream.readObject();
                    
                    for (Solutions s: solutionsToSolve){
                        gridsToSolve.add((Grid) s.grid.clone());
                    }

                    Solutions clientSolutions = new Solutions(gridsToSolve);

                    ForkJoinPool childBoardSolver = new ForkJoinPool();
                    childBoardSolver.invoke(clientSolutions);

                    socket.close();
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
            }

        } else {
            System.out.println("Typed in invalid command.... Please relaunch");

        }
        //launch(args);

    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setMinHeight(600);
        primaryStage.setMinWidth(500);
        primaryStage.setTitle("csc375a3");
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("MainPage.fxml"));
        Pane root = loader.load();
        primaryStage.setScene(new Scene(root));
        root.requestFocus();
        primaryStage.show();
    }
}
