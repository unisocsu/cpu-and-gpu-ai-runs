#include <windows.h>
#include <commctrl.h>
#include <cmath>
#include <cstring>
#include <cstdio>

#pragma comment(lib, "comctl32.lib")

// ─── Global State ────────────────────────────────────────
static HWND hDlg;
static HWND hDisplay;
static HWND hResult;
static char currentInput[64] = "";
static char lastResult[64] = "";
static bool newInput = true;

// ─── Update the display ──────────────────────────────────
void UpdateDisplay(const char* text) {
    SetWindowText(hDisplay, text);
}

void UpdateResult(const char* text) {
    SetWindowText(hResult, text);
}

// ─── Button Click Handler ────────────────────────────────
void OnButton(char btn) {
    if (newInput && (btn >= '0' && btn <= '9')) {
        currentInput[0] = btn;
        currentInput[1] = '\0';
        newInput = false;
    } else if (btn >= '0' && btn <= '9') {
        int len = strlen(currentInput);
        if (len < 63) {
            currentInput[len] = btn;
            currentInput[len + 1] = '\0';
        }
    } else if (btn == '.') {
        if (newInput) {
            strcpy(currentInput, "0.");
            newInput = false;
        } else if (strchr(currentInput, '.') == NULL) {
            int len = strlen(currentInput);
            currentInput[len] = '.';
            currentInput[len + 1] = '\0';
        }
    } else if (btn == 'C') {
        currentInput[0] = '\0';
        lastResult[0] = '\0';
        newInput = true;
        UpdateDisplay("");
        UpdateResult("");
    } else if (btn == '±') {
        if (strlen(currentInput) > 0 && currentInput[0] != '0') {
            if (currentInput[0] == '-') {
                memmove(currentInput, currentInput + 1, strlen(currentInput) + 1);
            } else {
                char temp[64];
                sprintf(temp, "-%s", currentInput);
                strcpy(currentInput, temp);
            }
            UpdateDisplay(currentInput);
        }
    } else if (btn == '⌫') {
        int len = strlen(currentInput);
        if (len > 0) {
            currentInput[len - 1] = '\0';
            UpdateDisplay(currentInput);
        }
    } else if (btn == '=') {
        if (strlen(currentInput) > 0) {
            double val = atof(currentInput);
            double result = pow(val, 2) + sin(val) * 100;
            char buf[64];
            sprintf(buf, "%.6f", result);
            strcpy(lastResult, buf);
            strcpy(currentInput, buf);
            newInput = true;
            UpdateDisplay(currentInput);
            UpdateResult("√(x² + sin(x)·100)");
        }
    }
}

// ─── Create Button ───────────────────────────────────────
HWND CreateButton(HWND parent, const char* text, int x, int y, int w, int h, int id) {
    return CreateWindowW(
        L"BUTTON", text,
        WS_VISIBLE | WS_CHILD | BS_DEFPUSHBUTTON,
        x, y, w, h,
        parent, (HMENU)id, GetModuleHandle(NULL), NULL
    );
}

// ─── Dialog Procedure ────────────────────────────────────
INT_PTR CALLBACK DialogProc(HWND hwnd, UINT msg, WPARAM wParam, LPARAM lParam) {
    switch (msg) {
        case WM_INITDIALOG:
            hDlg = hwnd;
            hDisplay = GetDlgItem(hwnd, 1001);
            hResult = GetDlgItem(hwnd, 1002);
            UpdateDisplay("");
            UpdateResult("");
            return TRUE;

        case WM_COMMAND:
            if (LOWORD(wParam) >= 2000 && LOWORD(wParam) < 3000) {
                OnButton((char)(LOWORD(wParam) - 2000));
                return TRUE;
            }
            break;

        case WM_CLOSE:
            EndDialog(hwnd, 0);
            break;

        case WM_DESTROY:
            PostQuitMessage(0);
            break;
    }
    return FALSE;
}

