#!/usr/bin/env python3
"""
חלל עמוק - אפליקציית חלל אינטראקטיבית
מציגה דף HTML מלא במסך מלא
"""

import sys
import os
import threading
import time
from http.server import HTTPServer, SimpleHTTPRequestHandler
import socketserver
import webview

def get_free_port():
    """מוצא פורט פנוי"""
    with socketserver.TCPServer(('127.0.0.1', 0), SimpleHTTPRequestHandler) as s:
        return s.server_address[1]

def run_server(port):
    """מריץ שרת HTTP מקומי"""
    os.chdir(os.path.dirname(os.path.abspath(__file__)))
    handler = SimpleHTTPRequestHandler
    httpd = HTTPServer(('127.0.0.1', port), handler)
    httpd.serve_forever()

def main():
    # מוצא פורט פנוי
    port = get_free_port()
    
    # מריץ שרת ברקע
    server_thread = threading.Thread(target=run_server, args=(port,), daemon=True)
    server_thread.start()
    time.sleep(0.5)
    
    # יוצר חלון
    window = webview.create_window(
        title='🚀 החלל העמוק - מסע בין כוכבים',
        url=f'http://127.0.0.1:{port}/space-adventure.html',
        width=1280,
        height=720,
        js_api=None,
        fullscreen=False,
        resizable=True
    )
    
    webview.start()

if __name__ == '__main__':
    main()
