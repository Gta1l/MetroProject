package com.example.metro;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Graph {
    private double[][] GraphMatrix;
    private double[][] OnesGraphMatrix;
    private boolean[] MarkedVertexes;
    private double[] values;

    Graph(int VertexCount, double[][] GM) {
        this.GraphMatrix = new double[VertexCount][VertexCount];
        this.OnesGraphMatrix = new double[VertexCount][VertexCount];
        for (double[] doubles : GM) {
            try {
                if (doubles.length != 3) {
                    throw new RuntimeException("Wrong size of graph matrix");
                }
                GraphMatrix[(int) doubles[0]][(int) doubles[1]] = doubles[2];
                GraphMatrix[(int) doubles[1]][(int) doubles[0]] = doubles[2];
                OnesGraphMatrix[(int) doubles[0]][(int) doubles[1]] = 1;
                OnesGraphMatrix[(int) doubles[1]][(int) doubles[0]] = 1;
            } catch (Exception e) {
                throw new RuntimeException("Wrong count of Vertex");
            }
        }
        for (int i = 0; i < VertexCount; i++) {
            for (int j = 0; j < VertexCount; j++) {
                if (GraphMatrix[i][j] == 0) {
                    GraphMatrix[i][j] = Double.POSITIVE_INFINITY;
                    OnesGraphMatrix[i][j] = Double.POSITIVE_INFINITY;
                }
                if (i == j) {
                    GraphMatrix[i][j] = 0;
                    OnesGraphMatrix[i][j] = 0;
                }
            }
        }

        MarkedVertexes = new boolean[VertexCount];
        Arrays.fill(MarkedVertexes, false);
        values = new double[VertexCount];
        Arrays.fill(values, Double.POSITIVE_INFINITY);
    }

    double ShortestDistance(int StartVertexID, int EndVertexID) {
        Arrays.fill(MarkedVertexes, false);
        Arrays.fill(values, Double.POSITIVE_INFINITY);
        values[StartVertexID] = 0;
        int CurrentVertexID = StartVertexID;
        for (int n = 0; n < values.length; n++) {
            MarkedVertexes[CurrentVertexID] = true;
            double min = Double.POSITIVE_INFINITY;
            int minID = CurrentVertexID;
            for (int i = 0; i < MarkedVertexes.length; i++) {

                if (!MarkedVertexes[i] && (values[CurrentVertexID] + GraphMatrix[CurrentVertexID][i] < values[i])) {
                    values[i] = values[CurrentVertexID] + GraphMatrix[CurrentVertexID][i];
                }
                if (!MarkedVertexes[i] && values[i] <= min) {
                    min = values[i];
                    minID = i;
                }
            }
            CurrentVertexID = minID;
        }
        return values[EndVertexID];
    }

    List<Integer> ShortestWay(int StartVertexID, int EndVertexID) {
        ShortestDistance(StartVertexID, EndVertexID);
        Arrays.fill(MarkedVertexes, false);
        int CurrentVertexID = EndVertexID;
        List<Integer> shortestway = new ArrayList<>();
        shortestway.addFirst(CurrentVertexID);
        for (int n = 0; n < values.length; n++) {
            MarkedVertexes[CurrentVertexID] = true;
            for (int i = 0; i < MarkedVertexes.length; i++) {
                if (!MarkedVertexes[i] && values[CurrentVertexID] - values[i] == GraphMatrix[CurrentVertexID][i] ) {
                    CurrentVertexID = i;
                    shortestway.addFirst(CurrentVertexID);
                    break;
                }
            }
            if (CurrentVertexID == EndVertexID) {
                break;
            }
        }
        return shortestway;
    }

    double MinVertex(int StartVertexID, int EndVertexID) {
        Arrays.fill(MarkedVertexes, false);
        Arrays.fill(values, Double.POSITIVE_INFINITY);
        values[StartVertexID] = 0;
        int CurrentVertexID = StartVertexID;
        for (int n = 0; n < values.length; n++) {
            MarkedVertexes[CurrentVertexID] = true;
            double min = Double.POSITIVE_INFINITY;
            int minID = CurrentVertexID;
            for (int i = 0; i < MarkedVertexes.length; i++) {
                if (!MarkedVertexes[i] && (values[CurrentVertexID] + OnesGraphMatrix[CurrentVertexID][i] < values[i])) {
                    values[i] = values[CurrentVertexID] + OnesGraphMatrix[CurrentVertexID][i];
                }
                if (!MarkedVertexes[i] && values[i] <= min) {
                    min = values[i];
                    minID = i;
                }
            }
            CurrentVertexID = minID;
        }
        return values[EndVertexID];
    }

    List<Integer> MinVertexWay(int StartVertexID, int EndVertexID) {
        MinVertex(StartVertexID, EndVertexID);
        Arrays.fill(MarkedVertexes, false);
        int CurrentVertexID = EndVertexID;
        List<Integer> minvertexway = new ArrayList<>();
        minvertexway.addFirst(CurrentVertexID);
        for (int n = 0; n < values.length; n++) {

            MarkedVertexes[CurrentVertexID] = true;

            for (int i = 0; i < MarkedVertexes.length; i++) {
                if (!MarkedVertexes[i] && values[CurrentVertexID] - values[i] == OnesGraphMatrix[CurrentVertexID][i] ) {
                    CurrentVertexID = i;
                    minvertexway.addFirst(CurrentVertexID);
                    break;
                }
            }
            if (CurrentVertexID == EndVertexID) {
                break;
            }
        }
        return minvertexway;
    }



    @Override
    public String toString() {
        String str = "";
        for (double[] graphMatrix : GraphMatrix) {
            for (double matrix : graphMatrix) {
                str += matrix + " ";
            }
            str += "\n";
        }
        str += "\n";
        for (double value : values) {
            str += value + " ";
        }
        str += "\n";
        return str;
    }
}
