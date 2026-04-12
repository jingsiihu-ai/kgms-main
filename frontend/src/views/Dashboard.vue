<template>
  <div class="dashboard-page">
    <el-row :gutter="16" class="mb16">
      <el-col :xs="24" :md="6" v-for="stat in stats" :key="stat.label">
        <div class="page-card stat-card">
          <div class="stat-label">{{ stat.label }}</div>
          <div class="stat-value">{{ stat.value }}</div>
          <div class="stat-sub">{{ stat.sub }}</div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="16">
      <el-col :xs="24" :md="14">
        <div class="page-card panel">
          <div class="panel-head"><span>最近上传</span></div>
          <div class="recent-item" v-for="item in recent" :key="item.id" @click="goDetail(item)">
            <div class="ri-main">{{ item.title }}</div>
            <div class="ri-sub">{{ item.uploaderName || '未知上传者' }} · {{ item.createdAt }}</div>
          </div>
        </div>
      </el-col>
      <el-col :xs="24" :md="10">
        <div class="page-card panel">
          <div class="panel-head"><span>热门标签</span></div>
          <div class="tag-wrap">
            <el-tag v-for="tag in hotTags" :key="tag.tagId" class="hot-tag" effect="plain" @click.native="goTag(tag.tagName)">
              {{ tag.tagName }} ({{ tag.useCount }})
            </el-tag>
          </div>
          <el-divider />
          <div class="panel-head"><span>我的收藏</span></div>
          <div class="fav-item" v-for="item in favoriteRecords.slice(0, 6)" :key="item.id">{{ item.title }}</div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { getHotTags, getMyFavorites, getRecent } from '@/api/resource';

export default {
  name: 'DashboardView',
  data() {
    return { recent: [], hotTags: [], favoriteRecords: [] };
  },
  computed: {
    stats() {
      return [
        { label: '最近上传', value: this.recent.length, sub: '资源更新动态' },
        { label: '热门标签', value: this.hotTags.length, sub: '高频检索标签' },
        { label: '我的收藏', value: this.favoriteRecords.length, sub: '个人关注内容' },
        { label: '当前角色', value: this.$store.getters.roles[0] || '-', sub: '来自统一用户表' }
      ];
    }
  },
  async created() {
    const [recent, hotTags, favorites] = await Promise.all([getRecent(8), getHotTags(10), getMyFavorites(1, 20)]);
    this.recent = recent || [];
    this.hotTags = hotTags || [];
    this.favoriteRecords = (favorites && favorites.records) || [];
  },
  methods: {
    goDetail(item) {
      this.$router.push({ path: '/resources', query: { focusId: item.id } });
    },
    goTag(tagName) {
      this.$router.push({ path: '/resources', query: { tagName } });
    }
  }
};
</script>

<style scoped>
.mb16 { margin-bottom:16px; }
.stat-card { padding:18px; }
.stat-label { color:#6b7c93; font-size:12px; }
.stat-value { font-size:28px; font-weight:700; color:#1f7ae0; margin-top:10px; }
.stat-sub { font-size:11px; color:#97a5b6; margin-top:8px; }
.panel { padding:18px; }
.panel-head { font-size:15px; font-weight:700; color:#1a2332; margin-bottom:14px; }
.recent-item { padding:10px 0; border-bottom:1px solid #edf0f3; cursor:pointer; }
.recent-item:last-child { border-bottom:none; }
.ri-main { font-size:13px; font-weight:600; }
.ri-sub { color:#7b8a9b; font-size:11px; margin-top:4px; }
.tag-wrap { display:flex; flex-wrap:wrap; gap:8px; }
.hot-tag { cursor:pointer; }
.fav-item { padding:8px 0; border-bottom:1px dashed #edf0f3; font-size:12px; }
</style>
