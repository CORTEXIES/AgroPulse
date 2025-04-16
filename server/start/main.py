from scripts.ProcessingAlgorithms.algorithms import LowerAlgorithm, AbbrExpandAlgorithm, NumsProcAlgorithm, SpellCheckAlgorithm
from scripts.ProcessingAlgorithms.texthandler import TextHandler
from scripts.helpers.helpers import generate_abbreviations
from scripts.model.model_usage import predict_labels, convert_multiple_labels_to_output
from pathlib import Path

def process_texts(texts, abbreviations = None):
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
