-- ============================================================
-- 兴趣圈子 测试数据
-- ============================================================

SET @pwd = '$2a$10$pzHmnWBK8FcGObP6um8HxO/S7WiIylqzI3KYVx300TTJcbYZ5J5Na';

-- ── 用户 (ID 4-18) ──────────────────────────────────────────
INSERT INTO users (id, username, email, password, avatar, bio, role, banned, created_at, updated_at) VALUES
(4,  'alex_designs',   'alex@example.com',    @pwd, 'https://api.dicebear.com/7.x/avataaars/svg?seed=alex',    '建筑设计师 & 光影爱好者。探索数字空间与物理舒适的交汇点。🌿✨', 'USER', false, DATE_SUB(NOW(), INTERVAL 120 DAY), NOW()),
(5,  'elena_art',      'elena@example.com',   @pwd, 'https://api.dicebear.com/7.x/avataaars/svg?seed=elena',   '自由插画师，热爱色彩与线条的无限可能', 'USER', false, DATE_SUB(NOW(), INTERVAL 100 DAY), NOW()),
(6,  'marcus_photo',   'marcus@example.com',  @pwd, 'https://api.dicebear.com/7.x/avataaars/svg?seed=marcus',  '摄影师 | 建筑摄影 | 城市探索者 📸', 'USER', false, DATE_SUB(NOW(), INTERVAL 90 DAY), NOW()),
(7,  'sarah_wilson',   'sarah@example.com',   @pwd, 'https://api.dicebear.com/7.x/avataaars/svg?seed=sarah',   '风光摄影 | 追逐金色时刻 | 自然之美', 'USER', false, DATE_SUB(NOW(), INTERVAL 85 DAY), NOW()),
(8,  'david_code',     'david@example.com',   @pwd, 'https://api.dicebear.com/7.x/avataaars/svg?seed=david',   '全栈开发者，开源爱好者，代码即诗', 'USER', false, DATE_SUB(NOW(), INTERVAL 80 DAY), NOW()),
(9,  'lily_music',     'lily@example.com',    @pwd, 'https://api.dicebear.com/7.x/avataaars/svg?seed=lily',    '独立音乐人 🎵 吉他 / 键盘 / 作曲', 'USER', false, DATE_SUB(NOW(), INTERVAL 75 DAY), NOW()),
(10, 'james_travel',   'james@example.com',   @pwd, 'https://api.dicebear.com/7.x/avataaars/svg?seed=james',   '数字游民 | 已到访30+国家 | 世界是一本书', 'USER', false, DATE_SUB(NOW(), INTERVAL 70 DAY), NOW()),
(11, 'mia_foodie',     'mia@example.com',     @pwd, 'https://api.dicebear.com/7.x/avataaars/svg?seed=mia',     '美食博主 | 烘焙达人 | 寻找城市里的隐藏美味', 'USER', false, DATE_SUB(NOW(), INTERVAL 65 DAY), NOW()),
(12, 'tom_fitness',    'tom@example.com',     @pwd, 'https://api.dicebear.com/7.x/avataaars/svg?seed=tom',     '健身教练 | CrossFit | 生活需要力量', 'USER', false, DATE_SUB(NOW(), INTERVAL 60 DAY), NOW()),
(13, 'nina_read',      'nina@example.com',    @pwd, 'https://api.dicebear.com/7.x/avataaars/svg?seed=nina',    '阅读是对灵魂的冒险 📚 年读100+', 'USER', false, DATE_SUB(NOW(), INTERVAL 55 DAY), NOW()),
(14, 'kevin_game',     'kevin@example.com',   @pwd, 'https://api.dicebear.com/7.x/avataaars/svg?seed=kevin',   '独立游戏开发 | Unity | 像素艺术', 'USER', false, DATE_SUB(NOW(), INTERVAL 50 DAY), NOW()),
(15, 'emma_yoga',      'emma@example.com',    @pwd, 'https://api.dicebear.com/7.x/avataaars/svg?seed=emma',    '瑜伽教练 | 冥想 | 内在平衡', 'USER', false, DATE_SUB(NOW(), INTERVAL 45 DAY), NOW()),
(16, 'ryan_sci',       'ryan@example.com',    @pwd, 'https://api.dicebear.com/7.x/avataaars/svg?seed=ryan',    '科幻迷 | 硬科幻 | 三体粉', 'USER', false, DATE_SUB(NOW(), INTERVAL 40 DAY), NOW()),
(17, 'sophie_draw',    'sophie@example.com',  @pwd, 'https://api.dicebear.com/7.x/avataaars/svg?seed=sophie',  '水彩画家 | 记录生活的颜色', 'USER', false, DATE_SUB(NOW(), INTERVAL 35 DAY), NOW()),
(18, 'leo_film',       'leo@example.com',     @pwd, 'https://api.dicebear.com/7.x/avataaars/svg?seed=leo',     '电影爱好者 | 短片导演 | 叙事的力量', 'USER', false, DATE_SUB(NOW(), INTERVAL 30 DAY), NOW());

