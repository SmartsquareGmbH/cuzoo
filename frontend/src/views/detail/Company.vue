<template>
  <v-container grid-list-md text-xs-left fluid>
    <v-fade-transition>
      <v-layout v-if="!loading" row wrap>
        <v-flex xs1>
          <v-btn flat small @click="goPageBack()">
            <v-icon size="22px" class="mr-1" dark>arrow_back</v-icon>
            Zurück
          </v-btn>
        </v-flex>
        <v-flex xs5 text-xs-right>
          <v-tooltip top>
            <v-btn v-show="false" slot="activator" disabled flat small @click="viewContactPoints()">
              <v-icon size="22px" class="mr-1" dark>forum</v-icon>
              Kontaktpunktliste
            </v-btn>
            Anzeigen der Kontaktpunktliste
          </v-tooltip>
          <v-btn slot="activator" flat small @click="editCompany()">
            <v-icon size="22px" class="mr-1" dark>edit</v-icon>
            Unternehmen editieren
          </v-btn>
        </v-flex>
        <company-dialog v-model="companyDialogState" @input="companyDialogState = false" />
        <v-flex xs2>
          <v-btn slot="activator" small flat @click="openContactDialog()">
            <v-icon size="22px" class="mr-1" dark>add</v-icon>
            Ansprechpartner hinzufügen
          </v-btn>
        </v-flex>
        <v-flex xs4 text-xs-right>
          <v-tooltip top>
            <v-btn slot="activator" small flat @click="downloadInfo(contactsOfCompany[contactsOfCompany.contact])">
              <v-icon dark size="22px" style="transform: rotate(180deg)">publish</v-icon>
              Datenauskunft
            </v-btn>
            Datenauskunft über Ansprechpartner herunterladen
          </v-tooltip>
          <v-btn
            v-if="contactsOfCompany.length > 0"
            slot="activator"
            small
            flat
            @click="editContact(contactsOfCompany[contactsOfCompany.contact])"
          >
            <v-icon size="22px" dark>edit</v-icon>
            Ansprechpartner editieren
          </v-btn>
        </v-flex>
        <contact-dialog
          v-model="contactDialogState"
          :companies="[Object.assign({}, { id: company.id, name: company.name })]"
          @input="contactDialogState = false"
        />
        <v-flex md12 lg6>
          <v-layout row wrap>
            <v-flex xs2>
              <v-card dark color="green">
                <v-card-text class="headline text-xs-center">
                  <v-icon size="30px">business</v-icon>
                </v-card-text>
              </v-card>
            </v-flex>
            <v-flex xs10>
              <v-card height="100%" dark>
                <v-tooltip bottom>
                  <v-card-text slot="activator" class="headline text-xs-left font-weight-light">
                    {{ company.name | truncate(60) }}
                  </v-card-text>
                  <span class="headline font-weight-light">{{ company.name }}</span>
                </v-tooltip>
              </v-card>
            </v-flex>
            <v-flex xs2>
              <v-card dark color="green">
                <v-card-text class="headline text-xs-center">
                  <v-icon size="30px">home</v-icon>
                </v-card-text>
              </v-card>
            </v-flex>
            <v-flex xs10>
              <v-card dark>
                <v-card-text v-if="company.homepage" class="headline text-xs-left font-weight-light">
                  <a :href="homepage" target="_blank">{{ company.homepage }}</a>
                </v-card-text>
                <v-card-text v-else class="headline text-xs-left font-weight-light font-italic error--text">
                  N/A
                </v-card-text>
              </v-card>
            </v-flex>
            <v-flex xs2>
              <v-card dark color="green" height="212%" class="centered">
                <v-card-text class="headline text-xs-center font-weight-light">
                  <v-icon size="30px">place</v-icon>
                </v-card-text>
              </v-card>
            </v-flex>
            <v-flex xs2>
              <v-card dark>
                <v-card-text v-if="company.zipcode" class="headline text-xs-left font-weight-light">
                  {{ company.zipcode }}
                </v-card-text>
                <v-card-text v-else class="headline text-xs-left font-weight-light font-italic error--text">
                  N/A
                </v-card-text>
              </v-card>
            </v-flex>
            <v-flex xs8>
              <v-card dark>
                <v-card-text v-if="company.place" class="headline text-xs-left font-weight-light">
                  {{ company.place }}
                </v-card-text>
                <v-card-text v-else class="headline text-xs-left font-weight-light font-italic error--text">
                  N/A
                </v-card-text>
              </v-card>
            </v-flex>
            <v-flex xs2>
              <v-card color="transparent elevation-0"> </v-card>
            </v-flex>
            <v-flex xs10>
              <v-card dark>
                <v-card-text v-if="company.street" class="headline text-xs-left font-weight-light">
                  {{ company.street }}
                </v-card-text>
                <v-card-text v-else class="headline text-xs-left font-weight-light font-italic error--text">
                  N/A
                </v-card-text>
              </v-card>
            </v-flex>
            <v-flex v-if="company.description" xs12>
              <v-card-text class="headline text-xs-left no-padding-left">
                <v-tooltip top>
                  <v-icon slot="activator" color="info" size="30px">info</v-icon>
                  Beschreibung des Unternehmens
                </v-tooltip>
                Beschreibung
              </v-card-text>
              <v-divider class="mb-3" />
            </v-flex>
            <v-flex v-if="company.description" xs12>
              <span class="marked" v-html="markdownify(company.description)"></span>
            </v-flex>
            <v-flex v-if="company.other" xs12>
              <v-card-text class="headline text-xs-left no-padding-left">
                <v-tooltip top>
                  <v-icon slot="activator" color="info" size="30px">info</v-icon>
                  Sonstige Angaben zum Unternehmen
                </v-tooltip>
                Sonstiges
              </v-card-text>
              <v-divider class="mb-3" />
            </v-flex>
            <v-flex v-if="company.other" xs12>
              <span class="marked" v-html="markdownify(company.other)"></span>
            </v-flex>
            <v-flex v-if="company.labels.length > 0" xs2>
              <v-card dark color="info">
                <v-card-text class="headline text-xs-center">
                  <v-icon size="30px" class="pt-1">label</v-icon>
                </v-card-text>
              </v-card>
            </v-flex>
            <v-flex v-if="company.labels.length > 0" xs10 class="text-xs-left">
              <v-chip v-for="label in company.labels" :key="label" class="title mt-3">
                {{ label }}
              </v-chip>
            </v-flex>
          </v-layout>
        </v-flex>
        <v-flex md12 lg6>
          <v-tabs
            v-if="contactsOfCompany.length > 0"
            v-model="contactsOfCompany.contact"
            centered
            height="64%"
            grow
            slider-color="info"
          >
            <v-tab v-for="contact in contactsOfCompany" :key="contact.id">
              {{ contact.name }}
            </v-tab>
            <v-tabs-items v-model="contactsOfCompany.contact">
              <v-tab-item v-for="contact in contactsOfCompany" :key="contact.id">
                <v-layout row wrap class="mt-1">
                  <v-flex xs2>
                    <v-card dark color="green" height="100%">
                      <v-card-text class="headline text-xs-center">
                        <v-icon size="30px">work</v-icon>
                      </v-card-text>
                    </v-card>
                  </v-flex>
                  <v-flex xs10>
                    <v-card dark height="100%">
                      <v-card-text class="headline text-xs-left font-weight-light">
                        {{ contact.role }}
                      </v-card-text>
                    </v-card>
                  </v-flex>
                  <v-flex xs2>
                    <v-card dark color="info">
                      <v-card-text class="headline text-xs-center">
                        <v-icon size="30px">mail</v-icon>
                      </v-card-text>
                    </v-card>
                  </v-flex>
                  <v-flex xs10>
                    <v-card dark>
                      <v-card-text v-if="contact.mail" class="headline text-xs-left font-weight-light">
                        {{ contact.mail }}
                      </v-card-text>
                      <v-card-text v-else class="headline text-xs-left font-weight-light font-italic error--text">
                        N/A
                      </v-card-text>
                    </v-card>
                  </v-flex>
                  <v-flex xs2>
                    <v-card dark color="info">
                      <v-card-text class="headline text-xs-center">
                        <v-icon size="30px">phone</v-icon>
                      </v-card-text>
                    </v-card>
                  </v-flex>
                  <v-flex xs4>
                    <v-card dark>
                      <v-card-text v-if="contact.telephone" class="headline text-xs-left font-weight-light text-truncate">
                        {{ contact.telephone }}
                      </v-card-text>
                      <v-card-text v-else class="headline text-xs-left font-weight-light font-italic error--text">
                        N/A
                      </v-card-text>
                    </v-card>
                  </v-flex>
                  <v-flex xs2>
                    <v-card dark color="info">
                      <v-card-text class="headline text-xs-center">
                        <v-icon size="30px">smartphone</v-icon>
                      </v-card-text>
                    </v-card>
                  </v-flex>
                  <v-flex xs4>
                    <v-card dark>
                      <v-card-text v-if="contact.mobile" class="headline text-xs-left font-weight-light text-truncate">
                        {{ contact.mobile }}
                      </v-card-text>
                      <v-card-text v-else class="headline text-xs-left font-weight-light font-italic error--text">
                        N/A
                      </v-card-text>
                    </v-card>
                  </v-flex>
                  <v-flex v-if="contact.comment" xs12>
                    <v-card-text class="headline text-xs-left no-padding-left">
                      <v-tooltip top>
                        <v-icon slot="activator" color="info" size="30px">info</v-icon>
                        Kommentar zum Ansprechpartner
                      </v-tooltip>
                      Bemerkung
                    </v-card-text>
                    <v-divider class="mb-3" />
                  </v-flex>
                  <v-flex v-if="contact.comment" xs12>
                    <span class="marked" v-html="markdownify(contact.comment)"></span>
                  </v-flex>
                  <v-flex v-if="contact.labels.length > 0" xs2>
                    <v-card dark color="info">
                      <v-card-text class="headline text-xs-center">
                        <v-icon size="30px" class="pt-1">label</v-icon>
                      </v-card-text>
                    </v-card>
                  </v-flex>
                  <v-flex v-if="contact.labels.length > 0" xs4 class="text-xs-left">
                    <v-chip v-for="label in contact.labels" :key="label" class="title mt-3">
                      {{ label }}
                    </v-chip>
                  </v-flex>
                  <v-flex xs2>
                    <v-card dark color="info">
                      <v-tooltip top>
                        <v-card-text slot="activator" class="headline text-xs-center">
                          <v-icon size="30px">account_circle</v-icon>
                        </v-card-text>
                        Verantwortlicher Manager für diesen Kontakt
                      </v-tooltip>
                    </v-card>
                  </v-flex>
                  <v-flex xs4>
                    <v-card dark>
                      <v-card-text class="headline text-xs-left font-weight-light">
                        {{ contact.manager }}
                      </v-card-text>
                    </v-card>
                  </v-flex>
                </v-layout>
              </v-tab-item>
            </v-tabs-items>
          </v-tabs>
          <v-card v-else dark color="transparent" class="elevation-0">
            <v-card-text class="headline text-xs-center font-weight-light font-italic error--text">
              N/A
            </v-card-text>
          </v-card>
        </v-flex>
      </v-layout>
    </v-fade-transition>
    <v-layout v-if="loading">
      <v-flex xs12 class="text-xs-center">
        <v-progress-circular :size="128" color="primary" indeterminate />
      </v-flex>
    </v-layout>
  </v-container>
