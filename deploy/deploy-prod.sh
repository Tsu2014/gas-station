#!/usr/bin/env bash
# =========================================================
# 生产部署脚本（149.88.70.82 / 宝塔面板）
#
# 功能：
#   1. 远程执行数据库增量迁移（sql/migration_reserve.sql）
#   2. 上传后端 jar 并重启服务
#   3. 上传管理后台 dist 静态文件
#   4. 健康检查
#
# 用法（凭证通过环境变量传入，切勿写入脚本）：
#   export PROD_DB_PASSWORD='数据库密码'
#   export PROD_SSH_PASSWORD='SSH 密码'        # 或 export PROD_SSH_KEY=~/.ssh/id_rsa
#   bash deploy/deploy-prod.sh
#
# 可选环境变量：
#   PROD_HOST        默认 149.88.70.82
#   PROD_SSH_USER    默认 root
#   PROD_DB_NAME     默认 alvin_gas
#   PROD_DB_USER     默认 alvin_gas
#   SKIP_DB=1        跳过数据库迁移
#   SKIP_JAR=1       跳过后端部署
#   SKIP_WEB=1       跳过前端部署
# =========================================================
set -euo pipefail

PROD_HOST="${PROD_HOST:-149.88.70.82}"
PROD_SSH_USER="${PROD_SSH_USER:-root}"
PROD_DB_NAME="${PROD_DB_NAME:-alvin_gas}"
PROD_DB_USER="${PROD_DB_USER:-alvin_gas}"

REMOTE_JAR_DIR="/www/wwwroot/gas-station"
REMOTE_WEB_DIR="/www/wwwroot/gas-station-admin"

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
JAR_FILE="$ROOT_DIR/server/target/gas-station-server.jar"
DIST_DIR="$ROOT_DIR/admin/dist"
SQL_FILE="$ROOT_DIR/sql/migration_reserve.sql"
STAMP="$(date +%Y%m%d%H%M%S)"

log() { echo -e "\033[1;32m[deploy]\033[0m $*"; }
err() { echo -e "\033[1;31m[deploy]\033[0m $*" >&2; }

# ---------- 前置检查 ----------
for f in "$JAR_FILE" "$DIST_DIR/index.html" "$SQL_FILE"; do
  [ -e "$f" ] || { err "缺少构建产物：$f（先执行 mvn package 与 npm run build）"; exit 1; }
done

SSH_OPTS=(-o StrictHostKeyChecking=accept-new -o ConnectTimeout=10)
if [ -n "${PROD_SSH_KEY:-}" ]; then
  SSH_OPTS+=(-i "$PROD_SSH_KEY")
  SSH_CMD=(ssh "${SSH_OPTS[@]}")
  SCP_CMD=(scp "${SSH_OPTS[@]}")
elif [ -n "${PROD_SSH_PASSWORD:-}" ]; then
  command -v sshpass >/dev/null || { err "需要 sshpass：sudo apt-get install -y sshpass"; exit 1; }
  SSH_CMD=(sshpass -p "$PROD_SSH_PASSWORD" ssh "${SSH_OPTS[@]}")
  SCP_CMD=(sshpass -p "$PROD_SSH_PASSWORD" scp "${SSH_OPTS[@]}")
else
  err "请设置 PROD_SSH_PASSWORD 或 PROD_SSH_KEY"
  exit 1
fi

# ---------- 1. 数据库迁移 ----------
if [ "${SKIP_DB:-0}" != "1" ]; then
  [ -n "${PROD_DB_PASSWORD:-}" ] || { err "请设置 PROD_DB_PASSWORD（或 SKIP_DB=1 跳过）"; exit 1; }
  log "1/4 远程执行数据库迁移 ${PROD_DB_NAME}@${PROD_HOST} ..."
  mysql -h "$PROD_HOST" --protocol=tcp -u"$PROD_DB_USER" -p"$PROD_DB_PASSWORD" "$PROD_DB_NAME" < "$SQL_FILE"
  log "数据库迁移完成"
else
  log "1/4 跳过数据库迁移"
fi

# ---------- 2. 后端 jar ----------
if [ "${SKIP_JAR:-0}" != "1" ]; then
  log "2/4 上传后端 jar（备份原 jar 为 gas-station-server.jar.bak-$STAMP）..."
  "${SSH_CMD[@]}" "$PROD_SSH_USER@$PROD_HOST" \
    "cp -f $REMOTE_JAR_DIR/gas-station-server.jar $REMOTE_JAR_DIR/gas-station-server.jar.bak-$STAMP 2>/dev/null || true"
  "${SCP_CMD[@]}" "$JAR_FILE" "$PROD_SSH_USER@$PROD_HOST:$REMOTE_JAR_DIR/gas-station-server.jar.new"
  "${SSH_CMD[@]}" "$PROD_SSH_USER@$PROD_HOST" \
    "mv $REMOTE_JAR_DIR/gas-station-server.jar.new $REMOTE_JAR_DIR/gas-station-server.jar"

  log "重启后端服务..."
  if "${SSH_CMD[@]}" "$PROD_SSH_USER@$PROD_HOST" "systemctl list-unit-files | grep -q '^gas-station-server'"; then
    "${SSH_CMD[@]}" "$PROD_SSH_USER@$PROD_HOST" "systemctl restart gas-station-server"
  elif "${SSH_CMD[@]}" "$PROD_SSH_USER@$PROD_HOST" "systemctl list-unit-files | grep -q '^gas-station\.'"; then
    "${SSH_CMD[@]}" "$PROD_SSH_USER@$PROD_HOST" "systemctl restart gas-station"
  else
    err "未找到 systemd 服务，请在宝塔面板的 Java 项目「gas-station-server」中手动重启"
  fi
else
  log "2/4 跳过后端部署"
fi

# ---------- 3. 前端 dist ----------
if [ "${SKIP_WEB:-0}" != "1" ]; then
  log "3/4 上传管理后台静态文件（备份原目录为 gas-station-admin.bak-$STAMP）..."
  "${SSH_CMD[@]}" "$PROD_SSH_USER@$PROD_HOST" \
    "cp -r $REMOTE_WEB_DIR $REMOTE_WEB_DIR.bak-$STAMP 2>/dev/null || true"
  "${SCP_CMD[@]}" -r "$DIST_DIR/." "$PROD_SSH_USER@$PROD_HOST:$REMOTE_WEB_DIR/"
else
  log "3/4 跳过前端部署"
fi

# ---------- 4. 健康检查 ----------
log "4/4 健康检查..."
sleep 8
curl -sf -m 15 "https://gas.tsu2026.top/api/public/station/nearby" -o /dev/null \
  && log "公共接口 OK" || { err "公共接口异常，请检查后端日志"; exit 1; }
BOARD_CODE="$(curl -s -m 15 -o /dev/null -w '%{http_code}' "https://gas.tsu2026.top/api/admin/reserve/board")"
log "预约看板接口 HTTP $BOARD_CODE（401 = 端点已存在，待登录）"

log "部署完成 ✅ https://gas.tsu2026.top"
