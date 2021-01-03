call echo "EXECUTION DU SCRAPER JAVA"
call echo "install dependencies"
call mvn compile -f "JavaExtractor"
call echo "compiler et executer un java"
call mvn package -f "JavaExtractor"
cd "JavaExtractor"
call java -cp "./target\WikipediaMatrix-1.0-Release.jar" fr.istic.pdl1819_grp5.wikiMainwikitext
call java -cp "./target\WikipediaMatrix-1.0-Release.jar" fr.istic.pdl1819_grp5.wikiMainhtml


call echo "EXECUTION DU SCRAPER PYTHON"
cd ..
cd "PyExtractor"
call python main.py


call echo "EXECUTION DU TEST DES PROGRAMMES"
cd ..
call python test_outputs.py