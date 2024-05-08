echo "Transfering data to the general repository node."
sshpass -f password ssh sd211@l040101-ws01.ua.pt 'mkdir -p GameOfRope'
sshpass -f password ssh sd211@l040101-ws01.ua.pt 'rm -rf GameOfRope/*'
sshpass -f password scp dirGeneralRepos.zip sd211@l040101-ws01.ua.pt:GameOfRope

echo "Decompressing data sent to the general repository node."
sshpass -f password ssh sd211@l040101-ws01.ua.pt 'cd GameOfRope ; unzip -uq dirGeneralRepos.zip'

echo "Executing program at the server general repository."
sshpass -f password ssh sd211@l040101-ws01.ua.pt 'cd GameOfRope/dirGeneralRepos ; java serverSide.main.ServerGameOfRopeGeneralRepos 22300'

echo "Server General repository shutdown."
sshpass -f password ssh sd211@l040101-ws01.ua.pt 'cd GameOfRope/dirGeneralRepos ; less log.txt'

