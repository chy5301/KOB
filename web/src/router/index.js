import {createRouter, createWebHistory} from 'vue-router'
import PkIndexView from "@/views/pk/PkIndexView.vue";
import RecordIndexView from "@/views/record/RecordIndexView.vue";
import RankListIndexView from "@/views/ranklist/RankListIndexView.vue";
import UserBotIndexView from "@/views/user/bot/UserBotIndexView.vue";
import NotFound from "@/views/error/NotFound.vue";
import UserAccountLoginView from "@/views/user/account/UserAccountLoginView.vue";
import UserAccountRegisterView from "@/views/user/account/UserAccountRegisterView.vue";

const routes = [
    {
        path: "/",
        name: "home",
        redirect: "/pk",
    },
    {
        path: "/pk",
        name: "pk_index",
        component: PkIndexView,
    },
    {
        path: "/record",
        name: "record_index",
        component: RecordIndexView,
    },
    {
        path: "/ranklist",
        name: "ranklist_index",
        component: RankListIndexView,
    },
    {
        path: "/user/bot",
        name: "user_bot_index",
        component: UserBotIndexView,
    },
    {
        path: "/user/account/login",
        name: "user_account_login",
        component: UserAccountLoginView,
    },
    {
        path: "/user/account/register",
        name: "user_account_register",
        component: UserAccountRegisterView,
    },
    {
        path: "/:catchAll(.*)",
        name: "not_found",
        component: NotFound,
    },
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router
