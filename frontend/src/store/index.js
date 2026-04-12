import Vue from 'vue';
import Vuex from 'vuex';
import { login, getMe, logout } from '@/api/auth';

Vue.use(Vuex);

export default new Vuex.Store({
  state: {
    token: localStorage.getItem('kgms_token') || '',
    user: null
  },
  getters: {
    isLoggedIn: state => !!state.token,
    roles: state => (state.user && state.user.roles) || [],
    isPrincipal: (state, getters) => getters.roles.includes('PRINCIPAL'),
    userName: state => (state.user && state.user.name) || ''
  },
  mutations: {
    SET_TOKEN(state, token) {
      state.token = token;
      if (token) localStorage.setItem('kgms_token', token);
      else localStorage.removeItem('kgms_token');
    },
    SET_USER(state, user) {
      state.user = user;
    }
  },
  actions: {
    async doLogin({ commit }, payload) {
      const data = await login(payload);
      commit('SET_TOKEN', data.token);
      commit('SET_USER', data.user);
      return data;
    },
    async fetchMe({ commit }) {
      const user = await getMe();
      commit('SET_USER', user);
      return user;
    },
    async doLogout({ commit }) {
      try {
        await logout();
      } finally {
        commit('SET_TOKEN', '');
        commit('SET_USER', null);
      }
    }
  }
});
