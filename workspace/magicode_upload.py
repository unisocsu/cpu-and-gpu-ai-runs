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
    time.sleep(3)
    
    print("📄 כותרת:", driver.title)
    
    # ניסיון 1: שימוש ב-JavaScript כדי ליצור אירוע העלאה
    print("\n🔧 ניסיון 1: העלאה באמצעות JavaScript...")
    
    # קריאה ל-JavaScript כדי להעלות קובץ
    upload_js = """
    function uploadFile(filePath) {
        // יצירת FileReader
        var xhr = new XMLHttpRequest();
        var formData = new FormData();
        
        // קריאה ל-API של האתר
        fetch('/send-file/upload', {
            method: 'GET',
            credentials: 'same-origin'
        })
        .then(response => response.text())
        .then(html => {
            console.log('Page loaded:', html.substring(0, 200));
        });
    }
    
    uploadFile();
    return 'JavaScript executed';
    """
    
    result = driver.execute_script(upload_js)
    print(f"📝 תוצאת JS: {result}")
    
    # ניסיון 2: חיפוש API endpoint
    print("\n🔍 ניסיון 2: בדיקת API באמצעות fetch...")
    
    api_check_js = """
    async function checkAPI() {
        try {
            // נסה להעלות קובץ קטן דרך fetch
            const response = await fetch('/send-file/upload', {
                method: 'POST',
                body: new Blob(['test'], {type: 'text/plain'})
            });
            return await response.text();
        } catch(e) {
            return 'Error: ' + e.message;
        }
    }
    return checkAPI();
    """
    
    api_result = driver.execute_async_script(api_check_js)
    print(f"📝 תוצאת API: {str(api_result)[:500]}")
    
    # ניסיון 3: בדיקת network requests
    print("\n🌐 בדיקת network requests...")
    driver.get('https://send.magicode.me/send-file/upload')
    time.sleep(2)
    
    # צילום מסך
    driver.save_screenshot('magicode_upload_attempt.png')
    print("📸 צילום מסך נשמר")
    
    # הדפסת cookies
    cookies = driver.get_cookies()
    print(f"\n🍪 Cookies ({len(cookies)}):")
    for c in cookies:
        print(f"  {c['name']} = {c['value'][:50]}")
    
except Exception as e:
    print(f"❌ שגיאה: {e}")
    import traceback
    traceback.print_exc()
finally:
    driver.quit()
    print("\n✅ סיום")
