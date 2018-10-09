package edu.iu.miniBatchKmeans.common;

import edu.iu.mlr.Instance;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class Utils {
  private final static int DATA_RANGE = 1;

  public static void generateData(
    int numOfDataPoints, int vectorSize,
    int numMapTasks, FileSystem fs,
    String localDirStr, Path dataDir)
    throws IOException, InterruptedException,
    ExecutionException {
    int numOfpointFiles = numMapTasks;
    int pointsPerFile =
      numOfDataPoints / numOfpointFiles;
    int pointsRemainder =
      numOfDataPoints % numOfpointFiles;
    System.out.println("Writing "
      + numOfDataPoints + " vectors to "
      + numMapTasks + " file evenly");

    // Check data directory
    if (fs.exists(dataDir)) {
      fs.delete(dataDir, true);
    }
    // Check local directory
    File localDir = new File(localDirStr);
    // If existed, regenerate data
    if (localDir.exists()
      && localDir.isDirectory()) {
      for (File file : localDir.listFiles()) {
        file.delete();
      }
      localDir.delete();
    }

    boolean success = localDir.mkdir();
    if (success) {
      System.out.println(
        "Directory: " + localDirStr + " created");
    }
    if (pointsPerFile == 0) {
      throw new IOException("No point to write.");
    }

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
    }

    Path localPath = new Path(localDirStr);
    fs.copyFromLocalFile(localPath, dataDir);
  }

  public static void generateInitialCentroids(
    int numCentroids, int vectorSize,
    Configuration configuration, Path cDir,
    FileSystem fs, int JobID) throws IOException {
    Random random = new Random();
    double[] data = null;
    if (fs.exists(cDir))
      fs.delete(cDir, true);
    if (!fs.mkdirs(cDir)) {
      throw new IOException(
        "Mkdirs failed to create "
          + cDir.toString());
    }

    data = new double[numCentroids * vectorSize];
    for (int i = 0; i < data.length; i++) {
      data[i] = random.nextDouble() * DATA_RANGE;
    }
    Path initClustersFile = new Path(cDir,
      MiniBatchKMeansConstants.CENTROID_FILE_PREFIX
        + JobID);
    System.out.println("Generate centroid data."
      + initClustersFile.toString());

    FSDataOutputStream out =
      fs.create(initClustersFile, true);
    BufferedWriter bw = new BufferedWriter(
      new OutputStreamWriter(out));
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
    System.out
      .println("Wrote centroids data to file");
  }

  public static Set<Integer> getRandomRange(int min, int max, int batchSize) {
    Set<Integer> resultSet = new HashSet<>();
    while (resultSet.size() < batchSize) {
      resultSet.add(generateRandomNumberInRange(min, max));
    }
    return resultSet;
  }


  private static int generateRandomNumberInRange(int min, int max) {
    Random random = new Random();
    return random.nextInt((max - min) + 1) + min;
  }

}
