import Vue from 'vue'
import VueRouter from 'vue-router'
import Login from './Login'
import MainPage from './MainPage'
Vue.use(VueRouter)

export default new VueRouter({
  mode: "history",
    routes: [
      {
        path: '/login',
        name: 'login',
        component: Login,
      },
      {
        path: '/',
        name: 'main',
        component: MainPage,
      },
      {
        path: '*',
        component: MainPage,
      }]
})
