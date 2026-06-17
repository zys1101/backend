# 联系人管理系统后端

## 项目简介

基于 Spring Boot 3 + MyBatis Plus + MySQL 8 开发的联系人管理系统后端，提供完整的RESTful API。

## 技术栈

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Security** - 安全框架
- **JWT** - Token认证
- **MyBatis Plus 3.5.5** - ORM框架
- **MySQL 8.0+** - 数据库
- **Lombok** - 简化代码
- **SpringDoc OpenAPI** - API文档
- **Maven** - 项目管理

## 项目结构

```
src/main/java/com/contact
├── common                  # 通用模块
│   ├── constant           # 常量类
│   ├── exception          # 异常处理
│   ├── result             # 统一返回结果
│   └── utils              # 工具类
├── config                  # 配置类
│   ├── MybatisPlusConfig  # MyBatis Plus配置
│   ├── SecurityConfig     # Spring Security配置
│   ├── SwaggerConfig      # Swagger配置
│   └── WebConfig          # Web配置
├── controller              # 控制器层
│   ├── AuthController     # 认证控制器
│   ├── BlacklistController# 黑名单控制器
│   ├── ContactController  # 联系人控制器
│   └── ReminderController # 事项控制器
├── dto                     # 数据传输对象
├── entity                  # 实体类
├── mapper                  # 数据访问层
├── security                # 安全模块
│   ├── JwtAuthenticationEntryPoint
│   └── JwtAuthenticationFilter
├── service                 # 服务层
│   └── impl               # 服务实现
├── vo                      # 视图对象
└── ContactApplication      # 启动类
```

## 快速开始

### 1. 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+

### 2. 数据库配置

```bash
# 创建数据库并导入初始数据
mysql -u root -p < src/main/resources/db/init.sql
```

### 3. 修改配置文件

编辑 `src/main/resources/application.yml`，修改数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/contact_db?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: root
    password: 你的密码
```

### 4. 编译运行

```bash
# 编译项目
mvn clean package

# 运行项目
java -jar target/contact-manager.jar

# 或者使用Maven运行
mvn spring-boot:run
```

### 5. 访问接口文档

启动成功后访问：http://localhost:8080/api/swagger-ui.html

## API接口

### 用户认证模块

| 接口 | 方法 | 描述 | 认证 |
|------|------|------|------|
| /auth/login | POST | 用户登录 | 否 |
| /auth/logout | POST | 用户登出 | 是 |
| /auth/refresh | POST | 刷新Token | 是 |
| /auth/current | GET | 获取当前用户 | 是 |

### 联系人管理模块

| 接口 | 方法 | 描述 | 认证 |
|------|------|------|------|
| /contacts | GET | 获取联系人列表 | 是 |
| /contacts/{id} | GET | 获取联系人详情 | 是 |
| /contacts | POST | 新增联系人 | 是 |
| /contacts/{id} | PUT | 更新联系人 | 是 |
| /contacts/{id} | DELETE | 删除联系人 | 是 |
| /contacts/{id}/avatar | POST | 上传头像 | 是 |
| /contacts/{id}/blacklist | POST | 加入黑名单 | 是 |

### 黑名单管理模块

| 接口 | 方法 | 描述 | 认证 |
|------|------|------|------|
| /blacklist | GET | 获取黑名单列表 | 是 |
| /blacklist/{id} | DELETE | 恢复联系人 | 是 |

### 事项提醒模块

| 接口 | 方法 | 描述 | 认证 |
|------|------|------|------|
| /reminders | GET | 获取事项列表 | 是 |
| /reminders/{id} | GET | 获取事项详情 | 是 |
| /reminders | POST | 新增事项 | 是 |
| /reminders/{id} | PUT | 更新事项 | 是 |
| /reminders/{id} | DELETE | 删除事项 | 是 |
| /reminders/{id}/complete | PUT | 完成事项 | 是 |
| /reminders/{id}/cancel | PUT | 取消事项 | 是 |

## 测试账号

| 用户名 | 密码 | 说明 |
|--------|------|------|
| admin | 123456 | 管理员账号 |
| user01 | 123456 | 普通用户 |
| user02 | 123456 | 普通用户 |

## 接口认证

所有需要认证的接口都需要在请求头中携带JWT Token：

```http
Authorization: Bearer <your_token>
```

## 统一返回格式

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {},
  "timestamp": 1704067200000
}
```

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 操作成功 |
| 400001 | 参数校验失败 |
| 400002 | 资源已存在 |
| 400003 | 资源不存在 |
| 400004 | 业务规则违反 |
| 401001 | Token无效 |
| 401002 | Token缺失 |
| 403001 | 权限不足 |
| 500001 | 数据库错误 |
| 500002 | 文件上传错误 |

## 开发规范

- 遵循阿里巴巴Java开发规范
- 使用Lombok简化代码
- 所有接口返回统一格式
- 使用Hibernate Validator进行参数校验
- 使用Slf4j记录日志
- Service层处理业务逻辑
- Controller层负责请求转发

## 注意事项

1. 生产环境请修改JWT密钥
2. 生产环境请关闭Swagger文档
3. 生产环境请配置HTTPS
4. 注意SQL注入防护
5. 文件上传需要配置存储路径

## 更新日志

### v1.0.0 (2026-06-17)
- 完成基础功能开发
- 实现用户认证模块
- 实现联系人管理模块
- 实现黑名单管理模块
- 实现事项提醒模块
- 集成Swagger API文档
