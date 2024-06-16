import pandas as pd
from sklearn.model_selection import train_test_split
from sklearn.neighbors import KNeighborsRegressor
import numpy as np

class KNN:
    def __init__(
            self,
            features_fields: pd.DataFrame = None,
            target_fields: pd.DataFrame = None,
            X: pd.DataFrame = None,
            y: pd.DataFrame = None,
            X_train: pd.DataFrame = None,
            y_train: pd.DataFrame = None,
            X_test: pd.DataFrame = None,
            y_test: pd.DataFrame = None
    ) -> None:
        self.features_fields = features_fields
        self.target_fields = target_fields
        self.X = X
        self.y = y
        self.X_train = X_train
        self.X_test = X_test
        self.y_train = y_train
        self.y_test = y_test

        self.knn_model = KNeighborsRegressor(n_neighbors=25)

    def setX(self, X: pd.DataFrame) -> None:
        self.X = X

    def setX_train(self, X_train: pd.DataFrame) -> None:
        self.X_train = X_train

    def setX_test(self, X_test: pd.DataFrame) -> None:
        self.X_test = X_test

    def setY(self, Y: pd.DataFrame) -> None:
        self.y = Y

    def setY_train(self, y_train: pd.DataFrame) -> None:
        self.y_train = y_train

    def setY_test(self, y_test: pd.DataFrame) -> None:
        self.y_test = y_test

    def getX(self) -> pd.DataFrame:
        return self.X

    def getX_train(self) -> pd.DataFrame:
        return self.X_train

    def getX_test(self) -> pd.DataFrame:
        return self.X_test

    def getY(self) -> pd.DataFrame:
        return self.y

    def getY_train(self) -> pd.DataFrame:
        return self.y_train

    def getY_test(self) -> pd.DataFrame:
        return self.y_test

    def split_dataset(self):
        self.X_train, self.X_test, self.y_train, self.y_test = train_test_split(self.X, self.y, test_size=0.2, random_state=42)

    def fit(self) -> None:
        self.knn_model.fit(self.X_train, self.y_train)

    def predict(self, X) -> pd.DataFrame:
        return self.knn_model.predict(X)
