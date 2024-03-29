import api from "../utils/http-common"

const datefns = require("date-fns")

export default {
  state: {
    todos: [],
    editedIndex: -1,
    editedCompany: {},
    editedTodo: {
      id: 0,
      description: "",
      company: {},
      expiration: "",
      reminder: "",
      done: false,
    },
  },
  getters: {
    todos: (state) => state.todos,
    editedTodo: (state) => state.editedTodo,
    editedTodoIndex: (state) => state.editedIndex,
    editedTodoCompany: (state) => state.editedCompany,
  },
  mutations: {
    storeTodos(state, payload) {
      state.todos = payload.todos
    },
    storeEditedTodoDetails(state, payload) {
      state.editedIndex = payload.editedIndex
      state.editedTodo = payload.editedTodo
    },
    storeEditedTodoCompany(state, payload) {
      state.editedCompany = payload.editedCompany
    },
  },
  actions: {
    getTodos() {
      return api
        .get("todo/get/")
        .then((response) => {
          let todos = response.data

          todos.forEach((it) => {
            it.creator = it.creator.username
          })

          this.commit({
            type: "storeTodos",
            todos: todos.sort(compareTodos),
          })
        })
        .catch((error) => {
          console.log(error)
        })
    },
  },
}

function compareTodos(a, b) {
  if (datefns.compareAsc(a.expiration, b.expiration) === 0) {
    if (a.id < b.id) return 1
    if (a.id > b.id) return -1
  } else {
    return datefns.compareAsc(a.expiration, b.expiration)
  }
}
