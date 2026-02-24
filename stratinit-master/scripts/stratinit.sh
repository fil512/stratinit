#!/usr/bin/env bash
set -euo pipefail

BASE_DIR="$(cd "$(dirname "$0")/.." && pwd)"

# REST server (Spring Boot on port 8081)
REST_PID="/tmp/stratinit-rest.pid"
REST_LOG="/tmp/stratinit-rest.log"

# UI dev server (Vite on port 5173)
UI_DIR="$BASE_DIR/stratinit-ui"
UI_PID="/tmp/stratinit-ui.pid"
UI_LOG="/tmp/stratinit-ui.log"

get_pid() {
    local pidfile="$1"
    if [ -f "$pidfile" ]; then
        local pid
        pid=$(cat "$pidfile")
        if kill -0 "$pid" 2>/dev/null; then
            echo "$pid"
            return 0
        fi
        rm -f "$pidfile"
    fi
    return 1
}

stop_process() {
    local name="$1" pidfile="$2" timeout="${3:-30}"
    if ! pid=$(get_pid "$pidfile"); then
        echo "$name is not running"
        return 0
    fi
    echo "Stopping $name (PID $pid)..."
    kill "$pid"
    for _ in $(seq 1 "$timeout"); do
        if ! kill -0 "$pid" 2>/dev/null; then
            rm -f "$pidfile"
            echo "$name stopped"
            return 0
        fi
        sleep 1
    done
    echo "Graceful shutdown timed out, forcing..."
    kill -9 "$pid" 2>/dev/null || true
    rm -f "$pidfile"
    echo "$name killed"
}

start_rest() {
    if pid=$(get_pid "$REST_PID"); then
        echo "REST server is already running (PID $pid)"
        return 1
    fi
    echo "Starting REST server..."
    nohup mvn spring-boot:run -pl stratinit-rest -f "$BASE_DIR/pom.xml" >> "$REST_LOG" 2>&1 &
    echo $! > "$REST_PID"
    echo "REST server started (PID $!), logging to $REST_LOG"
}

start_ui() {
    if pid=$(get_pid "$UI_PID"); then
        echo "UI dev server is already running (PID $pid)"
        return 1
    fi
    if [ ! -d "$UI_DIR/node_modules" ]; then
        echo "Installing UI dependencies..."
        (cd "$UI_DIR" && npm install)
    fi
    echo "Starting UI dev server..."
    (cd "$UI_DIR" && nohup npm run dev >> "$UI_LOG" 2>&1 &
    echo $! > "$UI_PID")
    echo "UI dev server started, logging to $UI_LOG"
}

get_rest_version() {
    local version
    version=$(curl -sf --max-time 2 http://localhost:8081/stratinit/version 2>/dev/null) && echo "$version" || echo ""
}

get_ui_version() {
    local version
    version=$(curl -sf --max-time 2 http://localhost:5173/ 2>/dev/null | grep -oP 'data-version="\K[^"]+' 2>/dev/null) && echo "$version" || echo ""
}

status_all() {
    local rc=0
    if pid=$(get_pid "$REST_PID"); then
        local version
        version=$(get_rest_version)
        if [ -n "$version" ]; then
            echo "REST server is running (PID $pid) — $version"
        else
            echo "REST server is running (PID $pid) — version unavailable (still starting?)"
        fi
    else
        echo "REST server is not running"
        rc=1
    fi
    if pid=$(get_pid "$UI_PID"); then
        echo "UI dev server is running (PID $pid)"
    else
        echo "UI dev server is not running"
        rc=1
    fi
    return $rc
}

do_start() {
    start_rest
    start_ui
}

do_stop() {
    stop_process "UI dev server" "$UI_PID" 5
    stop_process "REST server" "$REST_PID" 30
}

do_restart() {
    do_stop
    do_start
}

case "${1:-}" in
    start)   do_start   ;;
    stop)    do_stop    ;;
    restart) do_restart ;;
    status)  status_all ;;
    *)
        echo "Usage: $0 {start|stop|restart|status}"
        exit 1
        ;;
esac
