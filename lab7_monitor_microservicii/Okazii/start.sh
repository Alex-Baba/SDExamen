java -jar ./out/artifacts/MonitorMicroservice_jar/MonitorMicroservice.jar &


sleep 2
java -jar ./out/artifacts/BiddingProcessorMicroservice_jar/BiddingProcessorMicroservice.jar &

sleep 2
java -jar ./out/artifacts/MessageProcessorMicroservice_jar/MessageProcessorMicroservice.jar &

sleep 2
java -jar ./out/artifacts/AuctioneerMicroservice_jar/AuctioneerMicroservice.jar &

sleep 2
java -jar ./out/artifacts/BidderMicroservice_jar/BidderMicroservice.jar &
java -jar ./out/artifacts/BidderMicroservice_jar/BidderMicroservice.jar &
java -jar ./out/artifacts/BidderMicroservice_jar/BidderMicroservice.jar 



#/home/unwise/Desktop/SD_Laborator_07/Okazii/out/artifacts/AuctioneerMicroservice_jar/AuctioneerMicroservice.jar

