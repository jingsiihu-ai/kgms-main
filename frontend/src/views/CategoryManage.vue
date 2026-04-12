<template>
  <div>
    <div v-if="!isPrincipal" class="page-card no-auth">仅园长可访问目录管理模块。</div>
    <div v-else class="category-page">
      <el-row :gutter="16">
        <el-col :xs="24" :md="9">
          <div class="page-card panel tree-panel">
            <div class="head">
              <h3>目录树</h3>
              <el-button size="mini" type="primary" @click="openCreateRoot">新增一级目录</el-button>
            </div>
            <el-tree :data="treeData" node-key="id" :props="treeProps" default-expand-all @node-click="handleNodeClick">
              <span slot-scope="{ data }" class="custom-node">
                <span>{{ data.name }}</span>
                <span>
                  <el-button type="text" size="mini" @click.stop="openCreateChild(data)">新增下级</el-button>
                  <el-button type="text" size="mini" @click.stop="openEdit(data)">编辑</el-button>
                </span>
              </span>
            </el-tree>
          </div>
        </el-col>
        <el-col :xs="24" :md="15">
          <div class="page-card panel detail-panel">
            <div class="head"><h3>目录详情</h3></div>
            <div v-if="selectedNode">
              <el-descriptions :column="2" border>
                <el-descriptions-item label="名称">{{ selectedNode.name }}</el-descriptions-item>
                <el-descriptions-item label="ID">{{ selectedNode.id }}</el-descriptions-item>
                <el-descriptions-item label="层级">{{ selectedNode.level }}</el-descriptions-item>
                <el-descriptions-item label="排序">{{ selectedNode.sortOrder }}</el-descriptions-item>
                <el-descriptions-item label="启用状态">{{ selectedNode.enabled === 1 ? '启用' : '停用' }}</el-descriptions-item>
                <el-descriptions-item label="父级ID">{{ selectedNode.parentId || '-' }}</el-descriptions-item>
              </el-descriptions>
              <div class="detail-actions">
                <el-button type="primary" plain @click="openEdit(selectedNode)">编辑</el-button>
                <el-button type="danger" plain @click="removeNode(selectedNode)">删除</el-button>
              </div>
            </div>
            <div v-else class="placeholder">请选择左侧目录节点查看详情。</div>
          </div>
        </el-col>
      </el-row>
      <el-dialog :visible.sync="dialogVisible" :title="dialogMode === 'create' ? '新增目录' : '编辑目录'" width="460px">
        <el-form ref="form" :model="form" :rules="rules" label-width="90px">
          <el-form-item label="目录名称" prop="name"><el-input v-model="form.name" /></el-form-item>
          <el-form-item label="父级ID"><el-input v-model="form.parentId" disabled /></el-form-item>
          <el-form-item label="层级" prop="level"><el-input-number v-model="form.level" :min="1" :max="3" /></el-form-item>
          <el-form-item label="排序"><el-input-number v-model="form.sortOrder" :min="0" /></el-form-item>
          <el-form-item label="启用状态">
            <el-radio-group v-model="form.enabled">
              <el-radio :label="1">启用</el-radio>
              <el-radio :label="0">停用</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-form>
        <span slot="footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submit">保存</el-button>
        </span>
      </el-dialog>
    </div>
  </div>
</template>

<script>
import { createCategory, deleteCategory, listCategories, updateCategory } from '@/api/admin';

export default {
  name: 'CategoryManage',
  data() {
    return {
      treeData: [],
      treeProps: { label: 'name', children: 'children' },
      selectedNode: null,
      dialogVisible: false,
      dialogMode: 'create',
      form: { id: null, parentId: null, name: '', level: 1, sortOrder: 0, enabled: 1 },
      rules: { name: [{ required: true, message: '请输入目录名称', trigger: 'blur' }] }
    };
  },
  computed: { isPrincipal() { return this.$store.getters.isPrincipal; } },
  created() { if (this.isPrincipal) this.loadTree(); },
  methods: {
    async loadTree() { this.treeData = await listCategories(true); },
    handleNodeClick(node) { this.selectedNode = node; },
    openCreateRoot() { this.dialogMode = 'create'; this.dialogVisible = true; this.form = { id: null, parentId: null, name: '', level: 1, sortOrder: 0, enabled: 1 }; },
    openCreateChild(parent) { this.dialogMode = 'create'; this.dialogVisible = true; this.form = { id: null, parentId: parent.id, name: '', level: Math.min((parent.level || 1) + 1, 3), sortOrder: 0, enabled: 1 }; },
    openEdit(node) { this.dialogMode = 'edit'; this.dialogVisible = true; this.form = { id: node.id, parentId: node.parentId, name: node.name, level: node.level, sortOrder: node.sortOrder, enabled: node.enabled }; },
    async removeNode(node) { await this.$confirm('确认删除该目录吗？', '提示', { type: 'warning' }); await deleteCategory(node.id); this.$message.success('删除成功'); this.selectedNode = null; await this.loadTree(); },
    submit() {
      this.$refs.form.validate(async valid => {
        if (!valid) return;
        if (this.dialogMode === 'create') await createCategory(this.form);
        else await updateCategory(this.form.id, this.form);
        this.$message.success('保存成功');
        this.dialogVisible = false;
        await this.loadTree();
      });
    }
  }
};
</script>

<style scoped>
.category-page { display:flex; flex-direction:column; gap:16px; }
.panel { padding:16px; min-height:560px; }
.head { display:flex; align-items:center; justify-content:space-between; margin-bottom:14px; }
.head h3 { margin:0; font-size:16px; }
.custom-node { width:100%; display:flex; justify-content:space-between; align-items:center; }
.placeholder { color:#7a8b9d; padding:24px 0; }
.detail-actions { margin-top:18px; }
.no-auth { padding:30px; text-align:center; color:#607a94; }
</style>
