#!/bin/bash

echo --------------
# Absolute path to this script, e.g. /home/user/bin/foo.sh
SCRIPT=$(readlink -f "$0")
# Absolute path this script is in, thus /home/user/bin
SCRIPTPATH=$(dirname "$SCRIPT")
echo script is located in: $SCRIPTPATH
echo --------------

# determine if the user requested the clean operation
clean=0;
if [ $# -ne 0 ]; then
    if [ "$1" == "clean" ]; then
        clean=1;
    fi
fi

# this script needs to work from one level above the statistics folder
cd $SCRIPTPATH
cd ..

# declare directory vars
baseDir=$(pwd)
cd ..
repositoryDir=$(pwd)
cd $baseDir
gitBaseDir=$baseDir/statistics/src/main/resources
gitCommitsDir=$gitBaseDir/gitCommits
gitStatsDir=$gitBaseDir/gitStats
gitCloneDir=$baseDir/statistics/target/clone

# echo directory vars
echo baseDir = $baseDir
echo repositoryDir = $repositoryDir
echo gitBaseDir = $baseDir/statistics/src/main/resources
echo gitCommitsDir = $gitBaseDir/gitCommits
echo gitStatsDir = $gitBaseDir/gitStats
echo gitCloneDir = $baseDir/statistics/target/clone

# clean the commit dir
rm -rf $gitCommitsDir
mkdir $gitCommitsDir

# clean old stats
if [ $clean == 1 ]; then
    mvn clean
    rm -rf $gitStatsDir
fi

if [ ! -d "$gitStatsDir" ]; then
  mkdir $gitStatsDir
fi 

# become verbose
set -v

# store git log to a text file 
git log > $gitCommitsDir/gitlog.txt

# compile statistics project
mvn compile -pl statistics -am -e

# run statistics main to create commits.txt from gitlog.txt
mvn exec:java -Dexec.mainClass=org.dkeeney.git.Statistics -pl statistics -e

# create directory to host the git clone if it does not exist
if [ ! -d "$gitCloneDir" ]; then
  mkdir $gitCloneDir
fi 

cd $gitCloneDir
git clone $repositoryDir
cd workspace

pwd

# the previous commits didn't have all the folders needed
mkdir -p org.dkeeney/utils/src/test/resources/rendered
mkdir -p org.dkeeney/graphing/src/test/resources/rendered
mkdir -p graphing/src/test/resources/rendered

while read line
do 
commit=$line
if [ ! -f $gitStatsDir/$commit.txt ]; then
    echo $commit.txt not found
    git checkout $commit
    mvn package -f */pom.xml -e > $gitStatsDir/$commit.txt
fi
done < $gitCommitsDir/commits.txt

# clean up the git clone
cd $baseDir
rm -rf $gitCloneDir