</template>

<script>
import { mapActions, mapGetters, mapMutations } from "vuex"
import api from "../../utils/http-common"
import marked from "marked"

import CompanyDialog from "../../components/dialogs/CompanyDialog.vue"
import ContactDialog from "../../components/dialogs/ContactDialog.vue"

export default {
  components: {
    CompanyDialog,
    ContactDialog,
  },
  data() {
    return {
      companyId: parseInt(this.$route.params.companyId),
      contactDialogState: false,
      companyDialogState: false,
      loading: true,
    }
  },
  computed: {
    ...mapGetters(["companies", "contacts"]),
    company() {
      return this.companies.find((it) => it.id === this.companyId)
    },
    contactsOfCompany() {
      return this.contacts.filter((contact) => {
        if (contact.company !== null && this.company.id === contact.company.id) {
          return contact
        }
      })
    },
    homepage() {
      return `http://${this.company.homepage}`
    },
  },
  beforeMount() {
    this.refreshTable()
  },
  methods: {
    ...mapActions(["getCompanies", "getContacts"]),
    ...mapMutations({
      storeCompanyDetails: "storeEditedCompanyDetails",
      storeContactDetails: "storeEditedContactDetails",
    }),
    goPageBack() {
      this.$router.go(-1)
    },
    editCompany() {
      this.storeCompanyDetails({
        editedIndex: this.companies.indexOf(this.company),
        editedCompany: Object.assign({}, this.company),
      })
      this.openCompanyDialog()
    },
    editContact(item) {
      this.storeContactDetails({
        editedIndex: this.companies.indexOf(item),
        editedContact: Object.assign({}, item),
      })

      this.openContactDialog()
    },
    downloadInfo(item) {
      api.get("/contact/download/" + item.id).then((response) => download(response.data, item.name))
    },
    openCompanyDialog() {
      this.companyDialogState = true
    },
    openContactDialog() {
      this.contactDialogState = true
    },
    refreshTable() {
      this.getCompanies().then(() => {
        this.getContacts().then(() => (this.loading = false))
      })
    },
    viewContactPoints() {
      this.$router.push("/" + this.companyId)
    },
    markdownify(value) {
      return marked(value, { sanitize: true }).trim()
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
.centered {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100vh;
}

.no-padding-left {
  padding-left: 0;
}

.marked {
  white-space: pre-wrap;
}

.marked > p {
  margin-bottom: 16px !important;
}

.text-truncate {
  line-height: 1.3 !important;
}
</style>
