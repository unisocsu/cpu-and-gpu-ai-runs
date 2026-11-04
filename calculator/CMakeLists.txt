cmake_minimum_required(VERSION 3.10)
project(Calculator LANGUAGES CXX)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# הגדרות עבור ווינדוס (MSVC)
if(MSVC)
    add_compile_options(/W4 /permissive-)
    add_compile_options(/utf-8)
endif()

# הגדרות עבור GCC/Clang
if(CMAKE_CXX_COMPILER_ID STREQUAL "GNU" OR CMAKE_CXX_COMPILER_ID STREQUAL "Clang")
    add_compile_options(-Wall -Wextra -pedantic)
endif()

add_executable(Calculator Calculator.cpp)
