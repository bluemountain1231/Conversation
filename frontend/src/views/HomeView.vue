<template>
  <div class="home-page" ref="pageRef">
    <!-- Hero Banner -->
    <div class="hero-banner" ref="headerRef">
      <div class="hero-bg">
        <div class="hero-shape hero-shape-1"></div>
        <div class="hero-shape hero-shape-2"></div>
        <div class="hero-shape hero-shape-3"></div>
      </div>
      <div class="hero-content">
        <div class="hero-text">
          <h1 class="hero-title">
            发现你的<span class="highlight">兴趣圈子</span>
          </h1>
          <p class="hero-subtitle">
            探索有趣的内容，加入志同道合的社区，分享你的热爱
          </p>
          <div class="hero-stats">
            <div class="hero-stat">
              <span class="hs-num">{{ totalPosts }}+</span>
              <span class="hs-label">精彩帖子</span>
            </div>
            <div class="hero-stat-divider"></div>
            <div class="hero-stat">
              <span class="hs-num">{{ hotCircles.length }}+</span>
              <span class="hs-label">活跃圈子</span>
            </div>
            <div class="hero-stat-divider"></div>
            <div class="hero-stat">
              <span class="hs-num">{{ recommendedUsers.length }}+</span>
              <span class="hs-label">创作者</span>
            </div>
          </div>
        </div>
        <div class="hero-actions">
          <router-link to="/circles" class="hero-btn hero-btn-explore">
            <el-icon><Compass /></el-icon> 浏览圈子
          </router-link>
          <router-link
            v-if="userStore.isLoggedIn"
            to="/post/create"
            class="hero-btn hero-btn-create"
          >
            <el-icon><Edit /></el-icon> 发表内容
          </router-link>
          <router-link v-else to="/register" class="hero-btn hero-btn-create">
            <el-icon><User /></el-icon> 立即加入
          </router-link>
        </div>
      </div>
    </div>

    <!-- Quick Tags - Auto Scroll -->
    <div class="quick-tags">
      <span class="qt-label">热门话题</span>
      <div class="qt-track">
        <div class="qt-scroll">
          <span
            v-for="tag in [...quickTags, ...quickTags]"
            :key="tag + Math.random()"
            class="qt-item"
            @click.stop="$router.push({ path: '/search', query: { q: tag } })"
          >
            # {{ tag }}
          </span>
        </div>
      </div>
    </div>

    <div class="home-layout">
      <div class="main-col">
        <!-- Recommendation Strip -->
        <div
          class="rec-strip"
          v-if="hotCircles.length || recommendedUsers.length"
        >
          <!-- Hot Circles -->
          <div class="rec-block" v-if="hotCircles.length">
            <div class="rec-block-header">
              <span class="rec-block-title"
                ><el-icon><TrendCharts /></el-icon> 热门圈子</span
              >
              <router-link to="/circles" class="rec-block-more"
                >查看全部 →</router-link
              >
            </div>
            <div class="rec-scroll-wrap">
              <div class="rec-scroll-track">
                <div
                  v-for="c in hotCircles"
                  :key="c.id"
                  class="rec-circle-chip"
                  @click="$router.push(`/circle/${c.id}`)"
                >
                  <el-avatar
                    :size="44"
                    :src="
                      c.avatar ||
                      'https://api.dicebear.com/7.x/shapes/svg?seed=' + c.name
                    "
                    shape="square"
                    class="rec-circle-chip-avatar"
                  />
                  <div class="rec-circle-chip-info">
                    <span class="rec-circle-chip-name">{{ c.name }}</span>
                    <span class="rec-circle-chip-meta">
                      <el-icon><User /></el-icon>{{ c.memberCount }}
                      <el-icon style="margin-left: 6px"><Document /></el-icon
                      >{{ c.postCount }}
                    </span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Recommended Users -->
          <div class="rec-block" v-if="recommendedUsers.length">
            <div class="rec-block-header">
              <span class="rec-block-title"
                ><el-icon><UserFilled /></el-icon> 可能感兴趣的人</span
              >
            </div>
            <div class="rec-scroll-wrap">
              <div class="rec-scroll-track">
                <div
                  v-for="u in recommendedUsers"
                  :key="u.id"
                  class="rec-user-chip"
                  @click="$router.push(`/profile/${u.id}`)"
                >
                  <el-avatar
                    :size="44"
                    :src="u.avatar"
                    class="rec-user-chip-avatar"
                  />
                  <div class="rec-user-chip-info">
                    <span class="rec-user-chip-name">{{ u.username }}</span>
                    <span class="rec-user-chip-bio">{{
                      u.bio || "来自同一圈子"
                    }}</span>
                  </div>
                  <el-button
                    :type="u.followed ? 'default' : 'primary'"
                    size="small"
                    round
                    class="rec-user-chip-btn"
                    @click.stop="handleRecFollow(u)"
                  >
                    {{ u.followed ? "已关注" : "+ 关注" }}
                  </el-button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Feed Tabs -->
        <div class="feed-header">
          <div class="feed-tabs">
            <button
              :class="['tab-btn', { active: feedType === 'latest' }]"
              @click="
                feedType = 'latest';
                switchFeed();
              "
            >
              <el-icon><Clock /></el-icon> 最新
            </button>
            <button
              v-if="userStore.isLoggedIn"
              :class="['tab-btn', { active: feedType === 'follow' }]"
              @click="
                feedType = 'follow';
                switchFeed();
              "
            >
              <el-icon><Star /></el-icon> 关注
            </button>
          </div>
          <div class="feed-info">
            <span class="post-count">{{ posts.length }} 条内容</span>
          </div>
        </div>

        <div v-if="loading && posts.length === 0" class="loading-area">
          <div class="skeleton-card" v-for="i in 3" :key="i">
            <div class="skeleton-header">
              <el-skeleton-item
                variant="circle"
                style="width: 42px; height: 42px"
              />
              <div style="flex: 1">
                <el-skeleton-item
                  variant="text"
                  style="width: 120px; height: 16px"
                />
                <el-skeleton-item
                  variant="text"
                  style="width: 80px; height: 12px; margin-top: 6px"
                />
              </div>
            </div>
            <el-skeleton :rows="3" animated />
          </div>
        </div>
        <div v-else>
          <TransitionGroup name="post-list" tag="div">
            <PostCard
              v-for="post in posts"
              :key="post.id"
              :post="post"
              @update="handlePostUpdate"
            />
          </TransitionGroup>
          <el-empty
            v-if="posts.length === 0 && !loading"
            description="暂无帖子，去发现更多圈子吧"
          >
            <el-button type="primary" @click="$router.push('/circles')"
              >浏览圈子</el-button
            >
          </el-empty>
          <div ref="loadMoreRef" class="load-more">
            <div v-if="loading" class="loading-spinner">
              <div class="spinner"></div>
              <span>加载中...</span>
            </div>
            <div v-else-if="noMore" class="no-more">
              <div class="no-more-line"></div>
              <span>已经到底了</span>
              <div class="no-more-line"></div>
            </div>
          </div>
        </div>
      </div>

      <div class="side-col" ref="sideRef">
        <!-- User Welcome Card -->
        <div
          class="side-card welcome-card"
          v-if="userStore.isLoggedIn && userStore.userInfo"
        >
          <div class="wc-header">
            <el-avatar :size="48" :src="userStore.userInfo.avatar" />
            <div class="wc-info">
              <div class="wc-name">{{ userStore.userInfo.username }}</div>
              <div class="wc-bio">
                {{ userStore.userInfo.bio || "点击编辑个人简介" }}
              </div>
            </div>
          </div>
          <div class="wc-stats">
            <div
              class="wc-stat"
              @click="$router.push(`/profile/${userStore.userInfo.id}`)"
            >
              <span class="wcs-num">{{
                userStore.userInfo.postCount || 0
              }}</span>
              <span class="wcs-label">帖子</span>
            </div>
            <div class="wc-stat">
              <span class="wcs-num">{{
                userStore.userInfo.followerCount || 0
              }}</span>
              <span class="wcs-label">粉丝</span>
            </div>
            <div class="wc-stat">
              <span class="wcs-num">{{
                userStore.userInfo.followingCount || 0
              }}</span>
              <span class="wcs-label">关注</span>
            </div>
          </div>
          <router-link to="/post/create" class="wc-publish-btn">
            <el-icon><Edit /></el-icon> 发表新帖子
          </router-link>
        </div>

        <!-- Hot Circles -->
        <div class="side-card" v-if="hotCircles.length">
          <div class="side-header">
            <span class="side-title"
              ><el-icon><TrendCharts /></el-icon> 热门圈子</span
            >
            <router-link to="/circles" class="more-link">全部 →</router-link>
          </div>
          <div
            v-for="(c, idx) in hotCircles"
            :key="c.id"
            class="hot-circle-item"
            @click="$router.push(`/circle/${c.id}`)"
          >
            <div class="hc-rank" :class="'rank-' + (idx + 1)">
              {{ idx + 1 }}
            </div>
            <el-avatar
              :size="38"
              :src="
                c.avatar ||
                'https://api.dicebear.com/7.x/shapes/svg?seed=' + c.name
              "
              shape="square"
            />
            <div class="hc-info">
              <div class="hc-name">{{ c.name }}</div>
              <div class="hc-meta">
                <span
                  ><el-icon><User /></el-icon> {{ c.memberCount }}</span
                >
                <span
                  ><el-icon><Document /></el-icon> {{ c.postCount }}</span
                >
              </div>
            </div>
          </div>
        </div>

        <!-- Recommended Users -->
        <div class="side-card" v-if="recommendedUsers.length">
          <div class="side-header">
            <span class="side-title"
              ><el-icon><UserFilled /></el-icon> 推荐关注</span
            >
          </div>
          <UserCard
            v-for="u in recommendedUsers"
            :key="u.id"
            :user="u"
            @update="handleUserUpdate"
          />
        </div>

        <!-- Announcement -->
        <div class="side-card announcement-card">
          <div class="side-header">
            <span class="side-title"
              ><el-icon><Bell /></el-icon> 社区公告</span
            >
          </div>
          <div class="ann-item" v-for="(ann, i) in announcements" :key="i">
            <div class="ann-dot"></div>
            <div class="ann-text">{{ ann.text }}</div>
            <div class="ann-time">{{ ann.time }}</div>
          </div>
        </div>

        <!-- Footer Links -->
        <div class="side-footer">
          <div class="sf-links">
            <a href="#">关于我们</a><span>·</span> <a href="#">使用条款</a
            ><span>·</span> <a href="#">隐私政策</a><span>·</span>
            <a href="#">帮助中心</a>
          </div>
          <div class="sf-copy">&copy; 2026 兴趣圈子 Interest Circle</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from "vue";
