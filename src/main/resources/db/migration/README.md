# Flyway 数据库迁移脚本

## 命名规范

迁移脚本必须遵循以下命名格式：

```
V{版本号}__{描述}.sql
```

- `V` - 固定前缀，表示 Version
- `{版本号}` - 数字，如 1, 2, 3 或 1.1, 1.2 等
- `__` - 两个下划线
- `{描述}` - 描述信息，用下划线分隔单词
- `.sql` - 文件扩展名

### 示例

- `V1__Initial_schema.sql` - 初始版本
- `V2__Add_user_table.sql` - 添加用户表
- `V3__Add_order_index.sql` - 添加订单索引
- `V1.1__Fix_user_column.sql` - 修复用户列

## 执行顺序

Flyway 按版本号顺序执行脚本，版本号越大越后执行。

## 注意事项

1. **脚本一旦执行，不可修改** - 需要修改时，创建新的迁移脚本
2. **脚本必须幂等** - 使用 `IF NOT EXISTS` 等判断，避免重复执行报错
3. **版本号必须唯一** - 不能有两个相同版本号的脚本
4. **不要跳版本** - 建议按顺序递增

## 常用命令

```bash
# Maven 运行 Flyway 迁移
mvn flyway:migrate

# 查看当前版本状态
mvn flyway:info

# 验证脚本（不执行）
mvn flyway:validate

# 回滚（需要 Flyway Teams 版）
mvn flyway:undo
```
