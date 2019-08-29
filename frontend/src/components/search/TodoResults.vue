<template>
  <v-flex v-if="searchResults.length > 0" xs12 class="pa-0 ma-0">
    <todo-card v-for="(todo, index) in searchResults" :key="todo.id" class="todo-results" :todo="todo">
      <v-divider v-if="index !== searchResults.length - 1" />
    </todo-card>
  </v-flex>
  <v-flex v-else text-xs-center class="pt-4">
    <span class="error--text font-italic subheading">Keine TODOs gefunden</span>
  </v-flex>
</template>

<script>
import { mapGetters } from "vuex"
import TodoCard from "../cards/TodoCard.vue"

export default {
  components: {
    TodoCard,
  },
  props: ["search"],
  data: () => ({
    searchTerms: [],
  }),
  computed: {
    ...mapGetters(["todos"]),
    searchResults() {
      return this.todos
        .filter((todo) => todo.done === false)
        .filter((todo) => {
          this.defineSearchTerms(todo)

          if (this.search) {
            return this.searchTerms.some((term) => term.includes(this.search.toLowerCase()))
          } else {
            return this
          }
        })
        .splice(0, 10)
    },
  },
  methods: {
    defineSearchTerms(todo) {
      this.searchTerms = []

      for (let key in todo) {
        // eslint-disable-next-line no-prototype-builtins
        if (todo.hasOwnProperty(key) && todo[key]) {
          if (key === "company") {
            this.searchTerms.push(todo[key].name.toLowerCase())
          } else {
            this.searchTerms.push(todo[key].toString().toLowerCase())
          }
        }
      }
    },
  },
}
</script>

<style scoped>
.todo-results {
  margin-bottom: 0 !important;
  margin-left: 0 !important;
  margin-right: 0 !important;
}
</style>
