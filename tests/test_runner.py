"""בדיקות מקומיות ל-kaggle/runner.py: חילוץ קוד, בניית היסטוריה ולולאת ה-worker מול GitHub ו-LLM מדומים.
הרצה:  python tests/test_runner.py
"""
import hashlib
import json
import os
import re
import sys
import tempfile
import threading
import time
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer
from urllib.parse import parse_qs, urlparse

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
os.environ["OUT_DIR"] = tempfile.mkdtemp()
ns = {"JOB": {}}
exec(compile(open(os.path.join(ROOT, "kaggle", "runner.py"), encoding="utf-8").read(), "runner.py", "exec"), ns)

OWNER = "me"


# ---------------------------------------------------------------- חילוץ קוד
def test_extract():
    t = "הנה:\n```html\n<!DOCTYPE html><html></html>\n```\nועוד:\n```python\n# file: app.py\nprint(1)\n```\n```js\nlet a=1;\n```\n```css\nbody{"
    f = ns["extract_code"](t)
    names = [x["name"] for x in f]
    assert names == ["index.html", "app.py", "code_3.js", "code_4.css"], names
    assert [x["complete"] for x in f] == [True, True, True, False]
    raw = ns["extract_code"]("<!DOCTYPE html>\n<html><body>hi</body></html>")
    assert raw[0]["name"] == "index.html" and raw[0]["complete"]
    assert ns["extract_code"]("רק טקסט בלי קוד") == []
    bad = ns["extract_code"]("```py\n# file: ../../etc/passwd\nx=1\n```")
    assert bad[0]["name"] == "passwd", bad
    two = ns["extract_code"]("```html\na\n```\n```html\nb\n```")
    assert [x["name"] for x in two] == ["index.html", "code_2.html"], two
    ns["save_files"](f)
    assert os.path.exists(os.path.join(os.environ["OUT_DIR"], "files", "index.html"))
    print("extract ok")


# ---------------------------------------------------------------- היסטוריה
def test_history():
    M = ns["MARKER"]
    c = [
        {"id": 1, "body": "שלום", "user": {"login": OWNER}},
        {"id": 2, "body": M + "היי!\n\n<details><summary>חשיבה</summary>\n\nסוד\n</details>\n\n<sub>⏱ 1s</sub>", "user": {"login": "bot"}},
        {"id": 3, "body": "ספאם", "user": {"login": "stranger"}},
        {"id": 4, "body": "מה נשמע", "user": {"login": OWNER}},
        {"id": 5, "body": "עוד שאלה", "user": {"login": OWNER}},
    ]
    m = ns["build_messages"](c, OWNER, "SYS")
    assert m[0] == {"role": "system", "content": "SYS"}
    assert m[2] == {"role": "assistant", "content": "היי!"}, m[2]
    assert m[3]["content"] == "מה נשמע\n\nעוד שאלה"
    assert all("ספאם" not in x["content"] for x in m)
    assert ns["needs_reply"](c, OWNER, set()) is True
    assert ns["needs_reply"](c, OWNER, {5}) is False
    assert ns["needs_reply"](c[:2], OWNER, set()) is False
    assert ns["needs_reply"](c[:3], OWNER, set()) is False
    print("history ok")


# ---------------------------------------------------------------- GitHub מדומה
class State:
    def __init__(self):
        self.lock = threading.Lock()
        self.issues = {}
        self.comments = {}
        self.reactions = []
        self.next_id = 100
        self.llm_calls = []

    def add_issue(self, title, user, labels=(), body=""):
        n = len(self.issues) + 1
        self.issues[n] = {"number": n, "title": title, "state": "open", "body": body,
                          "user": {"login": user}, "labels": [{"name": l} for l in labels],
                          "updated_at": f"2026-01-01T00:00:{n:02d}Z", "_rev": 0}
        self.comments[n] = []
        return n

    def add_comment(self, n, user, body):
        self.next_id += 1
        c = {"id": self.next_id, "body": body, "user": {"login": user}}
        self.comments[n].append(c)
        self.issues[n]["_rev"] += 1
        self.issues[n]["updated_at"] = f"2026-01-01T00:01:{self.issues[n]['_rev']:02d}Z"
        return c


