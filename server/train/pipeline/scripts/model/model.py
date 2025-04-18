from transformers import BertTokenizerFast, BertForTokenClassification, BertConfig
from transformers import Trainer, TrainingArguments, EarlyStoppingCallback
import pandas as pd
import numpy as np
from transformers import TrainerCallback
from sklearn.model_selection import train_test_split
from datasets import Dataset
from pathlib import Path

label_list = ['O', 'B-data', 'I-data', 'B-operation', 'I-operation', 'B-plant', 'I-plant', 'B-perDay', 'I-perDay', 'B-perOperation', 
              'I-perOperation', 'B-department', 'I-department', 'B-grosPerDay', 'I-grosPerDay', 'B-grosPerOperation', 'I-grosPerOperation']
label_to_id = {label: i for i, label in enumerate(label_list)}
id_to_label = {i: label for i, label in enumerate(label_list)}

def get_id_to_label():
    return id_to_label

def download_pretrained_model():
    print("Загрузка предобученной модели по сети")
    tokenizer = BertTokenizerFast.from_pretrained("ai-forever/ruBert-large")
    config = BertConfig.from_pretrained("ai-forever/ruBert-large")
    config.hidden_dropout_prob = 0.3
    config.num_labels=len(label_list)
    model = BertForTokenClassification.from_pretrained(
        "ai-forever/ruBert-large",
        config=config,
    )
    return tokenizer, model

class DualEvalCallback(TrainerCallback):
    def __init__(self, trainer):
        self.trainer = trainer

    def on_epoch_end(self, args, state, control, **kwargs):
        train_metrics = self.trainer.evaluate(
            eval_dataset=self.trainer.train_dataset,
            metric_key_prefix="train"
        )
        
        eval_metrics = kwargs.get("metrics", {})
        
        print(f"\nEpoch {state.epoch}:")
        print(f"  Train Loss: {train_metrics.get('train_loss', 'N/A')}")
        print(f"  Valid Loss: {eval_metrics.get('eval_loss', 'N/A')}")

def train_model(tokenized_data, new_model = True):

    print("Обучение модели")
    if new_model:
        tokenizer, model = download_pretrained_model()
    else:
        tokenizer, model = load_pretrained_model()

    training_args = TrainingArguments(
        lr_scheduler_type="linear",
        output_dir="./results",
        eval_strategy="epoch",
        logging_strategy="epoch",
        save_strategy="epoch",
        learning_rate=2e-5,
        # warmup_steps=500,
        per_device_train_batch_size=16,
        per_device_eval_batch_size=16,
        num_train_epochs=30,
        weight_decay=0.01,
        save_total_limit=5,
        load_best_model_at_end=True,
        metric_for_best_model="eval_loss"
    )

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

    if new_model:
        trainer.train()
    else:
        try:
            trainer.train(resume_from_checkpoint=get_latest_checkpoint("./results"))
        except FileNotFoundError:
            trainer.train()
    return tokenizer, model

def get_latest_checkpoint(checkpoint_dir):
    checkpoint_dir = Path(checkpoint_dir)
    checkpoints = [d for d in checkpoint_dir.iterdir() if d.is_dir() and d.name.startswith("checkpoint")]
    
    if not checkpoints:
        raise FileNotFoundError("No checkpoints found in the directory.")
    
    latest_checkpoint = max(
        checkpoints,
        key=lambda x: int(x.name.split("-")[-1])
    )
    return str(latest_checkpoint)

def save_trained_model(model, tokenizer):
    model.save_pretrained("./saved/model/")
    tokenizer.save_pretrained("./saved/tokenizer")

def load_pretrained_model():
    tokenizer = BertTokenizerFast.from_pretrained("./saved/tokenizer")
    model = BertForTokenClassification.from_pretrained(
        "./saved/model/",
        num_labels=len(label_list)
    )
    return tokenizer, model

def align_labels_with_tokens(labels, word_ids):
    aligned_labels = []
    previous_word_idx = None
    for word_idx in word_ids:
        if word_idx is None: 
            aligned_labels.append(-100)
        elif word_idx != previous_word_idx: 
            if 0 <= word_idx < len(labels):
                aligned_labels.append(label_to_id[labels[word_idx]])
            else:
                aligned_labels.append(-100)
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

def tokenize_and_align_labels(data, tokenizer):
    print("Начало работы моделя токенизации и выравнивания меток")

    tokenized_inputs = tokenizer(
        data["postproc_data"].tolist(),
        truncation=True,
        padding='max_length',
        max_length=300,
        is_split_into_words=False, 
        return_tensors="pt",
    )
    
    all_labels = []
    for i, label in enumerate(data["label"]):
        try:
            word_ids = tokenized_inputs.word_ids(batch_index=i)
            labels = [item[1] for item in eval(label)]
            
            aligned_labels = align_labels_with_tokens(labels, word_ids)
            all_labels.append(aligned_labels)
        except Exception as e:
            print(f"Ошибка при обработке строки {i}: {e}")
            all_labels.append([-100] * len(word_ids))
    
    tokenized_inputs["labels"] = all_labels

    check_labels(all_labels, len(label_list))

    dataset = Dataset.from_dict(tokenized_inputs)
    return dataset

def prepare_datasets(data_dir, new_model=True):
    print("Начало работы модуля разделения данных на выборки")

    data = pd.read_excel(data_dir / 'datawithbiolabels.xlsx')
    
    train_data, val_data = train_test_split(data, test_size=0.2, random_state=42)
    print(train_data.head())
    print(val_data.head())
    
    if new_model:
        tokenizer, _ = download_pretrained_model()
    else:
        tokenizer, _ = load_pretrained_model()
    
    train_tokenized = tokenize_and_align_labels(train_data, tokenizer)
    val_tokenized = tokenize_and_align_labels(val_data, tokenizer)
    
    tokenized_datasets = {
        "train": train_tokenized,
        "validation": val_tokenized
    }
    return tokenized_datasets

def prepare_train_save_model(data_dir, new_model = True):
    print("Начало работы основного метода создания модели.")
    
    tokenized_datasets = prepare_datasets(data_dir, new_model)

    tokenizer, model = train_model(tokenized_datasets, new_model)
        
    save_trained_model(model, tokenizer)
