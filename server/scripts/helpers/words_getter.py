from .xlsx_saver import save_unique_words
import numpy as np
import pandas as pd

# В этом файле содержатся методы, помогающие в ходе работы


# Уже должен быть сформирован файл postprocdata.xlsx
def generate_unique_words(data_dir, preproc_dir):
    data = pd.read_excel(data_dir / 'postprocdata.xlsx').transpose().to_numpy()[2]
    words = set()
    for line in data:
        for word in line.split():
            if any(str.isdigit(ch) for ch in word):
                continue
            words.add(word.lower())
    words = sorted(list(words))
    save_unique_words(words, preproc_dir)
