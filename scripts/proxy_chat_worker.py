#!/usr/bin/env python3
"""CPU-only GitHub Issues chat worker backed by Kaggle Model Proxy."""
import json
import os
import re
import subprocess
import sys
import time
from pathlib import Path

import requests

API = "https://api.github.com"
REPO = os.environ["GITHUB_REPOSITORY"]
OWNER = REPO.split("/", 1)[0]
GH_TOKEN = os.environ["GH_TOKEN"]
KAGGLE_ENV = Path(os.environ.get("RUNNER_TEMP", "/tmp")) / "kaggle-model-proxy.env"
STATUS_TITLE = "[proxy-status]"
CHAT_LABEL = "proxy-chat"
BOT = "github-actions[bot]"
REPLY = "<!--llm-reply-->"
STREAM = "<!--llm-streaming-->"
STOP = "<!--llm-stop:"
SYSTEM = os.environ.get("PROXY_SYSTEM", "אתה עוזר מועיל. ענה בעברית ברורה.")
IDLE_MINUTES = max(1, int(os.environ.get("IDLE_MINUTES", "30")))
MAX_TOKENS = min(8192, max(256, int(os.environ.get("MAX_TOKENS", "4096"))))
REQUEST_TIMEOUT = 180
gh = requests.Session()
gh.headers.update({"Authorization": f"Bearer {GH_TOKEN}", "Accept": "application/vnd.github+json",
                   "X-GitHub-Api-Version": "2022-11-28"})
proxy = requests.Session()
proxy.headers.update({"Content-Type": "application/json"})


def gh_call(method, path, **kwargs):
    r = gh.request(method, API + path, timeout=30, **kwargs)
    if r.status_code >= 400:
        raise RuntimeError(f"GitHub API {r.status_code}: {r.text[:1200]}")
    return r.json() if r.content else None


def parse_env(path):
    result = {}
    for line in path.read_text(encoding="utf-8").splitlines():
        line = line.strip()
        if not line or line.startswith("#") or "=" not in line:
            continue
        k, v = line.split("=", 1)
        result[k.strip()] = v.strip().strip("'").strip('"')
    return result


def run_auth(initial=False):
    cmd = ["kaggle", "benchmarks", "init", "-y", "--env-file", str(KAGGLE_ENV),
           "--example-file", str(KAGGLE_ENV.parent / "proxy_example_task.py")] if initial else [
           "kaggle", "benchmarks", "auth", "-y", "--env-file", str(KAGGLE_ENV)]
    p = subprocess.run(cmd, text=True, capture_output=True, timeout=180)
    if p.returncode:
        raise RuntimeError("Kaggle Model Proxy authentication failed: " + (p.stderr or p.stdout)[-1800:])
    env = parse_env(KAGGLE_ENV)
    for k in ("MODEL_PROXY_URL", "MODEL_PROXY_API_KEY", "MODEL_PROXY_EXPIRY_TIME"):
        if not env.get(k):
            raise RuntimeError(f"Kaggle auth did not return {k}")
    model = os.environ.get("MODEL") or env.get("LLM_DEFAULT")
    if not model:
        models = [x.strip() for x in env.get("LLMS_AVAILABLE", "").split(",") if x.strip()]
        model = models[0] if models else "google/gemini-2.5-flash"
    env["MODEL"] = model
    return env


def proxy_headers(env):
    return {"Authorization": "Bearer " + env["MODEL_PROXY_API_KEY"],
            "Content-Type": "application/json"}


def proxy_url(env):
    # Kaggle's Model Proxy URL is the root; its OpenAI-compatible surface is /openapi/v1.
    return env["MODEL_PROXY_URL"].rstrip("/") + "/openapi/v1/chat/completions"


def ensure_chat_label():
    labels = gh_call("GET", f"/repos/{REPO}/labels?per_page=100")
    if not any(x.get("name") == CHAT_LABEL for x in labels):
        gh_call("POST", f"/repos/{REPO}/labels",
                json={"name": CHAT_LABEL, "color": "6f42c1",
                      "description": "Messages handled by the Kaggle Model Proxy CPU worker"})


