from transformers import BertTokenizerFast, BertForTokenClassification, BertConfig
from transformers import Trainer, TrainingArguments, EarlyStoppingCallback
import pandas as pd
import numpy as np
from transformers import TrainerCallback
from sklearn.model_selection import train_test_split
from datasets import Dataset

label_list = ['O', 'B-data', 'I-data', 'B-operation', 'I-operation', 'B-plant', 'I-plant', 'B-perDay', 'I-perDay', 'B-perOperation', 'I-perOperation', 'B-department', 'I-department']
label_to_id = {label: i for i, label in enumerate(label_list)}
id_to_label = {i: label for i, label in enumerate(label_list)}

def get_id_to_label():
    return id_to_label

def load_pretrained_model():
    tokenizer = BertTokenizerFast.from_pretrained("./saved/tokenizer")
    model = BertForTokenClassification.from_pretrained(
        "./saved/model/",
        num_labels=len(label_list)
    )
    return tokenizer, model
