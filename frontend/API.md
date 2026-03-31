# 兴趣社交圈子 — API 接口文档

**Base URL:** `http://124.223.89.124:29511`

**鉴权方式:** JWT Token，需在请求头中携带 `Authorization: Bearer <token>`

**统一响应格式：**

```json
{
  "code": 200,
  "message": "success",
  "data": "<具体数据>"
}
```

---

## 目录

1. [认证模块](#1-认证模块)
2. [用户模块](#2-用户模块)
3. [帖子模块](#3-帖子模块)
4. [评论模块](#4-评论模块)
5. [圈子模块](#5-圈子模块)
6. [关注模块](#6-关注模块)
7. [文件上传模块](#7-文件上传模块)
8. [通知模块](#8-通知模块)
9. [搜索模块](#9-搜索模块)
10. [统计模块](#10-统计模块)
11. [管理后台模块](#11-管理后台模块)
12. [即时通讯模块](#12-即时通讯模块)
13. [数据模型](#13-数据模型)

---

## 1. 认证模块

### POST `/api/auth/register` — 用户注册

| 项目 | 说明 |
|------|------|
| 鉴权 | 否 |
| Content-Type | application/json |

**请求体：**

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| username | String | 是 | 用户名 |
| email | String | 是 | 邮箱 |
| phone | String | 否 | 手机号 |
| password | String | 是 | 密码 |

**响应 data：**

```json
{
  "token": "eyJhbGciOi...",
  "user": { /* UserDTO */ }
}
```

---

### POST `/api/auth/login` — 用户登录

| 项目 | 说明 |
|------|------|
| 鉴权 | 否 |
| Content-Type | application/json |

**请求体：**

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| email | String | 是 | 邮箱 |
| password | String | 是 | 密码 |

**响应 data：**

```json
{
  "token": "eyJhbGciOi...",
  "user": { /* UserDTO */ }
}
```

---

## 2. 用户模块

### GET `/api/users/me` — 获取当前登录用户信息

| 项目 | 说明 |
|------|------|
| 鉴权 | 是 |

**响应 data：** `UserDTO`

---

### PUT `/api/users/me` — 更新当前用户资料

| 项目 | 说明 |
|------|------|
| 鉴权 | 是 |
| Content-Type | application/json |

**请求体：**

| 字段 | 类型 | 说明 |
|------|------|------|
| username | String | 用户名 |
| bio | String | 个人简介 |
| avatar | String | 头像 URL |

**响应 data：** `UserDTO`

---

### GET `/api/users/{id}` — 获取指定用户信息

| 项目 | 说明 |
|------|------|
| 鉴权 | 否 |
| 路径参数 | `id` — 用户 ID (Long) |

**响应 data：** `UserDTO`

---

## 3. 帖子模块

### POST `/api/posts` — 发布帖子

| 项目 | 说明 |
|------|------|
| 鉴权 | 是 |
| Content-Type | application/json |

**请求体：**

| 字段 | 类型 | 说明 |
|------|------|------|
| title | String | 标题 |
| content | String | 正文内容（富文本 HTML） |
| images | List\<String\> | 图片 URL 列表 |
| circleId | Long | 所属圈子 ID |

**响应 data：** `PostDTO`

---

### GET `/api/posts` — 帖子列表（分页）

| 项目 | 说明 |
|------|------|
| 鉴权 | 否 |

**查询参数：**

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| page | int | 0 | 页码（从 0 开始） |
| size | int | 10 | 每页数量 |

**响应 data：**

```json
{
  "content": [ /* PostDTO[] */ ],
  "totalElements": 100,
  "totalPages": 10,
  "currentPage": 0,
  "size": 10
}
```

---

### GET `/api/posts/{id}` — 帖子详情

| 项目 | 说明 |
|------|------|
| 鉴权 | 否 |
| 路径参数 | `id` — 帖子 ID (Long) |

**响应 data：** `PostDTO`

---

### DELETE `/api/posts/{id}` — 删除帖子

| 项目 | 说明 |
|------|------|
| 鉴权 | 是（仅作者可删除） |
| 路径参数 | `id` — 帖子 ID (Long) |

**响应 data：** 无

---

### POST `/api/posts/{id}/like` — 点赞 / 取消点赞

| 项目 | 说明 |
|------|------|
| 鉴权 | 是 |
| 路径参数 | `id` — 帖子 ID (Long) |

**响应 data：** `PostDTO`（包含更新后的 liked 状态和 likeCount）

---

### POST `/api/posts/{id}/favorite` — 收藏 / 取消收藏

| 项目 | 说明 |
|------|------|
| 鉴权 | 是 |
| 路径参数 | `id` — 帖子 ID (Long) |

**响应 data：** `PostDTO`（包含更新后的 favorited 状态和 favoriteCount）

---

### GET `/api/posts/user/{userId}` — 获取指定用户的帖子

| 项目 | 说明 |
|------|------|
| 鉴权 | 否 |
| 路径参数 | `userId` — 用户 ID (Long) |

**查询参数：**

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| page | int | 0 | 页码 |
| size | int | 10 | 每页数量 |

**响应 data：** 分页结构，同帖子列表

---

### GET `/api/posts/feed` — 关注动态（Feed 流）

| 项目 | 说明 |
|------|------|
| 鉴权 | 是 |

**查询参数：**

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| page | int | 0 | 页码 |
| size | int | 10 | 每页数量 |

**响应 data：** 分页结构，同帖子列表（返回关注用户和自己的帖子）

---

### GET `/api/posts/favorites` — 我的收藏

| 项目 | 说明 |
|------|------|
| 鉴权 | 是 |

**查询参数：**

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| page | int | 0 | 页码 |
| size | int | 10 | 每页数量 |

**响应 data：** 分页结构，同帖子列表

---

## 4. 评论模块

### GET `/api/posts/{postId}/comments` — 获取帖子评论列表

| 项目 | 说明 |
|------|------|
| 鉴权 | 否 |
| 路径参数 | `postId` — 帖子 ID (Long) |

**响应 data：** `List<CommentDTO>`（树形结构，含嵌套 replies）

---

### POST `/api/posts/{postId}/comments` — 发表评论

| 项目 | 说明 |
|------|------|
| 鉴权 | 是 |
| 路径参数 | `postId` — 帖子 ID (Long) |
| Content-Type | application/json |

**请求体：**

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| content | String | 是 | 评论内容 |
| parentId | Long | 否 | 父评论 ID（用于回复） |

**响应 data：** `CommentDTO`

---

## 5. 圈子模块

### POST `/api/circles` — 创建圈子

| 项目 | 说明 |
|------|------|
| 鉴权 | 是 |
| Content-Type | application/json |

**请求体：**

| 字段 | 类型 | 说明 |
|------|------|------|
| name | String | 圈子名称 |
| description | String | 圈子简介 |
| avatar | String | 圈子头像 URL |

**响应 data：** `CircleDTO`

---

### GET `/api/circles` — 圈子列表（分页）

| 项目 | 说明 |
|------|------|
| 鉴权 | 否 |

**查询参数：**

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| page | int | 0 | 页码 |
| size | int | 10 | 每页数量 |

**响应 data：**

```json
{
  "content": [ /* CircleDTO[] */ ],
  "totalElements": 50,
  "totalPages": 5,
  "currentPage": 0
}
```

---

### GET `/api/circles/hot` — 热门圈子

| 项目 | 说明 |
|------|------|
| 鉴权 | 否 |

**响应 data：** `List<CircleDTO>`

---

### GET `/api/circles/my` — 我加入的圈子

| 项目 | 说明 |
|------|------|
| 鉴权 | 是 |

**响应 data：** `List<CircleDTO>`

---

### GET `/api/circles/{id}` — 圈子详情

| 项目 | 说明 |
|------|------|
| 鉴权 | 否 |
| 路径参数 | `id` — 圈子 ID (Long) |

**响应 data：** `CircleDTO`

---

### POST `/api/circles/{id}/join` — 加入圈子

| 项目 | 说明 |
|------|------|
| 鉴权 | 是 |
| 路径参数 | `id` — 圈子 ID (Long) |

**响应 data：** `CircleDTO`

---

### POST `/api/circles/{id}/leave` — 退出圈子

| 项目 | 说明 |
|------|------|
| 鉴权 | 是 |
| 路径参数 | `id` — 圈子 ID (Long) |

**响应 data：** `CircleDTO`

---

### GET `/api/circles/{id}/members` — 圈子成员列表

| 项目 | 说明 |
|------|------|
| 鉴权 | 否 |
| 路径参数 | `id` — 圈子 ID (Long) |

**查询参数：**

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| page | int | 0 | 页码 |
| size | int | 20 | 每页数量 |

**响应 data：**

```json
{
  "content": [ /* UserDTO[] */ ],
  "totalElements": 100
}
```

---

### GET `/api/circles/{id}/posts` — 圈子帖子列表

| 项目 | 说明 |
|------|------|
| 鉴权 | 否 |
| 路径参数 | `id` — 圈子 ID (Long) |

**查询参数：**

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| page | int | 0 | 页码 |
| size | int | 10 | 每页数量 |

**响应 data：** 分页结构，同帖子列表

---

## 6. 关注模块

### POST `/api/users/{id}/follow` — 关注 / 取消关注

| 项目 | 说明 |
|------|------|
| 鉴权 | 是 |
| 路径参数 | `id` — 目标用户 ID (Long) |

**响应 data：**

```json
{
  "followed": true
}
```

---

### GET `/api/recommend/users` — 推荐用户

| 项目 | 说明 |
|------|------|
| 鉴权 | 是 |

**响应 data：** `List<UserDTO>`（最多 5 个推荐用户）

---

## 7. 文件上传模块

### POST `/api/upload/image` — 上传图片

| 项目 | 说明 |
|------|------|
| 鉴权 | 是 |
| Content-Type | multipart/form-data |

**请求参数：**

| 字段 | 类型 | 说明 |
|------|------|------|
| file | MultipartFile | 图片文件 |

**响应 data：**

```json
{
  "url": "/api/file/uploads/xxxx.jpg"
}
```

---

### GET `/api/file/**` — 获取文件

| 项目 | 说明 |
|------|------|
| 鉴权 | 否 |

**说明：** 通过路径访问已上传的文件，返回二进制内容。带 7 天缓存。

**示例：** `GET /api/file/uploads/abc.jpg`

---

## 8. 通知模块

### GET `/api/notifications` — 通知列表

| 项目 | 说明 |
|------|------|
| 鉴权 | 是 |

**查询参数：**

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| page | int | 0 | 页码 |
| size | int | 20 | 每页数量 |

**响应 data：**

```json
{
  "content": [ /* Notification[] */ ],
  "totalElements": 50,
  "totalPages": 3
}
```

---

### GET `/api/notifications/unread-count` — 未读通知数量

| 项目 | 说明 |
|------|------|
| 鉴权 | 是 |

**响应 data：** `Long`（未读数量）

---

### POST `/api/notifications/message` — 发送私信通知

| 项目 | 说明 |
|------|------|
| 鉴权 | 是 |
| Content-Type | application/json |

**请求体：**

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| targetUserId | Long | 是 | 目标用户 ID |
| text | String | 否 | 消息内容（默认"发来一条私信"） |

**响应 data：** 无

---

### PUT `/api/notifications/read` — 全部标记已读

| 项目 | 说明 |
|------|------|
| 鉴权 | 是 |

**响应 data：** 无

---

## 9. 搜索模块

### GET `/api/search` — 综合搜索

| 项目 | 说明 |
|------|------|
| 鉴权 | 否 |

**查询参数：**

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| keyword | String | 必填 | 搜索关键词 |
| type | String | "all" | 搜索类型：`all` / `post` / `circle` / `user` |
| page | int | 0 | 页码 |
| size | int | 10 | 每页数量 |

**响应 data：**

```json
{
  "posts": {
    "content": [ /* PostDTO[] */ ],
    "totalElements": 20
  },
  "circles": {
    "content": [ /* CircleDTO[] */ ],
    "totalElements": 5
  },
  "users": {
    "content": [ /* UserDTO[] */ ],
    "totalElements": 3
  }
}
```

> 当 type 不为 `all` 时，仅返回对应类型的数据。

---

## 10. 统计模块

### GET `/api/stats/user/{id}` — 用户数据统计

| 项目 | 说明 |
|------|------|
| 鉴权 | 否 |
| 路径参数 | `id` — 用户 ID (Long) |

**响应 data：**

```json
{
  "postCount": 42,
  "postTrend": [{ "date": "2026-03-01", "count": 3 }],
  "likeTrend": [{ "date": "2026-03-01", "count": 15 }],
  "commentTrend": [{ "date": "2026-03-01", "count": 8 }]
}
```

---

### GET `/api/stats/admin/overview` — 管理后台概览

| 项目 | 说明 |
|------|------|
| 鉴权 | 是（需管理员权限） |

**响应 data：**

```json
{
  "totalUsers": 1000,
  "totalPosts": 5000,
  "totalCircles": 50,
  "todayPosts": 23,
  "todayUsers": 5
}
```

---

### GET `/api/stats/admin/trends` — 管理后台趋势数据

| 项目 | 说明 |
|------|------|
| 鉴权 | 是（需管理员权限） |

**响应 data：**

```json
{
  "userTrend": [{ "date": "2026-03-01", "count": 10 }],
  "postTrend": [{ "date": "2026-03-01", "count": 25 }]
}
```

---

## 11. 管理后台模块

> 所有管理接口均需管理员权限（role = ADMIN）。

### GET `/api/admin/users` — 用户管理列表

**查询参数：**

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| page | int | 0 | 页码 |
| size | int | 10 | 每页数量 |
| keyword | String | 无 | 搜索关键词（可选） |

**响应 data：** 分页结构 `{ content: User[], totalElements, totalPages }`

---

### PUT `/api/admin/users/{id}/role` — 修改用户角色

| 路径参数 | `id` — 用户 ID (Long) |
|------|------|

**请求体：**

```json
{ "role": "ADMIN" }
```

**响应 data：** 无

---

### PUT `/api/admin/users/{id}/ban` — 封禁 / 解封用户

| 路径参数 | `id` — 用户 ID (Long) |
|------|------|

**响应 data：** 无

---

### GET `/api/admin/posts` — 帖子管理列表

**查询参数：**

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| page | int | 0 | 页码 |
| size | int | 10 | 每页数量 |
| status | String | 无 | 按状态筛选（可选） |

**响应 data：** 分页结构 `{ content: Post[], totalElements, totalPages }`

---

### PUT `/api/admin/posts/{id}/status` — 修改帖子状态

| 路径参数 | `id` — 帖子 ID (Long) |
|------|------|

**请求体：**

```json
{ "status": "HIDDEN" }
```

**响应 data：** 无

---

### GET `/api/admin/circles` — 圈子管理列表

**查询参数：**

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| page | int | 0 | 页码 |
| size | int | 10 | 每页数量 |

**响应 data：** 分页结构 `{ content: Circle[], totalElements, totalPages }`

---

### DELETE `/api/admin/circles/{id}` — 删除圈子

| 路径参数 | `id` — 圈子 ID (Long) |
|------|------|

**响应 data：** 无

---

### GET `/api/admin/logs` — 操作日志

**查询参数：**

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| page | int | 0 | 页码 |
| size | int | 20 | 每页数量 |

**响应 data：** 分页结构 `{ content: Log[], totalElements, totalPages }`

---

### GET `/api/admin/export/users` — 导出用户 CSV

**响应：** 直接下载 CSV 文件（`Content-Disposition: attachment; filename=users.csv`）

---

## 12. 即时通讯模块

### GET `/api/im/usersig` — 获取 IM UserSig

| 项目 | 说明 |
|------|------|
| 鉴权 | 是 |

**响应 data：**

```json
{
  "sdkAppId": 1400000000,
  "userId": "im_user_1",
  "userSig": "eJyrVgrx..."
}
```

---

### GET `/api/im/user-info/{userId}` — 获取 IM 用户信息

| 项目 | 说明 |
|------|------|
| 鉴权 | 否 |
| 路径参数 | `userId` — 用户 ID (Long) |

**响应 data：**

```json
{
  "userId": 1,
  "imUserId": "im_user_1",
  "username": "张三",
  "avatar": "/api/file/uploads/avatar.jpg"
}
```

---
## 13. 数据模型

### UserDTO

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 用户 ID |
| username | String | 用户名 |
| email | String | 邮箱 |
| phone | String | 手机号 |
| avatar | String | 头像 URL |
| bio | String | 个人简介 |
| postCount | Integer | 帖子数 |
| followerCount | Integer | 粉丝数 |
| followingCount | Integer | 关注数 |
| followed | Boolean | 当前用户是否已关注 |
| role | String | 角色（USER / ADMIN） |
| createdAt | DateTime | 注册时间 |

### PostDTO

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 帖子 ID |
| userId | Long | 作者 ID |
| circleId | Long | 所属圈子 ID |
| circleName | String | 圈子名称 |
| title | String | 标题 |
| content | String | 正文内容 |
| images | List\<String\> | 图片 URL 列表 |
| likeCount | Integer | 点赞数 |
| commentCount | Integer | 评论数 |
| favoriteCount | Integer | 收藏数 |
| createdAt | DateTime | 创建时间 |
| updatedAt | DateTime | 更新时间 |
| authorName | String | 作者名称 |
| authorAvatar | String | 作者头像 |
| liked | Boolean | 当前用户是否已点赞 |
| favorited | Boolean | 当前用户是否已收藏 |

### CommentDTO

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 评论 ID |
| postId | Long | 帖子 ID |
| userId | Long | 评论者 ID |
| parentId | Long | 父评论 ID（顶级为 null） |
| content | String | 评论内容 |
| createdAt | DateTime | 评论时间 |
| authorName | String | 评论者名称 |
| authorAvatar | String | 评论者头像 |
| replies | List\<CommentDTO\> | 子回复列表 |

### CircleDTO

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 圈子 ID |
| name | String | 圈子名称 |
| description | String | 圈子简介 |
| avatar | String | 圈子头像 URL |
| creatorId | Long | 创建者 ID |
| creatorName | String | 创建者名称 |
| memberCount | Integer | 成员数 |
| postCount | Integer | 帖子数 |
| createdAt | DateTime | 创建时间 |
| joined | Boolean | 当前用户是否已加入 |
