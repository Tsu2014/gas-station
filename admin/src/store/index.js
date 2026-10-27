import Vue from 'vue'
import Vuex from 'vuex'
import { login, getInfo } from '@/api/auth'

Vue.use(Vuex)

const TOKEN_KEY = 'gas_admin_token'
const USER_KEY = 'gas_admin_user'

export default new Vuex.Store({
  modules: {
    user: {
      namespaced: true,
      state: {
        token: localStorage.getItem(TOKEN_KEY) || '',
        info: JSON.parse(localStorage.getItem(USER_KEY) || '{}')
      },
      getters: {
        token: state => state.token,
        info: state => state.info
      },
      mutations: {
        SET_TOKEN(state, token) {
          state.token = token
          localStorage.setItem(TOKEN_KEY, token)
        },
        SET_INFO(state, info) {
          state.info = info
          localStorage.setItem(USER_KEY, JSON.stringify(info))
        },
        CLEAR(state) {
          state.token = ''
          state.info = {}
          localStorage.removeItem(TOKEN_KEY)
          localStorage.removeItem(USER_KEY)
        }
      },
      actions: {
        login({ commit }, data) {
          return login(data).then(res => {
            commit('SET_TOKEN', res.token)
            commit('SET_INFO', res)
            return res
          })
        },
        getInfo({ commit }) {
          return getInfo().then(res => {
            commit('SET_INFO', Object.assign({}, JSON.parse(localStorage.getItem(USER_KEY) || '{}'), res))
            return res
          })
        },
        logout({ commit }) {
          commit('CLEAR')
        }
      }
    }
  }
})
