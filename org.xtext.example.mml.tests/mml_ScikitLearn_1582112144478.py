import pandas as pd
 from sklearn.tree import DecisionTreeClassifier
mml_data = pd.read_csv('foo2.csv', sep=',')
Y = mml_data[mml_data.columns[len(mml_data.columns)-1]] X = mml_data.drop(mml_data.columns[len(mml_data.columns)-1])
algo = DecisionTreeClassifier()
