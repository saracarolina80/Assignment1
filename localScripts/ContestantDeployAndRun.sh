echo "Transfering data to the Contestant node."
cd /home/ubuntu/Desktop/ASS2_T2_98376/dirContestant
java -cp "../lib/genclass.jar:."  clientSide.main.ClientGameOfRopeContestant 127.0.0.1 22301  127.0.0.1 22303 127.0.0.1 22300
