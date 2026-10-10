# Saber-SSM 剩余待办清单

> 基于 2026-10-10 代码实际状态核对
> 项目路径:`C:\Users\32524\Desktop\编程项目\Saber-SSMProject-parent`
>
> **项目当前没有前端**(纯后端 + Postman 验证)。因此:
> - 所有"前端联调/浏览器相关"的项都可跳过(CORS、HiddenHttpMethodFilter、静态资源白名单)
> - **RESTful 改造现在是零成本时机** —— 改 URL 不会破坏任何调用方

---

## ✅ 已完成

| 项 | 说明 |
|---|---|
| 双容器划分 | `RootConfig` 管 service/dao,`WebConfig` 管 controller —— 划分正确 |
| 全局异常处理 | `MyExceptionHandler`,401/403/500 三个 handler |
| 登录态 | `login` 写入 session,并 `setPassword(null)` |
| 权限拦截 | `LoginInterceptor`(未登录)+ `AdminInterceptor`(角色不足) |
| PageHelper 插件 | `RootConfig.sqlSessionFactory` 里已 `setPlugins` |
| 分页接口 | `/user/findPage` + `UserService.findPage` 返回 `PageInfo` |
| `User implements Serializable` | 会话序列化 |
| 带角色的查询 | `findUserWithRolesById` |
| 字符编码过滤器 | `CharacterEncodingFilter` + `forceEncoding(true)` |
| 无限递归风险 | 已排除,叶子方法都是光秃秃的 `@Select` |

---

## 🔴 P0 — 安全(建议立刻做)

### 1. `/user/findAll` 仍然把密码返回给前端
- **现状**:`UserDao.findAll` 查出的 `User` 带 `password` 字段,`@RestController` 直接序列化
- **证据**:`@JsonProperty(WRITE_ONLY)` / `@JsonIgnore` 全项目 grep 零结果
- **注意**:你在 `login` 里手动 `setPassword(null)` **只保护了 session,没有保护这个接口**
- **修法**:`User.password` 字段上加注解(能收注册入参,不外发)
```java
@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
private String password;
```

### 2. `Role` 没有 `implements Serializable`
- **现状**:只有 `User` 加了,但 `User` 里嵌套 `List<Role>`
- **后果**:Tomcat 重启持久化 session 时抛 `NotSerializableException: com.saber.pojo.Role`
- **修法**:`Role` 也加 `implements Serializable` + `serialVersionUID`
- **同理检查**:`Permission`(如果以后放进 session)

### 3. `@RequestMapping` 默认接受所有 HTTP 方法
- **现状**:全项目零个 `@GetMapping` / `@PostMapping`
- **后果**:`GET /user/register?username=x&password=y` 就能注册用户(爬虫/预取可能误触发)
- **修法**:见 P2 RESTful 部分

### 4. `AdminInterceptor` 里的角色名大小写
- **现状**:`role.getRoleName().equals("admin")`
- **坑**:Java 的 `String.equals()` **区分大小写**,而 MySQL 的字符串比较**不区分**
- **风险**:如果库里存的是 `ADMIN`,这个判断永远为 false → 所有人 403
- **修法**:先 `SELECT id, roleName FROM roles;` 确认,然后统一大小写或用 `equalsIgnoreCase`

### 5. `db.properties` 仍缺字符集参数
- **现状**:`jdbc:mysql://localhost:3306/exercise?serverTimezone=Asia/Shanghai`
- **风险**:库/表如果是 `latin1`(MySQL 5.7 默认),中文必乱
- **必做**:`SHOW CREATE DATABASE exercise;` 确认 charset
- **修法**:
```
jdbc:mysql://localhost:3306/exercise?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true
```

---

## 🟠 P1 — 正确性 / 性能

### 6. N+1 只加了个"平行接口",没真正修掉
- **现状**:
  - `/user/findAll` → `userDao.findAll()`(**带 roles 嵌套**)→ `1 + N` 条 SQL ❌
  - `/user/simplyFindAll` → `userDao.simplyFindAll()` → `1` 条 SQL ✅
  - **两个接口并存,主接口还是慢的**
- **Order / Role 同理**
- **建议**:与其并存,不如**换掉**。让 `findAll` 指向简单的那个,带嵌套的降级为 `findByIdWithXxx`(只给详情用)
- **验证**:`log4j.logger.com.saber.dao=DEBUG`,调一次接口数 SQL 条数

