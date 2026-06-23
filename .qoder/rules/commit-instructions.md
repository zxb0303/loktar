---
trigger: always_on
alwaysApply: true
---
# Git 提交信息规范

## 生成规则

- 使用中文生成 commit message
- subject 行必须准确、简洁地概括变更要点
- 响应内容仅包含 commit message 本身，不得有其他说明
- message 模板中必须包含空行分隔

## Commit Message 模板

```
<type>(<scope>): <subject>
// blank line
<body>
// blank line
<footer>
```

- Header（第一行）必填，Body 和 Footer 可选
- 无论哪部分，每行不得超过 72 字符（或 100 字符），避免自动换行影响美观

## Type 枚举

| type | 说明 |
|------|------|
| feat | 新功能 |
| fix | 修复 bug |
| docs | 文档变更 |
| style | 格式调整（不影响代码执行） |
| refactor | 重构（既非新功能也非 bug 修复） |
| test | 添加测试 |
| chore | 构建过程或辅助工具变更 |

## Body 格式

- 详细描述本次 commit，可分多行
- 每行约 72 字符宽度换行
- 可使用列表项（bullet points）
- 使用悬挂缩进（hanging indent）
