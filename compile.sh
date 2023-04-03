find . -name "*.java" > paths.txt
javac -d "bin" @paths.txt -cp "src/"
cd ./bin/
find . -name "*.class" > classes.txt
jar cfe projet.jar  etu.uparis.bdd.Main @classes.txt
mv projet.jar ../projet.jar
rm classes.txt
cd ..
rm paths.txt
