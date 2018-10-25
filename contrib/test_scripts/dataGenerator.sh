#!/bin/bash

#
# test script for KMeans, using random generated dataset
#

#get the startup directory
startdir=$(dirname $0)
harproot=/Users/swithana/git/personal/harp
bin=$harproot/contrib/target/contrib-0.1.0.jar
hdfsroot=/harp-test
hdfsdatadir=$hdfsroot/km-syn/
hdfsoutput=$hdfsroot/km/
className=edu.iu.miniBatchKmeans.common.DataGenerator

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
# generateData <noOfDataPoints> <featureSize> <dataDirectory>
#
generateData()
{
       # <numOfDataPoints>: the number of data points you want to generate randomly
       # <size of vector>: the number of dimension of the data
       # <dataDirectory>: dataDirectory

    java -classpath $bin $className $1 100 $2

}

generateData 100 /Users/swithana/projects/harp