-- ── 圈子 (ID 1-10) ─────────────────────────────────────────
INSERT INTO circles (id, name, description, avatar, creator_id, member_count, post_count, created_at) VALUES
(1,  '极简生活',      '信奉少即是多的生活哲学。分享你的空间、技巧和极简主义旅程。',                          'https://picsum.photos/seed/minimal/200', 4,  1240, 85, DATE_SUB(NOW(), INTERVAL 100 DAY)),
(2,  '代码工匠',      '深入架构设计、整洁代码和新兴技术。为在乎细节的建造者而生。',                          'https://picsum.photos/seed/code/200',    8,  820,  62, DATE_SUB(NOW(), INTERVAL 90 DAY)),
(3,  '金色时刻',      '用最好的光线捕捉世界。户外摄影师和追逐黎明的人的集体。',                              'https://picsum.photos/seed/golden/200',  7,  450,  38, DATE_SUB(NOW(), INTERVAL 85 DAY)),
(4,  '设计圈',        '"设计不仅仅是看起来如何和感觉如何。设计是如何运作的。" — 史蒂夫·乔布斯',              'https://picsum.photos/seed/design/200',  5,  960,  71, DATE_SUB(NOW(), INTERVAL 80 DAY)),
(5,  '环球旅行',      '世界是一本书，不旅行的人只读了第一页。分享你的旅途故事。',                              'https://picsum.photos/seed/travel/200',  10, 680,  45, DATE_SUB(NOW(), INTERVAL 75 DAY)),
(6,  '美食探店',      '寻找城市里的隐藏美味，记录每一口感动。',                                                'https://picsum.photos/seed/food/200',    11, 520,  39, DATE_SUB(NOW(), INTERVAL 70 DAY)),
(7,  '独立音乐',      '分享独立音乐、原创作品和音乐制作心得。',                                                'https://picsum.photos/seed/music/200',   9,  340,  28, DATE_SUB(NOW(), INTERVAL 65 DAY)),
(8,  '科幻世界',      '探讨科幻文学、电影和未来科技的无限可能。',                                              'https://picsum.photos/seed/scifi/200',   16, 280,  22, DATE_SUB(NOW(), INTERVAL 60 DAY)),
(9,  '读书会',        '每月一本书，深度阅读，思想碰撞。',                                                      'https://picsum.photos/seed/books/200',   13, 410,  35, DATE_SUB(NOW(), INTERVAL 55 DAY)),
(10, '健身打卡',      '自律给我自由。分享训练计划、饮食和健身心得。',                                            'https://picsum.photos/seed/fitness/200', 12, 390,  30, DATE_SUB(NOW(), INTERVAL 50 DAY));