import PostCard from "../components/PostCard.vue";
import UserCard from "../components/UserCard.vue";
import { getPosts, getFeed } from "../api/post";
import { getHotCircles } from "../api/circle";
import { getRecommendedUsers, toggleFollow } from "../api/follow";
import { useUserStore } from "../stores/user";
import { gsap, fadeInUp, staggerFadeIn } from "../utils/animations";

const userStore = useUserStore();
const posts = ref([]);
const loading = ref(false);
const page = ref(0);
const noMore = ref(false);
const loadMoreRef = ref(null);
const feedType = ref("latest");
const pageRef = ref(null);
const headerRef = ref(null);
const sideRef = ref(null);
let observer = null;

const hotCircles = ref([]);
const recommendedUsers = ref([]);

const totalPosts = computed(() =>
  posts.value.length > 0 ? Math.max(posts.value.length * 3, 50) : 100,
);

const quickTags = ref([
  "前端开发",
  "摄影技巧",
  "科幻小说",
  "独立游戏",
  "读书分享",
  "美食制作",
  "电影推荐",
]);

const announcements = ref([
  { text: "社区规范更新，请遵守友善发言准则", time: "3天前" },
  { text: "新功能上线：支持私信和实时通知", time: "1周前" },
  { text: "欢迎加入兴趣圈子社区！", time: "2周前" },
]);

