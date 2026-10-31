#include <iostream>
#include <string>
#include <cstdlib>

using namespace std;

int main() {
    cout << "========================================" << endl;
    cout << "   מחשבון מתקדם - גרסת לינוקס" << endl;
    cout << "========================================" << endl;
    cout << endl;
    
    // מציג ברכה
    cout << "שלום ראובן! 😊" << endl;
    cout << "זה רץ על השרת שלך דרך GitHub Actions!" << endl;
    cout << endl;
    
    // מחשבון
    double num1, num2, result;
    char op;
    
    cout << "הכנס מספר ראשון: ";
    cin >> num1;
    
    cout << "הכנס פעולה (+, -, *, /): ";
    cin >> op;
    
    cout << "הכנס מספר שני: ";
    cin >> num2;
    
    switch(op) {
        case '+':
            result = num1 + num2;
            cout << "\nתוצאה: " << num1 << " + " << num2 << " = " << result << endl;
            break;
        case '-':
            result = num1 - num2;
            cout << "\nתוצאה: " << num1 << " - " << num2 << " = " << result << endl;
            break;
        case '*':
            result = num1 * num2;
            cout << "\nתוצאה: " << num1 << " * " << num2 << " = " << result << endl;
            break;
        case '/':
            if(num2 != 0) {
                result = num1 / num2;
                cout << "\nתוצאה: " << num1 << " / " << num2 << " = " << result << endl;
            } else {
                cout << "\nשגיאה: חלוקה באפס!" << endl;
            }
            break;
        default:
            cout << "\nפעולה לא מוכרת!" << endl;
    }
    
    cout << endl;
    cout << "הסביבה: Linux" << endl;
    cout << "מהדר: g++" << endl;
    cout << "========================================" << endl;
    
    return 0;
}
