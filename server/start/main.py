from scripts.ProcessingAlgorithms.algorithms import LowerAlgorithm, AbbrExpandAlgorithm, NumsProcAlgorithm
from scripts.ProcessingAlgorithms.texthandler import TextHandler
from scripts.helpers.helpers import generate_abbreviations
from scripts.model.model_usage import predict_labels, convert_multiple_labels_to_output
from pathlib import Path
import pandas as pd

def classify(texts, abbreviations = None):
    if abbreviations is None:
        abbreviations = generate_abbreviations(Path('./text_info'), generate_sorted_abbreviations=False)

    text_handler = TextHandler()

    text_handler.add_algorithm(LowerAlgorithm())
    text_handler.add_algorithm(AbbrExpandAlgorithm(abbreviations=abbreviations))
    text_handler.add_algorithm(NumsProcAlgorithm())

    for i in range(len(texts)):
        texts[i] = text_handler.process_text(texts[i])
    
    del text_handler

    labels_list = predict_labels(texts)
    outputs = convert_multiple_labels_to_output(texts, labels_list)
    return outputs



def main():
    data_dir = Path('.') / 'data'
    preproc_dir = Path('.') / 'text_info'

    texts = ["""Пахота зяби под мн тр
По Пу 26/488
Отд 12 26/221

Предп культ под оз пш
По Пу 215/1015
Отд 12 128/317
Отд 16 123/529

2-е диск сах св под пш
По Пу 22/627
Отд 11 22/217

2-е диск сои под оз пш
По Пу 45/1907
Отд 12 45/299"""]
    
    abbreviations = generate_abbreviations(Path('./text_info'), generate_sorted_abbreviations=False)

    text_handler = TextHandler()

    text_handler.add_algorithm(LowerAlgorithm())
    text_handler.add_algorithm(AbbrExpandAlgorithm(abbreviations=abbreviations))
    text_handler.add_algorithm(NumsProcAlgorithm())

    for i in range(len(texts)):
        texts[i] = text_handler.process_text(texts[i])
    
    del text_handler

    print(texts)
    labels_list = predict_labels(texts)
    print(labels_list)
    outputs = convert_multiple_labels_to_output(texts, labels_list)
    print(outputs)

if __name__ == "__main__":
    main()