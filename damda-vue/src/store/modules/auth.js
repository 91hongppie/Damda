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
      state.account = data.username
  },
}

const actions = {

  login(context, data) {
    context.commit('setState', data)
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
      return {id: jwtDecode(state.token),
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
