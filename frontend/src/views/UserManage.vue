<template>
  <div>
    <div v-if="!isPrincipal" class="page-card no-auth">仅园长可访问用户管理模块。</div>
    <div v-else class="user-page">
      <div class="page-card panel toolbar">
        <el-form :inline="true" size="small">
          <el-form-item><el-input v-model="keyword" placeholder="用户名 / 姓名 / 手机号" clearable /></el-form-item>
          <el-form-item>
            <el-button type="primary" @click="loadUsers">查询</el-button>
            <el-button type="success" @click="openCreate">新增用户</el-button>
          </el-form-item>
        </el-form>
      </div>

      <div class="page-card panel">
        <el-table :data="users" border stripe v-loading="loading">
          <el-table-column prop="username" label="用户名" width="140" />
          <el-table-column prop="name" label="姓名" width="120" />
          <el-table-column prop="phone" label="手机号" width="140" />
          <el-table-column prop="roleName" label="角色" width="100" />
          <el-table-column prop="status" label="状态" width="100">
            <template slot-scope="scope">
              <el-tag :type="scope.row.status === 'active' ? 'success' : 'info'" size="mini">{{ scope.row.status === 'active' ? '启用' : '停用' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createdAt" label="创建时间" min-width="160" />
          <el-table-column label="操作" width="120">
            <template slot-scope="scope">
              <el-button type="text" @click="openEdit(scope.row)">编辑</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <el-dialog :visible.sync="dialogVisible" :title="dialogMode === 'create' ? '新增用户' : '编辑用户'" width="520px">
        <el-form ref="form" :model="form" :rules="rules" label-width="90px">
          <el-form-item label="用户名" prop="username" v-if="dialogMode === 'create'"><el-input v-model="form.username" /></el-form-item>
          <el-form-item label="密码" prop="password" v-if="dialogMode === 'create'"><el-input v-model="form.password" type="password" show-password /></el-form-item>
          <el-form-item label="姓名" prop="name"><el-input v-model="form.name" /></el-form-item>
          <el-form-item label="手机号" prop="phone"><el-input v-model="form.phone" /></el-form-item>
          <el-form-item label="角色" prop="roleId">
            <el-select v-model="form.roleId" style="width:100%;">
              <el-option v-for="r in roleOptions" :key="r.id" :label="r.roleName" :value="r.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="状态">
            <el-radio-group v-model="form.status">
              <el-radio label="active">启用</el-radio>
              <el-radio label="inactive">停用</el-radio>
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
import { createUser, listRoles, listUsers, updateUser } from '@/api/admin';

export default {
  name: 'UserManage',
  data() {
    return {
      loading: false,
      keyword: '',
      users: [],
      roleOptions: [],
      dialogVisible: false,
      dialogMode: 'create',
      form: { id: null, username: '', password: '', name: '', phone: '', roleId: 2, status: 'active' },
      rules: {
        username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
        name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
        phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
        roleId: [{ required: true, message: '请选择角色', trigger: 'change' }]
      }
    };
  },
  computed: { isPrincipal() { return this.$store.getters.isPrincipal; } },
  created() { if (this.isPrincipal) this.bootstrap(); },
  methods: {
    async bootstrap() {
      this.roleOptions = await listRoles();
      await this.loadUsers();
    },
    async loadUsers() {
      this.loading = true;
      try { this.users = await listUsers(this.keyword); } finally { this.loading = false; }
    },
    openCreate() { this.dialogMode = 'create'; this.dialogVisible = true; this.form = { id: null, username: '', password: '', name: '', phone: '', roleId: 2, status: 'active' }; },
    openEdit(row) { this.dialogMode = 'edit'; this.dialogVisible = true; this.form = { id: row.id, username: row.username, password: '', name: row.name, phone: row.phone, roleId: row.roleId, status: row.status || 'active' }; },
    submit() {
      this.$refs.form.validate(async valid => {
        if (!valid) return;
        if (this.dialogMode === 'create') await createUser(this.form);
        else await updateUser(this.form.id, { name: this.form.name, phone: this.form.phone, roleId: this.form.roleId, status: this.form.status });
        this.$message.success('保存成功');
        this.dialogVisible = false;
        await this.loadUsers();
      });
    }
  }
};
</script>

<style scoped>
.user-page { display:flex; flex-direction:column; gap:16px; }
.panel { padding:16px; }
.no-auth { padding:30px; text-align:center; color:#607a94; }
</style>
