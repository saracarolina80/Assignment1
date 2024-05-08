echo "Executing the Concentration Site node."
cd /home/ubuntu/Desktop/ASS2_T2_98376/dirRefereeSite
java -cp "../lib/genclass.jar:."  serverSide.main.ServerGameOfRopeRefereeSite 22302 127.0.0.1 22300
echo "Server shutdown."