-- ── 圈子成员 ────────────────────────────────────────────────
INSERT INTO circle_members (circle_id, user_id, joined_at) VALUES
(1,4,DATE_SUB(NOW(),INTERVAL 99 DAY)),(1,5,DATE_SUB(NOW(),INTERVAL 95 DAY)),(1,6,DATE_SUB(NOW(),INTERVAL 90 DAY)),(1,7,DATE_SUB(NOW(),INTERVAL 85 DAY)),(1,15,DATE_SUB(NOW(),INTERVAL 80 DAY)),
(2,8,DATE_SUB(NOW(),INTERVAL 89 DAY)),(2,4,DATE_SUB(NOW(),INTERVAL 85 DAY)),(2,14,DATE_SUB(NOW(),INTERVAL 80 DAY)),(2,16,DATE_SUB(NOW(),INTERVAL 75 DAY)),
(3,7,DATE_SUB(NOW(),INTERVAL 84 DAY)),(3,6,DATE_SUB(NOW(),INTERVAL 80 DAY)),(3,17,DATE_SUB(NOW(),INTERVAL 75 DAY)),(3,5,DATE_SUB(NOW(),INTERVAL 70 DAY)),
(4,5,DATE_SUB(NOW(),INTERVAL 79 DAY)),(4,4,DATE_SUB(NOW(),INTERVAL 75 DAY)),(4,17,DATE_SUB(NOW(),INTERVAL 70 DAY)),(4,18,DATE_SUB(NOW(),INTERVAL 65 DAY)),
(5,10,DATE_SUB(NOW(),INTERVAL 74 DAY)),(5,7,DATE_SUB(NOW(),INTERVAL 70 DAY)),(5,11,DATE_SUB(NOW(),INTERVAL 65 DAY)),
(6,11,DATE_SUB(NOW(),INTERVAL 69 DAY)),(6,10,DATE_SUB(NOW(),INTERVAL 65 DAY)),(6,15,DATE_SUB(NOW(),INTERVAL 60 DAY)),
(7,9,DATE_SUB(NOW(),INTERVAL 64 DAY)),(7,18,DATE_SUB(NOW(),INTERVAL 60 DAY)),(7,14,DATE_SUB(NOW(),INTERVAL 55 DAY)),
(8,16,DATE_SUB(NOW(),INTERVAL 59 DAY)),(8,8,DATE_SUB(NOW(),INTERVAL 55 DAY)),(8,14,DATE_SUB(NOW(),INTERVAL 50 DAY)),
(9,13,DATE_SUB(NOW(),INTERVAL 54 DAY)),(9,15,DATE_SUB(NOW(),INTERVAL 50 DAY)),(9,5,DATE_SUB(NOW(),INTERVAL 45 DAY)),
(10,12,DATE_SUB(NOW(),INTERVAL 49 DAY)),(10,10,DATE_SUB(NOW(),INTERVAL 45 DAY)),(10,4,DATE_SUB(NOW(),INTERVAL 40 DAY)),
(1,1,DATE_SUB(NOW(),INTERVAL 30 DAY)),(2,1,DATE_SUB(NOW(),INTERVAL 28 DAY)),(4,1,DATE_SUB(NOW(),INTERVAL 25 DAY));

