# Проект команды CORTEX

## Схема решения

Проект представляет из себя группу взаиможействующих друг с другом сервисов. Диаграмму деятельности вы можете увидеть ниже.

![image](https://github.com/CORTEXIES/AgroPulse/blob/f5cecbee73ec14ac9a7215ab59859a15d84ff16c/assets/structure.png?raw=true)

## Подключение модуля ruBERT

Для подклчения необходимо установить python версии 13.3. После требуется создать виртуальное окружение в каталоге `server/start`.

```bash
python -m venv .venv
```

Для запуска среды введите одну из следующих команд:

```bash
.venv\Scripts\acitivate # Windows
source /bin/activate # Linux
```

Установка пакетов

```bash
pip install -r requirements.txt
```

Перед работой нужно установить модель. Скачать требуемую модель можно из [гугл диска](https://drive.google.com/drive/folders/17xnq0CM1wI_t5sfi8MtXsXZSVBRLZTtr?hl=ru). Требуется скачать файл `large_model_large_dataset_82.rar`. Содержимое файла необходимо распаковать в директорию `server/start`.

Теперь осталось только запустить микросервис.

```bash
fastapi run run_server.py
```