def status_issue():
    issues = gh_call("GET", f"/repos/{REPO}/issues?state=open&per_page=100")
    for item in issues:
        if not item.get("pull_request") and item.get("title") == STATUS_TITLE:
            return item
    return gh_call("POST", f"/repos/{REPO}/issues",
                   json={"title": STATUS_TITLE, "body": "Starting Kaggle Model Proxy worker"})


def set_status(issue_no, state, model="", detail=""):
    body = json.dumps({"state": state, "ts": int(time.time()), "model": model,
                       "detail": detail[:300]}, ensure_ascii=False)
    gh_call("PATCH", f"/repos/{REPO}/issues/{issue_no}", json={"body": body})


def list_chat_issues():
    return gh_call("GET", f"/repos/{REPO}/issues?state=open&labels={CHAT_LABEL}&per_page=100&sort=updated&direction=asc")


def get_comments(issue_no):
    return gh_call("GET", f"/repos/{REPO}/issues/{issue_no}/comments?per_page=100")


def comment(issue_no, body):
    return gh_call("POST", f"/repos/{REPO}/issues/{issue_no}/comments", json={"body": body})


def edit_comment(issue_no, comment_id, body):
    return gh_call("PATCH", f"/repos/{REPO}/issues/comments/{comment_id}", json={"body": body})


def is_owner_comment(c):
    return (c.get("user") or {}).get("login", "").lower() == OWNER.lower()


def pending_comment(comments):
    """Return the earliest unanswered owner comment, ignoring non-owner comments."""
    pending = None
    for c in comments:
        body = c.get("body") or ""
        if is_owner_comment(c) and not body.startswith(STOP):
            pending = c
        elif body.startswith(REPLY) and (c.get("user") or {}).get("login", "") == BOT:
            pending = None
    return pending


def build_messages(comments, target, system):
    messages = [{"role": "system", "content": system}]
    for c in comments:
        body = c.get("body") or ""
        if c.get("id") == target["id"]:
            user_text = body
            if user_text.startswith("/think\n"):
                user_text = user_text[len("/think\n"):]
                messages[0]["content"] += "\nענה לאחר חשיבה יסודית, אך הצג רק את התשובה הסופית."
            if user_text.startswith("/model "):
                user_text = user_text.split("\n", 1)[-1] if "\n" in user_text else ""
            if user_text.strip():
                messages.append({"role": "user", "content": user_text.strip()})
            break
        if is_owner_comment(c):
            if body.startswith("/think\n"):
                body = body[len("/think\n"):]
                messages[0]["content"] += "\nענה לאחר חשיבה יסודית, אך הצג רק את התשובה הסופית."
            if body.startswith("/model "):
                body = body.split("\n", 1)[-1] if "\n" in body else ""
            if body.strip():
                messages.append({"role": "user", "content": body.strip()})
        elif body.startswith(REPLY) and (c.get("user") or {}).get("login", "") == BOT:
            text = body[len(REPLY):].strip()
            text = re.sub(r"\n*<sub>[\s\S]*?</sub>\s*$", "", text)
            text = re.sub(r"<details><summary>[\s\S]*?</summary>[\s\S]*?</details>", "", text).strip()
            if text:
                messages.append({"role": "assistant", "content": text})
    # Keep the system message and a bounded recent context.
    tail = messages[1:][-40:]
    while sum(len(x["content"]) for x in tail) > 60000 and len(tail) > 2:
        tail.pop(0)
    return [messages[0]] + tail


def requested_model(body, default):
    m = re.match(r"^/model\s+([^\s]+)", body or "")
    return m.group(1) if m else default


