export default {
    state: {
        // 状态：matching = 匹配，playing = 对战
        status: "matching",
        socket: null,
        opponentUsername: "",
        opponentPhoto: "",
        gameMap: null,
        player1Id: 0,
        player1StartX: 0,
        player1StartY: 0,
        player2Id: 0,
        player2StartX: 0,
        player2StartY: 0,
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
        },
        updateGame(state, gameInfo) {
            state.gameMap = gameInfo.game_map;
            state.player1Id = gameInfo.player1_id;
            state.player1StartX = gameInfo.player1_startX;
            state.player1StartY = gameInfo.player1_startY;
            state.player2Id = gameInfo.player2_id;
            state.player2StartX = gameInfo.player2_startX;
            state.player2StartY = gameInfo.player2_startY;
        }
    },
    actions: {},
    modules: {}
}