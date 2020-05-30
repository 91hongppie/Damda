import jwtDecode from 'jwt-decode'
// state : data와 유사
const state = {
  token: null,
  user: null,
}

const mutations = {
  setToken(state, token) {
      state.token = token
  },
  setUser(state, user) {
    state.child = user
  }
}

const actions = {

  login(context, token) {
    context.commit('setToken', token)
  },
  logout(context) {
    context.commit('setToken', null)
    context.commit('setUser', null)
  },
  child(context, childData) {
    context.commit('setChild', childData)
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
