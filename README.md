# pocketlife

基于 Java 17、Spring Boot 3 和 Spring Cloud Alibaba 的微服务后端。本地网关地址为 `http://127.0.0.1:8000`，搜索服务端口为 `8092`，XXL-JOB 控制台端口为 `7777`。

## Windows 本地启动

先启动 Docker Desktop 和 Nacos。以下命令使用预先创建的容器名称，在项目根目录运行。

```powershell
docker start mysql8.0 redis minio zookeeper cassandra rocketmq-namesrv
docker start rocketmq-broker pocketlife-elasticsearch pocketlife-xxl-job
mvn package -DskipTests
.\dev\start-local.ps1
```

修改代码后，先运行 `.\dev\start-local.ps1 -Stop`，再打包并启动。服务日志默认写入 Git 忽略的 `.pocketlife/`，也可用 `POCKETLIFE_RUN_DIR` 指定项目外的目录。

启动脚本默认从本地容器读取 MySQL 和 MinIO 的凭据。也可通过 `POCKETLIFE_MYSQL_USER` 和 `POCKETLIFE_MYSQL_PASSWORD` 配置 MySQL。启用 Redis 认证时设置 `SPRING_DATA_REDIS_PASSWORD`。XXL-JOB 启用令牌校验时，设置与调度中心一致的 `POCKETLIFE_XXL_JOB_TOKEN`。

需要短信功能时，设置 `POCKETLIFE_SMS_ENABLED=true`、`POCKETLIFE_SMS_ACCESS_KEY_ID` 和 `POCKETLIFE_SMS_ACCESS_KEY_SECRET`。阿里云 OSS 模式使用 `POCKETLIFE_OSS_ACCESS_KEY` 和 `POCKETLIFE_OSS_SECRET_KEY`。凭据通过本地环境变量提供。

启动前需准备 MySQL 的 `pocketlife`、`leaf` 基础表和 Cassandra 的 `pocketlife` 键空间与笔记正文表。随后应用项目附带的补充迁移 [MySQL 补表脚本](dev/mysql-additional-tables.sql) 和 [Cassandra 补表脚本](dev/cassandra-additional-tables.cql)。笔记、评论、用户数据及上传媒体由本地数据库和对象存储管理。

XXL-JOB 的执行器名为 `xxl-job-executor-pocketlife`。日增量分片表由 `createTableJobHandler` 创建，空参数建次日表，也可传入 `yyyyMMdd` 指定日期。

## 搜索和索引

本地开发环境默认关闭 Canal 实时同步。修改用户或笔记后，分别向 `POST /search/user/document/rebuild` 或 `POST /search/note/document/rebuild` 发送 JSON `{"id": 123}` 重建指定文档。查询使用 `POST /search/user` 或 `POST /search/note`，请求字段为 `keyword` 和 `pageNo`。首次创建笔记索引时使用 [笔记索引映射](dev/search-note-index.json)。

## 前端

首次升级笔记模块时，在 `pocketlife` 数据库执行 [笔记频道迁移](dev/mysql-note-feed.sql)、[笔记标签迁移](dev/mysql-note-tags.sql) 和 [发现页频道初始化](dev/mysql-discover-channels.sql)。

启动后端后，从前端目录运行：

```powershell
cd pocketlife-vue3
npm ci
npm run dev
```

应用地址为 `http://127.0.0.1:5173`。更多配置见 [前端说明](pocketlife-vue3/README.md)。
