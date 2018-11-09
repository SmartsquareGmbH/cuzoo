<template>
<v-container fluid>
  <v-card-title>
    <h1 class="mr-3">UNTERNEHMEN</h1>
    <input class="input-file"
    type="file" 
    id="file" 
    ref="file"
    v-on:change="handleUpload()"/>
    <label for="file">
      <v-tooltip top>
        <v-icon 
        slot="activator" 
        color="primary"
        x-large>
          publish
        </v-icon>
        <span>CSV Import</span>
      </v-tooltip>
    </label>
    <v-btn 
    v-on:click="openDialog()" 
    color="transparent" 
    fab 
    small 
    depressed 
    flat>
      <v-tooltip top>
        <v-icon 
        color="light-green accent-2" 
        slot="activator"
        x-large>
          add
        </v-icon>
        <span>Unternehmen hinzufügen</span>
      </v-tooltip>
    </v-btn>
    <company-dialog></company-dialog>
    <v-spacer></v-spacer>
    <v-text-field
      v-model="search"
      append-icon="search"
      label="Suche ..."
      single-line
      hide-details
    ></v-text-field>
  </v-card-title>
  <v-data-table
  :headers="headers"
  :items="this.$store.getters.getCompanies"
  :search="search"
  rows-per-page-text="Unternehmen pro Seite"
  :rows-per-page-items=[10,25,50,100]
  dark
  >
    <template slot="items" slot-scope="props">
        <td class="text-xs-left">{{ props.item.name }}</td>
        <td class="text-xs-left">{{ props.item.street }}</td>
        <td class="text-xs-left">{{ props.item.zipCode }}</td>
        <td class="text-xs-left">{{ props.item.place }}</td>
        <td class="text-xs-left">{{ props.item.homepage }}</td>
        <td class="text-xs-left">{{ props.item.purpose }}</td>
        <td class="text-xs-left">{{ props.item.other }}</td>
        <td class="justify-center layout px-0">
        <v-icon size="22px" class="mr-2"
          v-on:click="editCompany(props.item)">
            edit
        </v-icon>
        <v-icon size="22px" color="red lighten-1"
          v-on:click="deleteCompany(props.item)">
            delete
        </v-icon>
      </td>
    </template>
    <template slot="no-data">
      <v-alert :value="true" color="error" icon="warning">
        Keine Daten verfügbar :'(
      </v-alert>
    </template>
    <v-alert slot="no-results" :value="true" color="error" icon="warning">
      Deine Suche nach "{{ search }}" ergab keinen Treffer :'(
    </v-alert>
  </v-data-table>
</v-container>
</template>

<script>
import { mapState } from 'vuex';
import axios from 'axios';
import CompanyDialog from "@/components/Company/CompanyDialog.vue"; 

export default {
  components: {
    CompanyDialog
  },
  data () {
  return {
      file: '',
      search: '',
      headers: [
          { text: 'Unternehmen', value: 'name', align: 'left' },
          { text: 'Straße', value: 'street' },
          { text: 'PLZ', value: 'zipCode' },
          { text: 'Ort', value: 'place' },
          { text: 'Homepage', value: 'homepage' },
          { text: 'Unternehmenszweck', value: 'purpose' },
          { text: 'Sonstiges', value: 'other' },
          { text: 'Aktionen', value: 'name', sortable: false }
      ]
    }
  },
  computed: {
    ...mapState(['companies']),
    companies: {
        get () {
            return this.$store.state.companies
        },
        set (companies) {
          this.$store.commit('storeCompanies', companies)
        }
    }
  },
  methods: {
    handleUpload() {
      this.file = this.$refs.file.files[0];
      this.submitFile();
    },
    submitFile() {
      let formData = new FormData();
      formData.append('file', this.file);

      axios.post('api/company/import', formData, {
          auth: {
            username: this.$store.getters.getLogName,
            password: this.$store.getters.getLogPass
          },
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        }).then (response => {
          console.log(response.data);
          this.refreshTable();
      }).catch(error => {
        console.log(error);
      });
    },
    refreshTable() {
      this.$store.dispatch('getCompanies');
    },
    editCompany: function (item) {
      this.$store.commit({
        type: 'storeEditedCompanyDetails',
        editedIndex: this.companies.indexOf(item),
        editedCompany: Object.assign({}, item)
      });
      this.openDialog();
    },
    deleteCompany: function (item) {
      this.editedCompany = Object.assign({}, item);

      if (confirm("Bist du dir sicher, dass du das Unternehmen löschen willst?")) {
        axios.post('api/company/delete', {
          name: this.editedCompany.name,
          id: this.editedCompany.id,
          street: this.editedCompany.street,
          zipCode: this.editedCompany.zipCode,
          place: this.editedCompany.place,
          homepage: this.editedCompany.homepage,
          purpose: this.editedCompany.purpose,
          other: this.editedCompany.other
        }, {
          auth: {
            username: this.$store.getters.getLogName,
            password: this.$store.getters.getLogPass
          }
        }).then (response => {
          this.refreshTable();
        }).catch(error => {
          console.log(error);
          alert(error);
        });
      }
    },
    openDialog() {
      this.$store.commit({
        type: 'storeDialogState',
        dialog: true
      })
    }
  }
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