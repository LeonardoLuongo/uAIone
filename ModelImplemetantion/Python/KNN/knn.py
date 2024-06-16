# Questa classe carica un dataset da un file CSV, 
# prepara i dati dividendo le colonne in features e target,
# suddivide i dati in set di addestramento e test, 
# addestra un modello KNN per predire i valori target basati sulle features, 
# e infine esegue una predizione utilizzando il modello addestrato.

import pandas as pd
from sklearn.model_selection import train_test_split
from sklearn.neighbors import KNeighborsRegressor
import numpy as np

# Step 1: Load your data from CSV
data = pd.read_csv('Peer/dataset.csv')

# Assuming the first row is the header, and the first column is the index
features = data.columns[1:80]  # excluding the first column (sample index)
target_fields = ['accel', 'brake', 'steer']

# Step 2: Prepare your X (features) and y (target variables)
X = data[features]
y = data[target_fields]

# Step 3: Split data into training and testing sets (if you have enough data)
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)

# Step 4: Initialize KNN model
knn_model = KNeighborsRegressor(n_neighbors=20)  # You can adjust n_neighbors as needed

# Step 5: Train the model
knn_model.fit(X_train, y_train)

# Step 6: Make predictions
# For example, predict the first sample in X_test
sample_to_predict = X_test.iloc[0].values.reshape(1, -1)

data = np.asarray([103.19900000,-0.03958390,-1.00000000,-1.00000000,-1.00000000,-1.00000000,-1.00000000,-1.00000000,-1.00000000,-1.00000000,-1.00000000,-1.00000000,-1.00000000,-1.00000000,-1.00000000,-1.00000000,-1.00000000,-1.00000000,-1.00000000,-1.00000000,-1.00000000,-1.00000000,-1.00000000,-1.00000000,-1.00000000,-1.00000000,-1.00000000,4.00000000,200.00000000,200.00000000,200.00000000,200.00000000,200.00000000,200.00000000,200.00000000,200.00000000,200.00000000,200.00000000,200.00000000,200.00000000,200.00000000,200.00000000,200.00000000,200.00000000,200.00000000,200.00000000,200.00000000,200.00000000,200.00000000,200.00000000,200.00000000,200.00000000,200.00000000,200.00000000,200.00000000,200.00000000,200.00000000,200.00000000,200.00000000,200.00000000,200.00000000,200.00000000,200.00000000,200.00000000,1.00000000,-3.47192000,31.58800000,55.00000000,1072.92000000,1097.92000000,93.41220000,0.00000000,4571.17000000,1.65888000,86.59350000,86.89430000,92.08400000,91.84590000,0.32619100,-0.24702700])

# Reshape to a column vector
reshaped_data = data.reshape(-1, 1)

predictions = knn_model.predict(sample_to_predict)

# Display predictions
print("Predicted values:")
print(pd.DataFrame(predictions, columns=target_fields))