def generate(env, model, messages, issue_no):
    url = proxy_url(env)
    headers = proxy_headers(env)
    payload = {"model": model, "messages": messages, "max_tokens": MAX_TOKENS, "stream": True}
    r = proxy.post(url, headers=headers, json=payload, stream=True, timeout=(20, REQUEST_TIMEOUT))
    if r.status_code >= 400:
        detail = r.text[:1800]
        r.close()
        raise RuntimeError(f"Kaggle Model Proxy HTTP {r.status_code}: {detail}")
    answer = ""
    last_edit = time.monotonic()
    reply = comment(issue_no, STREAM)
    reply_id = reply["id"]
    try:
        for raw in r.iter_lines():
            if isinstance(raw, bytes):
                raw = raw.decode("utf-8", "replace")
            if not raw or not raw.startswith("data:"):
                continue
            data = raw[5:].strip()
            if data == "[DONE]":
                break
            try:
                obj = json.loads(data)
            except json.JSONDecodeError:
                continue
            choices = obj.get("choices") or []
            if not choices:
                continue
            delta = choices[0].get("delta") or {}
            piece = delta.get("content")
            if isinstance(piece, str) and piece:
                answer += piece
                if time.monotonic() - last_edit >= 0.8 or len(answer) < 100:
                    edit_comment(issue_no, reply_id, STREAM + answer[-60000:])
                    last_edit = time.monotonic()
        if not answer.strip():
            raise RuntimeError("Kaggle Model Proxy החזיר תשובה ריקה.")
        edit_comment(issue_no, reply_id, REPLY + answer[-60000:])
    finally:
        r.close()


def process_issue(issue, env):
    comments = get_comments(issue["number"])
    target = pending_comment(comments)
    if not target:
        return False
    body = target.get("body") or ""
    model = requested_model(body, env["MODEL"])
    messages = build_messages(comments, target, SYSTEM)
    try:
        generate(env, model, messages, issue["number"])
    except Exception as e:
        # Keep the same marker the UI recognizes so the error appears as an assistant reply.
        comment(issue["number"], REPLY + "⚠️ שגיאה בקריאה ל-Kaggle Model Proxy:\n\n" + str(e)[:3500])
    return True


def main():
    print("Authenticating to Kaggle Model Proxy…", flush=True)
    ensure_chat_label()
    env = run_auth(initial=True)
    model = env["MODEL"]
    status = status_issue()
    status_no = status["number"]
    set_status(status_no, "online", model, "CPU runner + Kaggle Model Proxy")
    print(f"Proxy worker online; model={model}", flush=True)
    started = time.monotonic()
    last_activity = started
    last_heartbeat = 0
    last_auth = started
    try:
        while time.monotonic() - started < 345 * 60:
            # Proxy credentials are short-lived; renew before the expected expiry.
            if time.monotonic() - last_auth > 45 * 60:
                env = run_auth(initial=False)
                model = env["MODEL"]
                last_auth = time.monotonic()
                print("Refreshed Kaggle Model Proxy credentials.", flush=True)
            if time.monotonic() - last_heartbeat > 20:
                current = gh_call("GET", f"/repos/{REPO}/issues/{status_no}")
                state_match = re.search(r'"state"\s*:\s*"([^"]+)"', current.get("body") or "")
                if state_match and state_match.group(1) == "stop":
                    print("Stop requested.", flush=True)
                    break
                set_status(status_no, "online", model, "CPU runner + Kaggle Model Proxy")
                last_heartbeat = time.monotonic()
            did_work = False
            for issue in list_chat_issues():
                if issue.get("pull_request") or (issue.get("user") or {}).get("login", "").lower() != OWNER.lower():
                    continue
                if process_issue(issue, env):
                    did_work = True
                    last_activity = time.monotonic()
            if time.monotonic() - last_activity > IDLE_MINUTES * 60:
                print("Idle timeout reached.", flush=True)
                break
            time.sleep(3 if did_work else 5)
    finally:
        try:
            set_status(status_no, "offline", model, "Worker stopped")
        except Exception as e:
            print("Could not update offline status:", e, flush=True)
    print("Proxy worker stopped.", flush=True)


if __name__ == "__main__":
    try:
        main()
    except Exception as exc:
        print(f"FATAL: {exc}", file=sys.stderr, flush=True)
        raise
