export default {
    state: {
        selectedCompanyOnDash: '',
        todoWidgetListHeight: 32
    },
    getters: {
        selectedCompany: state => state.selectedCompanyOnDash,
        todoWidgetListHeight: state => state.todoWidgetListHeight
    },
    mutations: {
        storeSelectedCompany(state, payload) {
            state.selectedCompanyOnDash = payload.selectedCompanyOnDash
        },
        storeTodoWidgetListHeight(state, payload) {
            state.todoWidgetListHeight = payload.todoWidgetListHeight
        }
    }
};
