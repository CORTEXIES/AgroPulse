import pandas as pd
from scripts.ProcessingAlgorithms.algorithms import LowerAlgorithm, AbbrExpandAlgorithm, NumsProcAlgorithm, SpellCheckAlgorithm
from scripts.ProcessingAlgorithms.texthandler import TextHandler
from pathlib import Path
  
# def preproc_texts(data, preproc_dir):
#     # Предобработка текста
#     abbreviations = pd.read_excel(preproc_dir / 'abbriviations.xlsx').to_numpy()
#     abbreviations = dict(abbreviations[:, 1:3].tolist())
#     for key, val in list(abbreviations.items()):
#         new_key = ''.join(key.split())
#         abbreviations[new_key] = val
#         new_key = ''.join(map(lambda s: s + '.', key.split()))
#         abbreviations[new_key] = val
#         new_key = ' '.join(map(lambda s: s + '.', key.split()))
#         abbreviations[new_key] = val
#     abbreviations = np.array(list(abbreviations.items()))

#     abbreviations = abbreviations[abbreviations[:, 0].argsort()[::-1]]
#     save_sorted_abbr_table(abbreviations, preproc_dir)

#     sym_spell = SymSpell()

#     for i in range(len(data)):
#         # lower message
#         cur = data[i].replace('\xa0', ' ').lower()

#         # expand abbreviations
#         for pair in abbreviations:
#             if cur.startswith(pair[0] + ' '):
#                 cur.replace(pair[0] + ' ', pair[1] + ' ', 1)
#             cur = cur.replace(' ' + pair[0] + ' ', ' ' + pair[1] + ' ')
#             cur = cur.replace(' ' + pair[0] + '\n', ' ' + pair[1] + '\n')
#             cur = cur.replace('\n' + pair[0] + ' ', '\n' + pair[1] + ' ')
#             cur = cur.replace('\n' + pair[0] + '\n', '\n' + pair[1] + '\n')
        
#         # nums processing
#         pattern = r'(\d+/\d+)'
#         matches = re.findall(pattern, cur)
#         for match in matches:
#             per_day, per_operation = match.split('/')
#             cur = cur.replace(match, f'площадь за день {per_day} площадь с начала операции {per_operation}')
        
#         # spell check
#         correct_lines = []
#         for line in cur.split('\n'):
#             susgestions = sym_spell.lookup_compound(line, max_edit_distance=2)
#             correct_lines.append(susgestions[0].term)
#         cur = '\n'.join(correct_lines)

#         # save data
#         data[i] = cur
    
#     return data

def preproc_texts(data, preproc_dir):
    text_handler = TextHandler()
    
    text_handler.add_algorithm(LowerAlgorithm())
    text_handler.add_algorithm(AbbrExpandAlgorithm(preproc_dir))
    text_handler.add_algorithm(NumsProcAlgorithm())
    text_handler.add_algorithm(SpellCheckAlgorithm())

    for i in range(len(data)):
        data[i] = text_handler.process_text(data[i])
    
    return data


def save_sorted_abbr_table(data, dir):
    output = pd.DataFrame({'abbreviation': data[:,0], 'full_text': data[:,1]})
    output.to_excel(dir / 'sortedabbriviations.xlsx')

def save_data_table(data, initial_data, dir):
    output = pd.DataFrame({'initial': initial_data, 'postproc_data': data})
    output.to_excel(dir / "postprocdata.xlsx")


def main():
    # Получение информации
    root = Path('.')
    data_dir = root / "data"
    preproc_dir = root / "text_preproc_info"
    data = pd.read_excel(data_dir / "data.xlsx").transpose().to_numpy()[0]
    initial_data = data.copy()

    data = preproc_texts(data, preproc_dir)
    save_data_table(data, initial_data, data_dir)


main()
