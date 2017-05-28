/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package weatherproject;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author blaze
 */
public class WeatherProject extends Application {
    
    private static final int BUFFER_SIZE = 4096;
    static String fileName;
    static int size;
    /**
     * Downloads a file from a URL
     * @param fileURL HTTP URL of the file to be downloaded
     * @param saveDir path of the directory to save the file
     * @throws IOException
     */
    
    
    @Override
    public void start(Stage primaryStage) throws IOException, FileNotFoundException, ParseException {
        
        BorderPane bp = new BorderPane();
        VBox vBox = new VBox();
        StackPane stackPaneLeft = new StackPane();
        stackPaneLeft.setMinWidth(600);
        StackPane stackPaneRight = new StackPane();
        stackPaneRight.setMinWidth(600);
        bp.setCenter(stackPaneLeft);
        bp.setRight(stackPaneRight);
        Button btn = new Button();
        Button btn2 = new Button();
        btn.setText("Dzialaj");
        btn2.setText("Wykres");
        vBox.getChildren().add(btn);
        vBox.getChildren().add(btn2);
        bp.setLeft(vBox);
        bp.setMinWidth(1200);
        ArrayList humidityArray = new ArrayList();
        ArrayList tempArray = new ArrayList();
        ArrayList dateArray = new ArrayList();
        ArrayList crapArray = new ArrayList();
        
        
        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                humidityArray.clear();
                tempArray.clear();
                dateArray.clear();
                crapArray.clear();
                try {
                    String saveDir = "C:\\Users\\blaze\\Documents\\NetBeansProjects\\WeatherProject\\";
                    WeatherProject.downloadFile("https://data.sparkfun.com/output/a6y681mLw9sx7MRryDqm.json/", saveDir);
                    Scanner scanner = new Scanner( new File(fileName) );
                    JSONParser parser = new JSONParser();
                    String str = scanner.nextLine();
                    Object obj = parser.parse(str);
                    JSONArray array = (JSONArray)obj;
                 
                    size = array.size();
                    
                    for(int a=0; a < size; a++)
                    {
                        JSONObject crapObj = (JSONObject)array.get(a);
                        crapArray.add(crapObj.get("humidity"));
                    }
                    for (int i = 0; i < size ; i++)
                    {
                        JSONObject humidityObj = (JSONObject)array.get(i);
                        if(!humidityObj.get("humidity").equals("nan"))
                        {
                            humidityArray.add(humidityObj.get("humidity"));
                        }
                    }
                    for (int j = 0; j < size ; j++)
                    {
                        JSONObject tempObj = (JSONObject)array.get(j);
                        if(!tempObj.get("temp").equals("nan"))
                        {
                            tempArray.add(tempObj.get("temp"));
                        }
                    }
                    for (int k = 0; k < size ; k++)
                    {
                        JSONObject dateObj = (JSONObject)array.get(k);
                        if(!crapArray.get(k).equals("nan"))
                        {
                            dateArray.add(dateObj.get("timestamp"));
                        }
                    }
                    System.out.println(dateArray.size());
                    System.out.println(humidityArray.size());
                    System.out.println(tempArray.size());
                } catch (IOException ex) {
                    Logger.getLogger(WeatherProject.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ParseException ex) {
                    Logger.getLogger(WeatherProject.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
  btn2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
       
         CategoryAxis xAxis = new CategoryAxis();  
         NumberAxis yAxis = new NumberAxis(40, 65, 5);   
         yAxis.setLabel("Fruit units"); 
         AreaChart<String, Number> areaChart = new AreaChart(xAxis, yAxis);
         areaChart.setTitle("Humidity during time");  
         XYChart.Series series1 = new XYChart.Series(); 
         series1.setName("Humidity");
         
         for(int i = 0; i < humidityArray.size(); i++)
         {
         if((i%100) == 0)    
         series1.getData().add(new XYChart.Data(dateArray.get(i),Double.parseDouble(humidityArray.get(i).toString())));
         }
         
         areaChart.getData().addAll(series1); 
         stackPaneLeft.getChildren().add(areaChart);
         
         /////////////////////////////////////////////////////////
         CategoryAxis xAxis2 = new CategoryAxis();  
         NumberAxis yAxis2 = new NumberAxis(20, 30, 5);   
         yAxis2.setLabel("Fruit units"); 
         AreaChart<String, Number> areaChart2 = new AreaChart(xAxis2, yAxis2);
         areaChart2.setTitle("Temperature during time");  
         XYChart.Series series2 = new XYChart.Series(); 
         series2.setName("temp");
         
         for(int j = 0; j < tempArray.size(); j++)
         {
         if((j%100) == 0)    
         series2.getData().add(new XYChart.Data(dateArray.get(j),Double.parseDouble(tempArray.get(j).toString())));
         }
         
         areaChart2.getData().addAll(series2); 
         stackPaneRight.getChildren().add(areaChart2);
        }
        });
  
  
        Scene scene = new Scene(bp, 1200, 600);
        primaryStage.setResizable(false);
        primaryStage.setTitle("Hello World!");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    public static void downloadFile(String fileURL, String saveDir)
            throws IOException, FileNotFoundException, ParseException {
        URL url = new URL(fileURL);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        int responseCode = httpConn.getResponseCode();
 
        // always check HTTP response code first
        if (responseCode == HttpURLConnection.HTTP_OK) {
          //  String fileName = "";
            String disposition = httpConn.getHeaderField("Content-Disposition");
            String contentType = httpConn.getContentType();
            int contentLength = httpConn.getContentLength();
 
            
            if (disposition != null) {
                // extracts file name from header field
                int index = disposition.indexOf("filename=");
               if (index > 0) {
                    fileName = disposition.substring(index + 9,
                            disposition.length() );
                   
                }
            } else {
                // extracts file name from URL
                fileName = fileURL.substring(fileURL.lastIndexOf("/") +1,
                        
                        fileURL.length());
            }
           
            
            System.out.println("Content-Type = " + contentType);
            System.out.println("Content-Disposition = " + disposition);
            System.out.println("Content-Length = " + contentLength);
            System.out.println("fileName = " + fileName);
 
            // opens input stream from the HTTP connection
            InputStream inputStream = httpConn.getInputStream();
            String saveFilePath = saveDir + File.separator + fileName;
            System.out.println(saveFilePath);
            // opens an output stream to save into file
            FileOutputStream outputStream = new FileOutputStream(saveFilePath);
 
            int bytesRead = -1;
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
 
            outputStream.close();
            inputStream.close();
            //obj = GSONFileTest.convertFileToJSON(saveFilePath);
            System.out.println("File downloaded");
        } else {
            System.out.println("No file to download. Server replied HTTP code: " + responseCode);
        }
        httpConn.disconnect();
        System.out.println(fileName);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