async function loadPosts() {
  if (loading.value || noMore.value) return;
  loading.value = true;
  try {
    const res =
      feedType.value === "follow" && userStore.isLoggedIn
        ? await getFeed(page.value, 10)
        : await getPosts(page.value, 10);
    const newPosts = res.data.content || [];
    if (newPosts.length < 10) noMore.value = true;
    posts.value.push(...newPosts);
    page.value++;
  } catch {
  } finally {
    loading.value = false;
  }
}

function switchFeed() {
  posts.value = [];
  page.value = 0;
  noMore.value = false;
  loadPosts();
}
function handlePostUpdate(p) {
  const i = posts.value.findIndex((x) => x.id === p.id);
  if (i !== -1) posts.value[i] = p;
}
function handleUserUpdate(u) {
  const i = recommendedUsers.value.findIndex((x) => x.id === u.id);
  if (i !== -1) recommendedUsers.value[i] = u;
}

async function handleRecFollow(user) {
  try {
    await toggleFollow(user.id);
    const idx = recommendedUsers.value.findIndex((x) => x.id === user.id);
    if (idx !== -1) {
      recommendedUsers.value[idx] = {
        ...recommendedUsers.value[idx],
        followed: !user.followed,
      };
    }
  } catch {}
}

async function loadSidebar() {
  try {
    hotCircles.value = (await getHotCircles()).data || [];
  } catch {}
  if (userStore.isLoggedIn) {
    try {
      recommendedUsers.value = (await getRecommendedUsers()).data || [];
    } catch {}
  }
}

