// Calculator.cpp - אפליקציית מחשבון קטנה ב-C++
// רצה על ווינדוס - Console Application

#include <iostream>
#include <string>
#include <cmath>
#include <limits>

using namespace std;

// פונקציה להדפסת כותרת
void printHeader() {
    cout << "\n";
    cout << "╔══════════════════════════════════════╗\n";
    cout << "║       מחשבון קטן ויפה ב-C++        ║\n";
    cout << "║      לחץ 'q' בכל שלב ליציאה         ║\n";
    cout << "╚══════════════════════════════════════╝\n";
    cout << "\n";
}

// פונקציה לקבלת מספר מהמשתמש
double getNumber(const string& prompt) {
    double num;
    while (true) {
        cout << prompt;
        cin >> num;
        if (cin.fail()) {
            cin.clear();
            cin.ignore(numeric_limits<streamsize>::max(), '\n');
            cout << "⚠️  אנא הזן מספר תקין!\n";
        } else {
            return num;
        }
    }
}

// פעולות חשבון
double add(double a, double b) { return a + b; }
double subtract(double a, double b) { return a - b; }
double multiply(double a, double b) { return a * b; }
double divide(double a, double b) {
    if (b == 0) {
        cout << "❌ לא ניתן לחלק באפס!\n";
        return 0;
    }
    return a / b;
}
double power(double base, double exp) {
    return pow(base, exp);
}
double squareRoot(double num) {
    if (num < 0) {
        cout << "❌ לא ניתן לחשב שורש של מספר שלילי!\n";
        return 0;
    }
    return sqrt(num);
}

// תפריט ראשי
void showMainMenu() {
    cout << "📋 תפריט ראשי:\n";
    cout << "  1 ➕  חיבור\n";
    cout << "  2 ➖  חיסור\n";
    cout << "  3 ✖️  כפל\n";
    cout << "  4 ➗  חילוק\n";
    cout << "  5 🔢 חזקה\n";
    cout << "  6 √   שורש ריבועי\n";
    cout << "  7 📊  סטטיסטיקה (ממוצע, מקסימום, מינימום)\n";
    cout << "  8 🔄  תפריט פעולות מתקדמות\n";
    cout << "  0 🚪  יציאה\n";
    cout << "\n  בחר פעולה: ";
}

// תפריט מתקדם
void showAdvancedMenu() {
    cout << "\n🔧 תפריט מתקדם:\n";
    cout << "  1 📐  פונקציות מתמטיות (סינוס, קוסינוס)\n";
    cout << "  2 🔄  ממיר יחידות\n";
    cout << "  3 🔙  חזרה לתפריט ראשי\n";
    cout << "\n  בחר פעולה: ";
}

// פונקציות מתמטיות מתקדמות
void advancedMath() {
    cout << "\n📐 פונקציות מתמטיות:\n";
    cout << "  1 sin(סינוס)\n";
    cout << "  2 cos(קוסינוס)\n";
    cout << "  3 tan(טנגנס)\n";
    cout << "  4 π (מספר פאי)\n";
    cout << "  5 e (מספר אוילר)\n";
    cout << "  0 🔙 חזרה\n";
    cout << "  בחר: ";
}

// ממיר יחידות
void unitConverter() {
    cout << "\n🔄 ממיר יחידות:\n";
    cout << "  1 מעלות צלזיוס ➜ פארנהייט\n";
    cout << "  2 מעלות פארנהייט ➜ צלזיוס\n";
    cout << "  3 קילומטרים ➜ מיילים\n";
    cout << "  4 מיילים ➜ קילומטרים\n";
    cout << "  5 קילוגרמים ➜ פאונד\n";
    cout << "  6 פאונד ➜ קילוגרמים\n";
    cout << "  0 🔙 חזרה\n";
    cout << "  בחר: ";
}

// סטטיסטיקה
void statistics() {
    cout << "\n📊 סטטיסטיקה\n";
    cout << "הזן מספרים (הזן מספר שלילי גדול מ-99999 כדי לסיים):\n";
    
    double num, sum = 0;
    int count = 0;
    double maxVal = -99999, minVal = 99999;
    
    while (true) {
        cout << "  מספר #" << (count + 1) << ": ";
        cin >> num;
        if (num > 99999) break;
        sum += num;
        count++;
        if (num > maxVal) maxVal = num;
        if (num < minVal) minVal = num;
    }
    
    if (count == 0) {
        cout << "⚠️  לא הוזנו מספרים!\n";
        return;
    }
    
    cout << "\n📊 תוצאות:\n";
    cout << "  כמות מספרים:   " << count << "\n";
    cout << "  סכום:           " << sum << "\n";
    cout << "  ממוצע:          " << (sum / count) << "\n";
    cout << "  מקסימום:        " << maxVal << "\n";
    cout << "  מינימום:        " << minVal << "\n";
    cout << "  טווח:           " << (maxVal - minVal) << "\n";
}

