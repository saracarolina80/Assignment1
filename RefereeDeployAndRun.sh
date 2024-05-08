echo "Transfering data to the Referee node."
sshpass -f password ssh sd211@l040101-ws06.ua.pt 'mkdir -p GameOfRope'
sshpass -f password ssh sd211@l040101-ws06.ua.pt 'rm -rf GameOfRope/*'
sshpass -f password scp dirReferee.zip sd211@l040101-ws06.ua.pt:GameOfRope

echo "Decompressing data sent to the Referee node."
sshpass -f password ssh sd211@l040101-ws06.ua.pt 'cd GameOfRope ; unzip -uq dirReferee.zip'

echo "Executing program at the Referee node."
sshpass -f password ssh sd211@l040101-ws06.ua.pt 'cd GameOfRope/dirReferee ; java clientSide.main.ClientGameOfRopeReferee l040101-ws01.ua.pt 22300  l040101-ws04.ua.pt 22302 l040101-ws05.ua.pt 22303 '

echo "Server Referee shutdown."
