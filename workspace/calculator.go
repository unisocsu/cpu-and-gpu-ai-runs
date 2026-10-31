package main

import (
	"fmt"
)

func main() {
	fmt.Println("🧮 מחשבון פשוט - גרסת Windows")
	fmt.Println("================================")
	fmt.Println()
	
	fmt.Print("הכנס מספר ראשון: ")
	var a float64
	fmt.Scanln(&a)
	
	fmt.Print("הכנס מספר שני: ")
	var b float64
	fmt.Scanln(&b)
	
	fmt.Println()
	fmt.Printf("%v + %v = %v\n", a, b, a+b)
	fmt.Printf("%v - %v = %v\n", a, b, a-b)
	fmt.Printf("%v × %v = %v\n", a, b, a*b)
	
	if b != 0 {
		fmt.Printf("%v ÷ %v = %v\n", a, b, a/b)
	} else {
		fmt.Println("❌ חלוקה באפס אינה אפשרית")
	}
	
	fmt.Println()
	fmt.Println("✅ תוכנה זו הוקמפלה ל-Windows מסביבת Linux!")
	fmt.Println("   שם קובץ: calculator.exe")
}
