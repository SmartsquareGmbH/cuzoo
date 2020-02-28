<template>
  <v-dialog v-model="value" persistent max-width="950" @input="$emit('input')">
    <v-card>
      <v-card-title class="headline primary" primary-title>
        {{ formTitle }}
        <v-spacer />
        <v-menu top offset-x open-on-hover :close-on-content-click="false" :value="emojiMenuState">
          <v-btn slot="activator" small color="secondary">
            <span v-if="!editedContactPoint.rating" class="title font-weight-medium">+ 😃</span>
            <emoji v-else :emoji="editedContactPoint.rating" :size="24" set="messenger" class="emoji-on-dialog" />
          </v-btn>
          <emoji-picker @emoji-chosen="addEmoji" />
        </v-menu>
      </v-card-title>
      <v-card-text class="text-xs-right primary--text">
        <v-form ref="form" v-model="valid">
          <v-container grid-list-md>
            <v-layout wrap>
              <v-flex xs7>
                <v-text-field
                  v-model="editedContactPoint.title"
                  :rules="compulsory"
                  prepend-icon="title"
                  label="Titel"
                  suffix="*"
                  hide-details
                />
              </v-flex>
              <v-flex xs5>
                <label-box
                  :current-labels="editedContactPoint.types"
                  api-path="point/get/types"
                  type="Art"
                  hide-details=""
                  @label-added="setContactPointTypes"
                />
              </v-flex>
              <v-flex xs6>
                <v-combobox
                  v-model="editedContactPoint.contact.name"
                  :items="contactNames"
                  :rules="contactRules"
                  prepend-icon="person"
                  suffix="*"
                  label="Ansprechpartner"
                  hide-details
                />
              </v-flex>
              <v-flex xs1 style="padding-top: 20px;" class="text-xs-left">
                <v-btn small flat fab color="transparent" @click="addContactPoint()">
                  <v-tooltip top>
                    <v-icon slot="activator" large color="light-green accent-2">
                      add
                    </v-icon>
                    <span>Ansprechpartner hinzufügen</span>
                  </v-tooltip>
                </v-btn>
                <contact-dialog
                  v-model="contactDialogState"
                  :companies="mappedCompanies"
                  @refresh="refreshContactPoints"
                  @input="contactDialogState = false"
                />
              </v-flex>
              <v-flex xs5>
                <v-menu
                  ref="menu"
                  v-model="menu"
                  :close-on-content-click="false"
                  :nudge-right="35"
                  :return-value.sync="date"
                  transition="scale-transition"
                  offset-y
                  full-width
                  min-width="290px"
                  hide-details
                >
                  <v-text-field
                    slot="activator"
                    v-model="dateFormatted"
                    :rules="compulsory"
                    prepend-icon="event"
                    label="Datum"
                    suffix="*"
                    readonly
                    hide-details
                  />
                  <v-date-picker v-model="date" :max="new Date().toISOString()" scrollable locale="de">
                    <v-spacer />
                    <v-btn flat color="primary" @click="menu = false">Abbrechen</v-btn>
                    <v-btn flat color="primary" @click="$refs.menu.save(date)">OK</v-btn>
                  </v-date-picker>
                </v-menu>
              </v-flex>
              <v-flex xs12>
                <v-textarea v-model="editedContactPoint.comment" prepend-icon="comment" label="Kommentar" rows="5" />
              </v-flex>
              <v-flex xs12>
                <label-box
                  :current-labels="editedContactPoint.labels"
                  api-path="point/get/labels"
                  type="Labels"
                  hide-details
                  :class="`mb-${opportunityMenu ? 2 : 0}`"
                  @label-added="setContactPointLabels"
                />
              </v-flex>
              <v-flex xs12>
                <v-expand-transition>
                  <v-layout v-if="opportunityMenu || opportunity" row wrap>
                    <v-flex xs8>
                      <v-text-field
                        v-model="editedOpportunity.title"
                        :rules="oppTitleRules"
                        suffix="*"
                        prepend-icon="title"
                        label="Opportunity-Titel"
                        hide-details
                      />
                    </v-flex>
                    <v-flex xs4>
                      <v-combobox
                        v-model="editedOpportunity.state"
                        :items="oppStatuses"
                        :rules="oppStatusRules"
                        suffix="*"
                        prepend-icon="bubble_chart"
                        label="Status"
                        hide-details
                      />
                    </v-flex>
                    <v-expand-transition>
                      <v-flex v-if="newOpportunity" xs12>
                        <v-textarea
                          v-model="editedOpportunity.description"
                          prepend-icon="description"
                          label="Kurzbeschreibung"
                          rows="5"
                          hide-details
                        />
                      </v-flex>
                    </v-expand-transition>
                  </v-layout>
                </v-expand-transition>
              </v-flex>
              <v-flex xs12>
                <v-expand-transition>
                  <v-layout v-if="companyMenu || company" row wrap>
                    <v-flex xs12>
                      <v-text-field
                        v-model="editedCompany.name"
                        :rules="comTitleRules"
                        suffix="*"
                        prepend-icon="title"
                        label="Unternehmens-Name"
                        hide-details
                      />
                    </v-flex>
                    <v-flex xs6>
                      <v-text-field
                        v-model="editedCompany.street"
                        :rules="comStreetRules"
                        suffix="*"
                        prepend-icon="place"
                        label="Straße &amp; Hsnr."
                        hide-details
                      />
                    </v-flex>
                    <v-flex xs3>
                      <v-text-field
                        v-model="editedCompany.zipcode"
                        :rules="comZipRules"
                        suffix="*"
                        label="PLZ"
                        hide-details
                        mask="#####"
                        counter
                        min="5"
                      />
                    </v-flex>
                    <v-flex xs3>
                      <v-text-field
                        v-model="editedCompany.place"
                        :rules="comPlaceRules"
                        label="Ort"
                        suffix="*"
                        hide-details
                      >
                      </v-text-field>
                    </v-flex>
                    <v-flex xs12>
                      <v-text-field
                        v-model="editedCompany.homepage"
                        label="Homepage"
                        prepend-icon="home"
                      ></v-text-field>
                    </v-flex>
                    <v-expand-transition>
                      <v-flex v-if="newCompany" xs12>
                        <v-textarea
                          v-model="editedCompany.description"
                          prepend-icon="description"
                          label="Kurzbeschreibung"
                          rows="5"
                          hide-details
                        />
                      </v-flex>
                    </v-expand-transition>
                  </v-layout>
                </v-expand-transition>
              </v-flex>
            </v-layout>
          </v-container>
        </v-form>
        <div class="mr-2">* Pflichtfelder</div>
      </v-card-text>
      <v-card-actions>
        <div v-if="!opportunity">
          <v-btn
            v-if="companyOpportunities.length === 0 || (opportunityMenu && newOpportunity)"
            color="success"
            flat
            @click.native="createOpportunity()"
          >
            Neue Opportunity
            <v-icon v-if="opportunityMenu">keyboard_arrow_up</v-icon>
            <v-icon v-if="!opportunityMenu">keyboard_arrow_down</v-icon>
          </v-btn>
          <v-menu v-else top offset-y>
            <v-btn slot="activator" color="success" flat @click="opportunityList = !opportunityList">
              {{ opportunityButtonTitle }}
              <v-icon v-if="opportunityList">keyboard_arrow_down</v-icon>
              <v-icon v-if="!opportunityList">keyboard_arrow_up</v-icon>
            </v-btn>
            <v-list class="py-0">
              <v-list-tile v-for="opp in companyOpportunities" :key="opp.id" @click="updateOpportunity(opp)">
                <v-icon :color="getStateColor(opp.state)" class="mr-2">
                  bubble_chart
                </v-icon>
                <v-list-tile-title>{{ opp.title }}</v-list-tile-title>
              </v-list-tile>
              <v-divider class="my-0" />
              <v-list-tile class="my-0 py-0" @click="createOpportunity()">
                <v-icon color="light-green accent-2" class="mr-2">add</v-icon>
                <v-list-tile-title>Neue Opportunity</v-list-tile-title>
              </v-list-tile>
            </v-list>
          </v-menu>
        </div>
      </v-card-actions>
      <v-card-actions>
        <div v-if="!company">
          <v-btn
            v-if="companies.length === 0 || (companyMenu && newCompany)"
            color="success"
            flat
            @click.native="createCompany()"
          >
            Neues Unternehmen
            <v-icon v-if="companyMenu">keyboard_arrow_up</v-icon>
            <v-icon v-if="!companyMenu">keyboard_arrow_down</v-icon>
          </v-btn>
          <v-menu v-else top offset-y>
            <v-btn v-if="company" slot="activator" color="success" flat @click="companyList = !companyList">
              {{ companyButtonTitle }}
              <v-icon v-if="companyList">keyboard_arrow_down</v-icon>
              <v-icon v-if="!companyList">keyboard_arrow_up</v-icon>
            </v-btn>
            <v-list class="py-0">
              <v-list-tile v-for="com in companies" :key="com.id" @click="updateCompany(com)">
                <v-list-tile-title>{{ com.name }}</v-list-tile-title>
              </v-list-tile>
              <v-divider class="my-0" />
              <v-list-tile class="my-0 py-0" @click="createCompany()">
                <v-icon color="light-green accent-2" class="mr-2">add</v-icon>
                <v-list-tile-title>Neues Unternehmen</v-list-tile-title>
              </v-list-tile>
            </v-list>
          </v-menu>
        </div>
        <v-spacer />
        <v-btn color="primary" flat @click.native="closeDialog()">Abbrechen</v-btn>
        <v-btn color="primary" flat @click="clearDialog()">Zurücksetzen</v-btn>
        <v-btn color="primary" flat :disabled="!valid" @click="submitContactPoint()">Speichern</v-btn>
      </v-card-actions>
      <v-card-actions>
        <div>
          <v-menu top offset-y>
            <v-list class="py-0">
              <v-list-tile v-for="opp in companyOpportunities" :key="opp.id" @click="updateOpportunity(opp)">
                <v-icon :color="getStateColor(opp.state)" class="mr-2">
                  bubble_chart
                </v-icon>
                <v-list-tile-title>{{ opp.title }}</v-list-tile-title>
              </v-list-tile>
              <v-divider class="my-0" />
              <v-list-tile class="my-0 py-0" @click="createOpportunity()">
                <v-icon color="light-green accent-2" class="mr-2">add</v-icon>
                <v-list-tile-title>Neue Opportunity</v-list-tile-title>
              </v-list-tile>
            </v-list>
          </v-menu>
        </div>
      </v-card-actions>
    </v-card>
    <confirm-dialog
      v-model="confirmDialogState"
      :question-to-be-confirmed="createCompanyMessage"
      @confirmed="submitCompany()"
    />
  </v-dialog>
