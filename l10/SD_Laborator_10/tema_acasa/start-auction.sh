if [ "$#" -lt 3 ]; then
    echo "Not enough parameters!"
    exit -1
fi

#update versions
rm PrepareAuction_Docker/PrepareAuction.py
cp Okazii_v2/PrepareAuction.py PrepareAuction_Docker/

rm Bidder_Docker/Bidder.py
cp Okazii_v2/Bidder.py Bidder_Docker/

rm Bidding_Processor_Docker/BiddingProcessor.py
cp Okazii_v2/BiddingProcessor.py Bidding_Processor_Docker/

rm Message_Processor_Docker/MessageProcessor.py
cp Okazii_v2/MessageProcessor.py Message_Processor_Docker/

rm Auctioneer_Docker/Auctioneer.py
cp Okazii_v2/Auctioneer.py Auctioneer_Docker/

#prepare
cd PrepareAuction_Docker
docker-compose up
cd ..

# tip, valoare_start, nr_bidders
#docker-compose build --build-arg START_VALUE=$2 --build-arg AUC_TYPE=$1
START_VALUE=$2 AUC_TYPE=$1 docker-compose up --scale auctioneer=1 --scale bidder=$3 