ST = State()


class Handler(BaseHTTPRequestHandler):
    def log_message(self, *a):
        pass

    def _send(self, code, data=None, etag=None):
        raw = json.dumps(data).encode() if data is not None else b""
        self.send_response(code)
        self.send_header("Content-Type", "application/json")
        if etag:
            self.send_header("ETag", etag)
        self.send_header("Content-Length", str(len(raw)))
        self.end_headers()
        self.wfile.write(raw)

    def _body(self):
        n = int(self.headers.get("Content-Length") or 0)
        return json.loads(self.rfile.read(n)) if n else {}

    def do_POST(self):
        self.route("POST")

    def do_GET(self):
        self.route("GET")

    def do_PATCH(self):
        self.route("PATCH")

    def route(self, method):
        u = urlparse(self.path)
        q = {k: v[0] for k, v in parse_qs(u.query).items()}
        p = re.sub(r"^/repos/o/r", "", u.path)
        with ST.lock:
            if p == "/v1/chat/completions" or u.path == "/v1/chat/completions":
                body = self._body()
                ST.llm_calls.append(body)
                last = body["messages"][-1]["content"]
                think = body["chat_template_kwargs"]["enable_thinking"]
                return self._send(200, {"choices": [{"message": {"content": f"ECHO: {last}",
                                                                 "reasoning_content": "hmm" if think else ""},
                                                     "finish_reason": "stop"}],
                                        "usage": {"completion_tokens": 7}})
            if p == "/issues" and method == "GET":
                items = [i for i in ST.issues.values() if i["state"] == q.get("state", "open")]
                if "labels" in q:
                    items = [i for i in items if any(l["name"] == q["labels"] for l in i["labels"])]
                if q.get("sort") == "updated":
                    items.sort(key=lambda i: i["updated_at"], reverse=True)
                data = [{k: v for k, v in i.items() if not k.startswith("_")} for i in items]
                etag = '"' + hashlib.md5(json.dumps(data, sort_keys=True).encode()).hexdigest() + '"'
                if self.headers.get("If-None-Match") == etag:
                    return self._send(304)
                return self._send(200, data, etag)
            if p == "/issues" and method == "POST":
                b = self._body()
                n = ST.add_issue(b["title"], "github-actions[bot]", b.get("labels", []), b.get("body", ""))
                return self._send(201, ST.issues[n])
            m = re.fullmatch(r"/issues/(\d+)", p)
            if m:
                n = int(m.group(1))
                if method == "GET":
                    return self._send(200, ST.issues[n])
                b = self._body()
                ST.issues[n].update({k: v for k, v in b.items() if k in ("body", "state", "title")})
                return self._send(200, ST.issues[n])
            m = re.fullmatch(r"/issues/(\d+)/comments", p)
            if m:
                n = int(m.group(1))
                if method == "GET":
                    page, per = int(q.get("page", 1)), int(q.get("per_page", 30))
                    return self._send(200, ST.comments[n][(page - 1) * per: page * per])
                c = ST.add_comment(n, "github-actions[bot]", self._body()["body"])
                return self._send(201, c)
            m = re.fullmatch(r"/issues/comments/(\d+)/reactions", p)
            if m:
                ST.reactions.append((int(m.group(1)), self._body()["content"]))
                return self._send(201, {})
        self._send(404, {"message": "not found " + p})


