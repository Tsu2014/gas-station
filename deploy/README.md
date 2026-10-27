# 部署说明（已有 MySQL / JDK / Nginx 的环境）

目标：后端 jar + 前端 dist 直接部署，Nginx 同时承载后台页面与 `/api` 接口反向代理。

## 1. 目录约定

| 路径 | 内容 |
| --- | --- |
| `/opt/gas-station/` | 后端 jar、日志 |
| `/var/www/gas-station-admin/` | 前端 `admin/dist` 构建产物 |
| `/etc/nginx/conf.d/gas-station.conf` | Nginx 站点配置 |
| `/etc/systemd/system/gas-station.service` | 后端服务 |

## 2. 数据库

```bash
mysql -uroot -p -e "CREATE DATABASE IF NOT EXISTS gas_station DEFAULT CHARACTER SET utf8mb4;"
mysql -uroot -p gas_station < sql/gas_station.sql
```

## 3. 后端

```bash
mkdir -p /opt/gas-station/logs
# 上传 server/target/gas-station-server.jar 到 /opt/gas-station/
cp deploy/systemd/gas-station.service /etc/systemd/system/
systemctl daemon-reload
systemctl enable --now gas-station
systemctl status gas-station
```

环境变量（在 service 文件中修改）：`DB_HOST` `DB_PORT` `DB_NAME` `DB_USER` `DB_PASSWORD` `JWT_SECRET` `WX_APPID` `WX_SECRET`。

健康检查：`curl http://127.0.0.1:8080/api/public/station/nearby`

## 4. 前端

```bash
cd admin && npm run build:prod     # 产出 admin/dist
mkdir -p /var/www/gas-station-admin
cp -r dist/* /var/www/gas-station-admin/
```

## 5. Nginx

```bash
cp deploy/nginx/gas-station.conf /etc/nginx/conf.d/gas-station.conf
# 修改 server_name 为你的域名
nginx -t && systemctl reload nginx
```

## 6. HTTPS（小程序必须）

```bash
certbot --nginx -d your.domain.com     # 申请证书并自动改写配置
```

小程序 `miniprogram/config.js` 的 `baseUrl` 改为 `https://your.domain.com`。

## 7. 常用运维

```bash
systemctl restart gas-station
journalctl -u gas-station -f
tail -f /opt/gas-station/logs/gas-station-server.log
```
