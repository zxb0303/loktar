---
trigger: always_on
alwaysApply: true
---
# 权限与项目规范

## 敏感文件访问限制

Agent 不得打开、读取、打印、总结或以任何方式提取以下文件的内容：

- `.env`
- `.env.*`
- `**/application-dev.yml`
- `**/application-test.yml`
- `**/mybatis-generator.properties`
- 任何包含私钥、证书、API Token、Access Key、Secret Key、数据库密码等敏感信息的文件
- 常见敏感文件，例如：
    - `*.pem`
    - `*.key`
    - `*.crt`
    - `*.p12`
    - `*.jks`
    - `id_rsa`
    - `id_dsa`
如果任务确实需要查看或修改上述文件，必须先向用户请求明确授权。
## 禁止修改的文件
除非用户明确要求，Agent 不得修改以下文件：
- 锁文件，例如：
    - `package-lock.json`
    - `yarn.lock`
    - `pnpm-lock.yaml`
    - `poetry.lock`
    - `Pipfile.lock`
    - `Cargo.lock`
- `.gitignore`
## 其他要求
- 不要在日志、提交信息、代码注释或回复中暴露任何密钥、Token、密码或证书内容。
- 如果意外读取到敏感信息，不要复述内容，应立即停止相关操作并提示用户。