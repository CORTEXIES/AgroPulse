# Install script for directory: /Users/vladimir/Desktop/agro/agro_fin/AgroPulse/server/start/scripts/model_photo/llama.cpp/examples/llava

# Set the install prefix
if(NOT DEFINED CMAKE_INSTALL_PREFIX)
  set(CMAKE_INSTALL_PREFIX "/usr/local")
endif()
string(REGEX REPLACE "/$" "" CMAKE_INSTALL_PREFIX "${CMAKE_INSTALL_PREFIX}")

# Set the install configuration name.
if(NOT DEFINED CMAKE_INSTALL_CONFIG_NAME)
  if(BUILD_TYPE)
    string(REGEX REPLACE "^[^A-Za-z0-9_]+" ""
           CMAKE_INSTALL_CONFIG_NAME "${BUILD_TYPE}")
  else()
    set(CMAKE_INSTALL_CONFIG_NAME "Release")
  endif()
  message(STATUS "Install configuration: \"${CMAKE_INSTALL_CONFIG_NAME}\"")
endif()

# Set the component getting installed.
if(NOT CMAKE_INSTALL_COMPONENT)
  if(COMPONENT)
    message(STATUS "Install component: \"${COMPONENT}\"")
    set(CMAKE_INSTALL_COMPONENT "${COMPONENT}")
  else()
    set(CMAKE_INSTALL_COMPONENT)
  endif()
endif()

# Is this installation the result of a crosscompile?
if(NOT DEFINED CMAKE_CROSSCOMPILING)
  set(CMAKE_CROSSCOMPILING "FALSE")
endif()

# Set path to fallback-tool for dependency-resolution.
if(NOT DEFINED CMAKE_OBJDUMP)
  set(CMAKE_OBJDUMP "/Library/Developer/CommandLineTools/usr/bin/objdump")
endif()

if(CMAKE_INSTALL_COMPONENT STREQUAL "Unspecified" OR NOT CMAKE_INSTALL_COMPONENT)
  file(INSTALL DESTINATION "${CMAKE_INSTALL_PREFIX}/lib" TYPE SHARED_LIBRARY FILES "/Users/vladimir/Desktop/agro/agro_fin/AgroPulse/server/start/scripts/model_photo/llama.cpp/build/bin/libllava_shared.dylib")
  if(EXISTS "$ENV{DESTDIR}${CMAKE_INSTALL_PREFIX}/lib/libllava_shared.dylib" AND
     NOT IS_SYMLINK "$ENV{DESTDIR}${CMAKE_INSTALL_PREFIX}/lib/libllava_shared.dylib")
    execute_process(COMMAND /usr/bin/install_name_tool
      -delete_rpath "/Users/vladimir/Desktop/agro/agro_fin/AgroPulse/server/start/scripts/model_photo/llama.cpp/build/bin"
      "$ENV{DESTDIR}${CMAKE_INSTALL_PREFIX}/lib/libllava_shared.dylib")
    if(CMAKE_INSTALL_DO_STRIP)
      execute_process(COMMAND "/Library/Developer/CommandLineTools/usr/bin/strip" -x "$ENV{DESTDIR}${CMAKE_INSTALL_PREFIX}/lib/libllava_shared.dylib")
    endif()
  endif()
endif()

if(CMAKE_INSTALL_COMPONENT STREQUAL "Unspecified" OR NOT CMAKE_INSTALL_COMPONENT)
endif()

if(CMAKE_INSTALL_COMPONENT STREQUAL "Unspecified" OR NOT CMAKE_INSTALL_COMPONENT)
  file(INSTALL DESTINATION "${CMAKE_INSTALL_PREFIX}/lib" TYPE SHARED_LIBRARY FILES "/Users/vladimir/Desktop/agro/agro_fin/AgroPulse/server/start/scripts/model_photo/llama.cpp/build/bin/libmtmd_shared.dylib")
  if(EXISTS "$ENV{DESTDIR}${CMAKE_INSTALL_PREFIX}/lib/libmtmd_shared.dylib" AND
     NOT IS_SYMLINK "$ENV{DESTDIR}${CMAKE_INSTALL_PREFIX}/lib/libmtmd_shared.dylib")
    execute_process(COMMAND /usr/bin/install_name_tool
      -delete_rpath "/Users/vladimir/Desktop/agro/agro_fin/AgroPulse/server/start/scripts/model_photo/llama.cpp/build/bin"
      "$ENV{DESTDIR}${CMAKE_INSTALL_PREFIX}/lib/libmtmd_shared.dylib")
    if(CMAKE_INSTALL_DO_STRIP)
      execute_process(COMMAND "/Library/Developer/CommandLineTools/usr/bin/strip" -x "$ENV{DESTDIR}${CMAKE_INSTALL_PREFIX}/lib/libmtmd_shared.dylib")
    endif()
  endif()
