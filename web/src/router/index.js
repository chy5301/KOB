import {createRouter, createWebHistory} from 'vue-router'
import PkIndexView from "@/views/pk/PkIndexView.vue";
import RecordIndexView from "@/views/record/RecordIndexView.vue";
import RankListIndexView from "@/views/ranklist/RankListIndexView.vue";
import UserBotIndexView from "@/views/user/bot/UserBotIndexView.vue";
import NotFound from "@/views/error/NotFound.vue";
import UserAccountLoginView from "@/views/user/account/UserAccountLoginView.vue";
import UserAccountRegisterView from "@/views/user/account/UserAccountRegisterView.vue";
import store from "@/store/index";

const routes = [
    {
        path: "/",
        name: "home",
        redirect: "/pk",
        meta: {
            needsAuthorization: true,
        }
    },
    {
        path: "/pk",
        name: "pk_index",
        component: PkIndexView,
        meta: {
            needsAuthorization: true,
        },
    },
    {
        path: "/record",
        name: "record_index",
        component: RecordIndexView,
        meta: {
            needsAuthorization: true,
        },
    },
    {
        path: "/ranklist",
        name: "ranklist_index",
        component: RankListIndexView,
        meta: {
            needsAuthorization: true,
        },
    },
    {
        path: "/user/bot",
        name: "user_bot_index",
        component: UserBotIndexView,
        meta: {
            needsAuthorization: true,
        },
    },
    {
        path: "/user/account/login",
        name: "user_account_login",
        component: UserAccountLoginView,
        meta: {
            needsAuthorization: false,
        },
    },
    {
        path: "/user/account/register",
        name: "user_account_register",
        component: UserAccountRegisterView,
        meta: {
            needsAuthorization: false,
        },
    },
    {
        path: "/:catchAll(.*)",
        name: "not_found",
        component: NotFound,
        meta: {
            needsAuthorization: false,
        },
    },
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

// 在每个router起作用前执行的函数，to：表示要跳转到哪个页面，from：表示从哪个页面跳转过去，next：表示页面执行的下一步跳转操作
router.beforeEach((to, from, next) => {
    const jwtToken = localStorage.getItem("jwtToken");

    // 如果jwtToken存在
    if (jwtToken) {
        store.commit("updateToken", jwtToken);
        store.dispatch("getInfo", {
            // 如果getInfo函数验证了jwtToken是有效的
            success() {
                // 直接跳转
                next();
            },
            error() {
                alert("登录信息已过期，请重新登录！");
                // 调用logout函数清除浏览器内存和localStorage中的jwtToken
                store.dispatch("logout");
                // 跳转到登录页面
                next({name: "user_account_login"});
            }
        })
    }

    if (to.meta.needsAuthorization && !store.state.user.isLoggedIn) {
        next({name: "user_account_login"});
    } else {
        next();
    }
})

export default router
