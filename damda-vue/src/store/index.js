import Vue from 'vue'
import Vuex from 'vuex'
import auth from './modules/auth'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    server: 'https://k02b2051.p.ssafy.io/',
    // server: 'http://localhost:8000',
  },
  mutations: {
  },
  actions: {
  },
  modules: {
    auth
  }
})