onMounted(() => {
  loadPosts();
  loadSidebar();

  if (headerRef.value) fadeInUp(headerRef.value);
  if (sideRef.value)
    gsap.fromTo(
      sideRef.value,
      { opacity: 0, x: 30 },
      { opacity: 1, x: 0, duration: 0.6, delay: 0.2, ease: "power2.out" },
    );

  observer = new IntersectionObserver(
    (entries) => {
      if (entries[0].isIntersecting) loadPosts();
    },
    { threshold: 0.1 },
  );
  setTimeout(() => {
    if (loadMoreRef.value) observer.observe(loadMoreRef.value);
  }, 200);
});

onUnmounted(() => {
  if (observer) observer.disconnect();
});
</script>

<style scoped>
.home-page {
  max-width: 1080px;
  margin: 0 auto;
}

/* Hero Banner */
.hero-banner {
  position: relative;
  border-radius: var(--radius-xl);
  padding: 40px 40px 36px;
  margin-bottom: 24px;
  background: var(--bg-card);
  border: 1px solid var(--border-soft);
  box-shadow: var(--shadow-md);
  overflow: hidden;
}
.hero-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(
    135deg,
    rgba(79, 70, 229, 0.06) 0%,
    rgba(6, 182, 212, 0.04) 50%,
    rgba(168, 85, 247, 0.06) 100%
  );
}
.hero-shape {
  position: absolute;
  border-radius: 50%;
  filter: blur(60px);
  opacity: 0.6;
  animation: heroFloat 15s infinite ease-in-out;
}
.hero-shape-1 {
  width: 200px;
  height: 200px;
  top: -60px;
  right: 10%;
  background: rgba(79, 70, 229, 0.1);
}
.hero-shape-2 {
  width: 150px;
  height: 150px;
  bottom: -40px;
  left: 5%;
  background: rgba(6, 182, 212, 0.08);
  animation-delay: -5s;
}
.hero-shape-3 {
  width: 120px;
  height: 120px;
  top: 30%;
  right: 30%;
  background: rgba(168, 85, 247, 0.06);
  animation-delay: -10s;
}
@keyframes heroFloat {
  0%,
  100% {
    transform: translate(0, 0);
  }
  50% {
    transform: translate(20px, -15px);
  }
}
.hero-content {
  position: relative;
  z-index: 1;
}
.hero-text {
  margin-bottom: 24px;
}
.hero-title {
  font-size: 32px;
  font-weight: 800;
  color: var(--text-primary);
  line-height: 1.3;
  margin-bottom: 10px;
}
.highlight {
  background: var(--gradient-primary);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}
.hero-subtitle {
  color: var(--text-muted);
  font-size: 15px;
  line-height: 1.6;
  max-width: 500px;
}
.hero-stats {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-top: 24px;
}
.hero-stat {
  display: flex;
  flex-direction: column;
}
.hs-num {
  font-size: 22px;
  font-weight: 800;
  color: var(--primary);
}
.hs-label {
  font-size: 12px;
  color: var(--text-muted);
  font-weight: 500;
  margin-top: 2px;
}
.hero-stat-divider {
  width: 1px;
  height: 32px;
  background: var(--border-light);
}
.hero-actions {
  display: flex;
  gap: 12px;
}
.hero-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 10px 24px;
  border-radius: var(--radius-md);
  font-size: 14px;
  font-weight: 600;
  transition: all 0.3s;
  text-decoration: none;
}
.hero-btn-explore {
  background: var(--gradient-primary);
  color: #fff;
  box-shadow: var(--shadow-primary);
}
.hero-btn-explore:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 32px rgba(79, 70, 229, 0.35);
}
.hero-btn-create {
  background: var(--bg-hover);
  color: var(--text-primary);
  border: 1.5px solid var(--border-light);
}
.hero-btn-create:hover {
  border-color: var(--primary);
  color: var(--primary);
  background: var(--primary-bg);
}

