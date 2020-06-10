import Vue from 'vue'
import store from './store'
import router from './router'
import App from './App.vue'
import vuetify from './plugins/vuetify'
import VueSession from 'vue-session'

Vue.config.productionTip = false
var sessionOptions = {
  persist: true
}
Vue.use(VueSession, sessionOptions)

new Vue({
  router,
  vuetify,
  store,
  render: h => h(App)
}).$mount('#app')