-- ── 帖子 (ID 3-40+) ────────────────────────────────────────
INSERT INTO posts (user_id, circle_id, title, content, images, like_count, comment_count, favorite_count, status, created_at, updated_at) VALUES
(6, 1, '晨光中的中庭', '刚完成"中庭"项目的初稿。重点研究自然光如何塑造我们对空间的感知。这个混凝土纹理研究大家怎么看？🏛️', '["https://picsum.photos/seed/atrium1/800/600","https://picsum.photos/seed/atrium2/800/600"]', 124, 18, 32, 'APPROVED', DATE_SUB(NOW(), INTERVAL 2 HOUR), NOW()),
(5, 4, '设计的本质', '"设计不仅仅是看起来如何和感觉如何。设计是如何运作的。" 最近在读《设计心理学》，这段话让我思考了很久。你们觉得呢？', '[]', 89, 24, 15, 'APPROVED', DATE_SUB(NOW(), INTERVAL 5 HOUR), NOW()),
(7, 3, '城市的金色时刻', '城市中心的金色时刻。光线打在这些建筑纹理上，有种说不出的魔力。✨', '["https://picsum.photos/seed/golden1/800/600","https://picsum.photos/seed/golden2/800/600","https://picsum.photos/seed/golden3/800/600"]', 852, 42, 128, 'APPROVED', DATE_SUB(NOW(), INTERVAL 1 DAY), NOW()),
(4, 1, '我的极简工作台', '终于把工作台整理到了理想状态。一台显示器、一盏台灯、一株绿植。少即是多。', '["https://picsum.photos/seed/desk1/800/600"]', 156, 22, 45, 'APPROVED', DATE_SUB(NOW(), INTERVAL 2 DAY), NOW()),
(8, 2, 'Rust vs Go：2024年的选择', '最近在评估后端语言选型，Rust 和 Go 各有优劣。从性能、生态、学习曲线三个维度分析...', '[]', 203, 67, 38, 'APPROVED', DATE_SUB(NOW(), INTERVAL 2 DAY), NOW()),
(10, 5, '冰岛环岛自驾手记', '14天环岛自驾，从雷克雅未克出发，经过黄金圈、冰川湖、黑沙滩...每一天都是震撼。', '["https://picsum.photos/seed/iceland1/800/600","https://picsum.photos/seed/iceland2/800/600","https://picsum.photos/seed/iceland3/800/600"]', 445, 56, 89, 'APPROVED', DATE_SUB(NOW(), INTERVAL 3 DAY), NOW()),
(11, 6, '藏在巷子里的宝藏面馆', '这家只有8个座位的小面馆，汤头用牛骨熬了12小时。一碗面的执着让人感动。📍上海静安区', '["https://picsum.photos/seed/noodle1/800/600","https://picsum.photos/seed/noodle2/800/600"]', 178, 34, 52, 'APPROVED', DATE_SUB(NOW(), INTERVAL 3 DAY), NOW()),
(9, 7, '新歌demo分享', '花了两周写的新歌，融合了 lo-fi 和 city pop 的元素。录了个简单的 demo，欢迎大家给意见 🎵', '["https://picsum.photos/seed/music1/800/600"]', 92, 28, 18, 'APPROVED', DATE_SUB(NOW(), INTERVAL 4 DAY), NOW()),
(16, 8, '《三体》重读笔记', '第三遍读三体，每次都有新的理解。这次特别关注了"黑暗森林法则"的哲学基础...', '["https://picsum.photos/seed/scifi1/800/600"]', 167, 43, 62, 'APPROVED', DATE_SUB(NOW(), INTERVAL 4 DAY), NOW()),
(13, 9, '本月书单推荐', '这个月读了5本书，最推荐《百年孤独》和《人类简史》。前者是魔幻现实主义的巅峰，后者让你重新认识人类...', '["https://picsum.photos/seed/books1/800/600"]', 134, 31, 47, 'APPROVED', DATE_SUB(NOW(), INTERVAL 5 DAY), NOW()),
(12, 10, '30天健身变化记录', '坚持了一个月的训练计划，体脂从22%降到18%。分享一下我的训练方案和饮食计划。', '["https://picsum.photos/seed/fitness1/800/600","https://picsum.photos/seed/fitness2/800/600"]', 289, 45, 76, 'APPROVED', DATE_SUB(NOW(), INTERVAL 5 DAY), NOW()),
(17, 4, '水彩日记：春天的颜色', '用水彩记录春天的每一抹颜色。樱花粉、新叶绿、天空蓝...', '["https://picsum.photos/seed/watercolor1/800/600","https://picsum.photos/seed/watercolor2/800/600","https://picsum.photos/seed/watercolor3/800/600"]', 198, 26, 54, 'APPROVED', DATE_SUB(NOW(), INTERVAL 6 DAY), NOW()),
(18, 7, '短片幕后花絮', '新短片《归途》的拍摄幕后。这次尝试了全自然光拍摄，挑战很大但效果出乎意料。', '["https://picsum.photos/seed/film1/800/600","https://picsum.photos/seed/film2/800/600"]', 145, 19, 33, 'APPROVED', DATE_SUB(NOW(), INTERVAL 7 DAY), NOW()),
(15, 1, '晨间冥想的30天体验', '连续30天每天早起冥想20分钟，分享一些感受和变化。最大的改变是心态更平和了。', '["https://picsum.photos/seed/meditation1/800/600"]', 176, 38, 61, 'APPROVED', DATE_SUB(NOW(), INTERVAL 7 DAY), NOW()),
(14, 2, '用Unity做了一个像素风小游戏', '花了三个月做的独立游戏终于有了demo！像素风格的roguelike地牢探险游戏。', '["https://picsum.photos/seed/game1/800/600","https://picsum.photos/seed/game2/800/600","https://picsum.photos/seed/game3/800/600"]', 312, 54, 87, 'APPROVED', DATE_SUB(NOW(), INTERVAL 8 DAY), NOW()),
(6, 3, '夜间摄影：霓虹与雨', '雨夜的城市是另一个世界。霓虹灯在湿润的路面上倒映，每一步都是一幅画。', '["https://picsum.photos/seed/neon1/800/600","https://picsum.photos/seed/neon2/800/600"]', 567, 38, 95, 'APPROVED', DATE_SUB(NOW(), INTERVAL 9 DAY), NOW()),
(4, 4, '建筑中的留白艺术', '好的建筑如同好的设计，留白与填充同样重要。分享几个我最喜欢的留白建筑案例。', '["https://picsum.photos/seed/arch1/800/600","https://picsum.photos/seed/arch2/800/600","https://picsum.photos/seed/arch3/800/600"]', 234, 29, 68, 'APPROVED', DATE_SUB(NOW(), INTERVAL 10 DAY), NOW()),
(10, 5, '日本京都五日深度游', '避开热门景点，深入京都的小街小巷。寻找最地道的抹茶和最安静的庭院。', '["https://picsum.photos/seed/kyoto1/800/600","https://picsum.photos/seed/kyoto2/800/600","https://picsum.photos/seed/kyoto3/800/600"]', 389, 47, 102, 'APPROVED', DATE_SUB(NOW(), INTERVAL 11 DAY), NOW()),
(5, 4, 'UI设计中的微交互', '好的微交互能让产品从"能用"变成"好用"。总结了10个提升用户体验的微交互设计原则。', '["https://picsum.photos/seed/ui1/800/600"]', 278, 35, 71, 'APPROVED', DATE_SUB(NOW(), INTERVAL 12 DAY), NOW()),
(8, 2, '我的Vim配置分享', '用了五年Vim，终于把配置优化到了满意的状态。分享我的 .vimrc 和常用插件。', '["https://picsum.photos/seed/vim1/800/600"]', 156, 42, 29, 'APPROVED', DATE_SUB(NOW(), INTERVAL 13 DAY), NOW()),
(7, 3, '晨雾中的山', '清晨五点爬上山顶，等待太阳穿透晨雾的那一刻。等待是值得的。', '["https://picsum.photos/seed/mountain1/800/600","https://picsum.photos/seed/mountain2/800/600"]', 623, 35, 112, 'APPROVED', DATE_SUB(NOW(), INTERVAL 14 DAY), NOW()),
(11, 6, '在家做正宗那不勒斯披萨', '历经20次失败后终于成功！分享食谱和关键技巧：面团发酵48小时是秘诀。', '["https://picsum.photos/seed/pizza1/800/600","https://picsum.photos/seed/pizza2/800/600"]', 234, 41, 67, 'APPROVED', DATE_SUB(NOW(), INTERVAL 15 DAY), NOW()),
(9, 7, '音乐制作入门指南', '从零开始学音乐制作？分享我的学习路径：乐理→DAW→混音→母带。推荐的软件和教程都在这里。', '["https://picsum.photos/seed/daw1/800/600"]', 187, 29, 43, 'APPROVED', DATE_SUB(NOW(), INTERVAL 16 DAY), NOW()),
(16, 8, '赛博朋克美学赏析', '从Blade Runner到赛博朋克2077，赛博朋克美学的演变。高科技低生活的视觉魅力。', '["https://picsum.photos/seed/cyber1/800/600","https://picsum.photos/seed/cyber2/800/600","https://picsum.photos/seed/cyber3/800/600"]', 198, 33, 55, 'APPROVED', DATE_SUB(NOW(), INTERVAL 17 DAY), NOW()),
(12, 10, '新手健身避坑指南', '健身三年踩过的坑，希望新手们少走弯路。包括训练、饮食、恢复三个方面。', '["https://picsum.photos/seed/gym1/800/600"]', 345, 52, 89, 'APPROVED', DATE_SUB(NOW(), INTERVAL 18 DAY), NOW()),
(15, NULL, '推荐一个很棒的冥想App', '最近发现了一个宝藏冥想应用，有中文引导语，很适合入门。用了两周感觉非常好。', '[]', 78, 15, 22, 'APPROVED', DATE_SUB(NOW(), INTERVAL 19 DAY), NOW()),
(13, 9, '《挪威的森林》读后感', '村上春树笔下的孤独是温柔的。读完这本书，仿佛经历了一场安静的旅行。', '["https://picsum.photos/seed/norway1/800/600"]', 112, 21, 34, 'APPROVED', DATE_SUB(NOW(), INTERVAL 20 DAY), NOW()),
(17, NULL, '今日速写：街角咖啡店', '下午在街角咖啡店画了两小时速写。用线条记录城市的日常。', '["https://picsum.photos/seed/sketch1/800/600","https://picsum.photos/seed/sketch2/800/600"]', 145, 18, 39, 'APPROVED', DATE_SUB(NOW(), INTERVAL 21 DAY), NOW()),
(14, 8, '推荐5部被低估的科幻电影', '这些科幻电影可能你没听说过，但每一部都值得一看。第一部是《月球》...', '["https://picsum.photos/seed/movie1/800/600"]', 189, 37, 56, 'APPROVED', DATE_SUB(NOW(), INTERVAL 22 DAY), NOW()),
(18, NULL, '家庭影院搭建指南', '预算5000以内搭建一个小型家庭影院，设备选购和布局分享。', '["https://picsum.photos/seed/theater1/800/600","https://picsum.photos/seed/theater2/800/600"]', 123, 25, 31, 'APPROVED', DATE_SUB(NOW(), INTERVAL 23 DAY), NOW());

