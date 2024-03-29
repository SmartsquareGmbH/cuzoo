<template>
  <v-container grid-list-md text-xs-left fluid>
    <v-fade-transition hide-on-leave>
      <v-layout v-if="!loading" row wrap>
        <v-flex xs1>
          <v-btn flat small @click="goPageBack()">
            <v-icon size="22px" class="mr-1" dark>arrow_back</v-icon>
            Zurück
          </v-btn>
        </v-flex>
        <v-flex xs5 class="text-xs-right">
          <v-tooltip top>
            <v-btn v-show="false" slot="activator" disabled flat small @click="viewContactPoints()">
              <v-icon size="22px" class="mr-1" dark>forum</v-icon>
              Kontaktpunktliste
            </v-btn>
            Anzeigen der Kontaktpunktliste
          </v-tooltip>
          <v-btn flat small @click="editContactPoint()">
            <v-icon size="22px" class="mr-1" dark>edit</v-icon>
            Kontaktpunkt editieren
          </v-btn>
        </v-flex>
        <contact-point-dialog
          v-model="contactPointDialogState"
          :contact-names="contactNames"
          @input="contactPointDialogState = false"
          @refresh="refreshData"
        />
        <v-flex xs3>
          <v-btn id="upload-btn" flat small @click="uploadFiles()">
            <v-icon size="22px" class="mr-1" dark>publish</v-icon>
            Dateien hochladen
          </v-btn>
        </v-flex>
        <v-flex xs3 class="text-xs-right">
          <v-btn flat small @click="openConfirmDialog()">
            <v-icon size="22px" class="mr-1" dark>delete</v-icon>
            Kontaktpunkt löschen
          </v-btn>
          <confirm-dialog
            v-model="confirmDialogState"
            :question-to-be-confirmed="deleteContactPointMessage"
            @confirmed="deleteContactPoint()"
          />
        </v-flex>
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
              <v-card dark>
                <v-tooltip bottom>
                  <v-card-text
                    v-if="contactPoint.contact.company"
                    slot="activator"
                    class="headline text-xs-left font-weight-light"
                  >
                    {{ contactPoint.contact.company.name | truncate(60) }}
                  </v-card-text>
                  <span class="headline font-weight-light">{{ company.name }}</span>
                </v-tooltip>
              </v-card>
            </v-flex>
            <v-flex xs2>
              <v-card dark color="green" height="100%" class="centered">
                <v-card-text class="headline text-xs-center">
                  <v-icon size="30px">person</v-icon>
                </v-card-text>
              </v-card>
            </v-flex>
            <v-flex xs10>
              <v-card dark>
                <v-card-text class="headline text-xs-left font-weight-light">
                  {{ contactPoint.contact.name }}
                </v-card-text>
              </v-card>
            </v-flex>
            <v-flex xs2>
              <v-card height="100%" dark color="info">
                <v-card-text class="headline text-xs-center">
                  <v-icon size="30px">title</v-icon>
                </v-card-text>
              </v-card>
            </v-flex>
            <v-flex xs10>
              <v-card height="100%" dark>
                <v-card-text class="headline text-xs-left font-weight-light">
                  {{ contactPoint.title }}
                </v-card-text>
              </v-card>
            </v-flex>
            <v-flex xs2>
              <v-card dark color="info" height="100%">
                <v-card-text class="headline text-xs-center">
                  <v-icon size="30px">event</v-icon>
                </v-card-text>
              </v-card>
            </v-flex>
            <v-flex xs4>
              <v-card height="100%">
                <v-card-text class="headline text-xs-left font-weight-light">
                  {{ dateFormatted }}
                </v-card-text>
              </v-card>
            </v-flex>
            <v-flex xs2>
              <v-card dark color="info" height="100%">
                <v-card-text class="headline text-xs-center">
                  <v-tooltip top>
                    <v-icon slot="activator" size="30px">account_circle</v-icon>
                    Ersteller des Kontaktpunktes
                  </v-tooltip>
                </v-card-text>
              </v-card>
            </v-flex>
            <v-flex xs4>
              <v-card height="100%">
                <v-card-text class="headline text-xs-left font-weight-light">
                  {{ contactPoint.creator }}
                </v-card-text>
              </v-card>
            </v-flex>
            <v-flex xs2>
              <v-card dark color="info">
                <v-card-text class="headline text-xs-center">
                  <v-icon size="30px">share</v-icon>
                </v-card-text>
              </v-card>
            </v-flex>
            <v-flex xs4 class="text-xs-left">
              <v-chip v-for="type in contactPoint.types" :key="type" class="title mt-3">
                {{ type }}
              </v-chip>
            </v-flex>
            <v-flex v-if="contactPoint.labels.length > 0" xs2>
              <v-card dark color="info">
                <v-card-text class="headline text-xs-center">
                  <v-icon size="30px" class="pt-1">label</v-icon>
                </v-card-text>
              </v-card>
            </v-flex>
            <v-flex v-if="contactPoint.labels.length > 0" xs4 class="text-xs-left">
              <v-chip v-for="label in contactPoint.labels" :key="label" class="title mt-3">
                {{ label }}
              </v-chip>
            </v-flex>
            <v-flex xs12>
              <v-card-text v-if="contactPoint.comment" class="headline text-xs-left pl-0">
                <v-tooltip top>
                  <v-icon slot="activator" color="info" size="30px">comment</v-icon>
                  Kommentar zum Kontakpunkt
                </v-tooltip>
                Kommentar
              </v-card-text>
              <v-divider v-if="contactPoint.comment" class="mb-3" />
            </v-flex>
            <v-flex xs12>
              <span v-if="contactPoint.comment" class="marked" v-html="markdownify(contactPoint.comment)"></span>
            </v-flex>
          </v-layout>
        </v-flex>
        <v-flex md12 lg6>
          <v-layout row wrap>
            <v-flex xs2>
              <v-card dark color="info" height="100%" class="centered">
                <v-card-text class="headline text-xs-center">
                  <v-icon size="30px">attach_file</v-icon>
                </v-card-text>
              </v-card>
            </v-flex>
            <v-flex xs2>
              <v-card dark height="100%">
                <v-card-text class="headline text-xs-center font-weight-light">
                  {{ fileNames.length }}
                </v-card-text>
              </v-card>
            </v-flex>
            <v-flex xs8>
              <file-upload-dialog
                v-model="fileUploadDialogState"
                :company-name="company.name"
                :contact-point-id="contactPointId"
              />
            </v-flex>
            <v-flex xs12>
              <v-layout row wrap>
                <attachment-card
                  v-for="fileName in fileNames"
                  :key="fileName"
                  :file-name="fileName"
                  :contact-point="contactPoint"
                />
              </v-layout>
            </v-flex>
          </v-layout>
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

