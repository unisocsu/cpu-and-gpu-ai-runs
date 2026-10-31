#include <iostream>
#include <string>
#include <ctime>

int main() {
    std::cout << "=================================" << std::endl;
    std::cout << "  Hello from C++ on GitHub Actions!" << std::endl;
    std::cout << "=================================" << std::endl;
    std::cout << std::endl;
    
    // מידע על המערכת
    std::cout << "System Info:" << std::endl;
    std::cout << "  Date: " << __DATE__ << std::endl;
    std::cout << "  Time: " << __TIME__ << std::endl;
    std::cout << "  Compiler: " << __VERSION__ << std::endl;
    std::cout << std::endl;
    
    // יצירת מספר אקראי
    srand(time(NULL));
    int random_num = rand() % 100 + 1;
    std::cout << "Random number: " << random_num << std::endl;
    std::cout << std::endl;
    
    std::cout << "Compiled successfully! ✓" << std::endl;
    
    return 0;
}
