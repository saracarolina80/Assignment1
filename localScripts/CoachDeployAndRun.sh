echo "Transfering data to the Coach node."
cd /home/ubuntu/Desktop/ASS2_T2_98376/dirCoach
java -cp "../lib/genclass.jar:."  clientSide.main.ClientGameOfRopeCoach 127.0.0.1 22301  127.0.0.1 22302 127.0.0.1 22303 127.0.0.1 22300
