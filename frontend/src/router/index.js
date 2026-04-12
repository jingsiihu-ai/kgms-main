import Vue from 'vue';
import Router from 'vue-router';
import store from '@/store';

Vue.use(Router);

const router = new Router({
  mode: 'history',
  routes: [
    { path: '/login', name: 'Login', component: () => import('@/views/Login.vue') },
    {
      path: '/',
      component: () => import('@/layout/MainLayout.vue'),
      redirect: '/dashboard',
      meta: { requiresAuth: true },
      children: [
        { path: 'dashboard', name: 'Dashboard', component: () => import('@/views/Dashboard.vue') },
        { path: 'resources', name: 'Resources', component: () => import('@/views/ResourceCenter.vue') },
        { path: 'users', name: 'Users', component: () => import('@/views/UserManage.vue') },
        { path: 'dictionary', name: 'Dictionary', component: () => import('@/views/DictionaryManage.vue') },
        { path: 'categories', name: 'Categories', component: () => import('@/views/CategoryManage.vue') }
      ]
    }
  ]
});

router.beforeEach(async (to, from, next) => {
  const hasToken = store.getters.isLoggedIn;
  if (to.path === '/login') {
    if (hasToken) next('/dashboard'); else next();
    return;
  }
  if (to.matched.some(record => record.meta.requiresAuth)) {
    if (!hasToken) {
      next('/login');
      return;
    }
    if (!store.state.user) {
      try {
        await store.dispatch('fetchMe');
      } catch (e) {
        store.commit('SET_TOKEN', '');
        next('/login');
        return;
      }
    }
  }
  next();
});

export default router;
