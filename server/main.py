import pandas as pd
from pathlib import Path

# Получение информации
root = Path('.')
data_dir = root / "data"
data = pd.read_excel(data_dir / "data.xlsx").transpose().to_numpy()[0]

# Предобработка текста

abbreviations = {
    "сах св": "сахарную свеклу",
    "оз пш": "озимую пшеницу",
    "мн тр": "многолетние травы",
}

for i in range(len(data)):
    cur = data[i].replace('\xa0', ' ').lower()
    for pair in abbreviations.items():
        cur = cur.replace(pair[0], pair[1])
    data[i] = cur


output = pd.DataFrame({'test': data})
output.to_excel(data_dir / "output.xlsx")
