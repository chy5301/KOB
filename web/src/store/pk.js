export default {
    state: {
        // 状态：matching = 匹配，playing = 对战
        status: "matching",
        socket: null,
        opponentUsername: "",
        opponentPhoto: "",
    },
    getters: {},
    mutations: {
        updateSocket(state, socket) {
            state.socket = socket;
        },
        updateOpponent(state, opponent) {
            state.opponentUsername = opponent.username;
            state.opponentPhoto = opponent.photo;
        },
        updateStatus(state, status) {
            state.status = status;
        }
    },
    actions: {},
    modules: {}
}