# clean target
# rm -rf target/

echo "Compiling source code..."
javac -cp lib/genclass.jar -d target -sourcepath . main/GameOfRope.java entities/*.java sharedRegions/*.java

echo "Extracting library genclass..."
cp lib/genclass.jar ./target

echo -e "Executing source code:\n"
cd target ; jar xf genclass.jar ; java main.GameOfRope