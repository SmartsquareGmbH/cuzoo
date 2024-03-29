<template>
  <v-container fluid>
    <v-card-title class="px-1">
      <h1 class="mr-3">
        <v-icon size="32px">people</v-icon>
        ANSPRECHPARTNER
      </h1>
      <input v-if="!isDemo" id="file" ref="file" class="input-file" type="file" @change="handleUpload()" />
      <label for="file">
        <v-tooltip top>
          <v-icon slot="activator" :color="isDemo ? 'grey' : 'primary'" x-large>
            publish
          </v-icon>
          <span>CSV Import</span>
          <span v-show="isDemo" class="font-italic"> (deaktiviert)</span>
        </v-tooltip>
      </label>
      <v-btn depressed fab light small color="transparent" flat @click="openContactDialog()">
        <v-tooltip top>
          <v-icon slot="activator" x-large color="light-green accent-2">add</v-icon>
          <span>Ansprechpartner hinzufügen</span>
        </v-tooltip>
      </v-btn>
      <contact-dialog v-model="contactDialogState" :companies="mappedCompanies" @input="contactDialogState = false" />
      <v-spacer></v-spacer>
      <v-text-field v-model="search" append-icon="search" label="Suche ..." single-line hide-details />
    </v-card-title>
    <v-data-table
      :headers="headers"
      :items="contacts"
      :search="search"
      :loading="loading"
      rows-per-page-text="Ansprechpartner pro Seite"
      :rows-per-page-items="[10, 25, 50, 100]"
      dark
    >
      <v-progress-linear slot="progress" color="blue" indeterminate />
      <template slot="items" slot-scope="props">
        <tr
          v-if="!!props.item"
          style="cursor: pointer;"
          class="text-xs-left vertical-center"
          @click="editContact(props.item)"
        >
          <td>{{ props.item.name }}</td>
          <td v-if="props.item.company != null" class="text-xs-left">{{ props.item.company.name }}</td>
          <td v-else class="font-weight-light error--text">N/A</td>
          <td>{{ props.item.role }}</td>
          <td>{{ props.item.mail }}</td>
          <td>{{ props.item.telephone }}</td>
          <td>{{ props.item.mobile }}</td>
          <td>{{ props.item.manager }}</td>
          <td>
            <v-chip v-for="(label, index) in props.item.labels" :key="index" small>
              {{ label }}
            </v-chip>
          </td>
          <td class="justify-center layout px-0">
            <v-icon size="22px" class="mr-0 mt-2" @click.stop="editContact(props.item)">
              edit
            </v-icon>
            <v-tooltip top>
              <v-btn
                slot="activator"
                color="transparent"
                class="mr-2"
                flat
                icon
                small
                @click.stop="downloadInfo(props.item)"
              >
                <v-icon size="22px" color="white" style="transform: rotate(180deg)" dark>
                  publish
                </v-icon>
              </v-btn>
              <span>Export Informationen</span>
            </v-tooltip>
            <v-icon size="22px" class="mr-2 mt-2" color="red lighten-1" @click.stop="openConfirmDialog(props.item)">
              delete
            </v-icon>
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
      :question-to-be-confirmed="deleteContactMessage"
      @confirmed="deleteContact"
    />
  </v-container>
</template>

<script>
import { mapActions, mapGetters, mapMutations } from "vuex"
import api from "../../utils/http-common"

import ContactDialog from "../../components/dialogs/ContactDialog.vue"
import ConfirmDialog from "../../components/dialogs/ConfirmDialog.vue"

export default {
  components: {
    ContactDialog,
    ConfirmDialog,
  },
  data() {
    return {
      file: "",
      search: "",
      mappedCompanies: [],
      loading: true,
      contactDialogState: false,
      headers: [
        { text: "Name", align: "left", value: "name" },
        { text: "Unternehmen", value: "company.name" },
        { text: "Rolle", value: "role" },
        { text: "E-Mail", value: "mail" },
        { text: "Telefon", value: "telephone" },
        { text: "Mobil", value: "mobile" },
        { text: "Manager", value: "manager" },
        { text: "Labels", value: "labels", sortable: false },
        { text: "Aktionen", value: "name", sortable: false },
      ],
      confirmDialogState: false,
      deleteContactMessage: "Bist du dir sicher, dass du diesen Ansprechpartner löschen willst?",
    }
  },
  computed: {
    ...mapGetters(["companies", "contacts"]),
    isDemo() {
      return process.env.VUE_APP_ENV_MODE === "demo"
    },
  },
  beforeMount() {
    this.refreshTable()
  },
  methods: {
    ...mapActions(["getContacts", "getCompanies"]),
    ...mapMutations({
      storeEditedDetails: "storeEditedContactDetails",
      storeCompany: "storeEditedContactCompany",
    }),
    handleUpload() {
      this.loading = true
      this.file = this.$refs.file.files[0]
      this.submitFile()
    },
    submitFile() {
      let formData = new FormData()
      formData.append("file", this.file)
      api
        .post("contact/import", formData, {
          headers: {
            "Content-Type": "multipart/form-data",
          },
        })
        .then((response) => {
          console.log(response.data)
          this.refreshTable()
        })
        .catch((error) => {
          console.log(error)
        })
    },
    async refreshTable() {
      await this.getCompanies()
      await this.getContacts()
      this.mappedCompanies = this.companies
        .map((company) => Object.assign({}, { id: company.id, name: company.name }))
        .sort()
      this.loading = false
    },
    editContact(item) {
      this.storeEditedDetails({
        editedIndex: this.contacts.indexOf(item),
        editedContact: Object.assign({}, item),
      })

      if (item.company) {
        this.storeCompany({ editedCompany: item.company })
      }

      this.openContactDialog()
    },
    deleteContact() {
      api
        .delete(`contact/delete/${this.editedContact.id}`)
        .then(this.refreshTable())
        .catch((error) => {
          console.log(error)
          alert(error)
        })
    },
    openConfirmDialog(item) {
      this.editedContact = Object.assign({}, item)

      this.confirmDialogState = true
    },
    openContactDialog() {
      this.contactDialogState = true
    },
    downloadInfo(item) {
      api.get("/contact/download/" + item.id).then((response) => download(response.data, item.name))
    },
  },
}

function download(content, name) {
  const url = window.URL.createObjectURL(new Blob([content], { type: "text/plain" }))
  const link = document.createElement("a")
  link.href = url
  link.setAttribute("download", name.replace(" ", "_").toLowerCase() + ".txt")
  document.body.appendChild(link)
  link.click()
}
</script>

<style scoped>
.input-file {
  width: 0.1px;
  height: 0.1px;
  opacity: 0;
  overflow: hidden;
  position: absolute;
  z-index: -1;
}

.input-file + label {
  cursor: pointer;
}
</style>