import FileUploadDialog from "../../components/dialogs/FileUploadDialog.vue"
import ContactPointDialog from "../../components/dialogs/ContactPointDialog.vue"
import ConfirmDialog from "../../components/dialogs/ConfirmDialog.vue"
import AttachmentCard from "../../components/cards/AttachmentCard.vue"

const datefns = require("date-fns")
const de = require("date-fns")

export default {
  components: {
    FileUploadDialog,
    ContactPointDialog,
    ConfirmDialog,
    AttachmentCard,
  },
  data() {
    return {
      contactPointId: parseInt(this.$route.params.contactPointId),
      companyId: this.$route.params.companyId ? parseInt(this.$route.params.companyId) : null,
      fileUploadDialogState: false,
      fileNames: [],
      contactPointDialogState: false,
      contactNames: [],
      loading: true,
      confirmDialogState: false,
      deleteContactPointMessage: "Bist du dir sicher, dass du diesen Kontaktpunkt löschen willst?",
    }
  },
  computed: {
    ...mapGetters(["companies", "contacts", "contactPoints", "sortedContactPoints"]),
    contactPoint() {
      return this.contactPoints.find((it) => it.id === this.contactPointId)
    },
    company() {
      if (this.companyId) return this.companies.find((it) => it.id === this.companyId)
      else return ""
    },
    dateFormatted() {
      return datefns.format(this.contactPoint.date, "DD.MM.YY", { locale: de })
    },
  },
  mounted() {
    this.refreshData()
  },
  methods: {
    ...mapMutations({
      storeDetails: "storeEditedContactPointDetails",
      storeContactPoints: "storeContactPoints",
    }),
    ...mapActions(["getContactPoints", "getContacts", "getCompanies"]),
    goPageBack() {
      this.$router.go(-1)
    },
    uploadFiles() {
      this.fileUploadDialogState = true
    },
    getFileNames() {
      api
        .get(`file/get/names/${this.contactPoint.id}`)
        .then((response) => {
          this.fileNames = response.data
        })
        .catch((error) => {
          console.log(error)
        })
    },
    getContactsOfCompany() {
      return this.contacts.filter((contact) => {
        return contact.company?.name === this.company.name
      })
    },
    editContactPoint() {
      this.getContactsOfCompany().forEach((contact) => {
        this.contactNames.push(contact.name)
      })

      this.contactNames.sort()

      this.storeDetails({
        editedIndex: this.contactPoints.indexOf(this.contactPoint),
        editedContactPoint: Object.assign({}, this.contactPoint),
      })

      this.contactPointDialogState = true
    },
    deleteContactPoint() {
      api
        .delete(`point/delete/${this.contactPoint.id}`)
        .then(() => this.$router.go(-1))
        .catch((error) => {
          alert(error)
        })
    },
    openConfirmDialog() {
      this.confirmDialogState = true
    },
    refreshData() {
      this.loading = true

      this.getCompanies()
        .then(this.getContacts)
        .then(this.getContactPoints)
        .then(() => {
          api.get(`file/get/names/${this.contactPointId}`).then((response) => {
            this.fileNames = response.data
            this.loading = false
          })
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
</script>

<style scoped>
.marked {
  white-space: pre-wrap;
}

.marked > p {
  margin-bottom: 0 !important;
}
</style>
