from scripts.ProcessingAlgorithms.algorithms import LowerAlgorithm, AbbrExpandAlgorithm, NumsProcAlgorithm, SpellCheckAlgorithm
from scripts.ProcessingAlgorithms.texthandler import TextHandler
from scripts.helpers.words_getter import generate_unique_words
from scripts.helpers.xlsx_saver import save_data_table
from pathlib import Path
import pandas as pd

def preproc_texts(data, preproc_dir):
    text_handler = TextHandler()
    
    text_handler.add_algorithm(LowerAlgorithm())
    text_handler.add_algorithm(AbbrExpandAlgorithm(preproc_dir))
    text_handler.add_algorithm(NumsProcAlgorithm())
    # text_handler.add_algorithm(SpellCheckAlgorithm(preproc_dir))

    for i in range(len(data)):
        data[i] = text_handler.process_text(data[i])
    
    return data

def main():
    # Получение информации
    root = Path('.')
    data_dir = root / "data"
    preproc_dir = root / "text_info"
    data = pd.read_excel(data_dir / "data.xlsx").transpose().to_numpy()[0]

    initial_data = data.copy()

    data = preproc_texts(data, preproc_dir)
    save_data_table(data, initial_data, data_dir)


main()
