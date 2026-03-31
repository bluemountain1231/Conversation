const api = require("../../utils/api");
const auth = require("../../utils/auth");
const util = require("../../utils/util");

Page({
  data: {
    posts: [],
    page: 0,
    hasMore: true,
    loading: false,
    hotCircles: [],
    isLoggedIn: false,
    userAvatar: '',
  },

  onLoad() {
    this.loadPosts();
    this.loadHotCircles();
    this.checkLogin();
  },

  onShow() {
    if (typeof this.getTabBar === "function" && this.getTabBar()) {
      this.getTabBar().setData({ selected: 0 });
    }
    const isLoggedIn = !!wx.getStorageSync("token");
    if (isLoggedIn !== this.data.isLoggedIn) {
      this.setData({ isLoggedIn });
    }
    if (isLoggedIn) {
      const userInfo = wx.getStorageSync("userInfo");
      if (userInfo) {
        this.setData({ userAvatar: util.getImageUrl(userInfo.avatar) });
      }
    }
  },

  async onPullDownRefresh() {
    this.setData({ page: 0, hasMore: true, posts: [] });
    await Promise.all([this.loadPosts(), this.loadHotCircles()]);
    wx.stopPullDownRefresh();
  },

  onReachBottom() {
    if (this.data.hasMore && !this.data.loading) {
      this.loadPosts();
    }
  },

  checkLogin() {
    const isLoggedIn = !!wx.getStorageSync("token");
    this.setData({ isLoggedIn });
    if (isLoggedIn) {
      const userInfo = wx.getStorageSync("userInfo");
      if (userInfo) {
        this.setData({ userAvatar: util.getImageUrl(userInfo.avatar) });
      }
    }
  },

  async loadPosts() {
    if (this.data.loading) return;
    this.setData({ loading: true });
    try {
      const res = await api.getPosts(this.data.page, 10);
      const data = res.data || {};
      const newPosts = data.content || (Array.isArray(data) ? data : []);
      this.setData({
        posts: this.data.page === 0 ? newPosts : [...this.data.posts, ...newPosts],
        page: this.data.page + 1,
        hasMore: newPosts.length >= 10,
      });
    } catch (e) {}
    this.setData({ loading: false });
  },

  async loadHotCircles() {
    try {
      const res = await api.getHotCircles();
      const list = Array.isArray(res.data) ? res.data : [];
      const hotCircles = list.slice(0, 8).map((c) => ({
        ...c,
        avatarUrl: util.getImageUrl(c.avatar),
        memberText: util.formatNumber(c.memberCount),
      }));
      this.setData({ hotCircles });
    } catch (e) {}
  },

  onSearch() {
    wx.navigateTo({ url: "/pages/search/search" });
  },

  onCreatePost() {
    auth.requireAuth(() => {
      wx.navigateTo({ url: "/pages/post-create/post-create" });
    });
  },

  onPostTap(e) {
    const { id } = e.detail;
    if (id) wx.navigateTo({ url: `/pages/post-detail/post-detail?id=${id}` });
  },

  onLike(e) {
    const { id, liked, likeCount } = e.detail;
    if (!id) return;
    const posts = this.data.posts.map((p) =>
      p.id === id ? { ...p, liked, likeCount } : p,
    );
    this.setData({ posts });
  },

  onFavorite(e) {
    const { id, favorited, favoriteCount } = e.detail;
    if (!id) return;
    const posts = this.data.posts.map((p) =>
      p.id === id ? { ...p, favorited, favoriteCount } : p,
    );
    this.setData({ posts });
  },

  onStoryTap(e) {
    const id = e.currentTarget.dataset.id;
    if (id) wx.navigateTo({ url: `/pages/circle-detail/circle-detail?id=${id}` });
  },
});
