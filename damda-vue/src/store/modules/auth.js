import jwtDecode from 'jwt-decode'
const state = {
  token: null,
  username: null,
  family: null,
  account: null 
}

const mutations = {
  setState(state, data) {
    if (data.token) {
      state.token = data.token
    }
      state.username = data.first_name
      state.family = data.family
      state.account = data.username
  },
  setToken(state, token) {
    state.token = token
  }
}

const actions = {

  login(context, data) {
    context.commit('setState', data)
  },
  kakaoLogin(context, token) {
    context.commit('setToken', token)
  },
  logout(context) {
    context.commit('setState', {
      token: null,
      username: null,
      family: null,
      account: null 
    })
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
      const info = jwtDecode(state.token)
      return {id: info.user_id,
              username: state.username,
              account: state.account}
    }
    return false
  },
  family(state) {
    if (state.family) {
      return state.family
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
