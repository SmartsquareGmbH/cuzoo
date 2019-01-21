import Vue from "vue"
import Vuex from "vuex"

Vue.use(Vuex);

export default {
    state: {
        todos: [],
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
        editedTodo: state => state.editedTodo,
        editedTodoIndex: state => state.editedIndex
    },
    mutations: {
        storeTodos(state, payload) {
            state.todos = payload.todos
        },
        storeEditedTodoDetails(state, payload) {
            state.editedIndex = payload.editedIndex,
            state.editedTodo = payload.editedTodo
        }
    }
};
