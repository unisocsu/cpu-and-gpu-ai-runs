#include <iostream>
#include <string>
#include <iomanip>
#include <cmath>

using namespace std;

// פונקציה להדפסת התפריט
void printMenu() {
    cout << "\n";
    cout << "╔══════════════════════════════════════╗\n";
    cout << "║        🧮 מחשבון מתקדם 🧮            ║\n";
    cout << "╠══════════════════════════════════════╣\n";
    cout << "║  1. חיבור (+)                        ║\n";
    cout << "║  2. חיסור (-)                        ║\n";
    cout << "║  3. כפל (*)                          ║\n";
    cout << "║  4. חילוק (/)                        ║\n";
    cout << "║  5. חזקה (^)                         ║\n";
    cout << "║  6. שורש ריבועי (√)                  ║\n";
    cout << "║  7. אחוז (%)                         ║\n";
    cout << "║  8. פונקציית סינוס (sin)             ║\n";
    cout << "║  9. פונקציית קוסינוס (cos)           ║\n";
    cout << "║ 10. פונקציית טנגנס (tan)            ║\n";
    cout << "║  0. יציאה                            ║\n";
    cout << "╚══════════════════════════════════════╝\n";
    cout << "\n";
}

// פונקציה לקריאת מספר
double getNumber(const string& prompt) {
    double num;
    while (true) {
        cout << prompt;
        if (cin >> num) {
            cin.ignore(10000, '\n'); // נקה את ה-buffer
            return num;
        } else {
            cout << "⚠️  אנא הזן מספר תקין!\n";
            cin.clear();
            cin.ignore(10000, '\n');
        }
    }
}

// פונקציה לקריאת בחירה
int getChoice() {
    int choice;
    while (true) {
        cout << "בחר פעולה (0-10): ";
        if (cin >> choice) {
            cin.ignore(10000, '\n');
            if (choice >= 0 && choice <= 10) {
                return choice;
            }
        }
        cout << "⚠️  אנא הזן מספר תקין בין 0 ל-10!\n";
        cin.clear();
        cin.ignore(10000, '\n');
    }
}

int main() {
    cout << "╔══════════════════════════════════════╗\n";
    cout << "║   ברוכים הבאים למחשבון המתקדם!       ║\n";
    cout << "╚══════════════════════════════════════╝\n";
    
    int choice;
    
    do {
        printMenu();
        choice = getChoice();
        
        switch (choice) {
            case 1: { // חיבור
                double a = getNumber("הזן את המספר הראשון: ");
                double b = getNumber("הזן את המספר השני: ");
                cout << "\n✅ התוצאה: " << a << " + " << b << " = " << (a + b) << "\n";
                break;
            }
            case 2: { // חיסור
                double a = getNumber("הזן את המספר הראשון: ");
                double b = getNumber("הזן את המספר השני: ");
                cout << "\n✅ התוצאה: " << a << " - " << b << " = " << (a - b) << "\n";
                break;
            }
            case 3: { // כפל
                double a = getNumber("הזן את המספר הראשון: ");
                double b = getNumber("הזן את המספר השני: ");
                cout << "\n✅ התוצאה: " << a << " * " << b << " = " << (a * b) << "\n";
                break;
            }
            case 4: { // חילוק
                double a = getNumber("הזן את המספר הראשון: ");
                double b = getNumber("הזן את המספר השני: ");
                if (b == 0) {
                    cout << "\n❌ שגיאה: לא ניתן לחלק באפס!\n";
                } else {
                    cout << "\n✅ התוצאה: " << a << " / " << b << " = " << (a / b) << "\n";
                }
                break;
            }
            case 5: { // חזקה
                double base = getNumber("הזן את הבסיס: ");
                double exponent = getNumber("הזן את המעריך: ");
                cout << "\n✅ התוצאה: " << base << " ^ " << exponent << " = " << pow(base, exponent) << "\n";
                break;
            }
            case 6: { // שורש ריבועי
                double num = getNumber("הזן את המספר: ");
                if (num < 0) {
                    cout << "\n❌ שגיאה: לא ניתן לחשב שורש של מספר שלילי!\n";
                } else {
                    cout << "\n✅ התוצאה: √" << num << " = " << sqrt(num) << "\n";
                }
                break;
            }
            case 7: { // אחוז
                double num = getNumber("הזן את המספר: ");
                double percent = getNumber("הזן את האחוז: ");
                cout << "\n✅ התוצאה: " << percent << "% מ-" << num << " = " << (num * percent / 100) << "\n";
                break;
            }
            case 8: { // סינוס
                double angle = getNumber("הזן את הזווית ברדיאנים: ");
                cout << "\n✅ התוצאה: sin(" << angle << ") = " << sin(angle) << "\n";
                break;
            }
            case 9: { // קוסינוס
                double angle = getNumber("הזן את הזווית ברדיאנים: ");
                cout << "\n✅ התוצאה: cos(" << angle << ") = " << cos(angle) << "\n";
                break;
            }
            case 10: { // טנגנס
                double angle = getNumber("הזן את הזווית ברדיאנים: ");
                cout << "\n✅ התוצאה: tan(" << angle << ") = " << tan(angle) << "\n";
                break;
            }
            case 0: { // יציאה
                cout << "\n👋 תודה ש-השתמשת במחשבון! להתראות!\n";
                break;
            }
            default: {
                cout << "\n⚠️  בחירה לא תקינה! אנא נסה שוב.\n";
                break;
            }
        }
        
        if (choice != 0) {
            cout << "\nלחץ Enter להמשך...";
            cin.ignore(10000, '\n');
            cin.get();
        }
        
    } while (choice != 0);
    
    return 0;
}
