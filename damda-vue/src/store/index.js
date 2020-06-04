import Vue from 'vue'
import Vuex from 'vuex'
import auth from './modules/auth'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    server: 'https://k02b2051.p.ssafy.io/',
  },
  mutations: {
  },
  actions: {
  },
  modules: {
    auth
  }
})