-- ── 评论 ────────────────────────────────────────────────────
INSERT INTO comments (post_id, user_id, parent_id, content, created_at) VALUES
(3,5,NULL,'光影效果太美了！这个角度很棒',DATE_SUB(NOW(),INTERVAL 2 HOUR)),
(3,4,NULL,'中庭的光影处理很有安藤忠雄的感觉',DATE_SUB(NOW(),INTERVAL 1 HOUR)),
(4,6,NULL,'设计的本质确实如此，形式追随功能',DATE_SUB(NOW(),INTERVAL 4 HOUR)),
(4,17,NULL,'这段话每次读都有新的感悟',DATE_SUB(NOW(),INTERVAL 3 HOUR)),
(5,4,NULL,'金色时刻的城市真的太美了！',DATE_SUB(NOW(),INTERVAL 23 HOUR)),
(5,5,NULL,'第三张特别有感觉，光线完美',DATE_SUB(NOW(),INTERVAL 22 HOUR)),
(5,9,NULL,'请问用什么镜头拍的？',DATE_SUB(NOW(),INTERVAL 21 HOUR)),
(6,15,NULL,'极简主义就是生活的减法',DATE_SUB(NOW(),INTERVAL 47 HOUR)),
(6,7,NULL,'这个台灯是什么牌子的？',DATE_SUB(NOW(),INTERVAL 46 HOUR)),
(7,14,NULL,'Go更适合工程化，Rust更极致',DATE_SUB(NOW(),INTERVAL 46 HOUR)),
(7,4,NULL,'看场景选择，微服务推荐Go',DATE_SUB(NOW(),INTERVAL 45 HOUR)),
(8,7,NULL,'冰岛是我梦想的旅行目的地！',DATE_SUB(NOW(),INTERVAL 70 HOUR)),
(8,11,NULL,'黑沙滩真的震撼人心',DATE_SUB(NOW(),INTERVAL 69 HOUR)),
(9,11,NULL,'看起来好好吃！求地址',DATE_SUB(NOW(),INTERVAL 68 HOUR)),
(9,10,NULL,'下次去上海一定要试试',DATE_SUB(NOW(),INTERVAL 67 HOUR)),
(10,18,NULL,'很有感觉！期待完整版',DATE_SUB(NOW(),INTERVAL 90 HOUR)),
(11,16,NULL,'黑暗森林法则真的细思极恐',DATE_SUB(NOW(),INTERVAL 92 HOUR)),
(11,14,NULL,'第三部看了三遍，每次都有新发现',DATE_SUB(NOW(),INTERVAL 91 HOUR)),
(12,5,NULL,'百年孤独的开头就是经典',DATE_SUB(NOW(),INTERVAL 115 HOUR)),
(13,10,NULL,'体脂18%太厉害了！求食谱',DATE_SUB(NOW(),INTERVAL 118 HOUR)),
(13,15,NULL,'坚持就是胜利💪',DATE_SUB(NOW(),INTERVAL 117 HOUR)),
(14,4,NULL,'水彩的颜色好治愈',DATE_SUB(NOW(),INTERVAL 140 HOUR)),
(14,5,NULL,'春天的配色真美',DATE_SUB(NOW(),INTERVAL 139 HOUR)),
(15,9,NULL,'全自然光拍摄效果确实不一样',DATE_SUB(NOW(),INTERVAL 165 HOUR)),
(16,4,NULL,'冥想确实能改变很多',DATE_SUB(NOW(),INTERVAL 166 HOUR)),
(17,6,NULL,'像素风太可爱了！在哪里可以试玩？',DATE_SUB(NOW(),INTERVAL 190 HOUR)),
(17,8,NULL,'roguelike+像素风，我的菜！',DATE_SUB(NOW(),INTERVAL 189 HOUR)),
(18,7,NULL,'雨夜的霓虹真的太有氛围了',DATE_SUB(NOW(),INTERVAL 210 HOUR)),
(18,4,NULL,'每一张都可以做壁纸',DATE_SUB(NOW(),INTERVAL 209 HOUR)),
(19,17,NULL,'留白是最难的设计',DATE_SUB(NOW(),INTERVAL 235 HOUR)),
(20,7,NULL,'京都的小巷最有味道',DATE_SUB(NOW(),INTERVAL 260 HOUR)),
(21,8,NULL,'微交互太重要了，细节决定体验',DATE_SUB(NOW(),INTERVAL 285 HOUR)),
(22,14,NULL,'Vim大法好！分享一下配置',DATE_SUB(NOW(),INTERVAL 310 HOUR)),
(23,6,NULL,'日出那一刻值得所有等待',DATE_SUB(NOW(),INTERVAL 335 HOUR)),
(24,10,NULL,'48小时发酵确实是关键！',DATE_SUB(NOW(),INTERVAL 358 HOUR)),
(25,18,NULL,'推荐用Ableton入门',DATE_SUB(NOW(),INTERVAL 380 HOUR)),
(26,14,NULL,'赛博朋克2077的美术设计太棒了',DATE_SUB(NOW(),INTERVAL 405 HOUR)),
(27,4,NULL,'新手避坑太重要了',DATE_SUB(NOW(),INTERVAL 430 HOUR)),
(28,12,NULL,'什么App？求推荐',DATE_SUB(NOW(),INTERVAL 455 HOUR)),
(29,15,NULL,'村上的文字有种独特的温度',DATE_SUB(NOW(),INTERVAL 478 HOUR));

