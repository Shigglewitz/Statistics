#!/bin/bash

usage() 
{
cat << EOF
usage: $0 options

This script parses commit histories

OPTIONS:
   -h      Show this message
   -c      Clean previous stat logs
   -l      Limit the number of commits to evaluate
   -q      Non-verbose
EOF
}

CLEAN=
# default
LIMIT=10
VERBOSE=1
while getopts “hcl:q” OPTION
do
     case $OPTION in
         h)
             usage
             exit 1
             ;;
         c)
             CLEAN=1
             ;;
         l)
             LIMIT=$OPTARG
             re='^[0-9]+$'
             if ! [[ $LIMIT =~ $re ]] ; then
                 echo "error: Not a number" >&2;
                 usage
                 exit 1
             fi
             ;;
         q)
             VERBOSE=0
             ;;
         ?)
             usage
             exit
             ;;
     esac
done

# abort on the first failure
set -e

if [ 1 -eq $VERBOSE ]; then
    # become verbose
    set -v
fi


# --------------
# Absolute path to this script, e.g. /home/user/bin/foo.sh
SCRIPT=$(readlink -f "$0")
# Absolute path this script is in, thus /home/user/bin
SCRIPTPATH=$(dirname "$SCRIPT")
# script is located in: $SCRIPTPATH
# --------------

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
# baseDir = $baseDir
# repositoryDir = $repositoryDir
# gitBaseDir = $baseDir/statistics/src/main/resources
# gitCommitsDir = $gitBaseDir/gitCommits
# gitStatsDir = $gitBaseDir/gitStats
# gitCloneDir = $baseDir/statistics/target/clone

# clean the commit dir
rm -rf $gitCommitsDir
mkdir $gitCommitsDir

# clean old stats
if [ $CLEAN == 1 ]; then
    mvn clean
    rm -rf $gitStatsDir
fi

if [ ! -d "$gitStatsDir" ]; then
  mkdir $gitStatsDir
fi 

# store git log to a text file 
git log -$LIMIT > $gitCommitsDir/gitlog.txt

# compile statistics project
mvn clean compile -pl statistics -am -e

# run statistics main to create commits.txt from gitlog.txt
mvn exec:java -Dexec.mainClass=org.dkeeney.git.Harvester -pl statistics -e

# create directory to host the git clone if it does not exist
if [ -d "$gitCloneDir" ]; then
    rm -rf $gitCloneDir
fi
mkdir $gitCloneDir 

cd $gitCloneDir
git clone $repositoryDir
cd workspace

pwd
# this monster command gets the first commit message, extracts the hash, and checks it out
git log --pretty=oneline -$LIMIT | tail -1 | sed -r 's/^([0-9a-f]{40}).*/\1/' | xargs git checkout

# the previous commits didn't have all the folders needed
mkdir -p org.dkeeney/utils/src/test/resources/rendered
mkdir -p org.dkeeney/graphing/src/test/resources/rendered
mkdir -p graphing/src/test/resources/rendered

# while loop produces lots of output so hide it
set +v 
set +e

while read line
do 
commit=$line
if [ ! -f $gitStatsDir/$commit.txt ]; then
    echo $commit.txt not found
    git checkout $commit
    mvn package -f */pom.xml -e > $gitStatsDir/$commit.txt
    mvn clean -q -f */pom.xml
fi
done < $gitCommitsDir/commits.txt

set -e

if [ 1 -eq $VERBOSE ]; then
    set -v
fi

# clean up the git clone
cd $baseDir
rm -rf $gitCloneDir