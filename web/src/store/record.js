export default {
    state: {
        isRecord: false,
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
    },
    actions: {},
    modules: {}
}