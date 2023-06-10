FROM python:alpine

# instalare modul kafka-python
RUN pip install kafka-python

# adaugare fisier sursa pentru microserviciul Auctioneer
#WORKDIR /auctioneer
ADD ./Okazii_v2/Auctioneer/Auctioneer.py Auctioneer.py

# ENV START_VALUE=20000 AUC_TYPE="English"
# comanda de executie este: python <nume_fisier>.py
CMD ["python", "Auctioneer.py"]
