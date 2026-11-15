# pocketlife 前端

pocketlife 是记录和分享日常的社区应用。

从此目录执行：

```powershell
npm ci
npm run dev
```

默认打开 http://127.0.0.1:5173，API 通过同源的 `/api` 转发到本地网关 `http://127.0.0.1:8000`。可用 `POCKETLIFE_GATEWAY_URL` 覆盖代理目标。

`npm run build` 生成 `dist/`，`npm run preview` 在本地预览构建结果。部署时让 Web 服务器将 `/api/*` 转发给网关并去掉 `/api` 前缀，其余应用路径回退到 `index.html`。