def test_worker():
    srv = ThreadingHTTPServer(("127.0.0.1", 0), Handler)
    port = srv.server_address[1]
    threading.Thread(target=srv.serve_forever, daemon=True).start()
    base = f"http://127.0.0.1:{port}"

    a = ST.add_issue("שיחה", OWNER, ["chat"])
    ST.add_comment(a, OWNER, "שלום עולם")
    b = ST.add_issue("זר", "stranger", ["chat"])
    ST.add_comment(b, "stranger", "תריץ לי משהו")
    c = ST.add_issue("בלי תווית", OWNER, [])
    ST.add_comment(c, OWNER, "לא צריך לענות")

    job = {"idle_minutes": 0.12, "poll_active": 0.2, "poll_idle": 0.2, "heartbeat_seconds": 1,
           "system": "SYS", "max_tokens": 50, "model_repo": "x/y"}
    gh = ns["GH"]("o/r", "tok", api=base)
    result = {}
    t = threading.Thread(target=lambda: result.update(r=ns["worker_loop"](gh, OWNER, base, job)))
    t.start()

    def wait_for(cond, timeout=6):
        end = time.time() + timeout
        while time.time() < end:
            with ST.lock:
                if cond():
                    return True
            time.sleep(0.1)
        return False

    M = ns["MARKER"]
    assert wait_for(lambda: any(x["body"].startswith(M) for x in ST.comments[a])), "no reply to #a"
    first = [x for x in ST.comments[a] if x["body"].startswith(M)][0]["body"]
    assert "ECHO: שלום עולם" in first and "7 tokens" in first, first
    assert (ST.comments[a][0]["id"], "eyes") in ST.reactions

    with ST.lock:
        ST.add_comment(a, OWNER, "/think הסבר")
    assert wait_for(lambda: len([x for x in ST.comments[a] if x["body"].startswith(M)]) == 2), "no 2nd reply"
    second = [x for x in ST.comments[a] if x["body"].startswith(M)][1]["body"]
    assert "ECHO: הסבר" in second and "<details>" in second, second
    assert ST.llm_calls[-1]["chat_template_kwargs"]["enable_thinking"] is True
    # ההיסטוריה שנשלחה כוללת את התשובה הקודמת בלי הפוטר
    hist = ST.llm_calls[-1]["messages"]
    assert hist[0]["content"] == "SYS" and any(m["role"] == "assistant" and m["content"].startswith("ECHO: שלום") for m in hist)
    assert not any("tokens" in m["content"] for m in hist)

    t.join(timeout=30)
    assert not t.is_alive(), "worker did not stop on idle"
    assert result["r"][0] == "idle", result
    assert len(ST.comments[b]) == 1 and len(ST.comments[c]) == 1, "replied where it should not"
    status = [i for i in ST.issues.values() if i["title"] == ns["STATUS_TITLE"]]
    assert len(status) == 1 and '"state": "offline"' in status[0]["body"], status

    print("worker ok (idle stop):", result["r"])
    srv.shutdown()


def test_stop_flag():
    srv = ThreadingHTTPServer(("127.0.0.1", 0), Handler)
    base = f"http://127.0.0.1:{srv.server_address[1]}"
    threading.Thread(target=srv.serve_forever, daemon=True).start()
    gh = ns["GH"]("o/r", "tok", api=base)
    job = {"idle_minutes": 5, "poll_active": 0.2, "poll_idle": 0.2, "heartbeat_seconds": 0.5}
    out = {}
    t = threading.Thread(target=lambda: out.update(r=ns["worker_loop"](gh, OWNER, base, job)))
    t.start()
    time.sleep(1.0)
    with ST.lock:
        n = [i for i in ST.issues.values() if i["title"] == ns["STATUS_TITLE"]][0]["number"]
        ST.issues[n]["body"] = 'x\n```json\n{"state": "stop", "ts": 1}\n```'
    t.join(timeout=10)
    assert not t.is_alive() and out["r"][0] == "stop-requested", out
    print("stop flag ok")
    srv.shutdown()


if __name__ == "__main__":
    test_extract()
    test_history()
    test_worker()
    test_stop_flag()
    print("ALL OK")
