echo "Compiling source code."
javac -cp ../lib/genclass.jar */*.java */*/*.java

echo "Distributing intermediate code to the different execution environments."

echo "  General Repository of Information"
rm -rf dirGeneralRepos
mkdir -p dirGeneralRepos dirGeneralRepos/serverSide dirGeneralRepos/serverSide/main dirGeneralRepos/serverSide/entities dirGeneralRepos/serverSide/sharedRegions \
         dirGeneralRepos/clientSide dirGeneralRepos/clientSide/entities dirGeneralRepos/commInfra
cp serverSide/main/SimulPar.class serverSide/main/ServerGameOfRopeGeneralRepos.class dirGeneralRepos/serverSide/main
cp serverSide/entities/GeneralReposClientProxy.class dirGeneralRepos/serverSide/entities
cp serverSide/sharedRegions/GeneralReposInterface.class serverSide/sharedRegions/GeneralRepos.class dirGeneralRepos/serverSide/sharedRegions
cp clientSide/entities/CoachStates.class clientSide/entities/RefereeStates.class clientSide/entities/ContestantStates.class dirGeneralRepos/clientSide/entities
cp commInfra/Message.class commInfra/MessageType.class commInfra/MessageException.class commInfra/ServerCom.class dirGeneralRepos/commInfra


echo "  Contestant Bench"
rm -rf dirContestantBench
mkdir -p dirContestantBench dirContestantBench/serverSide dirContestantBench/serverSide/main dirContestantBench/serverSide/entities dirContestantBench/serverSide/sharedRegions \
         dirContestantBench/clientSide dirContestantBench/clientSide/entities dirContestantBench/clientSide/stubs dirContestantBench/commInfra
cp serverSide/main/SimulPar.class serverSide/main/ServerGameOfRopeContestantsBench.class dirContestantBench/serverSide/main
cp serverSide/entities/ContestantsBenchClientProxy.class dirContestantBench/serverSide/entities
cp serverSide/sharedRegions/GeneralReposInterface.class serverSide/sharedRegions/ContestantsBenchInterface.class serverSide/sharedRegions/ContestantsBench.class \
   dirContestantBench/serverSide/sharedRegions
cp clientSide/entities/RefereeStates.class clientSide/entities/CoachStates.class clientSide/entities/ContestantStates.class clientSide/entities/CoachCloning.class clientSide/entities/RefereeCloning.class clientSide/entities/ContestantCloning.class \
   dirContestantBench/clientSide/entities
cp clientSide/stubs/GeneralRepositoryStub.class dirContestantBench/clientSide/stubs
cp commInfra/*.class dirContestantBench/commInfra


echo "  Referee Site"
rm -rf dirRefereeSite
mkdir -p dirRefereeSite dirRefereeSite/serverSide dirRefereeSite/serverSide/main dirRefereeSite/serverSide/entities dirRefereeSite/serverSide/sharedRegions \
         dirRefereeSite/clientSide dirRefereeSite/clientSide/entities dirRefereeSite/clientSide/stubs dirRefereeSite/commInfra
cp serverSide/main/SimulPar.class serverSide/main/ServerGameOfRopeRefereeSite.class dirRefereeSite/serverSide/main
cp serverSide/entities/RefereeSiteClientProxy.class dirRefereeSite/serverSide/entities
cp serverSide/sharedRegions/GeneralReposInterface.class serverSide/sharedRegions/RefereeSiteInterface.class serverSide/sharedRegions/RefereeSite.class \
   dirRefereeSite/serverSide/sharedRegions
cp clientSide/entities/RefereeStates.class clientSide/entities/CoachStates.class clientSide/entities/ContestantStates.class clientSide/entities/CoachCloning.class clientSide/entities/RefereeCloning.class clientSide/entities/ContestantCloning.class \
   dirRefereeSite/clientSide/entities
cp clientSide/stubs/GeneralRepositoryStub.class dirRefereeSite/clientSide/stubs
cp commInfra/*.class dirRefereeSite/commInfra


echo "  Playground"
rm -rf dirPlayground
mkdir -p dirPlayground dirPlayground/serverSide dirPlayground/serverSide/main dirPlayground/serverSide/entities dirPlayground/serverSide/sharedRegions \
         dirPlayground/clientSide dirPlayground/clientSide/entities dirPlayground/clientSide/stubs dirPlayground/commInfra
cp serverSide/main/SimulPar.class serverSide/main/ServerGameOfRopePlayground.class dirPlayground/serverSide/main
cp serverSide/entities/PlaygroundClientProxy.class dirPlayground/serverSide/entities
cp serverSide/sharedRegions/GeneralReposInterface.class serverSide/sharedRegions/PlaygroundInterface.class serverSide/sharedRegions/Playground.class \
   dirPlayground/serverSide/sharedRegions
cp clientSide/entities/RefereeStates.class clientSide/entities/CoachStates.class  clientSide/entities/ContestantStates.class  clientSide/entities/CoachCloning.class clientSide/entities/RefereeCloning.class clientSide/entities/ContestantCloning.class  \
   dirPlayground/clientSide/entities
cp clientSide/stubs/GeneralRepositoryStub.class dirPlayground/clientSide/stubs
cp commInfra/*.class dirPlayground/commInfra



echo "  Referee"
rm -rf dirReferee
mkdir -p dirReferee dirReferee/serverSide dirReferee/serverSide/main dirReferee/clientSide dirReferee/clientSide/main dirReferee/clientSide/entities \
         dirReferee/clientSide/stubs dirReferee/commInfra
cp serverSide/main/SimulPar.class dirReferee/serverSide/main
cp clientSide/main/ClientGameOfRopeReferee.class dirReferee/clientSide/main
cp clientSide/entities/Referee.class clientSide/entities/RefereeStates.class dirReferee/clientSide/entities
cp clientSide/stubs/GeneralRepositoryStub.class clientSide/stubs/RefereeSiteStub.class clientSide/stubs/PlaygroundStub.class \
    clientSide/stubs/ContestantsBenchStub.class  dirReferee/clientSide/stubs
cp commInfra/Message.class commInfra/MessageType.class commInfra/MessageException.class commInfra/ClientCom.class dirReferee/commInfra

echo "  Coach"
rm -rf dirCoach
mkdir -p dirCoach dirCoach/serverSide dirCoach/serverSide/main dirCoach/clientSide dirCoach/clientSide/main dirCoach/clientSide/entities \
         dirCoach/clientSide/stubs dirCoach/commInfra
cp serverSide/main/SimulPar.class dirCoach/serverSide/main
cp clientSide/main/ClientGameOfRopeCoach.class dirCoach/clientSide/main
cp clientSide/entities/Coach.class clientSide/entities/CoachStates.class dirCoach/clientSide/entities
cp clientSide/stubs/GeneralRepositoryStub.class clientSide/stubs/RefereeSiteStub.class clientSide/stubs/PlaygroundStub.class \
     clientSide/stubs/ContestantsBenchStub.class dirCoach/clientSide/stubs
cp commInfra/Message.class commInfra/MessageType.class commInfra/MessageException.class commInfra/ClientCom.class dirCoach/commInfra

echo "  Contestant"
rm -rf dirContestant
mkdir -p dirContestant dirContestant/serverSide dirContestant/serverSide/main dirContestant/clientSide dirContestant/clientSide/main dirContestant/clientSide/entities \
         dirContestant/clientSide/stubs dirContestant/commInfra
cp serverSide/main/SimulPar.class dirContestant/serverSide/main
cp clientSide/main/ClientGameOfRopeContestant.class dirContestant/clientSide/main
cp clientSide/entities/Contestant.class clientSide/entities/ContestantStates.class dirContestant/clientSide/entities
cp clientSide/stubs/GeneralRepositoryStub.class clientSide/stubs/RefereeSiteStub.class clientSide/stubs/PlaygroundStub.class \
    clientSide/stubs/ContestantsBenchStub.class  dirContestant/clientSide/stubs
cp commInfra/Message.class commInfra/MessageType.class commInfra/MessageException.class commInfra/ClientCom.class dirContestant/commInfra


echo "Compressing execution environments."
echo "  General Repository of Information"
rm -f  dirGeneralRepos.zip
zip -rq dirGeneralRepos.zip dirGeneralRepos

echo "  Contestant Bench"
rm -f  dirContestantBench.zip
zip -rq dirContestantBench.zip dirContestantBench

echo "  Referee Site"
rm -f  dirRefereeSite.zip
zip -rq dirRefereeSite.zip dirRefereeSite

echo "  Playground"
rm -f  dirPlayground.zip
zip -rq dirPlayground.zip dirPlayground



echo "  Referee"
rm -f  dirReferee.zip
zip -rq dirReferee.zip dirReferee

echo "  Coach"
rm -f  dirCoach.zip
zip -rq dirCoach.zip dirCoach

echo "  Contestants"
rm -f  dirContestant.zip
zip -rq dirContestant.zip dirContestant



echo "Deploying and decompressing execution environments."
mkdir -p /home/ubuntu/Desktop/ASS2_T2_98376/GameOfRope
rm -rf /home/ubuntu/Desktop/ASS2_T2_98376/GameOfRope/*
cp dirGeneralRepos.zip /home/ubuntu/Desktop/ASS2_T2_98376/GameOfRope
cp dirContestantBench.zip /home/ubuntu/Desktop/ASS2_T2_98376/GameOfRope
cp dirRefereeSite.zip /home/ubuntu/Desktop/ASS2_T2_98376/GameOfRope
cp dirPlayground.zip /home/ubuntu/Desktop/ASS2_T2_98376/GameOfRope
cp dirReferee.zip /home/ubuntu/Desktop/ASS2_T2_98376/GameOfRope
cp dirCoach.zip /home/ubuntu/Desktop/ASS2_T2_98376/GameOfRope
cp dirContestant.zip /home/ubuntu/Desktop/ASS2_T2_98376/GameOfRope
cd /home/ubuntu/Desktop/ASS2_T2_98376/GameOfRope


unzip -q dirGeneralRepos.zip
unzip -q dirContestantBench.zip
unzip -q dirRefereeSite.zip
unzip -q dirPlayground.zip
unzip -q dirReferee.zip
unzip -q dirCoach.zip
unzip -q dirContestant.zip
