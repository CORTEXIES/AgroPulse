from pathlib import Path

from pipeline.Config import Config
from pipeline.scripts.helpers.helpers import generate_abbreviations, transform_labels
from pipeline.scripts.helpers.xlsx_saver_reader import (
    read_data_with_labels,
    save_data_with_transformed_labels,
)
from pipeline.scripts.model.model import prepare_train_save_model
from pipeline.scripts.model.model_precision import print_precision_scores
from pipeline.scripts.model.model_usage import (
    convert_multiple_labels_to_output,
    predict_labels,
)
from pipeline.scripts.ProcessingAlgorithms.algorithms import (
    AbbrExpandAlgorithm,
    LowerAlgorithm,
    NumsProcAlgorithm,
)
from pipeline.scripts.ProcessingAlgorithms.texthandler import TextHandler


def proc_labled_data(data_dir):
    data_with_labels = read_data_with_labels(data_dir)
    transformed_labels = transform_labels(data_with_labels)
    data_with_labels['label'] = transformed_labels
    data_with_labels = data_with_labels.drop(['id', 'updated_at'], axis=1)

    print(data_with_labels.head())

    save_data_with_transformed_labels(data_with_labels, data_dir)

def process_texts(texts, abbreviations = None):
    if abbreviations is None:
        abbreviations = generate_abbreviations(Path('./text_info'), generate_sorted_abbreviations=False)

    text_handler = TextHandler()

    text_handler.add_algorithm(LowerAlgorithm())
    text_handler.add_algorithm(AbbrExpandAlgorithm(abbreviations=abbreviations))
    text_handler.add_algorithm(NumsProcAlgorithm())

    for i in range(len(texts)):
        texts[i] = text_handler.process_text(texts[i])
    
    del text_handler

    labels_list = predict_labels(texts)
    outputs = convert_multiple_labels_to_output(texts, labels_list)
    return outputs



def main():
    data_dir = Path('.') / 'data'
    preproc_dir = Path('.') / 'text_info'
    config = Config({
        'data_dir': Path('.') / 'data', 
        'preproc_dir': Path('.') / 'text_info', 
    })
   

    # data = pd.read_excel(data_dir / "data.xlsx").transpose().to_numpy()[0]

    # pipeline = TrainPipeline()
    # pipeline.add_stage(TextPreprocStage(save_excel = True, save_csv = False, save_sorted_abbreviations = False))

    # pipeline.start_pipeline(data)

    # Перед обучением следует подготовить обработчик данных SpellCheckAlgorithm. 
    # Используйте следующий метод для генерации text_info/postprocdata.xlsx файла с уникальными словами.
    # generate_unique_words(data_dir, preproc_dir)
    # Теперь самостоятельно отредактируйте данный файл, чтобы убрать не нужные слова, если таковые имеются.

    # После редактирования сохраните словарь.
    # generate_dictionary(preproc_dir)

    # Чтение и преобразование данных с готовыми метками
    # proc_labled_data(data_dir)

    # Обучение модели
    # prepare_train_save_model(data_dir)


    # 1. Получение информации
    # root = Path('.')
    # data_dir = root / "data"
    # preproc_dir = root / "text_info"
    
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
    prepare_train_save_model(data_dir, new_model=False)

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
    print_precision_scores(data_dir)

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

if __name__ == "__main__":
    main()
