import time
import win32gui
from datetime import datetime
import requests
from plyer import notification
import pyautogui
import os

# ✅ FIXED
SERVER_URL = "http://127.0.0.1:3000"

banned_apps = ["Chrome", "YouTube", "Instagram", "Game"]

def get_active_window():
    window = win32gui.GetForegroundWindow()
    return win32gui.GetWindowText(window)

if not os.path.exists("screenshots"):
    os.makedirs("screenshots")

print("🔥 Tracker Started...")

last_alert = ""
last_app = ""
start_time = time.time()

while True:
    try:
        app_name = get_active_window()
        current_time = datetime.now().strftime("%Y-%m-%d %H:%M:%S")

        if app_name != last_app:
            start_time = time.time()
            last_app = app_name

        duration = int(time.time() - start_time)
        minutes = duration // 60
        seconds = duration % 60

        file_name = f"screenshots/{current_time.replace(':','-')}.png"
        pyautogui.screenshot().save(file_name)

        data = {
            "time": current_time,
            "app": app_name,
            "duration": f"{minutes}m {seconds}s"
        }

        # SEND
        try:
            with open(file_name, "rb") as f:
                requests.post(f"{SERVER_URL}/track", data=data, files={"file": f})
                print("✅ Sent:", data)
        except Exception as e:
            print("❌ Server error:", e)

        # COMMAND
        try:
            res = requests.get(f"{SERVER_URL}/get-command")
            cmd = res.json().get("command")

            if cmd:
                print("🎮 Command:", cmd)

                if cmd == "lock":
                    os.system("rundll32.exe user32.dll,LockWorkStation")

                elif cmd == "shutdown":
                    os.system("shutdown /s /t 5")

                elif cmd == "alert":
                    notification.notify(
                        title="Alert",
                        message="Remote Alert",
                        timeout=5
                    )
        except:
            pass

        time.sleep(5)

    except Exception as e:
        print("⚠️ Error:", e)