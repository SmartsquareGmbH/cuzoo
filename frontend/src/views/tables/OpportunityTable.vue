<template>
  <v-container fluid>
    <v-card-title class="px-1">
      <h1 class="mr-3">
        <v-icon size="32px">bubble_chart</v-icon>
        OPPORTUNITIES
      </h1>
      <v-spacer></v-spacer>
      <v-text-field v-model="search" append-icon="search" label="Suche ..." single-line hide-details />
    </v-card-title>
    <v-data-table
      :headers="headers"
      :items="opportunities"
      :loading="loading"
      :search="search"
      rows-per-page-text="Unternehmen pro Seite"
      :rows-per-page-items="[10, 25, 50, 100]"
      dark
    >
      <v-progress-linear slot="progress" color="blue" indeterminate />
      <template slot="items" slot-scope="props">
        <tr
          v-if="!!props.item"
          style="cursor: pointer;"
          class="text-xs-left vertical-center"
          @click="viewOpportunity(props.item.id)"
        >
          <td>{{ props.item.title }}</td>
          <td>
            <v-icon :color="getStateColor(props.item.state)" size="16px">
              bubble_chart
            </v-icon>
            {{ props.item.state }}
          </td>
          <v-tooltip v-if="props.item.description.length > 140" top max-width="750">
            <td slot="activator" class="vertical-center">
              {{ props.item.description | truncate(140) }}
            </td>
            {{ props.item.description }}
          </v-tooltip>
          <td v-else>{{ props.item.description }}</td>
          <td>
            <v-tooltip top>
              <v-icon slot="activator" size="22px" color="red lighten-1" @click.stop="openConfirmDialog(props.item)">
                delete
              </v-icon>
              Opportunity löschen
            </v-tooltip>
          </td>
        </tr>
      </template>
      <span slot="no-data">
        Keine Daten verfügbar :'(
      </span>
      <v-alert slot="no-results" :value="true" color="error" icon="warning">
        Deine Suche nach "{{ search }}" ergab keinen Treffer :'(
      </v-alert>
    </v-data-table>
    <confirm-dialog
      v-model="confirmDialogState"
      :question-to-be-confirmed="deleteOpportunityMessage"
      @confirmed="deleteOpportunity()"
    />
  </v-container>
</template>

<script>
import { mapActions, mapGetters } from "vuex"
import api from "../../utils/http-common"
import ConfirmDialog from "../../components/dialogs/ConfirmDialog.vue"

export default {
  components: {
    ConfirmDialog,
  },
  data() {
    return {
      confirmDialogState: false,
      deleteOpportunityMessage: "Bist du dir sicher, dass du diese Opportunity entgültig löschen willst?",
      search: "",
      loading: true,
      headers: [
        { text: "Titel", value: "title", align: "left" },
        { text: "Status", value: "state" },
        { text: "Beschreibung", value: "description" },
        { text: "Aktionen", value: "name", sortable: false },
      ],
    }
  },
  computed: {
    ...mapGetters(["opportunities"]),
  },
  mounted() {
    this.refreshTable()
  },
  methods: {
    ...mapActions(["getOpportunities"]),
    refreshTable() {
      this.getOpportunities().then(() => (this.loading = false))
    },
    deleteOpportunity() {
      api
        .delete(`opportunity/delete/${this.editedOpportunity.id}`)
        .then(() => {
          this.refreshTable()
        })
        .catch((error) => {
          console.log(error)
          alert(error)
        })
    },
    openConfirmDialog(item) {
      this.editedOpportunity = Object.assign({}, item)

      this.confirmDialogState = true
    },
    viewOpportunity(id) {
      this.$router.push("/opportunities/" + id)
    },
    getStateColor(state) {
      switch (state) {
        case "Lead":
          return "error"
        case "Prospect":
          return "warning"
        case "Quote":
          return "success"
        case "Win":
          return "primary"
      }
    },
  },
}
</script>

<style scoped>
.input-file + label {
  cursor: pointer;
}

.vertical-center {
  vertical-align: middle !important;
}
</style>
