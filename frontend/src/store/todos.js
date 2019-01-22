export default {
    state: {
        todos: [],
        sortedTodos: [],
        editedIndex: -1,
        editedTodo: {
            id: 0,
            description: '',
            company: {},
            expiration: '',
            reminder: '',
            done: false
        }
    },
    getters: {
        todos: state => state.todos,
        sortedTodos: state => state.sortedTodos,
        editedTodo: state => state.editedTodo,
        editedTodoIndex: state => state.editedIndex
    },
    mutations: {
        storeTodos(state, payload) {
            state.todos = payload.todos,
            state.sortedTodos = payload.sortedTodos
        },
        storeEditedTodoDetails(state, payload) {
            state.editedIndex = payload.editedIndex,
            state.editedTodo = payload.editedTodo
        }
    }
};
