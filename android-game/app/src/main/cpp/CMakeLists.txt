cmake_minimum_required(VERSION 3.22.1)
project("game-engine")

# הוספת C++ 11
set(CMAKE_CXX_STANDARD 11)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# מציאת ספריית הנדיד
find_library(log-lib log)

# הוספת קבצי המקור
add_library(game-engine SHARED
    src/main/cpp/game_engine.cpp
    src/main/cpp/target.cpp
)

# חיבור לספריות חיצוניות
target_link_libraries(game-engine
    ${log-lib}
)
