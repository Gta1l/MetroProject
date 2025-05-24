package com.example.metro;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import java.sql.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GUI extends Application {

    MetroDB myDB;

    List<List<Double>> LMetroGraph;

    double[][] AMetroGraph;

    Graph MetroGraph;

    List<Integer> ShortestWay = new ArrayList<>();

    double ShortestTime;

    List<Integer> MinStationsWay = new ArrayList<>();

    double MinStationsCount;

    private void DrawLines(Group gr) throws SQLException {
        ResultSet lines = this.myDB.StationConnectionsQueryResult();
        while (lines.next()) {
            Line line = new Line(lines.getInt(1), lines.getInt(2),
                    lines.getInt(3), lines.getInt(4));
            line.setStroke(Color.valueOf(lines.getString(5)));
            line.setStrokeWidth(5);
            gr.getChildren().add(line);
        }
    }

    private void DrawStations(Group gr) throws SQLException {
        ResultSet stations = this.myDB.StationsQueryResult();
        while (stations.next()) {
            Circle circle = new Circle(stations.getInt("XCoordinate"), stations.getInt("YCoordinate"), 10);
            circle.setFill(Color.WHITE);
            circle.setStroke(Color.BLACK);
            circle.setStrokeWidth(5);
            Text text = new Text(stations.getInt("XCoordinate") - 25, stations.getInt("YCoordinate") - 30, stations.getString("StationName"));
            text.setFont(Font.font("Helvetica", FontWeight.BOLD, 20));
            gr.getChildren().add(circle);
            gr.getChildren().add(text);
        }
    }

    private void DrawInterface(HBox HB, Group group) throws SQLException {
        ResultSet stations = this.myDB.StationsQueryResult();
        ObservableList<String> StationsNames = FXCollections.observableArrayList();

        while (stations.next()) {
            StationsNames.add(stations.getString("StationName"));
        }

        ChoiceBox<String> StationsNamesChoiceBox1 = new ChoiceBox<String>(StationsNames);
        ChoiceBox<String> StationsNamesChoiceBox2 = new ChoiceBox<String>(StationsNames);
        Button button = new Button("Найти маршрут");

        button.setOnMouseClicked(e -> {
            List<Integer> way = new ArrayList<>();
            try {
                ResultSet Id1 = this.myDB.IdByStationQueryResult(StationsNamesChoiceBox1.getValue());
                ResultSet Id2 = this.myDB.IdByStationQueryResult(StationsNamesChoiceBox2.getValue());
                if (Id1.next() && Id2.next()) {
                    ShortestWay = MetroGraph.ShortestWay(Id1.getInt("StationId"), Id2.getInt("StationId"));
                    ShortestTime = MetroGraph.ShortestDistance(Id1.getInt("StationId"), Id2.getInt("StationId"));
                    MinStationsWay = MetroGraph.MinVertexWay(Id1.getInt("StationId"), Id2.getInt("StationId"));
                    MinStationsCount = MetroGraph.MinVertex(Id1.getInt("StationId"), Id2.getInt("StationId"));
                }

                ShortestWaysText(HB, ShortestWay, ShortestTime, MinStationsWay, MinStationsCount);
                DrawShortestWays(group, ShortestWay , MinStationsWay);

            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }

        });
        HB.getChildren().add(StationsNamesChoiceBox1);
        HB.getChildren().add(StationsNamesChoiceBox2);
        HB.getChildren().add(button);
    }

    private void DrawShortestWays(Group gr, List<Integer> ShortestWay, List<Integer> MinStationsWay) throws SQLException {
        gr.getChildren().removeIf(item -> item instanceof Circle);
        gr.getChildren().removeIf(item -> item instanceof Line);
        DrawLines(gr);
        DrawStations(gr);
        for (int i = 0; i < ShortestWay.size(); i++) {
            if (i + 1 < ShortestWay.size()) {
                ResultSet lines = this.myDB.StationConnectionsByIDQueryResult(ShortestWay.get(i), ShortestWay.get(i + 1));
                if (lines.next()) {
                    Line line = new Line(lines.getInt(1), lines.getInt(2),
                            lines.getInt(3), lines.getInt(4));
                    line.setStroke(Color.valueOf(lines.getString(5)));
                    line.setStrokeWidth(20);
                    gr.getChildren().add(line);
                }
            }
            ResultSet stations = this.myDB.CoordinatesByIdQueryResult(ShortestWay.get(i));
            if (stations.next()) {
                Circle circle = new Circle(stations.getInt("XCoordinate"), stations.getInt("YCoordinate"), 25);
                gr.getChildren().add(circle);
            }
        }

        for (int i = 0; i < MinStationsWay.size(); i++) {
            if (i + 1 < MinStationsWay.size()) {
                ResultSet lines = this.myDB.StationConnectionsByIDQueryResult(MinStationsWay.get(i), MinStationsWay.get(i + 1));
                if (lines.next()) {
                    Line line = new Line(lines.getInt(1), lines.getInt(2),
                            lines.getInt(3), lines.getInt(4));
                    line.setStroke(Color.valueOf(lines.getString(5)).darker());
                    line.setStrokeWidth(10);
                    gr.getChildren().add(line);
                }
            }
            ResultSet stations = this.myDB.CoordinatesByIdQueryResult(MinStationsWay.get(i));
            if (stations.next()) {
                Circle circle = new Circle(stations.getInt("XCoordinate"), stations.getInt("YCoordinate"), 15);
                gr.getChildren().add(circle);
            }
        }
    }

    private void ShortestWaysText(HBox Interface, List<Integer> ShortestWay, double ShortestTime, List<Integer> MinStationsWay, double MinStationsCount) throws SQLException {
        Label text = new Label();
        text.setFont(Font.font("Helvetica", FontWeight.BOLD, 14));
        text.setTranslateY(text.getTranslateY() + 40);
        text.setTranslateX(text.getTranslateX() - 850);

        text.setText("Кратчайший маршрут: ");
        for (Integer i : ShortestWay) {
            ResultSet station = this.myDB.StationByIdQueryResult(i);
            if (station.next()) {
                text.setText(text.getText() + station.getString("StationName") + "->");
            }
        }
        text.setText(text.getText() + "Время: " + ShortestTime + " мин.\n");

        text.setText(text.getText() + "Наименьшее количество станций: ");
        for (Integer i : MinStationsWay) {
            ResultSet station = this.myDB.StationByIdQueryResult(i);
            if (station.next()) {
                text.setText(text.getText() + station.getString("StationName") + "->");
            }
        }
        text.setText(text.getText() + "Количество станций: " + MinStationsCount + " станций\n");

        Interface.getChildren().removeIf(item -> item instanceof Label);
        Interface.getChildren().add(text);

    }

    private void GraphInit() throws SQLException {
        ResultSet stations = this.myDB.ConnectionsTravelTimeQueryResult();
        this.LMetroGraph = new ArrayList<>();
        while (stations.next()) {
            List<Double> temp = new ArrayList<>();
            temp.add(stations.getDouble("Station1ID"));
            temp.add(stations.getDouble("Station2ID"));
            temp.add(stations.getDouble("TravelTime"));
            this.LMetroGraph.add(temp);
        }
        AMetroGraph = new double[LMetroGraph.size()][LMetroGraph.getFirst().size()];
        for (int i = 0; i < AMetroGraph.length; i++) {
            for (int j = 0; j < AMetroGraph[0].length; j++) {
                AMetroGraph[i][j] = LMetroGraph.get(i).get(j);
            }
        }

        ResultSet StationsCount = this.myDB.StationsCountQueryResult();
        if (StationsCount.next()) {
            MetroGraph = new Graph(StationsCount.getInt("count") + 1, AMetroGraph);
        }
    }

    private void MapInteraction(Group group, KeyEvent e) {
        double speed = 10;
        if (e.isShiftDown()) {
            speed *= 0.3;
        }
        if (e.isAltDown()) {
            speed *= 5;
        }
        if (e.getCode() == KeyCode.W) {
            group.setTranslateY(group.getTranslateY() - speed);
        } else if (e.getCode() == KeyCode.S) {
            group.setTranslateY(group.getTranslateY() + speed);
        } else if (e.getCode() == KeyCode.A) {
            group.setTranslateX(group.getTranslateX() - speed);
        } else if (e.getCode() == KeyCode.D) {
            group.setTranslateX(group.getTranslateX() + speed);
        } else if (e.getCode() == KeyCode.Q) {
            Scale newScale = new Scale();
            newScale.setX(group.getScaleX() + speed / 200);
            newScale.setY(group.getScaleY() + speed / 200);
            newScale.setPivotX(group.getScaleX());
            newScale.setPivotY(group.getScaleY());
            group.getTransforms().add(newScale);

        } else if (e.getCode() == KeyCode.E) {
            Scale newScale = new Scale();
            newScale.setX(group.getScaleX() - speed / 200);
            newScale.setY(group.getScaleY() - speed / 200);
            newScale.setPivotX(group.getScaleX());
            newScale.setPivotY(group.getScaleY());
            group.getTransforms().add(newScale);
        }
    }


    @Override
    public void start(Stage stage) throws IOException, SQLException, ClassNotFoundException {

        this.myDB = new MetroDB("jdbc:postgresql://localhost:5432/Metro", "postgres", "password");

        GraphInit();

        Group MainGroup = new Group();

        Group MapGroup = new Group();

        HBox Interface = new HBox();
        Interface.setSpacing(70);
        Interface.setTranslateX(320);
        Interface.setTranslateY(565);

        DrawLines(MapGroup);
        DrawStations(MapGroup);
        DrawInterface(Interface, MapGroup);

        MainGroup.getChildren().add(MapGroup);
        MainGroup.getChildren().add(Interface);

        Scene scene = new Scene(MainGroup, 1024, 700);

        scene.setOnKeyPressed(e -> {
            MapInteraction(MapGroup, e);
        });

        stage.setScene(scene);
        stage.setTitle("Metro");
        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }
}