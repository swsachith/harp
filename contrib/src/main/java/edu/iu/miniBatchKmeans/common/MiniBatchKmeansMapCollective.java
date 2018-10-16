package edu.iu.miniBatchKmeans.common;

import edu.iu.fileformat.MultiFileInputFormat;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

public class MiniBatchKmeansMapCollective
  extends Configured implements Tool {

  // memory allocated to each mapper (default value: 2 GB)
  private int mem_per_mapper = 2000;

  public static void main(String[] argv)
    throws Exception {
    int res = ToolRunner.run(new Configuration(),
      new MiniBatchKmeansMapCollective(), argv);
    System.exit(res);
  }

  @Override
  public int run(String[] args) throws Exception {
    if (args.length < 10) {
      System.err.println(
        "Usage: MiniBatchKmeansMapCollective <numOfDataPoints> <num of Centroids> "
          + "<size of vector> <number of map tasks> <number of threads><number of iteration> <workDir> <localDir> <communication operation> <batchSize>\n"
          + "<communication operation> includes:\n  "
          + "[allreduce]: use allreduce operation to synchronize centroids \n"
          + "[regroup-allgather]: use regroup and allgather operation to synchronize centroids \n"
          + "[broadcast-reduce]: use broadcast and reduce operation to synchronize centroids \n"
          + "[push-pull]: use push and pull operation to synchronize centroids\n");
      ToolRunner
        .printGenericCommandUsage(System.err);
      return -1;
    }

    int numOfDataPoints =
      Integer.parseInt(args[0]);
    int numCentroids = Integer.parseInt(args[1]);
    int sizeOfVector = Integer.parseInt(args[2]);
    int numMapTasks = Integer.parseInt(args[3]);
    int numThreads = Integer.parseInt(args[4]);
    int numIteration = Integer.parseInt(args[5]);
    String workDir = args[6];
    String localDir = args[7];
    String operation = args[8];
    int batchSize = Integer.parseInt(args[9]);

    if (args.length > 10)
      this.mem_per_mapper = Integer.parseInt(args[9]);

    System.out.println(
      "Number of Map Tasks = " + numMapTasks);
    System.out.println("Len : " + args.length);
    System.out.println("Args=:");
    for (String arg : args) {
      System.out.print(arg + ";");
    }
    System.out.println();

    launch(numOfDataPoints, numCentroids,
      sizeOfVector, numMapTasks, numThreads, numIteration,
      workDir, localDir, operation, batchSize);
    System.out.println("HarpKmeans Completed");
    return 0;
  }

  void launch(int numOfDataPoints,
    int numCentroids, int sizeOfVector,
    int numMapTasks, int numThreads, int numIteration,
    String workDir, String localDir,
    String operation, int batchSize) throws IOException,
    URISyntaxException, InterruptedException,
    ExecutionException, ClassNotFoundException {

    Configuration configuration = getConf();
    Path workDirPath = new Path(workDir);
    FileSystem fs = FileSystem.get(configuration);
    Path dataDir = new Path(workDirPath, "data");
    Path cenDir =
      new Path(workDirPath, "centroids");
    Path outDir = new Path(workDirPath, "out");
    if (fs.exists(outDir)) {
      fs.delete(outDir, true);
    }
    fs.mkdirs(outDir);

    //System.out.println("Generate data.");
//    Utils.generateData(numOfDataPoints, sizeOfVector, numMapTasks, fs, localDir, dataDir);

    int JobID = 0;
    //Utils.generateInitialCentroids(numCentroids, sizeOfVector, configuration, cenDir, fs, JobID);

    long startTime = System.currentTimeMillis();

    runKMeans(numOfDataPoints, numCentroids,
      sizeOfVector, numIteration, JobID,
      numMapTasks, numThreads, configuration, workDirPath,
      dataDir, cenDir, outDir, operation, batchSize);
    long endTime = System.currentTimeMillis();
    System.out
      .println("Total K-means Execution Time: "
        + (endTime - startTime));
  }

  private void runKMeans(int numOfDataPoints,
    int numCentroids, int vectorSize,
    int numIterations, int JobID, int numMapTasks, int numThreads,
    Configuration configuration, Path workDirPath,
    Path dataDir, Path cDir, Path outDir,
    String operation, int batchSize)
    throws IOException, URISyntaxException,
    InterruptedException, ClassNotFoundException {

    System.out.println("Starting Job");
    long jobSubmitTime;
    boolean jobSuccess = true;
    int jobRetryCount = 0;

    do {
      // ----------------------------------------------------------------------
      jobSubmitTime = System.currentTimeMillis();
      System.out
        .println("Start Job#" + JobID + " "
          + new SimpleDateFormat("HH:mm:ss.SSS")
            .format(
              Calendar.getInstance().getTime()));

      Job kmeansJob = configureKMeansJob(
        numOfDataPoints, numCentroids, vectorSize,
        numMapTasks, numThreads, configuration, workDirPath,
        dataDir, cDir, outDir, JobID,
        numIterations, operation, batchSize);

      System.out
        .println(
          "| Job#" + JobID + " configure in "
            + (System.currentTimeMillis()
              - jobSubmitTime)
            + " miliseconds |");

      // ----------------------------------------------------------
      jobSuccess =
        kmeansJob.waitForCompletion(true);

      System.out.println("end Jod#" + JobID + " "
        + new SimpleDateFormat("HH:mm:ss.SSS")
          .format(
            Calendar.getInstance().getTime()));
      System.out
        .println(
          "| Job#" + JobID + " Finished in "
            + (System.currentTimeMillis()
              - jobSubmitTime)
            + " miliseconds |");

      // ---------------------------------------------------------
      if (!jobSuccess) {
        System.out.println(
          "KMeans Job failed. Job ID:" + JobID);
        jobRetryCount++;
        if (jobRetryCount == 3) {
          break;
        }
      } else {
        break;
      }
    } while (true);
  }

  private Job configureKMeansJob(
    int numOfDataPoints, int numCentroids,
    int vectorSize, int numMapTasks, int numThreads,
    Configuration configuration, Path workDirPath,
    Path dataDir, Path cDir, Path outDir,
    int jobID, int numIterations,
    String operation, int batchSize)
    throws IOException, URISyntaxException {

    Job job = Job.getInstance(configuration,
      "kmeans_job_" + jobID);
    Configuration jobConfig =
      job.getConfiguration();
    Path jobOutDir =
      new Path(outDir, "kmeans_out_" + jobID);
    FileSystem fs = FileSystem.get(configuration);
    if (fs.exists(jobOutDir)) {
      fs.delete(jobOutDir, true);
    }
    FileInputFormat.setInputPaths(job, dataDir);
    FileOutputFormat.setOutputPath(job,
      jobOutDir);

    Path cFile = new Path(cDir,
      MiniBatchKMeansConstants.CENTROID_FILE_PREFIX
        + jobID);
    System.out.println(
      "Centroid File Path: " + cFile.toString());
    jobConfig.set(MiniBatchKMeansConstants.CFILE,
      cFile.toString());
    jobConfig.setInt(MiniBatchKMeansConstants.JOB_ID,
      jobID);
    jobConfig.setInt(MiniBatchKMeansConstants.NUM_ITERATONS, numIterations);
    jobConfig.setInt(MiniBatchKMeansConstants.NUM_THREADS, numThreads);
    jobConfig.setInt(MiniBatchKMeansConstants.BATCH_SIZE, batchSize);
    job.setInputFormatClass(
      MultiFileInputFormat.class);
    job.setJarByClass(MiniBatchKmeansMapCollective.class);

    // use different kinds of mappers
    if (operation.equalsIgnoreCase("allreduce")) {
      job.setMapperClass(
        edu.iu.miniBatchKmeans.allreduce.MiniBatchKmeansMapper.class);
    } else if (operation.equalsIgnoreCase("randomFile")) {
      job.setMapperClass(
        edu.iu.miniBatchKmeans.allreduce.RandomFileMBKmeansMapper.class);
    } else {// by default, allreduce
      job.setMapperClass(
        edu.iu.miniBatchKmeans.allreduce.MiniBatchKmeansMapper.class);
    }

    JobConf jobConf =
      (JobConf) job.getConfiguration();
    jobConf.set("mapreduce.framework.name",
      "map-collective");
    jobConf.setNumMapTasks(numMapTasks);
    jobConf.setInt(
      "mapreduce.job.max.split.locations", 10000);
    job.setNumReduceTasks(0);
    jobConfig.setInt(MiniBatchKMeansConstants.VECTOR_SIZE,
      vectorSize);
    jobConfig.setInt(
      MiniBatchKMeansConstants.NUM_CENTROIDS,
      numCentroids);
    jobConfig.set(MiniBatchKMeansConstants.WORK_DIR,
      workDirPath.toString());
    jobConfig.setInt(MiniBatchKMeansConstants.NUM_MAPPERS,
      numMapTasks);

    jobConf.setInt(
            "mapreduce.map.collective.memory.mb", this.mem_per_mapper);
    // mapreduce.map.collective.java.opts
    int xmx = (int) Math.ceil((this.mem_per_mapper)*0.8);
    int xmn = (int) Math.ceil(0.25 * xmx);
    jobConf.set(
            "mapreduce.map.collective.java.opts",
            "-Xmx" + xmx + "m -Xms" + xmx + "m"
                    + " -Xmn" + xmn + "m");
    return job;
  }

}
