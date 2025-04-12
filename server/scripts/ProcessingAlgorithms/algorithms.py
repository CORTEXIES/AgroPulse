import pandas as pd
import numpy as np
import re
from symspellpy import SymSpell, Verbosity
from pathlib import Path

# super class
class ProcessingAlgorithm:
    def process_text(self, text: str) -> str:
        return 'Processed text:' + text

# derived classes
class LowerAlgorithm(ProcessingAlgorithm):
    def __init__(self):
        super().__init__()

    def process_text(self, text: str) -> str:
        return text.replace('\xa0', ' ').lower()
    
class AbbrExpandAlgorithm(ProcessingAlgorithm):
    def __init__(self, preproc_dir: Path):
        super().__init__()
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

        output = pd.DataFrame({'abbreviation': abbreviations[:,0], 'full_text': abbreviations[:,1]})
        output.to_excel(preproc_dir / 'sortedabbriviations.xlsx')

    def process_text(self, text: str) -> str:
        for pair in self.abbreviations:
            if text.startswith(pair[0] + ' '):
                text.replace(pair[0] + ' ', pair[1] + ' ', 1)
            text = text.replace(' ' + pair[0] + ' ', ' ' + pair[1] + ' ')
            text = text.replace(' ' + pair[0] + '\n', ' ' + pair[1] + '\n')
            text = text.replace('\n' + pair[0] + ' ', '\n' + pair[1] + ' ')
            text = text.replace('\n' + pair[0] + '\n', '\n' + pair[1] + '\n')
        return text

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

class SpellCheckAlgorithm(ProcessingAlgorithm):
    def __init__(self):
        super().__init__()
        self.sym_spell = SymSpell()

    def process_text(self, text: str) -> str:
        correct_lines = []
        for line in text.split('\n'):
            susgestions = self.sym_spell.lookup_compound(line, max_edit_distance=2)
            correct_lines.append(susgestions[0].term)
        text = '\n'.join(correct_lines)
        return text

