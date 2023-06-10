FROM python:alpine

# instalare modul kafka-python
RUN pip install kafka-python

# adaugare fisier sursa pentru microserviciul PrepareAuction
WORKDIR /prepareAuction
ADD PrepareAuction.py $WORKDIR

# comanda de executie este: python <nume_fisier>.py
CMD ["python", "PrepareAuction.py"]
