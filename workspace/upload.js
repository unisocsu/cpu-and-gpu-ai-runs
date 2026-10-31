const puppeteer = require('puppeteer');
const fs = require('fs');
const path = require('path');

(async () => {
  console.log('🚀 Starting Magicode upload...');
  
  const browser = await puppeteer.launch({
    headless: 'new',
    args: ['--no-sandbox', '--disable-setuid-sandbox']
  });
  
  const page = await browser.newPage();
  
  // הגדרת User-Agent כדי להיראות כמו דפדפן אמיתי
  await page.setUserAgent('Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36');
  
  console.log('🌐 Navigating to Magicode...');
  await page.goto('https://send.magicode.me/', { 
    waitUntil: 'networkidle2',
    timeout: 30000 
  });
  
  console.log('📋 Page loaded. Current URL:', page.url());
  
  // הדפסת כל הכפתורים והאלמנטים בדף
  const elements = await page.evaluate(() => {
    const allElements = document.querySelectorAll('*');
    const importantElements = [];
    
    allElements.forEach(el => {
      const tag = el.tagName.toLowerCase();
      const type = el.getAttribute('type');
      const role = el.getAttribute('role');
      const className = el.className;
      const id = el.id;
      const text = el.textContent?.trim()?.substring(0, 50);
      
      // מסנן אלמנטים רלוונטיים
      if (
        tag === 'input' || 
        tag === 'button' || 
        tag === 'a' ||
        className?.includes('upload') ||
        className?.includes('drop') ||
        className?.includes('file') ||
        className?.includes('btn') ||
        text?.includes('upload') ||
        text?.includes('send') ||
        text?.includes('drag')
      ) {
        importantElements.push({
          tag,
          type,
          role,
          className: className?.substring(0, 50),
          id,
          text,
          selector: el.tagName.toLowerCase() + 
                   (id ? `#${id}` : '') + 
                   (className && typeof className === 'string' ? `.${className.split(' ')[0]}` : '')
        });
      }
    });
    
    return importantElements;
  });
  
  console.log('\n📝 Found elements:');
  elements.forEach((el, i) => {
    console.log(`${i + 1}. ${el.selector} - "${el.text}"`);
  });
  
  // נסה לאתר את ה-drop zone או כפתור העלאה
  const uploadSelectors = [
    'input[type="file"]',
    '.upload-dropzone',
    '.drop-zone',
    '[class*="upload"]',
    '[class*="drop"]',
    'button[class*="upload"]',
    'a[class*="upload"]',
    '.file-input',
    '[role="button"]'
  ];
  
  let fileInput = null;
  let uploadButton = null;
  
  for (const selector of uploadSelectors) {
    try {
      const element = await page.$(selector);
      if (element) {
        console.log(`\n✅ Found element with selector: ${selector}`);
        
        const tagName = await page.evaluate(el => el.tagName, element);
        const type = await page.evaluate(el => el.getAttribute('type'), element);
        const className = await page.evaluate(el => el.className, element);
        
        console.log(`   Tag: ${tagName}, Type: ${type}, Class: ${className}`);
        
        if (type === 'file' || tagName === 'INPUT') {
          fileInput = element;
          console.log('   → This is a file input!');
        }
        
        if (tagName === 'BUTTON' || tagName === 'A') {
          uploadButton = element;
          console.log('   → This is a button/link!');
        }
      }
    } catch (e) {
      console.log(`   ✗ Selector ${selector} failed: ${e.message}`);
    }
  }
  
  // אם מצאנו כפתור, נלחץ עליו קודם
  if (uploadButton) {
    console.log('\n🖱️ Clicking upload button...');
    await uploadButton.click();
    await new Promise(r => setTimeout(r, 2000));
  }
  
  // אם מצאנו file input, נעלה את הקובץ
  if (fileInput) {
    console.log('\n📤 Uploading calculator.exe...');
    
    const filePath = path.join(__dirname, 'calculator.exe');
    
    if (!fs.existsSync(filePath)) {
      console.error('❌ File not found:', filePath);
      await browser.close();
      process.exit(1);
    }
    
    await fileInput.uploadFile(filePath);
    console.log('✅ File selected!');
    
    // המתן לסימון הקובץ
    await new Promise(r => setTimeout(r, 2000));
    
    // חפש כפתור שליחה
    const sendSelectors = [
      'button[type="submit"]',
      'button[class*="send"]',
      'button[class*="upload"]',
      'button[class*="submit"]',
      'button[class*="next"]',
      '.btn-primary',
      '.btn-success',
      '[class*="send"]'
    ];
    
    let sendButton = null;
    for (const selector of sendSelectors) {
      try {
        const element = await page.$(selector);
        if (element) {
          const text = await page.evaluate(el => el.textContent?.trim(), element);
          if (text && text.length < 50) {
            sendButton = element;
            console.log(`Found send button: "${text}" (${selector})`);
            break;
          }
        }
      } catch (e) {}
    }
    
    if (sendButton) {
      console.log('\n🚀 Clicking send button...');
      await sendButton.click();
      
      // המתן לתוצאה
      await new Promise(r => setTimeout(r, 5000));
      
      // הדפסת התוצאה
      const result = await page.evaluate(() => {
        return {
          url: window.location.href,
          title: document.title,
          bodyText: document.body?.textContent?.substring(0, 500)
        };
      });
      
      console.log('\n📊 Result:');
      console.log('   URL:', result.url);
      console.log('   Title:', result.title);
      console.log('   Body:', result.bodyText);
      
    } else {
      console.log('\n⚠️ No send button found. Screenshot saved.');
    }
    
    // צלם מסך
    await page.screenshot({ path: 'workspace/upload_result.png', fullPage: true });
    console.log('\n📸 Screenshot saved to workspace/upload_result.png');
    
  } else {
    console.log('\n❌ No file input found!');
    await page.screenshot({ path: 'workspace/upload_debug.png', fullPage: true });
    console.log('📸 Debug screenshot saved to workspace/upload_debug.png');
  }
  
  await browser.close();
  console.log('\n✅ Done!');
  
})().catch(err => {
  console.error('❌ Error:', err);
  process.exit(1);
});
