pwd

# send git log to gitlog.txt
command="git log > statistics/src/main/resources/git/gitlog.txt"
echo $command
$command

# compile statistics project
command="mvn compile -pl statistics -am"
echo $command
$command

# run statistics main to create commits.txt
command="mvn exec:java -Dexec.mainClass=org.dkeeney.git.Statistics -pl statistics -am"
echo $command
$command