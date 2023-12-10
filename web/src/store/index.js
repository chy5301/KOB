import {createStore} from 'vuex'
import UserModel from '@/store/user'
import PkModel from '@/store/pk'

export default createStore({
    state: {},
    getters: {},
    mutations: {},
    actions: {},
    modules: {
        user: UserModel,
        pk: PkModel,
    }
})
