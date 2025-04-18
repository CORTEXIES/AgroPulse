# CMake generated Testfile for 
# Source directory: /Users/vladimir/Desktop/agro/agro_fin/AgroPulse/server/start/scripts/model_photo/llama.cpp/examples/eval-callback
# Build directory: /Users/vladimir/Desktop/agro/agro_fin/AgroPulse/server/start/scripts/model_photo/llama.cpp/build/examples/eval-callback
# 
# This file includes the relevant testing commands required for 
# testing this directory and lists subdirectories to be tested as well.
add_test(test-eval-callback "/Users/vladimir/Desktop/agro/agro_fin/AgroPulse/server/start/scripts/model_photo/llama.cpp/build/bin/llama-eval-callback" "--hf-repo" "ggml-org/models" "--hf-file" "tinyllamas/stories260K.gguf" "--model" "stories260K.gguf" "--prompt" "hello" "--seed" "42" "-ngl" "0")
set_tests_properties(test-eval-callback PROPERTIES  LABELS "eval-callback;curl" _BACKTRACE_TRIPLES "/Users/vladimir/Desktop/agro/agro_fin/AgroPulse/server/start/scripts/model_photo/llama.cpp/examples/eval-callback/CMakeLists.txt;8;add_test;/Users/vladimir/Desktop/agro/agro_fin/AgroPulse/server/start/scripts/model_photo/llama.cpp/examples/eval-callback/CMakeLists.txt;0;")
