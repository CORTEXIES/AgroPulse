from .Config import Config
from .scripts.helpers.helpers import generate_abbreviations
from .scripts.ProcessingAlgorithms.algorithms import LowerAlgorithm, AbbrExpandAlgorithm, NumsProcAlgorithm, SpellCheckAlgorithm
from .scripts.ProcessingAlgorithms.texthandler import TextHandler
from .scripts.helpers.xlsx_saver_reader import save_postproc_data_table
from .TrainPipeline import TrainStage
import pandas as pd

class TextPreprocStage(TrainStage):
    '''
    Перед использованием должены быть проинициализированы:
    1. Config
        1.1 data_dir: Путь к папке data
        1.2 preproc_dir: Путь к папке text_info
    2. data/data.xlsx должен содержать валидные данные (первая строка пуста)
    3. text_info/abbreviations.xlsx должен содержать правильные аббревиатуры и их расшифровки
    '''
    def __init__(self, save_excel = False, save_csv = False, save_sorted_abbreviations = False):
        self.save_excel = save_excel
        self.save_csv = save_csv
        self.save_sorted_abbrs = save_sorted_abbreviations

    def do_task(self, data):
        config = Config()
        data_dir = config.get('data_dir')
        preproc_dir = config.get('preproc_dir')
        initial_data = data.copy()
        abbreviations = generate_abbreviations(preproc_dir, self.save_sorted_abbrs)

        text_handler = TextHandler()
        
        text_handler.add_algorithm(LowerAlgorithm())
        text_handler.add_algorithm(AbbrExpandAlgorithm(preproc_dir, abbreviations))
        text_handler.add_algorithm(NumsProcAlgorithm())
        # text_handler.add_algorithm(SpellCheckAlgorithm(preproc_dir))

        for i in range(len(data)):
            data[i] = text_handler.process_text(data[i])

        data = pd.DataFrame({'initial': initial_data, 'postproc_data': data})
        if self.save_excel:
            data.to_excel(data_dir / "postprocdata.xlsx")
        
        if self.save_csv:
            data.to_csv(data_dir / "postprocdata.csv")

        return data

