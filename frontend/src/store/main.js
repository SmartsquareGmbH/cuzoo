export default {
    state: {
        darkState: true,
        selectedCompanyOnDash: '',
        todoWidgetListHeight: 32
    },
    getters: {
        darkState: state => state.darkState,
        selectedCompany: state => state.selectedCompanyOnDash,
        todoWidgetListHeight: state => state.todoWidgetListHeight
    },
    mutations: {
        storeDarkState(state, payload) {
            state.darkState = payload.darkState
        },
        storeSelectedCompany(state, payload) {
            state.selectedCompanyOnDash = payload.selectedCompanyOnDash
        },
        storeTodoWidgetListHeight(state, payload) {
            state.todoWidgetListHeight = payload.todoWidgetListHeight
        }
    }
};
