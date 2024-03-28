export default {
    state: {
        isRecord: false,
        player1Username: "",
        player1Photo: "",
        player2Username: "",
        player2Photo: "",
        player1Steps: [],
        player2Steps: [],
        recordLoser: "",
    },
    getters: {},
    mutations: {
        updateIsRecord(state, isRecord) {
            state.isRecord = isRecord;
        },
        updateRecordInfo(state, data) {
            state.player1Steps = data.player1Steps;
            state.player2Steps = data.player2Steps;
            state.recordLoser = data.recordLoser;
        },
        updateRecordPlayersInfo(state, recordPlayersInfo) {
            state.player1Username = recordPlayersInfo.player1_username;
            state.player1Photo = recordPlayersInfo.player1_photo;
            state.player2Username = recordPlayersInfo.player2_username;
            state.player2Photo = recordPlayersInfo.player2_photo;
        },
    },
    actions: {},
    modules: {}
}