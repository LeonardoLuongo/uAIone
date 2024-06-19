import sys
import os
from io import StringIO

import joblib
import numpy
import pandas as pd
import numpy as np
from sklearn.preprocessing import MinMaxScaler

sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), '..')))
from Peer import Host, Peer
from Model import KNN

from sklearn.metrics import mean_squared_error, mean_absolute_error, r2_score

if __name__ == '__main__':

    data = pd.read_csv('../../out/Dataset/dataset.csv')

    # List of features to exclude
    exclude_features = ['opponentSensor_', 'racePosition', 'focus', 'currentLapTime', 'damage', 'fuelLevel', 'key', 'lastLapTime']

    # Get all features except the first column (sample index)
    all_features = data.columns[1:80]

    target_fields = ['accel', 'brake', 'steer']

    # Filter features to exclude based on the pattern in exclude_features list
    features = [f for f in all_features if not any(p in f for p in exclude_features)]

    # Step 2: Prepare your X (features) and y (target variables)
    X = data[features]
    y_accel = data['accel']
    y_brake = data['brake']
    y_steer = data['steer']

    # Normalize data
    # Initialize the scaler
    scaler = MinMaxScaler()

    # Fit and transform the data
    X = scaler.fit_transform(X)

    joblib.dump(scaler, '../../out/Scaler/scaler.pkl')

    knn = {'accel': KNN.KNN(X=X, y=y_accel), 'brake': KNN.KNN(X=X, y=y_brake), 'steer': KNN.KNN(X=X, y=y_steer)}

    for singleModel in knn:
        print(singleModel, ":\n")

        knn[singleModel].split_dataset()
        knn[singleModel].setX_train(knn[singleModel].getX())
        knn[singleModel].setY_train(knn[singleModel].getY())

        print("KNN: Waiting for training...")
        knn[singleModel].fit()
        print("KNN: End of training...")

        print("KNN: TESTING...")
        y_pred = knn[singleModel].predict(knn[singleModel].getX_test())
        print("KNN: END OF TESTING...")

        y_test = knn[singleModel].getY_test()

        # Calculate Mean Squared Error
        mse = mean_squared_error(y_test, y_pred)
        print("Mean Squared Error (MSE):", mse)

        # Calculate Mean Absolute Error
        mae = mean_absolute_error(y_test, y_pred)
        print("Mean Absolute Error (MAE):", mae)

        # Calculate R2 Score
        r2 = r2_score(y_test, y_pred)
        print("R2 Score:", r2)

        print("\n\n")

'''

Mean Squared Error (MSE): 1.2202606528116339e-11
Mean Absolute Error (MAE): 4.645232910529237e-07
R2 Score: 0.999999999070737


Mean Squared Error (MSE): 0.0022496350660878416
Mean Absolute Error (MAE): 0.020416632411960886
R2 Score: 0.8290114206247848

'''
