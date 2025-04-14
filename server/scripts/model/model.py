from transformers import BertTokenizerFast, BertForTokenClassification, BertConfig
from transformers import Trainer, TrainingArguments, EarlyStoppingCallback
import pandas as pd
import numpy as np
from transformers import TrainerCallback
from sklearn.model_selection import train_test_split
from datasets import Dataset

# Создание словаря меток
label_list = ['O', 'B-data', 'I-data', 'B-operation', 'I-operation', 'B-plant', 'I-plant', 'B-perDay', 'I-perDay', 'B-perOperation', 'I-perOperation', 'B-department', 'I-department']
label_to_id = {label: i for i, label in enumerate(label_list)}
id_to_label = {i: label for i, label in enumerate(label_list)}

def get_id_to_label():
    return id_to_label

# Загрузка предобученной модели и токенизатора
def download_pretrained_model():
    print("Загрузка предобученной модели по сети")
    tokenizer = BertTokenizerFast.from_pretrained("DeepPavlov/rubert-base-cased")
    config = BertConfig.from_pretrained("DeepPavlov/rubert-base-cased")
    config.hidden_dropout_prob = 0.3  # Увеличенный dropout
    config.num_labels=len(label_list)
    model = BertForTokenClassification.from_pretrained(
        "DeepPavlov/rubert-base-cased",
        config=config,
    )
    return tokenizer, model

class DualEvalCallback(TrainerCallback):
    def __init__(self, trainer):
        self.trainer = trainer

    def on_epoch_end(self, args, state, control, **kwargs):
        # Оценка на тренировочных данных
        train_metrics = self.trainer.evaluate(
            eval_dataset=self.trainer.train_dataset,
            metric_key_prefix="train"
        )
        
        # Оценка на валидационных данных (уже выполнена Trainer'ом)
        eval_metrics = kwargs.get("metrics", {})
        
        print(f"\nEpoch {state.epoch}:")
        print(f"  Train Loss: {train_metrics.get('train_loss', 'N/A')}")
        print(f"  Valid Loss: {eval_metrics.get('eval_loss', 'N/A')}")

# Обучение модели
def train_model(tokenized_data):

    print("Обучение модели")
    tokenizer, model = download_pretrained_model()

    # Определение аргументов обучения
    training_args = TrainingArguments(
        lr_scheduler_type="linear",
        output_dir="./results",
        eval_strategy="epoch",
        logging_strategy="epoch",
        save_strategy="epoch",
        learning_rate=1e-5,
        # warmup_steps=500,
        per_device_train_batch_size=16,
        per_device_eval_batch_size=16,
        num_train_epochs=80,
        weight_decay=0.01,
        save_total_limit=5,
        load_best_model_at_end=True,
        metric_for_best_model="eval_loss"
    )

    # Создание объекта Trainer
    # print(f"Тип tokenized_data['train']: {type(tokenized_data['train'])}")
    # print(f"Тип tokenized_data['validation']: {type(tokenized_data['validation'])}")
    trainer = Trainer(
        model=model,
        args=training_args,
        train_dataset=tokenized_data["train"],
        eval_dataset=tokenized_data["validation"],
        tokenizer=tokenizer,
        # callbacks=[EarlyStoppingCallback(early_stopping_patience=4)]
    )

    # trainer.add_callback(DualEvalCallback(trainer))

    # Обучение модели
    trainer.train()
    return tokenizer, model

# Сохранение модели и токенизатора
def save_trained_model(model, tokenizer):
    print("Сохранение обученной модели и токенизатора")
    model.save_pretrained("./saved/model/")
    tokenizer.save_pretrained("./saved/tokenizer")

# Загрузка обученной модели и токенизатора
def load_pretrained_model():
    print("Загрузка обученной модели и токенизатора")
    tokenizer = BertTokenizerFast.from_pretrained("./saved/tokenizer")
    model = BertForTokenClassification.from_pretrained(
        "./saved/model/",
        num_labels=len(label_list)
    )
    return tokenizer, model