### 7. `AdminInterceptor` 的 `return false` 是死代码 + 隐形坑
```java
if(user != null) { ... }
return false;      // ← user == null 时走到这,但没写任何响应
```
- **`return false` 的契约是"响应我已经写好了"**,没写 → 客户端拿到 **HTTP 200 + 空 body**
- **目前是死代码**(`LoginInterceptor` 注册在前,未登录时先抛异常了),但这是顺序侥幸
- **修法**:改成 `throw new UserNoLoginException("你还未登录 请登录");`

### 8. 四个接口三种响应格式
| 接口 | 返回 |
|---|---|
| `UserController` 全部 | `Message` ✅ |
| `OrderController.findAll` | `List<Order>` ❌ 裸数据 |
| `RoleController.findAll` | `List<Role>` ❌ 裸数据 |
| `ProductController.save` | `void` ❌ 空 body |

- **后果**:前端要写 3 套解析逻辑;`ProductController.save` 前端**无法判断成功失败**
- **修法**:统一包 `Message`

### 9. 输入参数没有校验
- `pageNum` / `pageSize` 没有范围限制(传负数或 999999 会怎样?)
- `register` 的 `if(user != null)` 是**永远成立**的 —— Spring 对 POJO 参数总会 new 一个对象
- **建议**:`pageSize` 加上限(如 max 100),免得被人一次拉全表

---

## 🟡 P2 — RESTful 改造(你的第 1 项)

### 10. 注解改造
```java
@RequestMapping("/findAll")   →   @GetMapping
@RequestMapping("/register")  →   @PostMapping
@RequestMapping("/login")     →   @PostMapping
@RequestMapping("/save.do")   →   @PostMapping + 去掉 .do
```

### 11. 资源命名
| 现在 | RESTful |
|---|---|
| `GET /user/findAll` | `GET /users` |
| `POST /user/register` | `POST /users` |
| `POST /user/login` | `POST /sessions` |
| `GET /user/findPage` | `GET /users?page=1&size=10` |
| `GET /order/findAll` | `GET /orders` |
| `DELETE /order/{id}` | `DELETE /orders/{id}` |

### 12. 密码不能再走 URL
- **现状**:`login(String username, String password)` 是**查询参数** → 密码进 access log / 浏览器历史 / Referer
- **修法**:`@PostMapping` + `@RequestBody`(或至少表单 body),别用 URL 参数

### 13. 缺登出接口
```java
@DeleteMapping("/sessions")
public Message logout(HttpSession session) {
    session.invalidate();       // 比 removeAttribute 干净
    return new Message(200, "已退出", null);
}
```

### 14. 白名单建议改成注解式
- **现状**:`WebConfig.addInterceptors` 里逐个路径注册(10 行)
- **风险**:改 URL 时忘了改这里 → 接口突然要登录
- **修法**:自定义 `@Public` 注解,贴在 `login` / `register` 方法上

### 15. ~~如果前端跨端口:必须配 CORS~~ —— **无前端,跳过**(以后加前端时再回来做)
```java
@Override
public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
            .allowedOriginPatterns("*")
            .allowedMethods("GET","POST","PUT","DELETE","OPTIONS")
            .allowCredentials(true)      // ★ 要带 session cookie 必须开
            .maxAge(3600);
}
```
- 前端 axios 也要 `axios.defaults.withCredentials = true`
- 如果前端用 HTML 表单发 PUT/DELETE,还需要注册 `HiddenHttpMethodFilter`

---

## 🔵 P3 — 工程规范(面试/交作业加分)

### 16. pom 结构(和老师犯的同一个错)
- **现状**:父 pom 用 `<dependencies>` → **每个模块无条件继承所有依赖**
- **证据**:`Saber-SSMProject-pojo`(纯 POJO)里躺着 druid、pagehelper、mysql 驱动
- **修法**:改成 `<dependencyManagement>`,每个子模块显式声明自己需要的
- **收益**:依赖关系一眼可见,不传递多余的包。**这是 Maven 多模块的核心知识点**

### 17. 废弃 API
| 现在 | 应改为 |
|---|---|
| `extends WebMvcConfigurerAdapter`(WebConfig) | `implements WebMvcConfigurer` |
| `extends HandlerInterceptorAdapter`(两个拦截器) | `implements HandlerInterceptor` |
- Spring 5.0 起 `@Deprecated`,**Spring 6 已删除**

### 18. `web.xml` 是死文件
- 内容是空的 + **Servlet 2.3 DTD**(老师项目里 EL 被忽略的元凶)
- 你现在靠 `WebAppInitializer` 启动,这个文件毫无作用
- **修法**:**直接删掉**

