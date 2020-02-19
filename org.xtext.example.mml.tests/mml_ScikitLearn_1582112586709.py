import pandas as pd
 from sklearn.tree import DecisionTreeClassifier
from sklearn.model_selection import cross_val_predict
mml_data = pd.read_csv('foo2.csv', sep=',')
Y = mml_data[mml_data.columns[len(mml_data.columns)-1]] 
X = mml_data.drop(mml_data.columns[len(mml_data.columns)-1])
algo = DecisionTreeClassifier()
y_pred = cross_val_predict(algo, X, Y, cv=70)
