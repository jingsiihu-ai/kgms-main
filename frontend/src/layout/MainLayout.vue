<template>
  <div class="layout-shell">
    <aside class="sidebar">
      <div class="sidebar-logo">
        <div class="logo-mark">幼苗 · KGMS</div>
        <div class="logo-sub">幼儿园资源管理与检索系统</div>
      </div>

      <div class="nav-list">
        <div class="nav-item" :class="{ active: $route.path === '/dashboard' }" @click="go('/dashboard')">🏠 <span>总览</span></div>
        <div class="nav-item" :class="{ active: $route.path === '/resources' }" @click="go('/resources')">📂 <span>资源中心</span></div>
        <div v-if="isPrincipal" class="nav-item" :class="{ active: $route.path === '/users' }" @click="go('/users')">👥 <span>用户管理</span></div>
        <div v-if="isPrincipal" class="nav-item" :class="{ active: $route.path === '/dictionary' }" @click="go('/dictionary')">🏷️ <span>基础字典</span></div>
        <div v-if="isPrincipal" class="nav-item" :class="{ active: $route.path === '/categories' }" @click="go('/categories')">🗂️ <span>目录管理</span></div>
      </div>

      <div v-if="$route.path === '/resources'" class="tree-wrap">
        <div class="tree-title">资源目录</div>
        <el-tree
          :data="categoryTree"
          :props="treeProps"
          node-key="id"
          default-expand-all
          highlight-current
          :expand-on-click-node="false"
          @node-click="handleCategoryClick"
        >
          <span slot-scope="{ data }" class="tree-node">{{ data.name }}</span>
        </el-tree>
      </div>

      <div class="sidebar-footer">
        <div class="avatar">{{ (userName || '园').slice(0, 1) }}</div>
        <div class="user-meta">
          <div class="name">{{ userName || '未登录' }}</div>
          <div class="role">{{ roleText }}</div>
        </div>
      </div>
    </aside>

    <main class="main-area">
      <header class="topbar">
        <div>
          <div class="page-title">{{ pageTitle }}</div>
          <div class="page-subtitle">{{ pageSubtitle }}</div>
        </div>
        <div class="top-actions">
          <el-button type="text" @click="go('/resources')">资源中心</el-button>
          <el-button size="mini" type="danger" plain @click="handleLogout">退出</el-button>
        </div>
      </header>
      <section class="content-area">
        <router-view />
      </section>
    </main>
  </div>
</template>

<script>
import { listCategoryTree } from '@/api/resource';

export default {
  name: 'MainLayout',
  data() {
    return {
      categoryTree: [],
      treeProps: { label: 'name', children: 'children' }
    };
  },
  computed: {
    isPrincipal() { return this.$store.getters.isPrincipal; },
    userName() { return this.$store.getters.userName; },
    roleText() {
      const roles = this.$store.getters.roles;
      if (roles.includes('PRINCIPAL')) return '园长';
      if (roles.includes('TEACHER')) return '教师';
      if (roles.includes('NURSE')) return '保育员';
      if (roles.includes('PARENT')) return '家长';
      return '访客';
    },
    pageTitle() {
      return {
        '/dashboard': '系统总览',
        '/resources': '资源中心',
        '/users': '用户管理',
        '/dictionary': '基础字典',
        '/categories': '目录管理'
      }[this.$route.path] || 'KGMS';
    },
    pageSubtitle() {
      return {
        '/dashboard': '查看整体资源概览与热门内容',
        '/resources': '目录树 + 多条件检索 + 资源详情',
        '/users': '维护PC端用户与角色信息',
        '/dictionary': '维护标签等基础字典数据',
        '/categories': '维护三级资源目录树'
      }[this.$route.path] || '';
    }
  },
  created() {
    this.loadCategories();
  },
  methods: {
    async loadCategories() {
      try { this.categoryTree = await listCategoryTree(false); } catch (e) { this.categoryTree = []; }
    },
    go(path, query) {
      if (path !== this.$route.path || JSON.stringify(query || {}) !== JSON.stringify(this.$route.query || {})) {
        this.$router.push({ path, query: query || {} });
      }
    },
    handleCategoryClick(node) {
      this.go('/resources', { categoryId: node.id, categoryName: node.name });
    },
    async handleLogout() {
      await this.$store.dispatch('doLogout');
      this.$router.replace('/login');
    }
  },
  watch: {
    '$route.path'() {
      if (this.$route.path === '/resources' || this.$route.path === '/categories') this.loadCategories();
    }
  }
};
</script>

<style scoped>
.layout-shell { display:flex; min-height:100vh; background:#f5f3ef; }
.sidebar { width:248px; background:#1e2a3a; color:#c8d4e0; display:flex; flex-direction:column; }
.sidebar-logo { padding:22px 18px 16px; border-bottom:1px solid rgba(255,255,255,.08); }
.logo-mark { color:#fff; font-size:18px; font-weight:700; letter-spacing:1px; }
.logo-sub { margin-top:4px; font-size:10px; opacity:.55; }
.nav-list { padding:10px 0; border-bottom:1px solid rgba(255,255,255,.08); }
.nav-item { padding:10px 18px; display:flex; align-items:center; gap:8px; cursor:pointer; transition:.15s; }
.nav-item:hover { background:#2a3a4e; }
.nav-item.active { color:#4a9eff; background:rgba(74,158,255,.15); }
.tree-wrap { flex:1; overflow:auto; padding:12px 0 8px; }
.tree-title { padding:0 18px 10px; font-size:10px; letter-spacing:1px; text-transform:uppercase; opacity:.45; }
.tree-node { font-size:12px; color:#d7e4ef; }
::v-deep .el-tree { background:transparent; color:#d7e4ef; }
::v-deep .el-tree-node__content { height:34px; }
::v-deep .el-tree-node__content:hover { background:#2a3a4e; }
::v-deep .el-tree--highlight-current .el-tree-node.is-current > .el-tree-node__content { background:rgba(74,158,255,.16); color:#4a9eff; }
::v-deep .el-tree-node:focus > .el-tree-node__content { background:transparent; }
.sidebar-footer { border-top:1px solid rgba(255,255,255,.08); padding:14px 18px; display:flex; gap:10px; align-items:center; }
.avatar { width:34px; height:34px; border-radius:50%; background:linear-gradient(135deg,#4a9eff,#7b5ea7); display:flex; align-items:center; justify-content:center; color:#fff; font-weight:700; }
.user-meta .name { color:#fff; font-size:12px; }
.user-meta .role { font-size:10px; opacity:.5; }
.main-area { flex:1; display:flex; flex-direction:column; }
.topbar { height:64px; background:#fff; border-bottom:1px solid #e8e4dc; display:flex; align-items:center; justify-content:space-between; padding:0 22px; }
.page-title { font-size:18px; font-weight:700; color:#1a2332; }
.page-subtitle { font-size:12px; color:#6b7c93; margin-top:4px; }
.top-actions { display:flex; align-items:center; gap:10px; }
.content-area { flex:1; overflow:auto; padding:18px; }
</style>
