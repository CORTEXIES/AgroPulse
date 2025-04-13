from .model import get_id_to_label, load_pretrained_model
from transformers import BertTokenizerFast, BertForTokenClassification
import torch
from datasets import Dataset


def predict_labels(texts):

    tokenizer, model = load_pretrained_model()
    id_to_label = get_id_to_label()

    # Токенизация входных текстов
    tokenized_inputs = tokenizer(
        texts,
        truncation=True,
        padding=True,
        is_split_into_words=False,
        return_tensors="pt"
    )
    
    # Преобразование в Dataset
    dataset = Dataset.from_dict(tokenized_inputs)
    
    # Перемещение данных на GPU, если доступно
    device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
    model.to(device)
    
    # Получение предсказаний от модели
    all_labels = []
    for batch in dataset:
        inputs = {key: torch.tensor(val).unsqueeze(0).to(device) for key, val in batch.items()}
        with torch.no_grad():
            outputs = model(**inputs)
        
        # Извлечение предсказанных логитов
        logits = outputs.logits
        predictions = torch.argmax(logits, dim=-1).cpu().numpy()
        
        # Декодирование предсказаний обратно в метки
        word_ids = tokenized_inputs.word_ids(batch_index=0)
        previous_word_idx = None
        labels = []
        for word_idx, pred_id in zip(word_ids, predictions[0]):
            if word_idx is None:
                continue
            if word_idx != previous_word_idx:
                labels.append(id_to_label[pred_id])
            previous_word_idx = word_idx
        all_labels.append(labels)
    
    return all_labels
