import pandas as pd

# В этом файле содержатся методы для работы с xlsx

def save_sorted_abbr_table(data, dir):
    output = pd.DataFrame({'abbreviation': data[:,0], 'full_text': data[:,1]})
    output.to_excel(dir / 'sortedabbriviations.xlsx')

def save_data_table(data, initial_data, dir):
    output = pd.DataFrame({'initial': initial_data, 'postproc_data': data})
    output.to_excel(dir / "postprocdata.xlsx")

def save_unique_words(words, dir):
    output = pd.DataFrame({'word': words})
    output.to_excel(dir / 'unique_words.xlsx')