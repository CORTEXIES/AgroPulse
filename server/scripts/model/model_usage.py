from .model import get_id_to_label, load_pretrained_model
from transformers import BertTokenizerFast, BertForTokenClassification
import torch
from datasets import Dataset
import numpy as np

def predict_labels(texts):
    tokenizer, model = load_pretrained_model()
    id_to_label = get_id_to_label()

    # Токенизация входных текстов
    tokenized_inputs = tokenizer(
        texts,
        truncation=True,
        padding="max_length",
        is_split_into_words=False,
        max_length=300,
        return_tensors="pt"
    )

    device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
    model.to(device)

    # Создаем Dataset и устанавливаем формат torch
    dataset = Dataset.from_dict({
        'input_ids': tokenized_inputs['input_ids'],
        'attention_mask': tokenized_inputs['attention_mask']
    }).with_format("torch")

    all_labels = []
    for batch in dataset:
        # Подготавливаем входные данные
        inputs = {
            'input_ids': batch['input_ids'].unsqueeze(0).to(device),
            'attention_mask': batch['attention_mask'].unsqueeze(0).to(device)
        }

        # Получение предсказаний
        with torch.no_grad():
            outputs = model(**inputs)

        # Извлечение предсказанных логитов
        predictions = torch.argmax(outputs.logits, dim=-1).cpu().numpy()

        # Декодирование меток
        word_ids = tokenized_inputs.word_ids(batch_index=len(all_labels))
        labels = []
        previous_word_idx = None
        for word_idx, pred_id in zip(word_ids, predictions[0]):
            if word_idx is None:
                continue
            if word_idx != previous_word_idx:
                labels.append(id_to_label[pred_id])
            previous_word_idx = word_idx
        all_labels.append(labels)

    return all_labels