-- ── 点赞 ────────────────────────────────────────────────────
INSERT INTO post_likes (post_id, user_id, created_at) VALUES
(3,4,NOW()),(3,5,NOW()),(3,7,NOW()),(3,8,NOW()),(3,9,NOW()),(3,10,NOW()),(3,11,NOW()),(3,15,NOW()),
(4,4,NOW()),(4,6,NOW()),(4,7,NOW()),(4,17,NOW()),(4,18,NOW()),
(5,4,NOW()),(5,5,NOW()),(5,6,NOW()),(5,8,NOW()),(5,9,NOW()),(5,10,NOW()),(5,11,NOW()),(5,12,NOW()),(5,13,NOW()),(5,14,NOW()),(5,15,NOW()),(5,16,NOW()),(5,17,NOW()),(5,18,NOW()),
(6,5,NOW()),(6,7,NOW()),(6,15,NOW()),(6,9,NOW()),
(7,4,NOW()),(7,6,NOW()),(7,14,NOW()),(7,16,NOW()),
(8,4,NOW()),(8,7,NOW()),(8,11,NOW()),(8,5,NOW()),
(9,10,NOW()),(9,11,NOW()),(9,15,NOW()),
(10,14,NOW()),(10,18,NOW()),
(11,14,NOW()),(11,16,NOW()),(11,8,NOW()),
(12,5,NOW()),(12,15,NOW()),(12,4,NOW()),
(13,10,NOW()),(13,15,NOW()),(13,4,NOW()),(13,9,NOW()),
(14,4,NOW()),(14,5,NOW()),(14,18,NOW()),
(15,9,NOW()),(15,18,NOW()),
(16,15,NOW()),(16,4,NOW()),
(17,6,NOW()),(17,8,NOW()),(17,16,NOW()),(17,5,NOW()),(17,9,NOW()),
(18,7,NOW()),(18,4,NOW()),(18,17,NOW()),
(19,17,NOW()),(19,4,NOW()),
(20,7,NOW()),(20,11,NOW()),
(21,8,NOW()),(21,14,NOW()),
(22,14,NOW()),(22,4,NOW()),
(23,6,NOW()),(23,5,NOW()),(23,17,NOW()),
(24,10,NOW()),(24,11,NOW()),
(25,18,NOW()),(25,9,NOW()),
(26,14,NOW()),(26,8,NOW());

