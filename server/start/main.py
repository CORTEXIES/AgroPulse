from scripts.ProcessingAlgorithms.texthandler import TextHandler
from scripts.ProcessingAlgorithms.algorithms import LowerAlgorithm, AbbrExpandAlgorithm, NumsProcAlgorithm

from scripts.helpers.helpers import generate_abbreviations, transform_labels
from scripts.model.model_usage import predict_labels, convert_multiple_labels_to_output
from scripts.helpers.xlsx_saver_reader import read_data_with_labels, save_data_with_transformed_labels

import logging
from pathlib import Path

def preproc_texts(data, preproc_dir):
   
    abbreviations = generate_abbreviations(preproc_dir, generate_sorted_abbreviations=False)
    text_handler = TextHandler()
    
    text_handler.add_algorithm(LowerAlgorithm())
    text_handler.add_algorithm(AbbrExpandAlgorithm(preproc_dir, abbreviations))
    text_handler.add_algorithm(NumsProcAlgorithm())

    for i in range(len(data)):
        data[i] = text_handler.process_text(data[i])
    
    return data


def proc_labled_data(data_dir):
    data_with_labels = read_data_with_labels(data_dir)
    transformed_labels = transform_labels(data_with_labels)
    data_with_labels['label'] = transformed_labels

    save_data_with_transformed_labels(data_with_labels, data_dir)


def classify(reports: list[str], abbreviations = None):
    
    if abbreviations is None:
        abbreviations = generate_abbreviations(
                Path('./start/text_info'),
                generate_sorted_abbreviations=False
        )

    handler = TextHandler()

    handler.add_algorithm(LowerAlgorithm())
    handler.add_algorithm(AbbrExpandAlgorithm(abbreviations=abbreviations))
    handler.add_algorithm(NumsProcAlgorithm())

    for i in range(len(reports)):
        reports[i] = handler.process_text(reports[i])
    
    del handler

    labels_list = predict_labels(reports)
    classified_messages = convert_multiple_labels_to_output(reports, labels_list)
    logging.info(classified_messages)

    return classified_messages


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

    # 5. Получение готовой модели
    # prepare_train_save_model(data_dir)

    # 6. Применение готовой модели к предобработанным данным
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

#     labels_list = predict_labels(texts)
#     outputs = convert_multiple_labels_to_output(texts, labels_list)
#     print(outputs)

    # 7. Проверка точности
    # print_precision_scores(data_dir)

    # 8. Применение готовой модели с совместной обработкой данных
#     texts = ["""Пахота зяби под мн тр
# По Пу 26/488
# Отд 12 26/221

# Предп культ под оз пш
# По Пу 215/1015
# Отд 12 128/317
# Отд 16 123/529

# 2-е диск сах св под пш
# По Пу 22/627
# Отд 11 22/217

# 2-е диск сои под оз пш
# По Пу 45/1907
# Отд 12 45/299"""]
    
#     outputs = process_texts(texts)
#     print(outputs)

# if __name__ == "__main__":
#     main()
