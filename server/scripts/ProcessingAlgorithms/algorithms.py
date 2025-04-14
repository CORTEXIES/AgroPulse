import pandas as pd
import numpy as np
import re
import tempfile
import os
from symspellpy import SymSpell, Verbosity
from pathlib import Path

# super class
class ProcessingAlgorithm:
    def process_text(self, text: str) -> str:
        return 'Processed text:' + text

# derived classes
# Удаляет символы \xa0 и переводит в нижний регистр
class LowerAlgorithm(ProcessingAlgorithm):
    def __init__(self):
        super().__init__()

    def process_text(self, text: str) -> str:
        return text.replace('\xa0', ' ').lower()

# Расширяет аббревиатуры
# Для работы нужна таблица аббревиатур /text_info/abbriviations.xlsx
class AbbrExpandAlgorithm(ProcessingAlgorithm):
    def __init__(self, preproc_dir: Path = None, abbreviations = None):
        super().__init__()
        if abbreviations is None:
            # reading abbreviations
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
            self.abbreviations = abbreviations

            # output = pd.DataFrame({'abbreviation': abbreviations[:,0], 'full_text': abbreviations[:,1]})
            # output.to_excel(preproc_dir / 'sortedabbriviations.xlsx')
        else:
            self.abbreviations = abbreviations

    def process_text(self, text: str) -> str:
        for pair in self.abbreviations:
            if text.startswith(pair[0] + ' '):
                text.replace(pair[0] + ' ', pair[1] + ' ', 1)
            text = text.replace(' ' + pair[0] + ' ', ' ' + pair[1] + ' ')
            text = text.replace(' ' + pair[0] + '\n', ' ' + pair[1] + '\n')
            text = text.replace('\n' + pair[0] + ' ', '\n' + pair[1] + ' ')
            text = text.replace('\n' + pair[0] + '\n', '\n' + pair[1] + '\n')
        return text

# Приводит цифры в более удобный формат
class NumsProcAlgorithm(ProcessingAlgorithm):
    def __init__(self):
        super().__init__()

    def process_text(self, text: str) -> str:
        pattern = r'(\d+/\d+)'
        matches = re.findall(pattern, text)
        for match in matches:
            per_day, per_operation = match.split('/')
            text = text.replace(match, f'площадь за день {per_day} площадь с начала операции {per_operation}')
        return text

# TODO: Исправить этот класс. Он всегда выдает неверные предположения.
class SpellCheckAlgorithm(ProcessingAlgorithm):
    def __init__(self, preproc_dir: Path):
        super().__init__()
        self.sym_spell = SymSpell()
        
        # Получение слов из edited_unique_words.xlsx
        words = pd.read_excel(preproc_dir / "edited_unique_words.xlsx").transpose().to_numpy()[1]
        
        dictionary_file = preproc_dir / "dictionary.txt"
        with open(dictionary_file, mode="w", encoding="utf-8") as dict_file:
            for word in words:
                dict_file.write(word + "\n")
        
        self.sym_spell.create_dictionary(str(dictionary_file))

    def process_text(self, text: str) -> str:
        correct_lines = []
        for line in text.split('\n'):
            correct_words = []
            for word in line.split(' '):
                if any(str.isdigit(ch) for ch in word):
                    correct_words.append(word)
                    continue
                susgestions = self.sym_spell.lookup(word, max_edit_distance=2, verbosity=Verbosity.CLOSEST)
                # print(f"All suggestions for '{word}': {[s.term for s in susgestions]}")
                if susgestions:
                    correct_words.append(susgestions[0].term)
                else:
                    correct_words.append(word)
            correct_lines.append(' '.join(correct_words))
        text = '\n'.join(correct_lines)
        return text

