echo "Executing the ContestantBench node."
cd /home/ubuntu/Desktop/ASS2_T2_98376/dirContestantBench
java -cp "../lib/genclass.jar:."  serverSide.main.ServerGameOfRopeContestantBench 22301 127.0.0.1 22300
echo "Server shutdown."
