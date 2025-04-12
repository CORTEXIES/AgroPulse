from .algorithms import ProcessingAlgorithm

class TextHandler:
    def __init__(self):
        self.algos = list()

    def add_algorithm(self, algo: ProcessingAlgorithm):
        self.algos.append(algo)
    
    def process_text(self, text: str) -> str:
        for algo in self.algos:
            text = algo.process_text(text)
        return text