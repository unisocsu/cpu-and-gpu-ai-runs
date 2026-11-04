#include <windows.h>
#include <commctrl.h>
#include <cmath>
#include <cstring>
#include <cstdio>

#pragma comment(lib, "comctl32.lib")

// ─── Global State ────────────────────────────────────────
static HWND hDisplay;
static HWND hResult;
static char currentInput[64] = "";
static char lastResult[64] = "";
static bool newInput = true;

// ─── Update the display ──────────────────────────────────
void UpdateDisplay(const wchar_t* text) {
    SetWindowTextW(hDisplay, text);
}

void UpdateResult(const wchar_t* text) {
    SetWindowTextW(hResult, text);
}

// ─── Button Click Handler ────────────────────────────────
void OnButtonClick(int btnId) {
    wchar_t wbuf[64];
    char buf[64];

    if (btnId == 2000) { // C
        currentInput[0] = '\0';
        lastResult[0] = '\0';
        newInput = true;
        UpdateDisplay(L"");
        UpdateResult(L"");
    }
    else if (btnId == 2001) { // ±
        if (strlen(currentInput) > 0 && currentInput[0] != '0') {
            if (currentInput[0] == '-') {
                memmove(currentInput, currentInput + 1, strlen(currentInput) + 1);
            } else {
                sprintf(buf, "-%s", currentInput);
                strcpy(currentInput, buf);
            }
            mbstowcs(wbuf, currentInput, 63);
            wbuf[63] = L'\0';
            UpdateDisplay(wbuf);
        }
    }
    else if (btnId == 2002) { // ⌫ (backspace)
        int len = strlen(currentInput);
        if (len > 0) {
            currentInput[len - 1] = '\0';
            mbstowcs(wbuf, currentInput, 63);
            wbuf[63] = L'\0';
            UpdateDisplay(wbuf);
        }
    }
    else if (btnId == 2003) { // =
        if (strlen(currentInput) > 0) {
            double val = atof(currentInput);
            double result = pow(val, 2) + sin(val) * 100;
            sprintf(buf, "%.6f", result);
            strcpy(lastResult, buf);
            strcpy(currentInput, buf);
            newInput = true;
            mbstowcs(wbuf, currentInput, 63);
            wbuf[63] = L'\0';
            UpdateDisplay(wbuf);
            UpdateResult(L"x² + sin(x)·100");
        }
    }
    else if (btnId >= 2004 && btnId <= 2014) { // Numbers and dot
        char ch = (char)('0' + (btnId - 2004));
        if (btnId == 2014) ch = '.'; // dot button

        if (newInput) {
            if (ch == '.') {
                strcpy(currentInput, "0.");
            } else {
                currentInput[0] = ch;
                currentInput[1] = '\0';
            }
            newInput = false;
        } else {
            int len = strlen(currentInput);
            if (len < 63) {
                if (ch == '.' && strchr(currentInput, '.') != NULL) {
                    // Already has dot, ignore
                } else {
                    currentInput[len] = ch;
                    currentInput[len + 1] = '\0';
                }
            }
        }
        mbstowcs(wbuf, currentInput, 63);
        wbuf[63] = L'\0';
        UpdateDisplay(wbuf);
    }
}

// ─── Dialog Procedure ────────────────────────────────────
INT_PTR CALLBACK DialogProc(HWND hwnd, UINT msg, WPARAM wParam, LPARAM lParam) {
    switch (msg) {
        case WM_INITDIALOG:
            hDisplay = GetDlgItem(hwnd, 1001);
            hResult = GetDlgItem(hwnd, 1002);
            UpdateDisplay(L"");
            UpdateResult(L"");
            return TRUE;

        case WM_COMMAND:
            if (LOWORD(wParam) >= 2000 && LOWORD(wParam) < 3000) {
                OnButtonClick(LOWORD(wParam));
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
            const wchar_t* labels[] = {L"C", L"±", L"⌫", L"=", L"7", L"8", L"9", L"4", L"5", L"6", L"1", L"2", L"3", L"0", L"."};
            for (int i = 0; i < 15; i++) {
                int row = i / 4;
                int col = i % 4;
                int btnId = 2000 + i;
                CreateWindowW(
                    L"BUTTON", labels[i],
                    WS_VISIBLE | WS_CHILD | BS_DEFPUSHBUTTON,
                    15 + col * 95, 135 + row * 45, 90, 40,
                    hwnd, (HMENU)btnId, GetModuleHandle(NULL), NULL
                );
            }
            return 0;
        }

        case WM_COMMAND:
            if (LOWORD(wParam) >= 2000 && LOWORD(wParam) < 3000) {
                OnButtonClick(LOWORD(wParam));
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
        L"🧮 Calculator Pro — מחשבון",
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
