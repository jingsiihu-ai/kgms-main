<template>
  <div class="resource-page">
    <div class="path-banner page-card">
      <div>
        <div class="banner-title">{{ currentPathText }}</div>
        <div class="banner-sub">当前目录下的资源与检索结果</div>
      </div>
      <div class="banner-stats">
        <div><strong>{{ total }}</strong><span>资源数</span></div>
        <div><strong>{{ hotTags.length }}</strong><span>热门标签</span></div>
        <div><strong>{{ recentList.length }}</strong><span>最近上传</span></div>
      </div>
    </div>

    <div class="page-card filter-bar">
      <div class="filter-grid">
        <el-input v-model="filters.keyword" placeholder="输入标题、描述、文件名关键词" clearable />
        <el-select v-model="filters.applicableGrade" clearable placeholder="适用班级">
          <el-option label="小班" value="小班" />
          <el-option label="中班" value="中班" />
          <el-option label="大班" value="大班" />
          <el-option label="全部" value="全部" />
        </el-select>
        <el-select v-model="filters.semester" clearable placeholder="学期">
          <el-option label="2025-2026上学期" value="2025-2026上学期" />
          <el-option label="2025-2026下学期" value="2025-2026下学期" />
          <el-option label="2024-2025上学期" value="2024-2025上学期" />
          <el-option label="2024-2025下学期" value="2024-2025下学期" />
        </el-select>
        <el-select v-model="filters.resourceFormat" clearable placeholder="资源格式">
          <el-option label="Word文档" value="doc" />
          <el-option label="PDF" value="pdf" />
          <el-option label="PPT" value="ppt" />
          <el-option label="图片" value="img" />
          <el-option label="视频" value="vid" />
        </el-select>
        <el-select v-model="filters.tagName" clearable placeholder="标签">
          <el-option v-for="tag in tagOptions" :key="tag.id" :label="tag.name" :value="tag.name" />
        </el-select>
      </div>
      <div class="filter-actions">
        <el-button type="primary" @click="search">搜索</el-button>
        <el-button @click="resetFilters">重置</el-button>
        <el-button type="success" @click="uploadVisible = true">上传资源</el-button>
      </div>
    </div>

    <div class="active-tags" v-if="activeFilterTags.length">
      <span class="label">当前筛选：</span>
      <el-tag v-for="tag in activeFilterTags" :key="tag.key" closable @close="clearFilter(tag.key)">{{ tag.label }}</el-tag>
    </div>

    <el-row :gutter="16">
      <el-col :xs="24" :lg="17">
        <div class="page-card main-panel">
          <div class="section-head">
            <div>
              <div class="section-title">资源列表</div>
              <div class="section-sub">点击资源卡片查看详情</div>
            </div>
            <el-radio-group v-model="cardView" size="mini">
              <el-radio-button label="grid">网格</el-radio-button>
              <el-radio-button label="list">列表</el-radio-button>
            </el-radio-group>
          </div>

          <div class="upload-zone" @click="uploadVisible = true">⬆️ 拖拽文件到此处，或点击上传资源</div>

          <div v-if="cardView === 'grid'" class="card-grid">
            <div class="resource-card" :class="item.resourceFormat" v-for="item in records" :key="item.id" @click="showDetail(item)">
              <div class="card-icon">{{ iconMap[item.resourceFormat] || '📄' }}</div>
              <div class="card-title">{{ item.title }}</div>
              <div class="card-tags">
                <el-tag size="mini">{{ item.applicableGrade || '未设置班级' }}</el-tag>
                <el-tag size="mini" type="success">{{ item.resourceFormat || 'doc' }}</el-tag>
                <el-tag size="mini" effect="plain">{{ item.semester || '未设置学期' }}</el-tag>
              </div>
              <div class="card-footer">
                <span>{{ item.uploaderName || '-' }}</span>
                <span>{{ item.favoriteCount || 0 }} 收藏</span>
              </div>
            </div>
          </div>

          <el-table v-else :data="records" border stripe>
            <el-table-column prop="title" label="标题" min-width="220" />
            <el-table-column prop="categoryPath" label="目录" min-width="180" />
            <el-table-column prop="applicableGrade" label="班级" width="90" />
            <el-table-column prop="semester" label="学期" width="140" />
            <el-table-column prop="resourceFormat" label="格式" width="90" />
            <el-table-column prop="uploaderName" label="上传者" width="100" />
            <el-table-column label="操作" width="120">
              <template slot-scope="scope">
                <el-button type="text" @click.stop="showDetail(scope.row)">详情</el-button>
              </template>
            </el-table-column>
          </el-table>

          <div class="pager">
            <el-pagination layout="total, prev, pager, next" :total="total" :page-size="filters.pageSize" :current-page="filters.pageNo" @current-change="changePage" />
          </div>
        </div>
      </el-col>
      <el-col :xs="24" :lg="7">
        <div class="page-card side-panel">
          <div class="section-title sm">热门标签</div>
          <div class="tag-cloud">
            <el-tag v-for="tag in hotTags" :key="tag.tagId" effect="plain" class="hot-tag" @click.native="useTag(tag.tagName)">{{ tag.tagName }} ({{ tag.useCount }})</el-tag>
          </div>
          <el-divider />
          <div class="section-title sm">最近上传</div>
          <div class="recent-item" v-for="item in recentList" :key="item.id" @click="showDetail(item)">
            <div>{{ item.title }}</div>
            <small>{{ item.createdAt }}</small>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-dialog :visible.sync="uploadVisible" width="720px" title="上传资源">
      <el-form label-width="90px">
        <el-form-item label="标题"><el-input v-model="uploadForm.title" placeholder="单文件时可自定义标题，多文件默认用原文件名" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="uploadForm.description" type="textarea" :rows="2" /></el-form-item>
        <el-form-item label="目录">
          <el-cascader v-model="uploadCategoryPath" :options="categoryOptions" :props="cascaderProps" clearable style="width:100%;" />
        </el-form-item>
        <el-form-item label="适用班级">
          <el-select v-model="uploadForm.applicableGrade" clearable style="width:100%;">
            <el-option label="小班" value="小班" /><el-option label="中班" value="中班" /><el-option label="大班" value="大班" /><el-option label="全部" value="全部" />
          </el-select>
        </el-form-item>
        <el-form-item label="学期">
          <el-select v-model="uploadForm.semester" clearable style="width:100%;">
            <el-option label="2025-2026上学期" value="2025-2026上学期" /><el-option label="2025-2026下学期" value="2025-2026下学期" />
          </el-select>
        </el-form-item>
        <el-form-item label="标签">
          <el-select v-model="uploadForm.tagIds" multiple clearable style="width:100%;">
            <el-option v-for="item in tagOptions" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="可见性">
          <el-radio-group v-model="uploadForm.status">
            <el-radio label="public">公开</el-radio>
            <el-radio label="private">个人</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="文件">
          <el-upload drag multiple action="#" :auto-upload="false" :file-list="uploadFiles" :on-change="onFileChange" :on-remove="onFileRemove">
            <i class="el-icon-upload" />
            <div class="el-upload__text">将文件拖到此处，或点击选择文件</div>
          </el-upload>
          <el-progress v-if="uploadProgress > 0" :percentage="uploadProgress" style="margin-top:10px;" />
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="uploadVisible = false">取消</el-button>
        <el-button type="primary" :loading="uploading" @click="submitUpload">开始上传</el-button>
      </span>
    </el-dialog>

    <el-dialog :visible.sync="editVisible" width="620px" title="编辑资源">
      <el-form label-width="90px">
        <el-form-item label="标题"><el-input v-model="editForm.title" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="editForm.description" type="textarea" :rows="2" /></el-form-item>
        <el-form-item label="目录"><el-cascader v-model="editCategoryPath" :options="categoryOptions" :props="cascaderProps" clearable style="width:100%;" /></el-form-item>
        <el-form-item label="班级"><el-select v-model="editForm.applicableGrade" clearable style="width:100%;"><el-option label="小班" value="小班" /><el-option label="中班" value="中班" /><el-option label="大班" value="大班" /><el-option label="全部" value="全部" /></el-select></el-form-item>
        <el-form-item label="学期"><el-select v-model="editForm.semester" clearable style="width:100%;"><el-option label="2025-2026上学期" value="2025-2026上学期" /><el-option label="2025-2026下学期" value="2025-2026下学期" /></el-select></el-form-item>
        <el-form-item label="标签"><el-select v-model="editForm.tagIds" multiple clearable style="width:100%;"><el-option v-for="item in tagOptions" :key="item.id" :label="item.name" :value="item.id" /></el-select></el-form-item>
        <el-form-item label="可见性"><el-radio-group v-model="editForm.status"><el-radio label="public">公开</el-radio><el-radio label="private">个人</el-radio></el-radio-group></el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" @click="submitEdit">保存</el-button>
      </span>
    </el-dialog>

    <el-drawer title="资源详情" :visible.sync="detailVisible" size="420px">
      <div v-if="detail" class="detail-box">
        <div class="detail-preview">{{ iconMap[detail.resourceFormat] || '📄' }}</div>
        <div class="detail-title">{{ detail.title }}</div>
        <div class="detail-desc">{{ detail.description || '暂无描述' }}</div>
        <div class="detail-tags">
          <el-tag size="mini">{{ detail.applicableGrade || '未设置班级' }}</el-tag>
          <el-tag size="mini" type="success">{{ detail.resourceFormat || 'doc' }}</el-tag>
          <el-tag size="mini" effect="plain">{{ detail.semester || '未设置学期' }}</el-tag>
        </div>
        <el-descriptions :column="1" size="small" border>
          <el-descriptions-item label="目录">{{ detail.categoryPath || '-' }}</el-descriptions-item>
          <el-descriptions-item label="上传者">{{ detail.uploaderName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="文件名">{{ detail.fileName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="大小">{{ formatFileSize(detail.fileSize) }}</el-descriptions-item>
          <el-descriptions-item label="状态">{{ detail.status }}</el-descriptions-item>
        </el-descriptions>
        <div class="drawer-actions">
          <el-button type="primary" @click="download(detail)">下载</el-button>
          <el-button type="warning" plain @click="toggleFav(detail)">{{ detail.favorite ? '取消收藏' : '加入收藏' }}</el-button>
          <el-button v-if="canEdit(detail)" plain @click="openEdit(detail)">编辑</el-button>
          <el-button v-if="canEdit(detail)" type="danger" plain @click="remove(detail)">删除</el-button>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script>
import {
  listTags,
  listCategoryTree,
  uploadResources,
  searchResources,
  getResourceDetail,
  getRecent,
  getHotTags,
  toggleFavorite,
  getMyFavorites,
  deleteResource,
  updateResource,
  downloadResourceFile
} from '@/api/resource';

export default {
  name: 'ResourceCenter',
  data() {
    return {
      loading: false,
      cardView: 'grid',
      categoryTree: [],
      categoryOptions: [],
      cascaderProps: { value: 'id', label: 'name', children: 'children', emitPath: true, checkStrictly: false },
      tagOptions: [],
      hotTags: [],
      recentList: [],
      filters: {
        keyword: '',
        categoryId: null,
        applicableGrade: '',
        semester: '',
        resourceFormat: '',
        tagName: '',
        pageNo: 1,
        pageSize: 12
      },
      total: 0,
      records: [],
      uploadVisible: false,
      uploading: false,
      uploadProgress: 0,
      uploadFiles: [],
      uploadForm: { title: '', description: '', status: 'public', applicableGrade: '', semester: '', tagIds: [] },
      uploadCategoryPath: [],
      editVisible: false,
      editForm: { id: null, title: '', description: '', status: 'public', applicableGrade: '', semester: '', tagIds: [] },
      editCategoryPath: [],
      detailVisible: false,
      detail: null,
      iconMap: { doc: '📄', pdf: '📕', ppt: '📊', img: '🖼️', vid: '🎬' }
    };
  },
  computed: {
    currentPathText() {
      if (!this.filters.categoryId) return '全部资源';
      const node = this.findNodeById(this.categoryTree, Number(this.filters.categoryId));
      return node ? this.buildPathById(this.categoryTree, node.id).join(' / ') : '目录资源';
    },
    activeFilterTags() {
      const pairs = [
        ['keyword', this.filters.keyword],
        ['applicableGrade', this.filters.applicableGrade],
        ['semester', this.filters.semester],
        ['resourceFormat', this.filters.resourceFormat],
        ['tagName', this.filters.tagName]
      ];
      return pairs.filter(item => item[1]).map(([key, value]) => ({ key, label: `${key}: ${value}` }));
    }
  },
  async created() {
    await this.bootstrap();
  },
  watch: {
    '$route.query': {
      immediate: true,
      handler(query) {
        this.filters.categoryId = query.categoryId ? Number(query.categoryId) : null;
        if (query.tagName) this.filters.tagName = query.tagName;
        if (query.focusId) this.openDetailById(query.focusId);
        this.search();
      }
    }
  },
  methods: {
    normalizeCascaderOptions(list) {
  return (list || []).map(item => {
    const node = {
      id: item.id,
      name: item.name
    };
    if (item.children && item.children.length) {
      node.children = this.normalizeCascaderOptions(item.children);
    }
    return node;
  });
},
async bootstrap() {
  const [categories, tags, hotTags, recent] = await Promise.all([
    listCategoryTree(false),
    listTags(),
    getHotTags(10),
    getRecent(8)
  ]);

  this.categoryTree = categories || [];
  this.categoryOptions = this.normalizeCascaderOptions(categories || []);
  this.tagOptions = tags || [];
  this.hotTags = hotTags || [];
  this.recentList = recent || [];

  this.refreshByCurrentCategory();
},
    async search() {
      const data = await searchResources(this.filters);
      this.records = data.records || [];
      this.total = data.total || 0;
    },
    resetFilters() {
      this.filters = { keyword: '', categoryId: null, applicableGrade: '', semester: '', resourceFormat: '', tagName: '', pageNo: 1, pageSize: 12 };
      this.$router.replace({ path: '/resources' });
      this.search();
    },
    clearFilter(key) {
      this.filters[key] = '';
      if (key === 'categoryId') this.filters[key] = null;
      this.search();
    },
    changePage(page) { this.filters.pageNo = page; this.search(); },
    useTag(tagName) { this.filters.tagName = tagName; this.search(); },
    onFileChange(file, fileList) { this.uploadFiles = fileList; },
    onFileRemove(file, fileList) { this.uploadFiles = fileList; },
    async submitUpload() {
      if (!this.uploadFiles.length) { this.$message.warning('请先选择文件'); return; }
      this.uploading = true;
      try {
        const fd = new FormData();
        this.uploadFiles.forEach(item => fd.append('files', item.raw));
        fd.append('title', this.uploadForm.title || '');
        fd.append('description', this.uploadForm.description || '');
        fd.append('status', this.uploadForm.status || 'public');
        fd.append('applicableGrade', this.uploadForm.applicableGrade || '');
        fd.append('semester', this.uploadForm.semester || '');
        fd.append('tagIds', (this.uploadForm.tagIds || []).join(','));
        const categoryId = this.uploadCategoryPath && this.uploadCategoryPath.length ? this.uploadCategoryPath[this.uploadCategoryPath.length - 1] : '';
        fd.append('categoryId', categoryId);
        await uploadResources(fd, event => { if (event.total) this.uploadProgress = Math.round((event.loaded * 100) / event.total); });
        this.$message.success('上传完成');
        this.uploadVisible = false;
        this.uploadFiles = [];
        this.uploadProgress = 0;
        this.uploadForm = { title: '', description: '', status: 'public', applicableGrade: '', semester: '', tagIds: [] };
        this.uploadCategoryPath = [];
        await this.bootstrap();
      } finally {
        this.uploading = false;
      }
    },
    async showDetail(item) {
      this.detail = await getResourceDetail(item.id);
      this.detailVisible = true;
    },
    async openDetailById(id) {
      if (!id) return;
      try { this.detail = await getResourceDetail(id); this.detailVisible = true; } catch (e) { /* ignore */ }
    },
    async download(item) {
  try {
    await downloadResourceFile(item.id, item.fileName || item.title || 'resource');
    this.$message.success('开始下载');
  } catch (e) {
    this.$message.error('下载失败，请重新登录后重试');
  }
},
    async toggleFav(item) {
      await toggleFavorite(item.id, !item.favorite);
      await this.showDetail(item);
      await this.search();
    },
    openEdit(item) {
      this.detailVisible = false;
      this.editVisible = true;
      this.editForm = {
        id: item.id,
        title: item.title,
        description: item.description,
        status: item.status,
        applicableGrade: item.applicableGrade,
        semester: item.semester,
        tagIds: (item.tags || []).map(tag => tag.id)
      };
      this.editCategoryPath = this.buildIdPath(this.categoryTree, item.categoryId) || [];
    },
    async submitEdit() {
      const categoryId = this.editCategoryPath && this.editCategoryPath.length ? this.editCategoryPath[this.editCategoryPath.length - 1] : null;
      await updateResource(this.editForm.id, { ...this.editForm, categoryId });
      this.$message.success('保存成功');
      this.editVisible = false;
      await this.search();
      if (this.detail && this.detail.id === this.editForm.id) this.detail = await getResourceDetail(this.editForm.id);
    },
    async remove(item) {
      await this.$confirm('确认删除该资源吗？', '提示', { type: 'warning' });
      await deleteResource(item.id);
      this.$message.success('删除成功');
      this.detailVisible = false;
      await this.search();
    },
    canEdit(item) {
      const user = this.$store.state.user || {};
      return this.$store.getters.isPrincipal || user.id === item.userId;
    },
    formatFileSize(size) {
      if (!size && size !== 0) return '-';
      if (size < 1024) return `${size} B`;
      if (size < 1024 * 1024) return `${(size / 1024).toFixed(1)} KB`;
      return `${(size / 1024 / 1024).toFixed(1)} MB`;
    },
    findNodeById(list, id) {
      for (const item of list || []) {
        if (item.id === id) return item;
        const child = this.findNodeById(item.children || [], id);
        if (child) return child;
      }
      return null;
    },
    buildPathById(list, id, path = []) {
      for (const item of list || []) {
        const next = [...path, item.name];
        if (item.id === id) return next;
        const child = this.buildPathById(item.children || [], id, next);
        if (child.length) return child;
      }
      return [];
    },
    buildIdPath(list, id, path = []) {
      for (const item of list || []) {
        const next = [...path, item.id];
        if (item.id === id) return next;
        const child = this.buildIdPath(item.children || [], id, next);
        if (child && child.length) return child;
      }
      return [];
    }
  }
};
</script>

<style scoped>
.resource-page { display:flex; flex-direction:column; gap:16px; }
.path-banner { padding:18px 20px; display:flex; justify-content:space-between; align-items:center; background:linear-gradient(135deg,#1e2a3a,#2d4668); color:#fff; }
.banner-title { font-size:16px; font-weight:700; }
.banner-sub { margin-top:6px; font-size:12px; opacity:.7; }
.banner-stats { display:flex; gap:24px; }
.banner-stats div { display:flex; flex-direction:column; align-items:center; }
.banner-stats strong { color:#4a9eff; font-size:22px; }
.banner-stats span { font-size:11px; opacity:.65; }
.filter-bar { padding:16px; display:flex; gap:16px; align-items:flex-start; justify-content:space-between; }
.filter-grid { flex:1; display:grid; grid-template-columns:2fr 1fr 1fr 1fr 1fr; gap:10px; }
.filter-actions { display:flex; gap:10px; }
.active-tags { display:flex; align-items:center; gap:8px; flex-wrap:wrap; }
.active-tags .label { color:#6b7c93; font-size:12px; }
.main-panel,.side-panel { padding:16px; }
.section-head { display:flex; justify-content:space-between; align-items:center; margin-bottom:14px; }
.section-title { font-size:15px; font-weight:700; color:#1a2332; }
.section-title.sm { font-size:14px; }
.section-sub { margin-top:6px; font-size:12px; color:#7a8b9d; }
.upload-zone { padding:16px; border:2px dashed #d7dde5; border-radius:10px; background:#fff; text-align:center; cursor:pointer; color:#6b7c93; margin-bottom:16px; }
.upload-zone:hover { border-color:#1f7ae0; color:#1f7ae0; }
.card-grid { display:grid; grid-template-columns:repeat(auto-fill,minmax(220px,1fr)); gap:14px; }
.resource-card { background:#fff; border:1px solid #e8e4dc; border-radius:12px; padding:14px; cursor:pointer; transition:.15s; }
.resource-card:hover { transform:translateY(-2px); box-shadow:0 10px 24px rgba(30,42,58,.08); }
.card-icon { font-size:26px; margin-bottom:8px; }
.card-title { font-size:13px; font-weight:600; line-height:1.5; min-height:40px; }
.card-tags { margin-top:10px; display:flex; gap:6px; flex-wrap:wrap; }
.card-footer { margin-top:12px; padding-top:10px; border-top:1px solid #edf0f3; display:flex; justify-content:space-between; color:#7a8b9d; font-size:11px; }
.pager { margin-top:16px; text-align:right; }
.tag-cloud { display:flex; flex-wrap:wrap; gap:8px; }
.hot-tag { cursor:pointer; }
.recent-item { padding:8px 0; border-bottom:1px dashed #edf0f3; cursor:pointer; }
.recent-item small { color:#7a8b9d; display:block; margin-top:4px; }
.detail-box { padding:0 16px; }
.detail-preview { height:110px; border-radius:12px; background:#f4f7fb; display:flex; align-items:center; justify-content:center; font-size:48px; margin-bottom:16px; }
.detail-title { font-size:18px; font-weight:700; margin-bottom:10px; }
.detail-desc { color:#6b7c93; font-size:13px; line-height:1.6; margin-bottom:12px; }
.detail-tags { display:flex; gap:8px; flex-wrap:wrap; margin-bottom:16px; }
.drawer-actions { margin-top:16px; display:flex; gap:8px; flex-wrap:wrap; }
@media (max-width: 1280px) { .filter-grid { grid-template-columns:repeat(2,1fr); } }
</style>
