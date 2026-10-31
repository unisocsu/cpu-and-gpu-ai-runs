from selenium import webdriver
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.common.by import By
import time

# הגדרת אופציות כרום
chrome_options = Options()
chrome_options.add_argument('--headless')  # מצב headless - ללא ממשק גרפי
chrome_options.add_argument('--no-sandbox')
chrome_options.add_argument('--disable-dev-shm-usage')
chrome_options.add_argument('--disable-gpu')
chrome_options.add_argument('--window-size=1920,1080')
chrome_options.add_argument('--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36')

# הפעלת הדפדפן
driver = webdriver.Chrome(options=chrome_options)

try:
    print("🌐 פותח את האתר magicode.me...")
    driver.get('https://send.magicode.me/')
    time.sleep(3)
    
    print("📄 כותרת הדף:", driver.title)
    print("🔗 URL נוכחי:", driver.current_url)
    
    # הדפסת ה-HTML של הגוף
    body_html = driver.find_element(By.TAG_NAME, 'body').get_attribute('innerHTML')
    print("\n📝 תוכן הדף (ראשון 2000 תווים):")
    print(body_html[:2000])
    
    # ניסיון לאתר אלמנטים רלוונטיים
    print("\n🔍 חיפוש אלמנטים רלוונטיים...")
    
    # חיפוש כפתורים
    buttons = driver.find_elements(By.TAG_NAME, 'button')
    print(f"🔘 נמצאו {len(buttons)} כפתורים:")
    for i, btn in enumerate(buttons[:10]):
        print(f"  {i+1}. טקסט: '{btn.text}' | ID: {btn.get_attribute('id')} | Class: {btn.get_attribute('class')}")
    
    # חיפוש אינפוטס
    inputs = driver.find_elements(By.TAG_NAME, 'input')
    print(f"\n📝 נמצאו {len(inputs)} שדות קלט:")
    for i, inp in enumerate(inputs[:10]):
        print(f"  {i+1}. Type: {inp.get_attribute('type')} | ID: {inp.get_attribute('id')} | Name: {inp.get_attribute('name')}")
    
    # חיפוש קישורים
    links = driver.find_elements(By.TAG_NAME, 'a')
    print(f"\n🔗 נמצאו {len(links)} קישורים:")
    for i, link in enumerate(links[:10]):
        print(f"  {i+1}. Text: '{link.text}' | Href: {link.get_attribute('href')}")
    
    # צילום מסך
    driver.save_screenshot('workspace/magicode_screenshot.png')
    print("\n📸 צילום מסך נשמר: workspace/magicode_screenshot.png")
    
except Exception as e:
    print(f"❌ שגיאה: {e}")
finally:
    driver.quit()
    print("\n✅ הדפדפן נסגר")