endif()

if(CMAKE_INSTALL_COMPONENT STREQUAL "Unspecified" OR NOT CMAKE_INSTALL_COMPONENT)
endif()

if(CMAKE_INSTALL_COMPONENT STREQUAL "Unspecified" OR NOT CMAKE_INSTALL_COMPONENT)
  file(INSTALL DESTINATION "${CMAKE_INSTALL_PREFIX}/bin" TYPE EXECUTABLE FILES "/Users/vladimir/Desktop/agro/agro_fin/AgroPulse/server/start/scripts/model_photo/llama.cpp/build/bin/llama-llava-cli")
  if(EXISTS "$ENV{DESTDIR}${CMAKE_INSTALL_PREFIX}/bin/llama-llava-cli" AND
     NOT IS_SYMLINK "$ENV{DESTDIR}${CMAKE_INSTALL_PREFIX}/bin/llama-llava-cli")
    execute_process(COMMAND /usr/bin/install_name_tool
      -delete_rpath "/Users/vladimir/Desktop/agro/agro_fin/AgroPulse/server/start/scripts/model_photo/llama.cpp/build/bin"
      "$ENV{DESTDIR}${CMAKE_INSTALL_PREFIX}/bin/llama-llava-cli")
    if(CMAKE_INSTALL_DO_STRIP)
      execute_process(COMMAND "/Library/Developer/CommandLineTools/usr/bin/strip" -u -r "$ENV{DESTDIR}${CMAKE_INSTALL_PREFIX}/bin/llama-llava-cli")
    endif()
  endif()
endif()

if(CMAKE_INSTALL_COMPONENT STREQUAL "Unspecified" OR NOT CMAKE_INSTALL_COMPONENT)
  file(INSTALL DESTINATION "${CMAKE_INSTALL_PREFIX}/bin" TYPE EXECUTABLE FILES "/Users/vladimir/Desktop/agro/agro_fin/AgroPulse/server/start/scripts/model_photo/llama.cpp/build/bin/llama-minicpmv-cli")
  if(EXISTS "$ENV{DESTDIR}${CMAKE_INSTALL_PREFIX}/bin/llama-minicpmv-cli" AND
     NOT IS_SYMLINK "$ENV{DESTDIR}${CMAKE_INSTALL_PREFIX}/bin/llama-minicpmv-cli")
    execute_process(COMMAND /usr/bin/install_name_tool
      -delete_rpath "/Users/vladimir/Desktop/agro/agro_fin/AgroPulse/server/start/scripts/model_photo/llama.cpp/build/bin"
      "$ENV{DESTDIR}${CMAKE_INSTALL_PREFIX}/bin/llama-minicpmv-cli")
    if(CMAKE_INSTALL_DO_STRIP)
      execute_process(COMMAND "/Library/Developer/CommandLineTools/usr/bin/strip" -u -r "$ENV{DESTDIR}${CMAKE_INSTALL_PREFIX}/bin/llama-minicpmv-cli")
    endif()
  endif()
endif()

