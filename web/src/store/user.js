import $ from "jquery"

export default {
    state: {
        id: "", // 用户ID
        username: "", // 用户名
        photo: "", // 用户头像
        jwtToken: "", // JWT Token
        isLoggedIn: false // 是否已登录
    },
    getters: {},
    mutations: {
        // 更新用户信息
        updateUser(state, user) {
            state.id = user.id;
            state.username = user.username;
            state.photo = user.photo;
            state.isLoggedIn = user.isLoggedIn;
        },
        // 更新JWT Token
        updateToken(state, token) {
            state.jwtToken = token;
        },
        // 清除用户信息
        clearUser(state) {
            state.id = "";
            state.username = "";
            state.photo = "";
            state.jwtToken = "";
            state.isLoggedIn = false;
        }
    },
    actions: {
        // 用户登录
        login(context, data) {
            $.ajax({
                // 请求URL
                url: "http://localhost:3000/user/account/token",
                // 请求类型
                type: "POST",
                // 请求数据
                data: {
                    username: data.username,
                    password: data.password,
                },
                // 请求成功回调
                success(response) {
                    // 如果响应的状态消息为"Success"
                    if (response.status_message === "Success") {
                        // 将token信息储存到本地
                        localStorage.setItem("jwtToken",response.token);
                        // 将响应的token信息更新到token
                        context.commit("updateToken", response.token);
                        // 调用成功回调函数
                        data.success(response);
                    } else {
                        // 调用错误回调函数
                        data.error(response);
                    }
                },
                // 请求错误回调
                error(response) {
                    data.error(response);
                }
            });
        },
        // 获取用户信息
        getInfo(context, data) {
            $.ajax({
                // 请求URL
                url: "http://localhost:3000/user/account/info",
                // 请求类型
                type: "GET",
                // 将ajax函数从默认异步修改为同步的
                async: false,
                // 请求头部信息
                headers: {
                    // 添加授权头，用于身份验证
                    Authorization: "Bearer " + context.state.jwtToken,
                },
                // 请求成功回调
                success(response) {
                    // 如果响应的状态消息为"Success"
                    if (response.status_message === "Success") {
                        // 将响应的数据更新到用户信息，并设置isLoggedIn为true
                        context.commit("updateUser", {
                            ...response,
                            isLoggedIn: true,
                        });
                        // 调用成功回调函数，传递更新后的用户信息
                        data.success(response);
                    } else {
                        // 调用错误回调函数，传递错误的响应数据
                        data.error(response);
                    }
                },
                // 请求错误回调
                error(response) {
                    // 调用错误回调函数，传递错误的响应数据
                    data.error(response);
                }
            });
        },
        // 登出用户
        logout(context){
            // 清除用户信息
            localStorage.removeItem("jwtToken");
            context.commit("clearUser");
        }
    },
    modules: {}
}
