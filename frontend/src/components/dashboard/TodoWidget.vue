<template>
  <v-flex xs12>
    <v-card :class="`pl-0 mb-3 mt-${!(breakpoint.lg || breakpoint.xl) ? '4' : '0'}`" elevation="6" height="75">
      <v-layout row wrap text-xs-left>
        <v-flex xs2>
          <v-sheet elevation="8" class="v-sheet--offset text-xs-center" height="75" width="100" color="primary">
            <v-icon color="secondary" size="56px" class="pt-2">
              done_all
            </v-icon>
          </v-sheet>
        </v-flex>
        <v-flex xs2 class="more-padding-top">
          <span v-show="breakpoint.width > 1680" class="headline font-weight-light pl-3">
            TODOs
          </span>
        </v-flex>
        <v-flex xs2 style="padding-top: 12px;" class="text-xs-right">
          <v-btn small flat fab color="transparent" @click="addTODO()">
            <v-tooltip top>
              <v-icon slot="activator" large color="light-green accent-2">
                add
              </v-icon>
              <span>TODO hinzufügen</span>
            </v-tooltip>
          </v-btn>
          <todo-dialog
            v-model="todoDialogState"
            :companies="mappedCompanies"
            @refresh="refreshTodos()"
            @input="todoDialogState = false"
          />
        </v-flex>
        <v-flex xs6>
          <v-text-field
            ref="searchBarTodos"
            v-model="searchTodos"
            class="pr-3"
            append-icon="search"
            label="Suche nach TODOs"
            color="primary"
            hide-details
          />
        </v-flex>
      </v-layout>
    </v-card>
    <v-card elevation="6">
      <v-layout v-resize="onResize" row wrap>
        <div class="dash">
          <perfect-scrollbar :options="settings">
            <v-flex xs12 class="todo-results" :style="`height: ${widgetHeight}px`">
              <todo-results :search="searchTodos" />
            </v-flex>
          </perfect-scrollbar>
        </div>
      </v-layout>
    </v-card>
  </v-flex>
</template>

<script>
import { mapGetters } from "vuex"
import TodoDialog from "../dialogs/TodoDialog.vue"
import TodoResults from "../search/TodoResults.vue"

export default {
  components: {
    TodoDialog,
    TodoResults,
  },
  data: () => ({
    windowHeight: 0,
    mappedCompanies: [],
    todoDialogState: false,
    searchTodos: "",
    settings: {
      maxScrollbarLength: 120,
      wheelSpeed: 0.75,
      suppressScrollX: true,
    },
  }),
  computed: {
    ...mapGetters(["companies", "todos", "selectedCompany"]),
    breakpoint() {
      return this.$vuetify.breakpoint
    },
    widgetHeight() {
      const height = this.windowHeight / 5.54
      const cardHeight = 54
      const excess = height % cardHeight

      if (excess < cardHeight / 2) return height - excess
      else return height + (cardHeight - excess)
    },
  },
  watch: {
    selectedCompany() {
      if (!this.selectedCompany || this.companies.map((it) => it.name).includes(this.selectedCompany)) {
        this.searchTodos = this.selectedCompany
      }
    },
  },
  beforeMount() {
    this.onResize()
  },
  methods: {
    addTODO() {
      this.mappedCompanies = this.companies.map((it) => Object.assign({}, { id: it.id, name: it.name })).sort()
      this.todoDialogState = true
    },
    onResize() {
      this.windowHeight = window.innerHeight
    },
    refreshTodos() {
      this.$parent.refreshData()
    },
  },
}
</script>

<style scoped>
.dash {
  padding-right: 4px;
  height: 100%;
  width: 100%;
  position: relative;
}

.v-sheet--offset {
  top: -24px;
  left: 16px;
  position: relative;
}

.more-padding-top {
  padding-top: 20px !important;
}

.todo-results {
  padding: 0 0 0 4px !important;
  margin: 0 !important;
}
</style>
