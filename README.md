# Схема решения

![image](https://github.com/CORTEXIES/AgroPulse/assets/structure.png)









# Запуск чата с модулем распознаванием таблиц

Установака модуля 

cd ./AgroPulse/server/start/scripts/model_photo/llama.cpp/ 

mkdir build

cd  build

cmake ..

cmake --build . --config Release

cd ./AgroPulse/server/start/scripts/model_photo/model_llm

Нужно загрузить 2 модели по ссылке  и поместить в папку model_llm

https://drive.google.com/uc?export=download&id=17ANQO9QpMENTyT8AGxZKYpbvX-__d0zR
https://drive.google.com/file/d/1LE50gm6RAUW4uGc6JAcBZBsnysOCFl_R/view

Команда для запуска программы с поддержкой модуля распознавание таблиц

fastapi dev run_server.py
