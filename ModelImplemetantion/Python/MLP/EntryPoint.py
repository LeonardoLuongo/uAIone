# Questa classe carica un dataset, addestra un modello di rete neurale MLP con i dati,
# e poi ascolta messaggi da un client via UDP. Quando riceve dati, fa una previsione e 
# invia i risultati a un altro host. 

import sys
import os
from io import StringIO

import pandas as pd
import numpy as np

sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), '..')))
from UDP import Host, Peer
from Model import MLP

if __name__ == '__main__':

    data = pd.read_csv('../../Dataset/datasetSlow.csv')

    features = data.columns[1:80]  # excluding the first column (sample index)
    target_fields = ['accel', 'brake', 'steer']

    # Step 2: Prepare your X (features) and y (target variables)
    X = data[features]
    y = data[target_fields]

    mlp = MLP.MLP(X=X, y=y)

    mlp.split_dataset()

    print("\n\tWaiting for training...\n\n")
    mlp.fit()

    print("\n\tEnd of training...\n\n")
    thisHost = Host.Host("localhost", 9002)
    remoteHost = Host.Host("localhost", 9003)
    peer = Peer.Peer(addr=thisHost.get_address(), port=thisHost.get_port())

    end_waiting = "END WAITING"
    while True:
        if end_waiting != "END WAITING":
            msg = end_waiting
        else:
            print("\n\tWaiting for client data...\n\n")

            msg = peer.receive_string(2_000)

        string_values = msg.split(',')

        sampleToPredict = np.asarray([float(value) for value in string_values]).reshape(1, -1)

        prediction = mlp.predict(sampleToPredict)

        csv_buffer = StringIO()

        np.savetxt(csv_buffer, prediction, delimiter=",", fmt='%4.8f')

        csv_prediction = csv_buffer.getvalue().replace("\n", "").replace("\r", "").replace(" ", "")

        print(csv_prediction + "\n")

        peer.send_string(csv_prediction, remoteHost)

        csv_buffer.close()