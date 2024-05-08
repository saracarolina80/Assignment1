echo "Transfering data to the Contestant node."
sshpass -f password ssh sd211@l040101-ws08.ua.pt 'mkdir -p GameOfRope'
sshpass -f password ssh sd211@l040101-ws08.ua.pt 'rm -rf GameOfRope/*'
sshpass -f password scp dirContestant.zip sd211@l040101-ws08.ua.pt:GameOfRope

echo "Decompressing data sent to the Contestant node."
sshpass -f password ssh sd211@l040101-ws08.ua.pt 'cd GameOfRope ; unzip -uq dirContestant.zip'

echo "Executing program at the Contestant node."
sshpass -f password ssh sd211@l040101-ws08.ua.pt 'cd GameOfRope/dirRefereeContestant ; java clientSide.main.ClientGameOfRopeContestant l040101-ws01.ua.pt 22300 l040101-ws02.ua.pt 22301 l040101-ws05.ua.pt 22303 '

echo "Server Contestant shutdown."
