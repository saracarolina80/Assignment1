echo "Transfering data to the Coach node."
sshpass -f password ssh sd211@l040101-ws07.ua.pt 'mkdir -p GameOfRope'
sshpass -f password ssh sd211@l040101-ws07.ua.pt 'rm -rf GameOfRope/*'
sshpass -f password scp dirCoach.zip sd211@l040101-ws07.ua.pt:GameOfRope

echo "Decompressing data sent to the Coach node."
sshpass -f password ssh sd211@l040101-ws07.ua.pt 'cd GameOfRope ; unzip -uq dirCoach.zip'

echo "Executing program at the Coach node."
sshpass -f password ssh sd211@l040101-ws07.ua.pt 'cd GameOfRope/dirCoach ; java clientSide.main.ClientGameOfRopeCoach l040101-ws01.ua.pt 22300 l040101-ws02.ua.pt 22301 l040101-ws04.ua.pt 22302 l040101-ws05.ua.pt 22303 '

echo "Server Coach shutdown."