if(CMAKE_INSTALL_COMPONENT STREQUAL "Unspecified" OR NOT CMAKE_INSTALL_COMPONENT)
  file(INSTALL DESTINATION "${CMAKE_INSTALL_PREFIX}/bin" TYPE EXECUTABLE FILES "/Users/vladimir/Desktop/agro/agro_fin/AgroPulse/server/start/scripts/model_photo/llama.cpp/build/bin/llama-qwen2vl-cli")
  if(EXISTS "$ENV{DESTDIR}${CMAKE_INSTALL_PREFIX}/bin/llama-qwen2vl-cli" AND
     NOT IS_SYMLINK "$ENV{DESTDIR}${CMAKE_INSTALL_PREFIX}/bin/llama-qwen2vl-cli")
    execute_process(COMMAND /usr/bin/install_name_tool
      -delete_rpath "/Users/vladimir/Desktop/agro/agro_fin/AgroPulse/server/start/scripts/model_photo/llama.cpp/build/bin"
      "$ENV{DESTDIR}${CMAKE_INSTALL_PREFIX}/bin/llama-qwen2vl-cli")
    if(CMAKE_INSTALL_DO_STRIP)
      execute_process(COMMAND "/Library/Developer/CommandLineTools/usr/bin/strip" -u -r "$ENV{DESTDIR}${CMAKE_INSTALL_PREFIX}/bin/llama-qwen2vl-cli")
    endif()
  endif()
endif()

if(CMAKE_INSTALL_COMPONENT STREQUAL "Unspecified" OR NOT CMAKE_INSTALL_COMPONENT)
  file(INSTALL DESTINATION "${CMAKE_INSTALL_PREFIX}/bin" TYPE EXECUTABLE FILES "/Users/vladimir/Desktop/agro/agro_fin/AgroPulse/server/start/scripts/model_photo/llama.cpp/build/bin/llama-gemma3-cli")
  if(EXISTS "$ENV{DESTDIR}${CMAKE_INSTALL_PREFIX}/bin/llama-gemma3-cli" AND
     NOT IS_SYMLINK "$ENV{DESTDIR}${CMAKE_INSTALL_PREFIX}/bin/llama-gemma3-cli")
    execute_process(COMMAND /usr/bin/install_name_tool
      -delete_rpath "/Users/vladimir/Desktop/agro/agro_fin/AgroPulse/server/start/scripts/model_photo/llama.cpp/build/bin"
      "$ENV{DESTDIR}${CMAKE_INSTALL_PREFIX}/bin/llama-gemma3-cli")
    if(CMAKE_INSTALL_DO_STRIP)
      execute_process(COMMAND "/Library/Developer/CommandLineTools/usr/bin/strip" -u -r "$ENV{DESTDIR}${CMAKE_INSTALL_PREFIX}/bin/llama-gemma3-cli")
    endif()
  endif()
endif()

if(CMAKE_INSTALL_COMPONENT STREQUAL "Unspecified" OR NOT CMAKE_INSTALL_COMPONENT)
  file(INSTALL DESTINATION "${CMAKE_INSTALL_PREFIX}/bin" TYPE EXECUTABLE FILES "/Users/vladimir/Desktop/agro/agro_fin/AgroPulse/server/start/scripts/model_photo/llama.cpp/build/bin/llama-llava-clip-quantize-cli")
  if(EXISTS "$ENV{DESTDIR}${CMAKE_INSTALL_PREFIX}/bin/llama-llava-clip-quantize-cli" AND
     NOT IS_SYMLINK "$ENV{DESTDIR}${CMAKE_INSTALL_PREFIX}/bin/llama-llava-clip-quantize-cli")
    execute_process(COMMAND /usr/bin/install_name_tool
      -delete_rpath "/Users/vladimir/Desktop/agro/agro_fin/AgroPulse/server/start/scripts/model_photo/llama.cpp/build/bin"
      "$ENV{DESTDIR}${CMAKE_INSTALL_PREFIX}/bin/llama-llava-clip-quantize-cli")
    if(CMAKE_INSTALL_DO_STRIP)
      execute_process(COMMAND "/Library/Developer/CommandLineTools/usr/bin/strip" -u -r "$ENV{DESTDIR}${CMAKE_INSTALL_PREFIX}/bin/llama-llava-clip-quantize-cli")
    endif()
  endif()
endif()

string(REPLACE ";" "\n" CMAKE_INSTALL_MANIFEST_CONTENT
       "${CMAKE_INSTALL_MANIFEST_FILES}")
if(CMAKE_INSTALL_LOCAL_ONLY)
  file(WRITE "/Users/vladimir/Desktop/agro/agro_fin/AgroPulse/server/start/scripts/model_photo/llama.cpp/build/examples/llava/install_local_manifest.txt"
     "${CMAKE_INSTALL_MANIFEST_CONTENT}")
endif()
