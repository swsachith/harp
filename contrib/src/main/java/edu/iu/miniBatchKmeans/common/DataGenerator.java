package edu.iu.miniBatchKmeans.common;

import java.io.*;
import java.util.Random;

public class DataGenerator {
    private final static int DATA_RANGE = 10;

    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Requres numberOfDataPoints, featureSize and dataDirectory as arguments.");
            System.exit(0);
        }

        int numberOfDataPoints = Integer.parseInt(args[0]);
        int featureSize = Integer.parseInt(args[1]);
        String dataDirectory = args[2];

        String centroidsDir = dataDirectory + "/centroids";
        int numberOfFiles = 1;
        try {
            generateData(numberOfDataPoints, featureSize, dataDirectory, numberOfFiles);
            generateInitialCentroids(10, 100, centroidsDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void generateData(int numOfDataPoints, int vectorSize, String localDirStr, int numMapTasks) throws IOException{

        int numOfpointFiles = numMapTasks;
        int pointsPerFile =
                numOfDataPoints / numOfpointFiles;
        int pointsRemainder =
                numOfDataPoints % numOfpointFiles;
        System.out.println("Writing "
                + numOfDataPoints + " vectors to "
                + numMapTasks + " file evenly");

        double point;
        int hasRemainder = 0;
        Random random = new Random();
        for (int k = 0; k < numOfpointFiles; k++) {
            try {
                String filename = Integer.toString(k);
                File file = new File(localDirStr
                        + File.separator + "data_" + filename);
                FileWriter fw =
                        new FileWriter(file.getAbsoluteFile());
                BufferedWriter bw =
                        new BufferedWriter(fw);

                if (pointsRemainder > 0) {
                    hasRemainder = 1;
                    pointsRemainder--;
                } else {
                    hasRemainder = 0;
                }
                int pointsForThisFile =
                        pointsPerFile + hasRemainder;
                for (int i =
                     0; i < pointsForThisFile; i++) {
                    for (int j = 0; j < vectorSize; j++) {
                        point =
                                random.nextDouble() * DATA_RANGE;
                        // System.out.println(point+"\t");
                        if (j == vectorSize - 1) {
                            bw.write(point + "");
                            bw.newLine();
                        } else {
                            bw.write(point + " ");
                        }
                    }
                }
                bw.close();
                System.out.println(
                        "Done written" + pointsForThisFile
                                + "points" + "to file " + filename);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
    }

    public static void generateInitialCentroids(int numCentroids, int vectorSize, String cDir) throws IOException {
        Random random = new Random();
        double[] data = null;

        data = new double[numCentroids * vectorSize];
        for (int i = 0; i < data.length; i++) {
            data[i] = random.nextDouble() * DATA_RANGE;
        }
        File file = new File(cDir + File.separator + MiniBatchKMeansConstants.CENTROID_FILE_PREFIX + vectorSize);
        FileWriter fw = new FileWriter(file.getAbsoluteFile());

        BufferedWriter bw = new BufferedWriter(fw);
        for (int i = 0; i < data.length; i++) {
            if ((i % vectorSize) == (vectorSize - 1)) {
                bw.write(data[i] + "");
                bw.newLine();
            } else {
                bw.write(data[i] + " ");
            }
        }
        bw.flush();
        bw.close();
        // out.flush();
        // out.sync();
        // out.close();
        System.out.println("Wrote centroids data to file");
    }
}
