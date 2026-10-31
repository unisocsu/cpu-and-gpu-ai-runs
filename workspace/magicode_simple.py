from selenium import webdriver
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.common.by import By
import time
import os

# יצירת קובץ קטן להעלאה
test_content = "שלום ראובן! זהו קובץ מבחן להעלאה לאתר Magicode.\n"
test_content += "נוצר אוטומטית על ידי Qwen AI\n"
with open('test_upload.txt', 'w', encoding='utf-8') as f:
    f.write(test_content)

print("📄 נוצר קובץ מבחן: test_upload.txt")
print(f"📊 גודל קובץ: {os.path.getsize('test_upload.txt')} בתים")

# הגדרת אופציות כרום
chrome_options = Options()
chrome_options.add_argument('--headless')
chrome_options.add_argument('--no-sandbox')
chrome_options.add_argument('--disable-dev-shm-usage')
chrome_options.add_argument('--disable-gpu')
chrome_options.add_argument('--window-size=1920,1080')
chrome_options.add_argument('--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36')

driver = webdriver.Chrome(options=chrome_options)

try:
    print("\n🌐 פותח את אתר ההעלאה...")
    driver.get('https://send.magicode.me/send-file/upload')
    time.sleep(5)
    
    print("📄 כותרת:", driver.title)
    print("🔗 URL:", driver.current_url)
    
    # הדפסת HTML מלא
    html = driver.find_element(By.TAG_NAME, 'html').get_attribute('innerHTML')
    print(f"\n📝 HTML אורך: {len(html)} תווים")
    print("ראשית ה-HTML:")
    print(html[:1500])
    
    # חיפוש אלמנטים
    print("\n🔍 חיפוש אלמנטים...")
    
    buttons = driver.find_elements(By.TAG_NAME, 'button')
    print(f"כפתורים ({len(buttons)}):")
    for btn in buttons:
        print(f"  - text='{btn.text}' id='{btn.get_attribute('id')}'")
    
    inputs = driver.find_elements(By.TAG_NAME, 'input')
    print(f"\nשדות קלט ({len(inputs)}):")
    for inp in inputs:
        print(f"  - type='{inp.get_attribute('type')}' id='{inp.get_attribute('id')}' name='{inp.get_attribute('name')}'")
    
    # חיפוש div עם dropzone
    dropzones = driver.find_elements(By.CSS_SELECTOR, '[class*="drop"], [class*="upload"], [class*="file"]')
    print(f"\nאלמנטי העלאה ({len(dropzones)}):")
    for dz in dropzones:
        print(f"  - class='{dz.get_attribute('class')}'")
    
    # צילום מסך
    driver.save_screenshot('magicode_upload_attempt.png')
    print("\n📸 צילום מסך נשמר: magicode_upload_attempt.png")
    
except Exception as e:
    print(f"❌ שגיאה: {e}")
    import traceback
    traceback.print_exc()
finally:
    driver.quit()
    print("\n✅ סיום")
