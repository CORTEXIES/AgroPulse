import pandas as pd
from .model_usage import predict_labels
from seqeval.metrics import precision_score, recall_score, f1_score, classification_report

def print_precision_scores(data_dir):
    data_with_labels = pd.read_excel(data_dir / "datawithbiolabels.xlsx")

    predicted = predict_labels(data_with_labels['postproc_data'].to_list())
    true = data_with_labels['label'].to_list()
    for i in range(len(true)):
        true[i] = [item[1] for item in eval(true[i])]

    for i in range(len(predicted)):
        predicted[i] = predicted[i][:len(true[i])]
    
    for i in range(len(predicted)):
        true[i] = true[i][:len(predicted[i])]

    precision = precision_score(true, predicted)
    recall = recall_score(true, predicted)
    f1 = f1_score(true, predicted)

    print(f"Точность работы модели: {precision}")
    print(f"Полнота: {recall}")
    print(f"F1-score: {f1}")

    print(predicted)