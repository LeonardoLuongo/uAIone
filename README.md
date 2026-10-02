# 🏎️ uAIone - Autonomous Driving AI in TORCS

![Python](https://img.shields.io/badge/Python-3776AB?style=for-the-badge&logo=python&logoColor=white)
![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Scikit-Learn](https://img.shields.io/badge/scikit--learn-%23F7931E.svg?style=for-the-badge&logo=scikit-learn&logoColor=white)
![AI & Machine Learning](https://img.shields.io/badge/AI_&_Machine_Learning-000000?style=for-the-badge)

## 📖 Project Overview
**uAIone** is an Artificial Intelligence project aimed at developing an **Autonomous Driving Model** for the **TORCS** (The Open Racing Car Simulator) environment. 
The system leverages **Behavioral Cloning** (Imitation Learning), where the AI learns to drive by mimicking the behavior of an expert human driver.

This project was developed for the **Artificial Intelligence: Methods and Applications** course (Prof. Mario Vento) at the **University of Salerno (UNISA)**, Academic Year 2023-2024.

## ✨ Architecture & Tech Stack
The system is built on a **Service-Oriented Architecture (SOA)**, allowing parallel development and high efficiency. Components communicate in real-time via **UDP Sockets**.

*   **TORCS Server:** Simulates the physics and environment.
*   **Java Client:** Collects sensor data (speed, track edges, opponents) and sends it to the AI model or Dataset Writer.
*   **Java DatasetWriter:** Stores the expert's driving sessions into a CSV file for training.
*   **Python AI Model:** Normalizes data and predicts the next actions (accelerate, brake, steer) using Machine Learning algorithms (`scikit-learn`).

### 🧠 Machine Learning Approach
*   **Algorithm:** K-Nearest Neighbors (KNN) with $K=5$, chosen for the best trade-off between performance and stability over a ~50,000 record dataset.
*   **Feature Engineering:** Utilized a Correlation Matrix to drop redundant features and optimize inference time.
*   **Data Scaling:** Applied Scalers to normalize inputs, ensuring stable and efficient predictions.
*   **Multi-Model Strategy:** Instead of a single model, we trained **three distinct models** (one for steering, one for accelerating, one for braking) to maximize the accuracy of each specific action.

## 🚀 How to Run

*(Note: This guide assumes that all Python dependencies, such as `scikit-learn`, `pandas`, and `numpy`, are already installed).*

The project is divided into three main modules:

### 1. DatasetCreation (Manual Driving & Data Collection)
To create a new dataset by driving manually:
1. Navigate to the `DatasetCreation/Java/` folder.
2. Compile and run the project (e.g., using `run.bat` or your IDE).
3. Start the `DatasetWriter.java` process to begin logging data.
4. Launch a Quick Race in **TORCS**.
5. Control the car using the `OurContinuousCharReaderUI` window with the **W, A, S, D** keys.

### 2. DatasetRecoverCreation (Critical Situations Data)
To record data specifically for recovery situations (e.g., getting back on track):
1. Navigate to the `DatasetRecoverCreation/Java/` folder.
2. Follow the same compilation and startup process as above.
3. Drive the car using **W, A, S, D**.
4. **Hold down the 'I' key** only when you want to write the recovery records to the dataset.

### 3. ModelImplementation (Autonomous Driving)
To let the AI drive the car autonomously:
1. **Start the AI:** Navigate to the `ModelImplementation/Python/KNN/EntryPoint/` folder and run `main.py`. The Python server will wait for sensor data.
2. **Start the Client:** Navigate to the `ModelImplementation/Java/` folder, compile, and run the Java client.
3. Launch a Quick Race in **TORCS**. The AI will now take over and drive autonomously!

## 🔮 Future Developments
*   **Multi-Layer Perceptron (MLP):** Replacing KNN with an MLP Neural Network for faster inference times.
*   **Recovery Model AI:** Implementing a secondary "emergency" AI model that activates only when the car goes off-track, suspending the primary model until the car is stable.
*   **Reinforcement Learning:** Automating the data collection phase by letting the agent learn from its own mistakes rather than relying on human demonstrations.

---
*Developed by Group 11 (Francesco Lemmo, Leonardo Luongo, Francesco Monda) - Università degli Studi di Salerno.*