/* Quick Tags - Auto Scroll */
.quick-tags {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 24px;
  padding: 14px 20px;
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  border: 1px solid var(--border-soft);
  box-shadow: var(--shadow-sm);
  overflow: hidden;
}
.qt-label {
  font-size: 13px;
  font-weight: 700;
  color: var(--text-muted);
  white-space: nowrap;
  flex-shrink: 0;
}
.qt-track {
  overflow: hidden;
  flex: 1;
  mask-image: linear-gradient(
    90deg,
    transparent,
    #000 5%,
    #000 95%,
    transparent
  );
  -webkit-mask-image: linear-gradient(
    90deg,
    transparent,
    #000 5%,
    #000 95%,
    transparent
  );
}
.qt-scroll {
  display: flex;
  gap: 8px;
  width: max-content;
  animation: tagScroll 25s linear infinite;
}
.qt-scroll:hover {
  animation-play-state: paused;
}
@keyframes tagScroll {
  0% {
    transform: translateX(0);
  }
  100% {
    transform: translateX(-50%);
  }
}
.qt-item {
  padding: 5px 14px;
  border-radius: 20px;
  background: var(--primary-bg);
  color: var(--primary);
  font-size: 13px;
  font-weight: 600;
  white-space: nowrap;
  cursor: pointer;
  transition: all 0.25s;
  flex-shrink: 0;
}
.qt-item:hover {
  background: var(--primary);
  color: #fff;
  transform: translateY(-1px);
}

/* Layout */
.home-layout {
  display: flex;
  gap: 28px;
  align-items: flex-start;
}
.main-col {
  flex: 1;
  min-width: 0;
}
.side-col {
  width: 320px;
  flex-shrink: 0;
  position: sticky;
  top: 80px;
  max-height: calc(100vh - 100px);
  overflow-y: auto;
  scrollbar-width: none;
}
.side-col::-webkit-scrollbar {
  display: none;
}

/* Feed Header - Sticky */
.feed-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 6px 6px 6px 8px;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border-radius: var(--radius-lg);
  border: 1px solid var(--border-soft);
  box-shadow: var(--shadow-sm);
  position: sticky;
  top: 72px;
  z-index: 50;
}
.feed-tabs {
  display: flex;
  gap: 4px;
}
.tab-btn {
  border: none;
  background: transparent;
  padding: 9px 20px;
  border-radius: var(--radius-sm);
  font-size: 13px;
  font-weight: 600;
  color: var(--text-muted);
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 5px;
}
.tab-btn .el-icon {
  font-size: 15px;
}
.tab-btn.active {
  background: var(--gradient-primary);
  color: #fff;
  box-shadow: var(--shadow-primary);
}
.tab-btn:hover:not(.active) {
  color: var(--text-secondary);
  background: var(--bg-hover);
}
.feed-info {
  padding-right: 12px;
}
.post-count {
  font-size: 12px;
  color: var(--text-muted);
  font-weight: 500;
}

/* Skeleton */
.skeleton-card {
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  padding: 24px;
  margin-bottom: 16px;
  border: 1px solid var(--border-soft);
}
.skeleton-header {
  display: flex;
  gap: 12px;
  align-items: center;
  margin-bottom: 16px;
}

/* Transitions */
.post-list-enter-active {
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}
.post-list-enter-from {
  opacity: 0;
  transform: translateY(24px);
}