</template>

<script>
import api from "../../utils/http-common"
import { mapActions, mapGetters, mapMutations } from "vuex"

import LabelBox from "../core/LabelBox.vue"
import { Emoji } from "emoji-mart-vue-fast"
import EmojiPicker from "../core/EmojiPicker.vue"
import ContactDialog from "../dialogs/ContactDialog.vue"

const datefns = require("date-fns")
const de = require("date-fns/locale/de")

export default {
  components: {
    LabelBox,
    Emoji,
    EmojiPicker,
    ContactDialog,
  },
  props: ["value", "opportunity", "company"],
  data() {
    return {
      mappedCompanies: [],
      contactNames: [],
      contactDialogState: false,
      confirmDialogState: false,
      createCompanyMessage: "Das Unternehmen zum Ansprechpartner existiert nicht, möchtest du es anlegen?",
      companyMenu: false,
      companyList: false,
      newCompany: false,
      opportunityMenu: false,
      opportunityList: false,
      date: new Date().toISOString().substr(0, 10),
      menu: false,
      valid: false,
      emojiMenuState: undefined,
      compulsory: [(v) => !!v || "Bitte geben Sie etwas ein"],
      contactRules: [
        (v) => !!v || "Bitte geben Sie einen Ansprechpartner an oder fügen Sie einen neuen hinzu.",
        this.companyMenu === true,
        (this.newCompany = true),
      ],
      comTitleRules: [(v) => !!v || "Bitte geben Sie einen Titel an", this.companyMenu === true],
      comStreetRules: [(v) => !!v || "Bitte geben Sie eine Straße an", this.companyMenu === true],
      comZipRules: [(v) => !!v || "Bitte geben Sie eine PLZ an", this.companyMenu === true],
      comPlaceRules: [(v) => !!v || "Bitte geben Sie einen Ort an", this.companyMenu === true],
      newOpportunity: false,
      companyOpportunities: [],
      oppStatuses: ["Lose", "Lead", "Prospect", "Quote", "Win"],
      oppStatusRules: [
        (v) => !!v || "Bitte geben Sie einen Status an",
        (v) => this.oppStatuses.includes(v) || "Dieser Status existiert nicht",
        this.opportunityMenu === true,
      ],
      oppTitleRules: [(v) => !!v || "Bitte geben Sie einen Titel an", this.opportunityMenu === true],
      defaultContactPoint: {
        value: false,
        id: 0,
        title: "",
        contact: {},
        contactName: "",
        date: "",
        comment: "",
        opportunityState: "",
        rating: "",
        types: [],
        labels: [],
      },
      defaultContact: {
        value: false,
        id: 0,
        name: "",
        company: {},
        role: "",
        mail: "",
        telephone: "",
        mobile: "",
        comment: "",
        manager: "",
        labels: [],
      },
      defaultOpportunity: {
        value: false,
        id: 0,
        title: "",
        state: "",
        description: "",
        lastProgress: "",
      },
      defaultCompany: {
        value: false,
        id: 0,
        name: "",
        street: "",
        zipcode: "",
        place: "",
        homepage: "",
        description: "",
        other: "",
        labels: [],
      },
    }
  },
  beforeMount() {
    this.refreshContactPoints()
  },
  computed: {
    ...mapGetters({
      editedIndex: "editedContactPointIndex",
      editedContactPoint: "editedContactPoint",
      editedOpportunity: "editedOpportunity",
      editedCompany: "editedCompany",
      editedContact: "editedContact",
      username: "username",
      contacts: "contacts",
      contactPoints: "contactPoints",
      companies: "companies",
    }),
    contactsOfCompany() {
      return this.contacts.filter((contact) => {
        if (contact.company !== null && this.company.id === contact.company.id) {
          return contact
        }
      })
    },
    formTitle() {
      return this.editedIndex === -1 ? "Kontaktpunkt hinzufügen" : "Kontaktpunkt bearbeiten"
    },
    dateFormatted: {
      get() {
        return datefns.format(this.date, "DD.MM.YY", { locale: de })
      },
      set(newDate) {
        this.date = newDate
      },
    },
    contactName() {
      return this.editedContactPoint.contact.name
    },
    companyName() {
      return this.companies.name
    },
    opportunityButtonTitle() {
      return this.companyOpportunities.length + " Opportunities"
    },
    companyButtonTitle() {
      return this.companies.length + " Companies"
    },
  },
  watch: {
    value() {
      this.$refs.form.resetValidation()

      if (this.editedContactPoint.date) {
        this.date = datefns.format(this.editedContactPoint.date, "YYYY-MM-DD", { locale: de })
      } else {
        this.date = new Date().toISOString().substr(0, 10)
      }
    },
    contactName(value) {
      if (!this.opportunity) {
        this.resetEditedOpportunity()
        this.opportunityMenu = false

        if (value) {
          let contact = this.contacts.find((it) => it.name === value)
          this.getOpportunities(contact.company.id)
        } else {
          this.newOpportunity = true
          this.companyOpportunities = []
        }
      }
    },
    emojiMenuState() {
      setTimeout(() => (this.emojiMenuState = undefined))
    },
    companyName(value) {
      if (!this.companyNames) {
        this.resetEditedCompany()
        this.companyMenu = false

        if (value) {
          // let companyName = this.companies.find((it) => it.name === value)
          this.getCompanies()
        } else {
          this.newCompany = true
          this.companyNames = []
        }
      }
    },
    MenuState() {
      this.companyMenu = !this.opportunityMenu
      this.opportunity = !this.company
    },
  },
  methods: {
    ...mapActions(["getContactPointLabels", "getContacts", "getCompanies"]),
    ...mapMutations({
      storeContactPointDetails: "storeEditedContactPointDetails",
      storeOpportunityDetails: "storeEditedOpportunityDetails",
      storeCompanyDetails: "storeEditedCompanyDetails",
    }),
    addContactPoint() {
      this.contactNames = this.contacts.map((it) => it.name).sort()
      this.contactDialogState = true
    },
    clearDialog() {
      let tempContactName = this.editedContactPoint.contact.name
      this.$refs.form.reset()
      this.editedContactPoint.contact.name = tempContactName
      this.editedContactPoint.rating = undefined

      setTimeout(() => {
        this.date = new Date().toISOString().substr(0, 10)
        this.editedOpportunity.state = "Lead"
        this.opportunityMenu = false
        this.companyMenu = false
        this.storeContactDetails({
          editedIndex: -1,
          editedContact: Object.assign({}, this.defaultContact),
        })
      })
    },
    closeDialog() {
      this.$emit("input")

      setTimeout(() => {
        this.storeContactPointDetails({
          editedIndex: -1,
          editedContactPoint: Object.assign({}, this.defaultContactPoint),
        })
        this.resetEditedOpportunity()
        this.editedContactPoint.rating = undefined

        this.opportunityMenu = false
        this.companyMenu = false
      }, 300)
    },
    submitContactPoint() {
      let contact = this.contacts.find((it) => it.name.includes(this.editedContactPoint.contact.name))

      api
        .put(`point/submit/${contact.id}`, {
          title: this.editedContactPoint.title,
          id: this.editedContactPoint.id,
          date: this.getDateWithCurrentTimeInMillis(this.date),
          comment: this.editedContactPoint.comment,
          rating: this.editedContactPoint.rating,
          types: this.editedContactPoint.types,
          labels: this.editedContactPoint.labels,
          creator: this.username,
        })
        .then((res) => {
          let contactPointId = res.data

          if (this.opportunityMenu || this.opportunity) {
            api
              .put(`opportunity/submit/contact/${contactPointId}`, {
                id: this.editedOpportunity.id,
                title: this.editedOpportunity.title,
                state: this.getOpportunityState(),
                description: this.editedOpportunity.description,
                progress: this.editedOpportunity.progress,
              })
              .then(() => {
                this.$emit("refresh")
                this.closeDialog()
              })
              .catch((error) => {
                console.log(error)
                alert(error)
              })
          }
          if (this.companyMenu || this.company) {
            api
              .put(`company/submit/${contactPointId}`, {
                id: this.editedCompany.id,
                name: this.editedCompany.name,
                street: this.editedCompany.street,
                zipcode: this.editedCompany.zipcode,
                place: this.editedCompany.place,
                description: this.editedCompany.description,
                other: this.editedCompany.other,
                labels: this.editedCompany.labels,
                contacts: this.editedCompany.setContact,
              })
              .then(() => {
                this.$emit("refresh")
                this.closeDialog()
              })
              .catch((error) => {
                console.log(error)
                alert(error)
              })
          } else {
            this.$emit("refresh")
            this.closeDialog()
          }
          if (this.contact.name.isEqual("") || this.contact.id === 0) {
            api
              .put(`contact/submit`, {
                id: this.editedContact.id,
                name: "",
                company: this.editedContact.company,
                role: this.editedContact.role,
                mail: this.editedContact.mail,
                telephone: this.editedContact.telephone,
                mobile: this.editedContact.mobile,
                comment: this.editedContact.comment,
                manager: this.editedContact.manager,
                labels: this.editedContact.labels,
              })
              .then(() => {
                this.$emit("refresh")
                this.closeDialog()
              })
              .catch((error) => {
                console.log(error)
                alert(error)
              })
              .catch((error) => {
                console.log(error)
                alert(error)
              })
          } else {
            api
              .put(`contact/submit`, {
                id: this.editedContact.id,
                name: this.editedContact.name,
                company: this.editedContact.company,
                role: this.editedContact.role,
                mail: this.editedContact.mail,
                telephone: this.editedContact.telephone,
                mobile: this.editedContact.mobile,
                comment: this.editedContact.comment,
                manager: this.editedContact.manager,
                labels: this.editedContact.labels,
              })
              .then(() => {
                this.$emit("refresh")
                this.closeDialog()
              })
              .catch((error) => {
                console.log(error)
                alert(error)
              })
              .catch((error) => {
                console.log(error)
                alert(error)
              })
          }
        })
    },

    submitCompany() {
      api
        .put(`company/submit/${this.contact.id}`, {
          name: this.companyNameEntered,
          id: -1,
          street: "",
          zipcode: "",
          place: "",
          homepage: "",
          description: "",
          other: "",
          labels: [],
        })
        .then((response) => {
          this.submitContact(response.data)
        })
        .catch((error) => {
          console.log(error)
          alert(error)
        })
    },
    setContact(contact) {
      this.editedCompany.contact.name = contact.name
    },
    setContactPointLabels(labels) {
      this.editedContactPoint.labels = labels
    },
    setCompanyLabels(labels) {
      this.editedCompany.labels = labels
    },
    setContactPointTypes(types) {
      this.editedContactPoint.types = types
    },
    getOpportunities(companyId) {
      api.get(`opportunity/get/list/${companyId}`).then((res) => {
        this.companyOpportunities = res.data.filter((it) => it.state !== "Win" && it.state !== "Lose")
      })
    },
    // getCompanies() {
    //   api.get(`company/get`).then((res) => {
    //     this.companies = res.data.map((company) => Object.assign({}, { id: company.id, name: company.name })).sort()
    //   })
    // },
    createOpportunity() {
      this.resetEditedOpportunity()

      if (this.opportunityMenu && this.newOpportunity) {
        this.opportunityMenu = false
      } else {
        this.newOpportunity = true
        this.opportunityMenu = true
      }
    },
    createCompany() {
      this.resetEditedCompany()

      if (this.companyMenu && this.newCompany) {
        this.companyMenu = false
      } else {
        this.newCompany = true
        this.companyMenu = true
      }
    },
    updateOpportunity(opportunity) {
      if (this.editedOpportunity.title.includes(opportunity.title)) {
        this.resetEditedOpportunity()

        this.opportunityMenu = false
      } else {
        this.resetEditedOpportunity(opportunity)

        this.newOpportunity = false
        this.opportunityMenu = true
      }
    },
    updateCompany(company) {
      if (this.editedCompany.name.includes(company.name)) {
        this.resetEditedCompany()

        this.companyMenu = false
      } else {
        this.resetEditedCompany(company)

        this.newCompany = false
        this.companyMenu = true
      }
    },
    resetEditedCompany(com) {
      this.storeCompanyDetails({
        editedIndex: com ? com.id : -1,
        editedCompany: Object.assign({}, com ? com : this.defaultCompany),
      })
    },
    resetEditedOpportunity(opp) {
      this.storeOpportunityDetails({
        editedIndex: opp ? opp.id : -1,
        editedOpportunity: Object.assign({}, opp ? opp : this.defaultOpportunity),
      })
    },
    getOpportunityStateForContactPoint() {
      if (this.opportunityMenu || this.opportunity) return this.editedOpportunity.state
      if (this.editedContactPoint.id > 0) return this.editedContactPoint.opportunityState

      return "Lead"
    },
    getOpportunityState() {
      if (this.opportunityMenu) return this.editedOpportunity.state

      if (datefns.compareDesc(this.date, this.opportunity.lastProgress) === 1) {
        return this.opportunity.state
      } else {
        return this.editedOpportunity.state
      }
    },
    getDateWithCurrentTimeInMillis(date) {
      let selectedDateInMillis = datefns.parse(date).getTime()
      let secondsInMillis = new Date().getSeconds() * 1000
      let minutesInMillis = new Date().getMinutes() * 60000
      let hoursInMillis = new Date().getHours() * 3600000
      let millisecondsOfToday = hoursInMillis + minutesInMillis + secondsInMillis + new Date().getMilliseconds()

      return selectedDateInMillis + millisecondsOfToday
    },
    refreshContactPoints() {
      this.getCompanies().then(() => {
        this.getContacts().then(() => {
          this.mappedCompanies = this.companies
            .map((company) => Object.assign({}, { id: company.id, name: company.name }))
            .sort()
        })
      })
      this.name = this.editedContact.name
      this.refreshData()
    },
    addEmoji(emoji) {
      this.editedContactPoint.rating = emoji.colons
      this.emojiMenuState = false
    },
    getStateColor(state) {
      switch (state) {
        case "Lead":
          return "error"
        case "Prospect":
          return "warning"
        case "Quote":
          return "success"
      }
    },
  },
}
</script>

<style scoped>
.emoji-on-dialog {
  padding-top: 2px !important;
  padding-bottom: 0 !important;
}
</style>
