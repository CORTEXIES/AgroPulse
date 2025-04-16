from .model import get_id_to_label, load_pretrained_model
from transformers import BertTokenizerFast, BertForTokenClassification
import torch
from datasets import Dataset
import numpy as np

def predict_labels(texts):
    tokenizer, model = load_pretrained_model()
    id_to_label = get_id_to_label()

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

    dataset = Dataset.from_dict({
        'input_ids': tokenized_inputs['input_ids'],
        'attention_mask': tokenized_inputs['attention_mask']
    }).with_format("torch")

    all_labels = []
    for batch in dataset:
        inputs = {
            'input_ids': batch['input_ids'].unsqueeze(0).to(device),
            'attention_mask': batch['attention_mask'].unsqueeze(0).to(device)
        }

        with torch.no_grad():
            outputs = model(**inputs)

        predictions = torch.argmax(outputs.logits, dim=-1).cpu().numpy()

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

def convert_labels_to_output(text, labels):
    tokens = list(text.split())
    result = []
    out_per_it = dict()

    if len(labels) < len(tokens):
        tokens = tokens[:len(labels)]
    
    last_saved = True
    prev_label = None
    for i in range(len(tokens)):
        label = labels[i]
        if label == 'O':
            continue
        label = label[2:]
        if out_per_it.get(label, None):
            if prev_label == None or prev_label == label:
                out_per_it[label] += " " + tokens[i]
                last_saved = False
            else:
                result.append(out_per_it)
                out_per_it = dict()
                last_saved = True
        else:
            out_per_it[label] = tokens[i]
            last_saved = False
        prev_label = label

    if not last_saved:
        result.append(out_per_it)

    return result

def convert_multiple_labels_to_output(texts, labels_list):
    if len(texts) != len(labels_list):
        raise ValueError("Length of texts and list of labels are NOT equal!")
    
    result = []
    for i in range(len(texts)):
        result.append(convert_labels_to_output(texts[i], labels_list[i]))

    return result