-- ── 收藏 ────────────────────────────────────────────────────
INSERT INTO post_favorites (post_id, user_id, created_at) VALUES
(3,4,NOW()),(3,15,NOW()),(3,11,NOW()),
(5,4,NOW()),(5,6,NOW()),(5,9,NOW()),(5,10,NOW()),(5,15,NOW()),
(6,7,NOW()),(6,5,NOW()),
(7,6,NOW()),(7,4,NOW()),
(8,10,NOW()),(8,7,NOW()),(8,5,NOW()),
(13,4,NOW()),(13,15,NOW()),
(14,5,NOW()),(14,4,NOW()),
(17,8,NOW()),(17,6,NOW()),(17,5,NOW()),
(18,4,NOW()),(18,17,NOW()),
(20,7,NOW()),(20,10,NOW()),
(23,17,NOW()),(23,6,NOW()),
(27,4,NOW()),(27,12,NOW());

-- ── 用户关注 ────────────────────────────────────────────────
INSERT INTO user_follows (follower_id, following_id, created_at) VALUES
(4,5,DATE_SUB(NOW(),INTERVAL 80 DAY)),(4,6,DATE_SUB(NOW(),INTERVAL 75 DAY)),(4,7,DATE_SUB(NOW(),INTERVAL 70 DAY)),(4,8,DATE_SUB(NOW(),INTERVAL 65 DAY)),
(5,4,DATE_SUB(NOW(),INTERVAL 78 DAY)),(5,6,DATE_SUB(NOW(),INTERVAL 73 DAY)),(5,17,DATE_SUB(NOW(),INTERVAL 68 DAY)),
(6,4,DATE_SUB(NOW(),INTERVAL 76 DAY)),(6,7,DATE_SUB(NOW(),INTERVAL 71 DAY)),(6,5,DATE_SUB(NOW(),INTERVAL 66 DAY)),
(7,6,DATE_SUB(NOW(),INTERVAL 74 DAY)),(7,4,DATE_SUB(NOW(),INTERVAL 69 DAY)),(7,10,DATE_SUB(NOW(),INTERVAL 64 DAY)),
(8,4,DATE_SUB(NOW(),INTERVAL 72 DAY)),(8,14,DATE_SUB(NOW(),INTERVAL 67 DAY)),(8,16,DATE_SUB(NOW(),INTERVAL 62 DAY)),
(9,7,DATE_SUB(NOW(),INTERVAL 70 DAY)),(9,18,DATE_SUB(NOW(),INTERVAL 65 DAY)),
(10,7,DATE_SUB(NOW(),INTERVAL 68 DAY)),(10,11,DATE_SUB(NOW(),INTERVAL 63 DAY)),(10,4,DATE_SUB(NOW(),INTERVAL 58 DAY)),
(11,10,DATE_SUB(NOW(),INTERVAL 66 DAY)),(11,6,DATE_SUB(NOW(),INTERVAL 61 DAY)),
(12,4,DATE_SUB(NOW(),INTERVAL 64 DAY)),(12,10,DATE_SUB(NOW(),INTERVAL 59 DAY)),
(13,5,DATE_SUB(NOW(),INTERVAL 62 DAY)),(13,15,DATE_SUB(NOW(),INTERVAL 57 DAY)),
(14,8,DATE_SUB(NOW(),INTERVAL 60 DAY)),(14,16,DATE_SUB(NOW(),INTERVAL 55 DAY)),
(15,4,DATE_SUB(NOW(),INTERVAL 58 DAY)),(15,13,DATE_SUB(NOW(),INTERVAL 53 DAY)),(15,7,DATE_SUB(NOW(),INTERVAL 48 DAY)),
(16,8,DATE_SUB(NOW(),INTERVAL 56 DAY)),(16,14,DATE_SUB(NOW(),INTERVAL 51 DAY)),
(17,5,DATE_SUB(NOW(),INTERVAL 54 DAY)),(17,4,DATE_SUB(NOW(),INTERVAL 49 DAY)),(17,6,DATE_SUB(NOW(),INTERVAL 44 DAY)),
(18,9,DATE_SUB(NOW(),INTERVAL 52 DAY)),(18,14,DATE_SUB(NOW(),INTERVAL 47 DAY)),
(1,4,DATE_SUB(NOW(),INTERVAL 20 DAY)),(1,6,DATE_SUB(NOW(),INTERVAL 18 DAY)),(1,7,DATE_SUB(NOW(),INTERVAL 15 DAY));

