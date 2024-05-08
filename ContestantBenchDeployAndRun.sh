echo "Transfering data to the contestant bench  node."
sshpass -f password ssh sd211@l040101-ws02.ua.pt 'mkdir -p GameOfRope'
sshpass -f password ssh sd211@l040101-ws02.ua.pt 'rm -rf GameOfRope/*'
sshpass -f password scp dirContestantBench.zip sd211@l040101-ws02.ua.pt:GameOfRope

echo "Decompressing data sent to the contestant bench   node."
sshpass -f password ssh sd211@l040101-ws02.ua.pt 'cd GameOfRope ; unzip -uq dirContestantBench.zip'

echo "Executing program at the server contestant bench ."
sshpass -f password ssh sd211@l040101-ws02.ua.pt 'cd GameOfRope/dirContestantBench ; java serverSide.main.ServerGameOfRopeContestantsBench 22301 l040101-ws01.ua.pt 22300'

echo "Server contestant bench  shutdown."
