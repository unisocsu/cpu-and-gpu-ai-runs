import tkinter as tk
from tkinter import messagebox

def show_message():
    messagebox.showinfo("שלום", "שלום עולם!")

# יצירת החלון הראשי
root = tk.Tk()
root.title("תוכנת שלום עולם")
root.geometry("300x200")

# הוספת תווית
label = tk.Label(root, text="לחץ על הכפתור כדי לראות הודעה", font=("Arial", 12))
label.pack(pady=20)

# הוספת כפתור
button = tk.Button(root, text="הצג הודעה", command=show_message, font=("Arial", 12))
button.pack(pady=10)

# הרצת לולאת הממשק
root.mainloop()
