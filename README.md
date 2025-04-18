# 🌱 AgroPulse — проект команды CORTEX

AgroPulse — это система, предназначенная для автоматизации обработки сообщений агрономов в мессенджере Telegram. Решение построено на микросервисной архитектуре и включает модули для классификации текстов, распознавания таблиц, работы с изображениями и взаимодействия с ИИ-моделями.

# 🧩 Архитектура решения

![image](https://github.com/CORTEXIES/AgroPulse/blob/f5cecbee73ec14ac9a7215ab59859a15d84ff16c/assets/structure.png?raw=true)


# 🧠 Подключение модуля ruBERT (Классификация текстов)

## 🔧 Подготовка окружения

- Установите Python версии >= 3.13.

- Перейдите в каталог `server/start` и создайте виртуальное окружение:

```bash
python -m venv .venv
```

- Активируйте окружение:

```bash
# Windows
.venv\Scripts\activate

# Linux/macOS
source .venv/bin/activate
```

- Установите зависимости:

```bash
pip install -r requirements.txt
```

## 📥 Загрузка модели

Скачайте модель `large_model_large_dataset_82.rar` по [ссылке](https://drive.google.com/drive/folders/17xnq0CM1wI_t5sfi8MtXsXZSVBRLZTtr?hl=ru) на Google Drive, распакуйте содержимое в директорию:

```
server/start/
```

## 🚀 Запуск микросервиса

```bash
fastapi run run_server.py
```

# 🧾 Распознавание таблиц (Gemma 3 / LLaMA)

## 🔧 Установка модуля

Перейдите в директорию LLaMA:

```bash
cd ./AgroPulse/server/start/scripts/model_photo/llama.cpp/
```

Соберите проект:

```bash
mkdir build
cd build
cmake ..
cmake --build . --config Release
```

Перейдите в директорию модели:

```bash
cd ../../model_llm
```

## 📥 Загрузка моделей

Скачайте две модели и поместите их в папку `model_llm`:

- 👉 [Тык](https://drive.google.com/uc?export=download&id=17ANQO9QpMENTyT8AGxZKYpbvX-__d0zR)
- 👉 [И тык](https://drive.google.com/file/d/1LE50gm6RAUW4uGc6JAcBZBsnysOCFl_R/view)

## 🚀 Запуск сервиса с поддержкой распознавания таблиц

```bash
fastapi run run_server.py
```