# Функция для выравнивания меток с токенами
def align_labels_with_tokens(labels, word_ids):
    # print("Начало работы моделя выравнивания меток с токенами")
    aligned_labels = []
    previous_word_idx = None
    for word_idx in word_ids:
        if word_idx is None: 
            aligned_labels.append(-100)
        elif word_idx != previous_word_idx: 
            # Проверка, что word_idx находится в допустимом диапазоне
            if 0 <= word_idx < len(labels):
                aligned_labels.append(label_to_id[labels[word_idx]]) # Преобразование меток в числа
            else:
                aligned_labels.append(-100)  # Игнорируем некорректные индексы
        else: 
            aligned_labels.append(-100)
        previous_word_idx = word_idx
    return aligned_labels

# Проверка меток
def check_labels(labels, num_labels):
    for label_seq in labels:
        for label in label_seq:
            if label >= num_labels or label < -100:
                raise ValueError(f"Некорректная метка: {label}. Допустимый диапазон: [-100, {num_labels - 1}]")

# Токенизация данных и выравнивание меток
def tokenize_and_align_labels(data, tokenizer):
    print("Начало работы моделя токенизации и выравнивания меток")

    # print(f"Работа метода tokenize_and_align_labels. Значение исходного data:\n{data.head()}")

    tokenized_inputs = tokenizer(
        data["postproc_data"].tolist(),
        truncation=True,
        padding='max_length',
        max_length=300,
        is_split_into_words=False, 
        return_tensors="pt",
    )

    # print(f"Работа метода tokenize_and_align_labels. Значение data после работы токенизатора:\n{data.head()}")
    
    all_labels = []
    for i, label in enumerate(data["label"]):
        # print(f"{i}: Значание data: {data.head()}")
        try:
            word_ids = tokenized_inputs.word_ids(batch_index=i)
            labels = [item[1] for item in eval(label)]
            
            # print(f"Столбец postproc_data: {data['postproc_data'][:5]}")
            # print(f"Текст: {data['postproc_data'][i]}")
            # print(f"Метки: {labels}")
            # print(f"Word IDs: {word_ids}")
            
            aligned_labels = align_labels_with_tokens(labels, word_ids)
            all_labels.append(aligned_labels)
        except Exception as e:
            print(f"Ошибка при обработке строки {i}: {e}")
            all_labels.append([-100] * len(word_ids))  # Все метки игнорируются
    
    tokenized_inputs["labels"] = all_labels

    check_labels(all_labels, len(label_list))

    dataset = Dataset.from_dict(tokenized_inputs)
    return dataset

# Разделение данных на обучающую и валидационную выборки
def prepare_datasets(data_dir):
    print("Начало работы модуля разделения данных на выборки")

    # Загрузка данных
    data = pd.read_excel(data_dir / 'datawithbiolabels.xlsx')
    
    # Разделение на train и validation
    train_data, val_data = train_test_split(data, test_size=0.2, random_state=42)
    print(train_data.head())
    print(val_data.head())
    
    # Загрузка токенизатора
    tokenizer, _ = download_pretrained_model()
    
    # Токенизация данных
    train_tokenized = tokenize_and_align_labels(train_data, tokenizer)
    val_tokenized = tokenize_and_align_labels(val_data, tokenizer)
    
    tokenized_datasets = {
        "train": train_tokenized,
        "validation": val_tokenized
    }
    return tokenized_datasets

# Основная функция для запуска процесса
def prepare_train_save_model(data_dir):
    print("Начало работы основного метода создания модели.")
    
    # Подготовка данных
    tokenized_datasets = prepare_datasets(data_dir)
    
    # Обучение модели
    tokenizer, model = train_model(tokenized_datasets)
    
    # Сохранение модели
    save_trained_model(model, tokenizer)