/* Load More */
.load-more {
  text-align: center;
  padding: 36px 0;
}
.loading-spinner {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  color: var(--text-muted);
  font-size: 13px;
}
.spinner {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  border: 3px solid var(--border-light);
  border-top-color: var(--primary);
  animation: spin 0.8s linear infinite;
}
@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}
.no-more {
  display: flex;
  align-items: center;
  gap: 16px;
  color: var(--text-muted);
  font-size: 13px;
  font-weight: 500;
}
.no-more-line {
  flex: 1;
  height: 1px;
  background: var(--border-light);
}

/* Welcome Card */
.welcome-card {
  background: linear-gradient(
    135deg,
    rgba(79, 70, 229, 0.03),
    rgba(168, 85, 247, 0.03)
  ) !important;
}
.wc-header {
  display: flex;
  gap: 14px;
  align-items: center;
  margin-bottom: 16px;
}
.wc-info {
  flex: 1;
  min-width: 0;
}
.wc-name {
  font-weight: 700;
  font-size: 15px;
  color: var(--text-primary);
}
.wc-bio {
  font-size: 12px;
  color: var(--text-muted);
  margin-top: 2px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.wc-stats {
  display: flex;
  gap: 0;
  margin-bottom: 16px;
  background: var(--bg-card);
  border-radius: var(--radius-md);
  overflow: hidden;
  border: 1px solid var(--border-soft);
}
.wc-stat {
  flex: 1;
  text-align: center;
  padding: 10px 0;
  cursor: pointer;
  transition: background 0.2s;
}
.wc-stat:hover {
  background: var(--primary-bg);
}
.wc-stat:not(:last-child) {
  border-right: 1px solid var(--border-soft);
}
.wcs-num {
  display: block;
  font-size: 16px;
  font-weight: 800;
  color: var(--primary);
}
.wcs-label {
  font-size: 11px;
  color: var(--text-muted);
  font-weight: 500;
}
.wc-publish-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  width: 100%;
  padding: 10px 0;
  border-radius: var(--radius-md);
  background: var(--gradient-primary);
  color: #fff;
  font-size: 14px;
  font-weight: 600;
  text-decoration: none;
  box-shadow: var(--shadow-primary);
  transition: all 0.3s;
}
.wc-publish-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 32px rgba(79, 70, 229, 0.35);
}

/* Side Cards */
.side-card {
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  padding: 20px;
  margin-bottom: 16px;
  border: 1px solid var(--border-soft);
  box-shadow: var(--shadow-sm);
  transition: box-shadow 0.3s;
}
.side-card:hover {
  box-shadow: var(--shadow-md);
}
.side-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}
.side-title {
  font-weight: 700;
  font-size: 15px;
  color: var(--text-primary);
  display: flex;
  align-items: center;
  gap: 6px;
}
.side-title .el-icon {
  color: var(--primary);
  font-size: 17px;
}
.more-link {
  font-size: 12px;
  color: var(--text-muted);
  font-weight: 500;
  transition: color 0.2s;
}
.more-link:hover {
  color: var(--primary);
}

