xterm  -T "General Repository" -hold -e "./GeneralReposDeployAndRun.sh" &
xterm  -T "Contestant Bench" -hold -e "./ContestantBenchDeployAndRun.sh" &
xterm  -T "Referee Site" -hold -e "./RefereeSiteDeployAndRun.sh" &
xterm  -T "Playground" -hold -e "./PlaygroundDeployAndRun.sh" &
sleep 1
xterm  -T "Referee" -hold -e "./RefereeDeployAndRun.sh" &
xterm  -T "Coach" -hold -e "./CoachDeployAndRun.sh" &
xterm  -T "Contestant" -hold -e "./ContestantDeployAndRun.sh" &