### 19. 依赖版本混乱
| 依赖 | 现状 | 建议 |
|---|---|---|
| `spring-*` | 5.0.2 (2017) | 升到 **5.3.39**(5.x 最后一版,兼容 Java 8) |
| `aspectjweaver` | **1.6.8 (2010)** | 升到 1.9.x,Spring 5 需要 1.8+ |
| `log4j` | **1.2.12 (2007)** | 换 logback 或 log4j2,1.x 有已知 CVE |
| `slf4j` | 1.6.6 | 跟着升 |
| `jackson-databind` | 2.15.2 | 与 Spring 5.0.2 **不对齐**,升 Spring 后自然解决 |
| `jsp-api` / `jstl` | — | **你不用 JSP,删掉** |
| `oracle.version` 属性 | — | 死属性,删掉 |

### 20. 表名清理
| 问题 | 表 | 修法 |
|---|---|---|
| **保留字** | `order` | 改名 `orders`(每次都要写反引号) |
| **首字母大写** | `Member` | 改名 `member` —— **Linux 上 MySQL 表名区分大小写,部署必炸** |
| 风格不一 | `users` / `roles`(复数)vs `user_role` / `order_traveller`(单数) | 统一 |

### 21. 类型问题
| 问题 | 位置 | 修法 |
|---|---|---|
| 日期用 `String` | `Order.orderTime`、`Product.departureTime` | 改 `LocalDateTime` + `@JsonFormat`(你引了 jsr310 正好用上) |
| 基本类型不能装 NULL | `Order.orderStatus` / `peopleCount`、`Traveller.id` | 改 `Integer` |
| id 是 `String` | `User` / `Order` / `Product` / `Role` | 建议统一 `Integer`,和 `INT` 列对齐 |

### 22. 代码清理
- [ ] `OrderServiceImpl` 上的 `@Primary` —— 每个接口只有一个实现,它不起作用
- [ ] `Saber-SSMProject-utils` **是空模块**(零 java 文件,无模块依赖它)—— 删掉或放工具类
- [ ] 无用 import(7 处):`ProductDao`、`MemberDao`、`TravellerDao`、`UserServiceImpl`、`OrderServiceImpl`、`RootConfig`、`RoleController`
- [ ] 包名拼写:`com.saber.pojo.constant` → **`constant`**
- [ ] `LoginCode.LOGIN_FILED` → **`LOGIN_FAILED`**
- [ ] `Message` 的 code 混用三种编码(`200` / `1000+` / `2000+`)→ 统一一套

---

## ⚪ P4 — 补功能

- [ ] `MemberController` / `TravellerController`(DAO 有了,缺 Service + Controller)
- [ ] 订单详情接口(`OrderDao` 只有 findAll)
- [ ] 删除 / 修改接口(现在只有 findAll + save)
- [ ] `Permission` 相关(可选加分项:URL 级动态鉴权)

---

## 建议顺序

**第一批(半天,收益最大)**
1. P0-1 密码不外发(2 行注解)
2. P0-2 `Role implements Serializable`(2 行)
3. P1-6 换掉三个 `findAll` 的嵌套边(接口从 301 条 SQL → 1 条)
4. P0-5 `db.properties` 补字符集 + 确认建库 charset

**第二批(一天,功能完整度)**
5. P1-7 `AdminInterceptor` 的 `return false` 改成抛异常
6. P1-8 统一响应格式(4 个 Controller)
7. P2 RESTful 改造(注解 + 命名 + 密码进 body + 登出)
8. P0-4 确认角色名大小写

**第三批(收尾,专业度)**
9. P3-16 pom 改 `dependencyManagement`
10. P3-17 废弃 API 替换
11. P3-18 删 `web.xml`
12. P3-20 表名统一(部署到 Linux 前**必须**做)
13. P3-22 代码清理(import / 包名 / 拼写)

**最后(可选)**
14. P3-19 升级依赖版本
15. P4 补功能
16. Permission 表的 URL 级鉴权

---

## 每改一步的验证方法

| 改什么 | 怎么验 |
|---|---|
| 嵌套边 / 分页 | `log4j.logger.com.saber.dao=DEBUG`,数 SQL 条数 |
| 异常处理 | 故意触发(重复注册 / `1/0`),看是 JSON 还是 HTML |
| 拦截器 | 不登录 401 → 登录 → 非管理员 403 → 管理员 200 |
| 密码不外发 | `GET /user/findAll`,响应里不该有 `password` |
| 字符集 | 注册一个中文用户名,查库和看响应 |
