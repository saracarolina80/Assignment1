xterm  -T "General Repository" -hold -e "./GeneralReposDeployAndRun.sh" &
xterm  -T "RefereeSite" -hold -e "./RefereeSiteDeployAndRun.sh" &
xterm  -T "PLayground" -hold -e "./PlaygroundDeployAndRun.sh" &
xterm  -T "Contestant Bench" -hold -e "./ContestantBenchDeployAndRun.sh" &

sleep 1
xterm  -T "Referee" -hold -e "./RefereeDeployAndRun.sh" &
xterm  -T "Coach" -hold -e "./CoachDeployAndRun.sh" &
xterm  -T "Contestant" -hold -e "./ContestantDeployAndRun.sh" &
