echo "Transfering data to the playground node."
sshpass -f password ssh sd211@l040101-ws05.ua.pt 'mkdir -p GameOfRope'
sshpass -f password ssh sd211@l040101-ws05.ua.pt 'rm -rf GameOfRope/*'
sshpass -f password scp dirPlayground.zip sd211@l040101-ws05.ua.pt:GameOfRope

echo "Decompressing data sent to the playground node."
sshpass -f password ssh sd211@l040101-ws05.ua.pt 'cd GameOfRope ; unzip -uq dirPlayground.zip'

echo "Executing program at the server playground."
sshpass -f password ssh sd211@l040101-ws05.ua.pt 'cd GameOfRope/dirPlayground ; java serverSide.main.ServerGameOfRopePlayground 22303 l040101-ws01.ua.pt 22300'

echo "Server playground  shutdown."
