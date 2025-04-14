from .xlsx_saver_reader import save_unique_words
import numpy as np
import pandas as pd

# В этом файле содержатся методы, помогающие в ходе работы

# Нужен для формирования словаря для SymSpell, проверяющего опечатки
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

# Служит для генерации словаря аббревиатур
# Метод может также генерировать список отсортированных аббревиатур, если есть необходимость
def generate_abbreviations(preproc_dir, generate_sorted_abbreviations = False):
    abbreviations = pd.read_excel(preproc_dir / 'abbriviations.xlsx').to_numpy()
    abbreviations = dict(abbreviations[:, 1:3].tolist())
    for key, val in list(abbreviations.items()):
        new_key = ''.join(key.split())
        abbreviations[new_key] = val
        new_key = ''.join(map(lambda s: s + '.', key.split()))
        abbreviations[new_key] = val
        new_key = ' '.join(map(lambda s: s + '.', key.split()))
        abbreviations[new_key] = val
    abbreviations = np.array(list(abbreviations.items()))
    abbreviations = abbreviations[abbreviations[:, 0].argsort()[::-1]]

    if generate_sorted_abbreviations:
        output = pd.DataFrame({'abbreviation': abbreviations[:,0], 'full_text': abbreviations[:,1]})
        output.to_excel(preproc_dir / 'sortedabbriviations.xlsx')
    return abbreviations

# Метод для преобразования label из label studio в label, читаемый ruBERT
def transform_labels(data):
    bio_data = []
    for _, row in data.iterrows():
        text = row["postproc_data"]
        labels = eval(row["label"])  # Преобразование JSON в список словарей
        tokens = text.split()
        bio_labels = ["O"] * len(tokens)
        
        for label in labels:
            start = label["start"]
            end = label["end"]
            entity = label["labels"][0]
            
            # Найдем токены, соответствующие метке
            token_indices = [i for i, tok in enumerate(tokens) if text.find(tok) >= start and text.find(tok) < end]
            if token_indices:
                bio_labels[token_indices[0]] = f"B-{entity}"
                for idx in token_indices[1:]:
                    bio_labels[idx] = f"I-{entity}"
        
        bio_data.append(list(zip(tokens, bio_labels)))
    
    return bio_data
