import numpy as np
import pandas as pd

def generate_unique_words(data_dir, preproc_dir):
    data = pd.read_excel(data_dir / 'postprocdata.xlsx').transpose().to_numpy()[2]
    words = set()
    for line in data:
        for word in line.split():
            if any(str.isdigit(ch) for ch in word) or any(ch == '.' or ch == '/' for ch in word) or len(word) < 3:
                continue
            words.add(word.lower())
    words = sorted(list(words))
    output = pd.DataFrame({'word': words})
    output.to_excel(preproc_dir / 'unique_words.xlsx')

def generate_dictionary(preproc_dir):
    words = pd.read_excel(preproc_dir / "unique_words.xlsx").transpose().to_numpy()[1]
        
    dictionary_file = preproc_dir / "dictionary.txt"
    with open(dictionary_file, mode="w", encoding="utf-8") as dict_file:
        for word in words:
            dict_file.write(word + "\n")

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

def transform_labels(data):
    bio_data = []
    for _, row in data.iterrows():
        text = row["postproc_data"]
        labels = eval(row["label"])
        tokens = text.split()
        bio_labels = ["O"] * len(tokens)
        
        for label in labels:
            start = label["start"]
            end = label["end"]
            entity = label["labels"][0]
            
            token_indices = [i for i, tok in enumerate(tokens) if text.find(tok) >= start and text.find(tok) < end]
            if token_indices:
                bio_labels[token_indices[0]] = f"B-{entity}"
                for idx in token_indices[1:]:
                    bio_labels[idx] = f"I-{entity}"
        
        bio_data.append(list(zip(tokens, bio_labels)))
    
    return bio_data