-- ── 通知 ────────────────────────────────────────────────────
INSERT INTO notifications (target_user_id, from_user_id, from_username, type, content, related_id, is_read, created_at) VALUES
(6,5,'elena_art','LIKE','点赞了你的帖子',3,false,DATE_SUB(NOW(),INTERVAL 1 HOUR)),
(6,4,'alex_designs','COMMENT','评论了你的帖子：中庭的光影处理很有安藤忠雄的感觉',3,false,DATE_SUB(NOW(),INTERVAL 1 HOUR)),
(7,4,'alex_designs','LIKE','点赞了你的帖子',5,false,DATE_SUB(NOW(),INTERVAL 20 HOUR)),
(4,15,'emma_yoga','LIKE','点赞了你的帖子',6,true,DATE_SUB(NOW(),INTERVAL 45 HOUR)),
(8,4,'alex_designs','COMMENT','评论了你的帖子：看场景选择，微服务推荐Go',7,true,DATE_SUB(NOW(),INTERVAL 44 HOUR)),
(5,4,'alex_designs','FOLLOW','关注了你',NULL,true,DATE_SUB(NOW(),INTERVAL 80 DAY)),
(6,4,'alex_designs','FOLLOW','关注了你',NULL,true,DATE_SUB(NOW(),INTERVAL 75 DAY)),
(4,5,'elena_art','FOLLOW','关注了你',NULL,true,DATE_SUB(NOW(),INTERVAL 78 DAY)),
(4,6,'marcus_photo','FOLLOW','关注了你',NULL,true,DATE_SUB(NOW(),INTERVAL 76 DAY)),
(4,7,'sarah_wilson','FOLLOW','关注了你',NULL,true,DATE_SUB(NOW(),INTERVAL 69 DAY));
