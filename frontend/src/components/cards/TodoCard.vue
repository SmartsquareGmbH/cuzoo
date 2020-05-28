<template>
  <v-flex xs12>
    <v-hover>
      <v-card
        slot-scope="{ hover }"
        :color="`${hover || expandMenu ? '#616161' : ''}`"
        class="clickable elevation-0"
        @click="expandMenu = !expandMenu"
      >
        <v-card-title class="white--text subheading text-xs-left low-padding-bottom">
          <v-layout row wrap>
            <v-flex xs8>
              <p :class="`mb-0 ${fullDescription ? `` : `text-truncate`}`">
                {{ todo.description }}
              </p>
            </v-flex>
            <v-flex xs4 class="text-xs-right">
              <v-icon
                :style="`transform: rotate(${hover ? 0 : 0}deg)`"
                class="mr-1"
                :color="`${dateIsExpired(todo.expiration) ? 'error' : 'white'}`"
              >
                timer
              </v-icon>
              <chip class="mb-2" :font-color="getUrgency(todo.expiration)">
                {{ distanceInWords }}
              </chip>
            </v-flex>
          </v-layout>
        </v-card-title>
        <v-expand-transition>
          <div v-if="expandMenu" color="success">
            <v-card-title class="title font-weight-light todo-footer">
              <v-icon class="mr-1">business</v-icon>
              <v-tooltip top>
                <chip slot="activator" font-color="primary">
                  {{ todo.company.name | truncate(30) }}
                </chip>
                <span class="title font-weight-light">{{ todo.company.name }}</span>
              </v-tooltip>
              <v-icon class="ml-1">person</v-icon>
              <chip font-color="primary">
                {{ todo.creator }}
              </chip>
              <v-spacer />
              <v-btn small color="cyan darken-1" @click="editToDo(todo)">
                <v-icon size="24px">edit</v-icon>
              </v-btn>
              <v-btn small color="success" @click="taskIsDone(todo)">
                <v-icon size="24px" class="ml-0">done</v-icon>
              </v-btn>
            </v-card-title>
          </div>
        </v-expand-transition>
        <slot />
      </v-card>
    </v-hover>
    <todo-dialog
      v-model="todoDialogState"
      :companies="mappedCompanies"
      @refresh="refreshTodos()"
      @input="todoDialogState = false"
    ></todo-dialog>
  </v-flex>
</template>

<script>
import api from "../../utils/http-common"

import Chip from "../core/Chip.vue"
import { mapActions, mapGetters, mapMutations } from "vuex"
import TodoDialog from "../dialogs/TodoDialog.vue"

const datefns = require("date-fns")
const de = require("date-fns/locale/de")

export default {
  components: {
    TodoDialog,
    Chip,
  },
  props: ["todo"],
  data: () => ({
    mappedCompanies: [],
    expandMenu: false,
    fullDescription: false,
    todoDialogState: false,
  }),
  computed: {
    ...mapGetters(["companies", "todos", "selectedCompany"]),
    distanceInWords() {
      if (this.expandMenu) return this.expirationDate
      if (this.dateIsExpired(this.todo.expiration)) return "Abgelaufen"

      return datefns.distanceInWords(this.todo.expiration, new Date(), { locale: de })
    },
    expirationDate() {
      return datefns.format(this.todo.expiration, "DD.MM.YY", { locale: de })
    },
  },
  watch: {
    expandMenu(value) {
      if (value) {
        this.fullDescription = value
      } else {
        setTimeout(() => {
          this.fullDescription = value
        }, 65)
      }
    },
  },
  beforeMount() {
    this.refreshTodos()
  },
  methods: {
    ...mapActions(["getCompanies", "getTodos"]),
    ...mapMutations({
      storeEditedTodoDetails: "storeEditedTodoDetails",
      storeEditedTodoCompany: "storeEditedTodoCompany",
    }),
    taskIsDone(todo) {
      todo.done = true
      api.put(`todo/done/${this.todo.id}`)
    },
    getUrgency(expiration) {
      let differenceInHours = datefns.differenceInHours(expiration, new Date())

      if (differenceInHours < 48) {
        return "error"
      } else if (differenceInHours < 168) {
        return "warning"
      } else {
        return "primary"
      }
    },
    dateIsExpired(date) {
      return datefns.isPast(date)
    },
    editToDo(todo) {
      this.storeEditedTodoDetails({
        creator: this.todos.username,
        editedIndex: this.todos.indexOf(todo),
        editedTodo: Object.assign({}, todo),
      })
      this.storeEditedTodoCompany({
        editedCompany: Object.assign({}, { id: todo.company.id, name: todo.company.name }),
      })
      this.mappedCompanies = this.companies.map((it) => Object.assign({}, { id: it.id, name: it.name })).sort()
      this.todoDialogState = true
    },
    async refreshTodos() {
      await this.getCompanies()
      await this.getTodos()
      this.mappedCompanies = this.companies.map((company) => Object.assign({}, { id: company.id, name: company.name }))
    },
  },
}
</script>

<style scoped>
.low-padding-bottom {
  padding-bottom: 6px;
}

.todo-footer {
  padding-top: 0;
  padding-right: 12px;
  padding-bottom: 6px;
}
</style>
