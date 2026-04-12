<template>
  <div>
    <div v-if="!isPrincipal" class="page-card no-auth">仅园长可访问基础字典管理模块。</div>
    <div v-else class="dict-page">
      <div class="page-card panel">
        <div class="head">
          <div>
            <h3>标签管理</h3>
            <p>当前 PC 端基础字典仅维护共享标签表 tags。</p>
          </div>
          <el-button type="primary" size="small" @click="openDialog()">新增标签</el-button>
        </div>
        <el-table :data="tags" border stripe>
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="name" label="标签名称" />
          <el-table-column prop="createdAt" label="创建时间" width="180" />
          <el-table-column label="操作" width="150">
            <template slot-scope="scope">
              <el-button type="text" @click="openDialog(scope.row)">编辑</el-button>
              <el-button type="text" style="color:#e55;" @click="removeItem(scope.row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <el-dialog :visible.sync="dialogVisible" :title="dialogMode === 'create' ? '新增标签' : '编辑标签'" width="420px">
        <el-form ref="form" :model="form" :rules="rules" label-width="90px">
          <el-form-item label="标签名称" prop="name"><el-input v-model="form.name" /></el-form-item>
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
import { createTag, deleteTag, updateTag } from '@/api/admin';
import { listTags } from '@/api/resource';

export default {
  name: 'DictionaryManage',
  data() {
    return {
      tags: [],
      dialogVisible: false,
      dialogMode: 'create',
      form: { id: null, name: '' },
      rules: { name: [{ required: true, message: '请输入标签名称', trigger: 'blur' }] }
    };
  },
  computed: { isPrincipal() { return this.$store.getters.isPrincipal; } },
  created() { if (this.isPrincipal) this.loadData(); },
  methods: {
    async loadData() { this.tags = await listTags(); },
    openDialog(row) {
      this.dialogMode = row ? 'edit' : 'create';
      this.dialogVisible = true;
      this.form = row ? { ...row } : { id: null, name: '' };
    },
    async removeItem(row) {
      await this.$confirm('确认删除该标签吗？', '提示', { type: 'warning' });
      await deleteTag(row.id);
      this.$message.success('删除成功');
      await this.loadData();
    },
    submit() {
      this.$refs.form.validate(async valid => {
        if (!valid) return;
        if (this.dialogMode === 'create') await createTag({ name: this.form.name });
        else await updateTag(this.form.id, { name: this.form.name });
        this.$message.success('保存成功');
        this.dialogVisible = false;
        await this.loadData();
      });
    }
  }
};
</script>

<style scoped>
.dict-page { display:flex; flex-direction:column; gap:16px; }
.panel { padding:16px; }
.head { display:flex; align-items:center; justify-content:space-between; margin-bottom:14px; }
.head h3 { margin:0; font-size:16px; }
.head p { margin:6px 0 0; color:#7a8b9d; font-size:12px; }
.no-auth { padding:30px; text-align:center; color:#607a94; }
</style>
