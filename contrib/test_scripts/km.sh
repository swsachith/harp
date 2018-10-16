#!/bin/bash

#
# test script for KMeans, using random generated dataset
#

#get the startup directory
startdir=$(dirname $0)
harproot=/Users/swithana/git/personal/harp
bin=$harproot/contrib/target/contrib-0.1.0.jar
dfsroot=/harp-test
hdfsdatadir=$hdfsroot/km-syn/
hdfsoutput=$hdfsroot/km/

if [ ! -f $bin ] ; then
    echo "harp contrib app not found at "$bin
    exit -1
fi
if [ -z ${HADOOP_HOME+x} ];then
    echo "HADOOP not setup"
    exit
fi

#workdir
workdir=test_km

mkdir -p $workdir
cd $workdir

#
# runtest <outputdir> <comm>
#
runtest()
{
       # <numOfDataPoints>: the number of data points you want to generate randomly
       # <num of centriods>: the number of centroids you want to clustering the data to
       # <size of vector>: the number of dimension of the data
       # <number of map tasks>: number of map tasks
       # <number of threads>: number of threads per task
       # <number of iteration>: the number of iterations to run
       # <work dir>: the root directory for this running in HDFS
       # <local dir>: the harp kmeans will firstly generate files which contain data points to local directory. Set this argument to determine the local directory.
       # <communication operation> includes:
    	# 	[allreduce]: use allreduce operation to synchronize centroids
    	# 	[regroup-allgather]: use regroup and allgather operation to synchronize centroids 
    	# 	[broadcast-reduce]: use broadcast and reduce operation to synchronize centroids
    	# 	[push-pull]: use push and pull operation to synchronize centroids
    hadoop jar $bin edu.iu.kmeans.common.KmeansMapCollective 1000000 10 100 1 4 100 $1 /tmp/kmeans $2

    if [ $? -ne 0 ]; then
        echo "run km failure"
        exit -1
    fi
    
    #check the result
    echo "checking result of :"$2
    ret=$(hdfs dfs -cat $1/evaluation)
    echo "MSE="$ret
    eval=$(echo "($ret < 20) && ($ret >0)" | bc)
    if [ $eval -eq 1 ]; then
        echo "Pass!"
        #exit 0
    else
        echo "Fail!"
        exit -1
    fi
    
}

#run test
#hadoop jar $bin edu.iu.kmeans.common.KmeansMapCollective 1000 10 10 2 100 $hdfsoutput/allreduce /tmp/kmeans allreduce
#hadoop jar $bin edu.iu.kmeans.common.KmeansMapCollective 1000 10 10 2 100 $hdfsoutput/regroup /tmp/kmeans regroup-allgather
#hadoop jar $bin edu.iu.kmeans.common.KmeansMapCollective 1000 10 10 2 100 $hdfsoutput/broadcast /tmp/kmeans broadcast-reduce
#hadoop jar $bin edu.iu.kmeans.common.KmeansMapCollective 1000 10 10 2 100 $hdfsoutput/pushpull /tmp/kmeans push-pull
runtest /tmp allreduce
#runtest $hdfsoutput/regroup regroup-allgather
#runtest $hdfsoutput/broadcast broadcast-reduce
#runtest $hdfsoutput/pushpull push-pull