// --- MAIN ---
int main() {
    // הגדרת קידוד עברי
    system("chcp 65001 >nul");
    
    printHeader();
    
    int choice = 0;
    
    while (choice != 0) {
        showMainMenu();
        cin >> choice;
        
        if (choice == 'q' || choice == 'Q') {
            cout << "\n👋 התראיין! בהצלחה!\n";
            return 0;
        }
        
        switch (choice) {
            case 1: {
                double a = getNumber("  מספר ראשון: ");
                double b = getNumber("  מספר שני:   ");
                cout << "\n✅ התוצאה: " << a << " + " << b << " = " << add(a, b) << "\n";
                break;
            }
            case 2: {
                double a = getNumber("  מספר ראשון: ");
                double b = getNumber("  מספר שני:   ");
                cout << "\n✅ התוצאה: " << a << " - " << b << " = " << subtract(a, b) << "\n";
                break;
            }
            case 3: {
                double a = getNumber("  מספר ראשון: ");
                double b = getNumber("  מספר שני:   ");
                cout << "\n✅ התוצאה: " << a << " x " << b << " = " << multiply(a, b) << "\n";
                break;
            }
            case 4: {
                double a = getNumber("  מספר ראשון: ");
                double b = getNumber("  מספר שני:   ");
                double result = divide(a, b);
                if (b != 0) {
                    cout << "\n✅ התוצאה: " << a << " / " << b << " = " << result << "\n";
                }
                break;
            }
            case 5: {
                double base = getNumber("  בסיס: ");
                double exp = getNumber("  מעריך (חזקה): ");
                cout << "\n✅ התוצאה: " << base << " ^ " << exp << " = " << power(base, exp) << "\n";
                break;
            }
            case 6: {
                double num = getNumber("  מספר: ");
                double result = squareRoot(num);
                if (num >= 0) {
                    cout << "\n✅ התוצאה: √" << num << " = " << result << "\n";
                }
                break;
            }
            case 7:
                statistics();
                break;
            case 8: {
                int advChoice = 0;
                while (advChoice != 0) {
                    showAdvancedMenu();
                    cin >> advChoice;
                    
                    if (advChoice == 'q' || advChoice == 'Q') {
                        cout << "\n👋 התראיין!\n";
                        return 0;
                    }
                    
                    switch (advChoice) {
                        case 1: {
                            double angle = getNumber("  הזן זווית ברדיאנים: ");
                            cout << "\n✅ sin(" << angle << ") = " << sin(angle) << "\n";
                            cout << "   cos(" << angle << ") = " << cos(angle) << "\n";
                            cout << "   tan(" << angle << ") = " << tan(angle) << "\n";
                            break;
                        }
                        case 2: {
                            int unitChoice = 0;
                            while (unitChoice != 0) {
                                unitConverter();
                                cin >> unitChoice;
                                
                                if (unitChoice == 'q' || unitChoice == 'Q') {
                                    cout << "\n👋 התראיין!\n";
                                    return 0;
                                }
                                
                                double value, result;
                                switch (unitChoice) {
                                    case 1:
                                        value = getNumber("  מעלות צלזיוס: ");
                                        result = (value * 9.0/5.0) + 32;
                                        cout << "✅ " << value << "°C = " << result << "°F\n";
                                        break;
                                    case 2:
                                        value = getNumber("  מעלות פארנהייט: ");
                                        result = (value - 32) * 5.0/9.0;
                                        cout << "✅ " << value << "°F = " << result << "°C\n";
                                        break;
                                    case 3:
                                        value = getNumber("  קילומטרים: ");
                                        result = value * 0.621371;
                                        cout << "✅ " << value << " km = " << result << " miles\n";
                                        break;
                                    case 4:
                                        value = getNumber("  מיילים: ");
                                        result = value * 1.60934;
                                        cout << "✅ " << value << " miles = " << result << " km\n";
                                        break;
                                    case 5:
                                        value = getNumber("  קילוגרמים: ");
                                        result = value * 2.20462;
                                        cout << "✅ " << value << " kg = " << result << " lbs\n";
                                        break;
                                    case 6:
                                        value = getNumber("  פאונד: ");
                                        result = value * 0.453592;
                                        cout << "✅ " << value << " lbs = " << result << " kg\n";
                                        break;
                                    case 0:
                                        cout << "  🔙 חזרה לתפריט מתקדם\n";
                                        break;
                                    default:
                                        cout << "  ⚠️  בחירה לא תקינה!\n";
                                }
                            }
                            break;
                        }
                        case 3:
                            cout << "  🔙 חזרה לתפריט ראשי\n";
                            break;
                        case 0:
                            cout << "  🔙 חזרה לתפריט ראשי\n";
                            break;
                        default:
                            cout << "  ⚠️  בחירה לא תקינה!\n";
                    }
                }
                break;
            }
            case 0:
                cout << "\n👋 התראיין! בהצלחה!\n";
                break;
            default:
                cout << "  ⚠️  בחירה לא תקינה! אנא בחר מספר בין 0 ל-8\n";
        }
        
        if (choice != 0) {
            cout << "\n  לחץ Enter להמשך...";
            cin.ignore(numeric_limits<streamsize>::max(), '\n');
            cin.get();
        }
    }
    
    return 0;
}
