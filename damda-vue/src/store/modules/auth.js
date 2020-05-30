import jwtDecode from 'jwt-decode'
const state = {
  token: null,
  username: null,
  family: null,
  account: null 
}

const mutations = {
  setState(state, data) {
      state.token = data.token
      state.username = data.first_name
      state.family = data.family
      state.account = data.account
  },
}

const actions = {

  login(context, data) {
    context.commit('setState', data)
  },
  logout(context) {
    context.commit('setState', null)
  }
}

const getters = {
  options(state) {
    return {
      headers: {
        Authorization: `JWT ${state.token}`
      }
    }
  },
  user(state) {
    if (state.token) {
      return jwtDecode(state.token)
    }
    return false
  },
  userInfo(state) {
    if (state.user) {
      return state.user
    }
    return false
  },
}

export default {
  state,
  mutations,
  actions,
  getters
}