/* Hot Circle with Rank */
.hot-circle-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 8px;
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: all 0.25s;
  margin-bottom: 2px;
}
.hot-circle-item:hover {
  background: var(--primary-bg);
  transform: translateX(4px);
}
.hc-rank {
  width: 22px;
  height: 22px;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 11px;
  font-weight: 800;
  color: var(--text-muted);
  background: var(--bg-hover);
  flex-shrink: 0;
}
.hc-rank.rank-1 {
  background: linear-gradient(135deg, #f59e0b, #f97316);
  color: #fff;
}
.hc-rank.rank-2 {
  background: linear-gradient(135deg, #94a3b8, #64748b);
  color: #fff;
}
.hc-rank.rank-3 {
  background: linear-gradient(135deg, #d97706, #b45309);
  color: #fff;
}
.hc-name {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
}
.hc-meta {
  font-size: 12px;
  color: var(--text-muted);
  margin-top: 2px;
  display: flex;
  gap: 10px;
}
.hc-meta span {
  display: flex;
  align-items: center;
  gap: 3px;
}
.hc-meta .el-icon {
  font-size: 12px;
}

/* Announcement */
.announcement-card {
}
.ann-item {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  padding: 8px 0;
}
.ann-item:not(:last-child) {
  border-bottom: 1px solid var(--border-light);
}
.ann-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: var(--primary);
  flex-shrink: 0;
  margin-top: 7px;
}
.ann-text {
  flex: 1;
  font-size: 13px;
  color: var(--text-secondary);
  line-height: 1.5;
}
.ann-time {
  font-size: 11px;
  color: var(--text-muted);
  white-space: nowrap;
}

/* Footer */
.side-footer {
  text-align: center;
  padding: 16px 0;
}
.sf-links {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 6px;
  margin-bottom: 8px;
}
.sf-links a {
  font-size: 12px;
  color: var(--text-muted);
  transition: color 0.2s;
}
.sf-links a:hover {
  color: var(--primary);
}
.sf-links span {
  color: var(--border-light);
  font-size: 12px;
}
.sf-copy {
  font-size: 11px;
  color: var(--text-muted);
  opacity: 0.7;
}

/* Recommendation Strip */
.rec-strip {
  margin-bottom: 20px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.rec-block {
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  border: 1px solid var(--border-soft);
  box-shadow: var(--shadow-sm);
  padding: 18px 20px 14px;
  transition: box-shadow 0.3s;
}
.rec-block:hover {
  box-shadow: var(--shadow-md);
}

.rec-block-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 14px;
}
.rec-block-title {
  font-weight: 700;
  font-size: 15px;
  color: var(--text-primary);
  display: flex;
  align-items: center;
  gap: 6px;
}
.rec-block-title .el-icon {
  color: var(--primary);
  font-size: 17px;
}
.rec-block-more {
  font-size: 12px;
  color: var(--text-muted);
  font-weight: 500;
  text-decoration: none;
  transition: color 0.2s;
}
.rec-block-more:hover {
  color: var(--primary);
}

.rec-scroll-wrap {
  overflow-x: auto;
  margin: 0 -20px;
  padding: 0 20px;
  scrollbar-width: none;
}
.rec-scroll-wrap::-webkit-scrollbar {
  display: none;
}
.rec-scroll-track {
  display: flex;
  gap: 12px;
  padding-bottom: 6px;
}

/* Circle Chip */
.rec-circle-chip {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 16px;
  border-radius: var(--radius-md);
  background: var(--bg-hover);
  border: 1px solid var(--border-light);
  cursor: pointer;
  transition: all 0.25s;
  flex-shrink: 0;
  min-width: 180px;
}
.rec-circle-chip:hover {
  border-color: var(--primary);
  background: var(--primary-bg);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(79, 70, 229, 0.1);
}
.rec-circle-chip-avatar {
  flex-shrink: 0;
}
.rec-circle-chip-info {
  display: flex;
  flex-direction: column;
  min-width: 0;
}
.rec-circle-chip-name {
  font-size: 13px;
  font-weight: 600;
  color: var(--text-primary);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.rec-circle-chip-meta {
  font-size: 11px;
  color: var(--text-muted);
  display: flex;
  align-items: center;
  gap: 3px;
  margin-top: 2px;
}
.rec-circle-chip-meta .el-icon {
  font-size: 12px;
}

/* User Chip */
.rec-user-chip {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 14px;
  border-radius: var(--radius-md);
  background: var(--bg-hover);
  border: 1px solid var(--border-light);
  cursor: pointer;
  transition: all 0.25s;
  flex-shrink: 0;
  min-width: 240px;
}
.rec-user-chip:hover {
  border-color: var(--primary);
  background: var(--primary-bg);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(79, 70, 229, 0.1);
}
.rec-user-chip-avatar {
  flex-shrink: 0;
}
.rec-user-chip-info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
}
.rec-user-chip-name {
  font-size: 13px;
  font-weight: 600;
  color: var(--text-primary);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.rec-user-chip-bio {
  font-size: 11px;
  color: var(--text-muted);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-top: 2px;
}
.rec-user-chip-btn {
  flex-shrink: 0;
}

@media (max-width: 768px) {
  .side-col {
    display: none;
  }
  .hero-banner {
    padding: 28px 20px;
  }
  .hero-title {
    font-size: 24px;
  }
  .hero-stats {
    gap: 12px;
  }

  .rec-strip {
    gap: 12px;
  }
  .rec-block {
    padding: 14px 16px 10px;
  }
  .rec-circle-chip {
    min-width: 160px;
    padding: 8px 12px;
  }
  .rec-user-chip {
    min-width: 220px;
    padding: 8px 12px;
  }
}
</style>
