echo "Transfering data to the Referee node."
cd /home/ubuntu/Desktop/ASS2_T2_98376/dirReferee
java -cp "../lib/genclass.jar:."  clientSide.main.ClientGameOfRopeReferee 127.0.0.1 22302 127.0.0.1 22303 127.0.0.1 22300
