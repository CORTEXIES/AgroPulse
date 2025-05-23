import re
from pathlib import Path

from symspellpy import SymSpell, Verbosity


class ProcessingAlgorithm:
    def process_text(self, text: str) -> str:
        return 'Processed text:' + text

class LowerAlgorithm(ProcessingAlgorithm):
    def __init__(self):
        super().__init__()

    def process_text(self, text: str) -> str:
        return text.replace('\xa0', ' ').replace(' га', ' ').replace(' га.', ' ').replace(' га/', ' ').lower()

class AbbrExpandAlgorithm(ProcessingAlgorithm):
    def __init__(self, preproc_dir: Path = None, abbreviations = None):
        super().__init__()
        if abbreviations is None:
            raise AssertionError("abbreviations not provided at AbbrExpandAlgorithm class!")
        else:
            self.abbreviations = abbreviations

    def process_text(self, text: str) -> str:
        for pair in self.abbreviations:
            if text.startswith(pair[0] + ' '):
                text = text.replace(pair[0] + ' ', pair[1] + ' ', 1)
            text = text.replace(' ' + pair[0] + ' ', ' ' + pair[1] + ' ')
            text = text.replace(' ' + pair[0] + '\n', ' ' + pair[1] + '\n')
            text = text.replace('\n' + pair[0] + ' ', '\n' + pair[1] + ' ')
            text = text.replace('\n' + pair[0] + '\n', '\n' + pair[1] + '\n')
        return text

class NumsProcAlgorithm(ProcessingAlgorithm):
    def __init__(self):
        super().__init__()

    def process_text(self, text: str) -> str:
        matches = re.findall(r'(\d+/\d+)', text)
        for match in matches:
            per_day, per_operation = match.split('/')
            text = text.replace(match, f'день {per_day} от начала {per_operation}')
        text = re.sub(r'[^a-zA-Zа-яА-ЯёЁ0-9,.\s]', ' ', text)
        text = text.replace('-', ' ')
        text = text.replace('   ', ' ').replace('  ', ' ')
        return text

class SpellCheckAlgorithm(ProcessingAlgorithm):
    def __init__(self, preproc_dir: Path):
        super().__init__()
        self.sym_spell = SymSpell()
        self.sym_spell.create_dictionary(str(preproc_dir / "dictionary.txt"))

    def process_text(self, text: str) -> str:
        correct_lines = []
        for line in text.split('\n'):
            correct_words = []
            for word in line.split(' '):
                if any(str.isdigit(ch) for ch in word):
                    correct_words.append(word)
                    continue
                susgestions = self.sym_spell.lookup(word, max_edit_distance=2, verbosity=Verbosity.CLOSEST)
                if susgestions:
                    correct_words.append(susgestions[0].term)
                else:
                    correct_words.append(word)
            correct_lines.append(' '.join(correct_words))
        text = '\n'.join(correct_lines)
        return text