// ─── WndProc ─────────────────────────────────────────────
LRESULT CALLBACK WndProc(HWND hwnd, UINT msg, WPARAM wParam, LPARAM lParam) {
    switch (msg) {
        case WM_CREATE: {
            INITCOMMONCONTROLSEX icex;
            icex.dwSize = sizeof(INITCOMMONCONTROLSEX);
            icex.dwICC = ICC_BAR_CLASSES;
            InitCommonControlsEx(&icex);

            // Title bar
            CreateWindowW(L"STATIC", L"🧮 Calculator Pro",
                WS_VISIBLE | WS_CHILD | SS_CENTER,
                10, 5, 380, 30, hwnd, NULL, GetModuleHandle(NULL), NULL);

            // Display
            hDisplay = CreateWindowW(L"EDIT", L"",
                WS_VISIBLE | WS_CHILD | WS_BORDER | ES_RIGHT | ES_READONLY | ES_MULTILINE,
                15, 45, 370, 50, hwnd, NULL, GetModuleHandle(NULL), NULL);

            // Result label
            hResult = CreateWindowW(L"STATIC", L"",
                WS_VISIBLE | WS_CHILD | SS_CENTER,
                15, 100, 370, 25, hwnd, NULL, GetModuleHandle(NULL), NULL);

            // Buttons
            const char* labels[] = {"C", "±", "⌫", "=", "7", "8", "9", "4", "5", "6", "1", "2", "3", "0", "."};
            for (int i = 0; i < 15; i++) {
                int row = i / 4;
                int col = i % 4;
                int btnId = 2000 + i;
                CreateButton(hwnd, labels[i],
                    15 + col * 95, 135 + row * 45, 90, 40, btnId);
            }
            return 0;
        }

        case WM_COMMAND:
            if (LOWORD(wParam) >= 2000 && LOWORD(wParam) < 3000) {
                OnButton((char)(LOWORD(wParam) - 2000));
                return 0;
            }
            break;

        case WM_CLOSE:
            DestroyWindow(hwnd);
            break;

        case WM_DESTROY:
            PostQuitMessage(0);
            break;
    }
    return DefWindowProcW(hwnd, msg, wParam, lParam);
}

// ─── WinMain ─────────────────────────────────────────────
int WINAPI WinMain(HINSTANCE hInstance, HINSTANCE hPrevInstance, LPSTR lpCmdLine, int nCmdShow) {
    UNREFERENCED_PARAMETER(hPrevInstance);
    UNREFERENCED_PARAMETER(lpCmdLine);

    // Register window class
    WNDCLASSEXW wc = {0};
    wc.cbSize = sizeof(WNDCLASSEXW);
    wc.style = CS_HREDRAW | CS_VREDRAW;
    wc.lpfnWndProc = WndProc;
    wc.hInstance = hInstance;
    wc.hCursor = LoadCursor(NULL, IDC_ARROW);
    wc.hbrBackground = (HBRUSH)(COLOR_WINDOW + 1);
    wc.lpszClassName = L"CalculatorProClass";
    wc.hIcon = LoadIcon(NULL, IDI_APPLICATION);
    wc.hIconSm = LoadIcon(NULL, IDI_APPLICATION);

    if (!RegisterClassExW(&wc)) return 1;

    // Create window
    HWND hwnd = CreateWindowW(
        L"CalculatorProClass",
        L"🧮 Calculator Pro — מכוונן מחשבון",
        WS_OVERLAPPEDWINDOW & ~WS_THICKFRAME & ~WS_MAXIMIZEBOX,
        CW_USEDEFAULT, CW_USEDEFAULT, 400, 420,
        NULL, NULL, hInstance, NULL
    );

    if (!hwnd) return 1;

    ShowWindow(hwnd, nCmdShow);
    UpdateWindow(hwnd);

    // Message loop
    MSG msg;
    while (GetMessageW(&msg, NULL, 0, 0) > 0) {
        TranslateMessage(&msg);
        DispatchMessageW(&msg);
    }

    return (int)msg.wParam;
}
