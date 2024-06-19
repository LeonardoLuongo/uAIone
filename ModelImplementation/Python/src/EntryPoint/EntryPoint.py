import sys
import os
from io import StringIO

import joblib
import pandas as pd
import numpy as np

sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), '..')))
from Peer import Host, Peer
from Model import KNN

def createMsg(prediction):
    csv_buffer = StringIO()

    np.savetxt(csv_buffer,[prediction], delimiter=",", fmt='%4.8f')

    csv_prediction = csv_buffer.getvalue().replace("\n", "").replace("\r", "").replace(" ", "")

    msg = csv_prediction

    csv_buffer.close()

    return msg



if __name__ == '__main__':

    data = pd.read_csv('C:/Users/maumo/Desktop/AI/lello/uAIone/uAIone/ModelImplementation/resources/Dataset/dataset.csv')

    # List of features to exclude
    exclude_features = ['opponentSensor_', 'racePosition', 'focus', 'currentLapTime', 'damage', 'fuelLevel', 'key', 'lastLapTime']

    # Get all features except the first column (sample index)
    all_features = data.columns[1:80]

    # Filter features to exclude based on the pattern in exclude_features list
    features = [f for f in all_features if not any(p in f for p in exclude_features)]

    target_fields = ['accel', 'brake', 'steer']

    # Step 2: Prepare your X (features) and y (target variables)
    X = data[features]
    y_accel = data['accel']
    y_brake = data['brake']
    y_steer = data['steer']

    scaler = joblib.load('C:/Users/maumo/Desktop/AI/lello/uAIone/uAIone/ModelImplementation/resources/Scaler/scaler.pkl')

    X = scaler.transform(X)

    knn = {'accel': KNN.KNN(X=X, y=y_accel), 'brake': KNN.KNN(X=X, y=y_brake), 'steer': KNN.KNN(X=X, y=y_steer)}

    for singleModel in knn:
        print(singleModel, ":\n")

        knn[singleModel].split_dataset()
        knn[singleModel].setX_train(knn[singleModel].getX())
        knn[singleModel].setY_train(knn[singleModel].getY())

        print("KNN: Waiting for training...")
        knn[singleModel].fit()
        print("KNN: End of training...")

    thisHost = Host.Host("localhost", 9002)
    remoteHost = Host.Host("localhost", 9003)
    peer = Peer.Peer(addr=thisHost.get_address(), port=thisHost.get_port())

    end_waiting = "END WAITING"
    while True:
        if end_waiting != "END WAITING":
            msg = end_waiting
        else:
            msg = peer.receive_string(2_000)

        _data = msg.split(",")

        _data = np.asarray([float(value) for value in _data]).reshape(1, -1)

        pd_msg = pd.DataFrame(_data)

        pd_msg.columns = data.columns[1:80]

        sampleToPredict = pd_msg[[f for f in all_features if not any(p in f for p in exclude_features)]]

        # Convert sampleToPredict to DataFrame with the correct feature names
        sampleToPredict_df = pd.DataFrame(sampleToPredict)

        sampleToPredict_df = scaler.transform(sampleToPredict_df)

        predictions = {}

        for singleModel in knn:
            predictions[singleModel] = knn[singleModel].predict(sampleToPredict_df)

        prediction = [i[0] for i in predictions.values()]

        msg = createMsg(prediction)

        peer.send_string(msg, remoteHost)
