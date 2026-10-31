from selenium import webdriver
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
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
    time.sleep(3)
    
    print("📄 כותרת:", driver.title)
    
    # איתור שדה הקלט
    file_input = driver.find_element(By.CSS_SELECTOR, '#file-input-container input[type="file"]')
    print("✅ נמצא שדה קלט להעלאה")
    
    # העלאת הקובץ
    print(f"\n📤 מעלה קובץ: test_upload.txt...")
    file_input.send_keys(os.path.abspath('test_upload.txt'))
    time.sleep(5)
    
    # המתנה להשלמת ההעלאה
    print("⏳ ממתיין להשלמת ההעלאה...")
    try:
        # מחכים שהתוצאה תופיע
        result_div = WebDriverWait(driver, 15).until(
            EC.presence_of_element_located((By.ID, 'result'))
        )
        print("✅ אזור התוצאה הופיע!")
        
        # קריאת התוצאה
        link_element = driver.find_element(By.ID, 'link-file')
        download_link = link_element.get_attribute('href') or link_element.text
        
        print(f"\n🎉 ההעלאה הושלחה בהצלחה!")
        print(f"🔗 קישור להורדה: {download_link}")
        
        # צילום מסך
        driver.save_screenshot('magicode_upload_success.png')
        print("📸 צילום מסך נשמר: magicode_upload_success.png")
        
    except Exception as e:
        print(f"❌ שגיאה בהמתנה לתוצאה: {e}")
        # הדפסת ה-HTML הנוכחי
        html = driver.find_element(By.TAG_NAME, 'body').get_attribute('innerHTML')
        print("\n📝 HTML נוכחי:")
        print(html)
        
except Exception as e:
    print(f"❌ שגיאה: {e}")
    import traceback
    traceback.print_exc()
finally:
    driver.quit()
    print("\n✅ סיום")
