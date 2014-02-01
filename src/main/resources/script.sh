echo sup
cd ..
pwd
git log > statistics/src/main/resources/git/gitlog.txt
mvn compile -pl statistics -am