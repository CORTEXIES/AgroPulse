from scripts.ProcessingAlgorithms.algorithms import LowerAlgorithm, AbbrExpandAlgorithm, NumsProcAlgorithm, SpellCheckAlgorithm
from scripts.ProcessingAlgorithms.texthandler import TextHandler
from scripts.helpers.helpers import generate_unique_words, generate_abbreviations, transform_labels
from scripts.helpers.xlsx_saver_reader import save_postproc_data_table, read_data_with_labels, transform_xlsx_into_csv, save_data_with_transformed_labels
from scripts.model.model import prepare_train_save_model
from scripts.model.model_usage import predict_labels
from scripts.model.model_precision import print_precision_scores
from pathlib import Path
import pandas as pd

# Настройка конвейера для предобратоки текста
def preproc_texts(data, preproc_dir):
    abbreviations = generate_abbreviations(preproc_dir, generate_sorted_abbreviations=False)
    text_handler = TextHandler()
    
    text_handler.add_algorithm(LowerAlgorithm())
    text_handler.add_algorithm(AbbrExpandAlgorithm(preproc_dir, abbreviations))
    text_handler.add_algorithm(NumsProcAlgorithm())
    # text_handler.add_algorithm(SpellCheckAlgorithm(preproc_dir)) # Не работает :<

    for i in range(len(data)):
        data[i] = text_handler.process_text(data[i])
    
    return data

def proc_labled_data(data_dir):
    data_with_labels = read_data_with_labels(data_dir)
    transformed_labels = transform_labels(data_with_labels)
    data_with_labels['label'] = transformed_labels

    print(data_with_labels.head())

    save_data_with_transformed_labels(data_with_labels, data_dir)

def main():
    # 1. Получение информации
    root = Path('.')
    data_dir = root / "data"
    preproc_dir = root / "text_info"
    
    # 2. Предобработка данных
    # data = pd.read_excel(data_dir / "data.xlsx").transpose().to_numpy()[0]

    # initial_data = data.copy()

    # data = preproc_texts(data, preproc_dir)
    # save_postproc_data_table(data, initial_data, data_dir)

    # 3. Перевод предобработанных данных в csv формат
    # transform_xlsx_into_csv(data_dir, 'postprocdata')

    # 4. Чтение и преобразование данных с готовыми метками
    # proc_labled_data(data_dir)

    # 5. получение готовой модели
    prepare_train_save_model(data_dir)

    # 6. Применение готовой модели
#     texts = ["""пахота зяби под многолетние травы
# по пу площадь за день 26 площадь с начала операции 514
# отделение 12 площадь за день 26 площадь с начала операции 247

# пахота зяби под сою
# по пу площадь за день 13 площадь с начала операции 1351
# отделение 16 площадь за день 13 площадь с начала операции 731

# предпосевная культура под озимая пшеница
# по пу площадь за день 56 площадь с начала операции 1071
# отделение 16 площадь за день 56 площадь с начала операции 585

# дискование сахарная свекла
# по пу площадь за день 85 площадь с начала операции 870
# отделение 17 площадь за день 85 площадь с начала операции 168

# второе дискование сахарная свекла под пшеница
# по пу площадь за день 123 площадь с начала операции 750
# отделение 12 площадь за день 123 площадь с начала операции 450

# второе дискование сои под озимая пшеница
# по пу площадь за день 82 площадь с начала операции 1989
# отделение 11 площадь за день 82 площадь с начала операции 993"""]

#     labels = predict_labels(texts)
#     print(labels)

    # 7. Проверка точности
    print_precision_scores(data_dir)



main()
