echo "Transfering data to the referee site node."
sshpass -f password ssh sd211@l040101-ws04.ua.pt 'mkdir -p GameOfRope'
sshpass -f password ssh sd211@l040101-ws04.ua.pt 'rm -rf GameOfRope/*'
sshpass -f password scp dirRefereeSite.zip sd211@l040101-ws04.ua.pt:GameOfRope

echo "Decompressing data sent to the referee  site node."
sshpass -f password ssh sd211@l040101-ws04.ua.pt 'cd GameOfRope ; unzip -uq dirRefereeSite.zip'

echo "Executing program at the server referee site."
sshpass -f password ssh sd211@l040101-ws04.ua.pt 'cd GameOfRope/dirRefereeSite ; java serverSide.main.ServerGameOfRopeRefereeSite 22302 l040101-ws01.ua.pt 22300'

echo "Server referee site  shutdown."
