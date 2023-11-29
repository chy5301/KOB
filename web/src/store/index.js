import {createStore} from 'vuex'
import UserModel from '@/store/user'

export default createStore({
    state: {},
    getters: {},
    mutations: {},
    actions: {},
    modules: {
        user: UserModel,
    }
})
