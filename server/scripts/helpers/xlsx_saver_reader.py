import pandas as pd

# В этом файле содержатся методы для работы с xlsx

# Сохраняет таблицу с данными о сортированных аббривиатурах
def save_sorted_abbr_table(data, dir):
    output = pd.DataFrame({'abbreviation': data[:,0], 'full_text': data[:,1]})
    output.to_excel(dir / 'sortedabbriviations.xlsx')

# Сохраняет предобработанные данные
def save_postproc_data_table(data, initial_data, dir):
    output = pd.DataFrame({'initial': initial_data, 'postproc_data': data})
    output.to_excel(dir / "postprocdata.xlsx")

# сохраняет словарь уникальных слов
def save_unique_words(words, dir):
    output = pd.DataFrame({'word': words})
    output.to_excel(dir / 'unique_words.xlsx')

# метод используется для преобразования файла postprocdata.xlsx в csv формат, пригодный для label studio
def transform_xlsx_into_csv(data_dir, name):
    data = pd.read_excel(data_dir / (name + '.xlsx'))
    # print(data.head())
    data = data.drop(data.columns[[0, 1]], axis=1)
    data.to_csv(data_dir / (name + '.csv'), index=False)

# Данный метод нужен для чтения файла с готовыми метками
# Метки ставятся отдельно в label-studio
def read_data_with_labels(data_dir):
    data = pd.read_csv(data_dir / 'datawithlabels.csv')
    data = data.drop(data.columns[[0, 1, 2, 3, 5, 7]], axis=1)
    print(data.head())
    return data

def save_data_with_transformed_labels(data_df, data_dir):
    data_df.to_excel(data_dir / "datawithbiolabels.xlsx")

