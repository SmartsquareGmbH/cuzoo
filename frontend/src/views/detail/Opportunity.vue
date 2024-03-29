<template>
  <v-container fluid>
    <v-fade-transition>
      <v-layout v-if="!loadingData" row wrap text-xs-left>
        <v-flex xs12 class="ma-0 pa-0">
          <v-layout row wrap>
            <v-flex xs6>
              <v-btn flat small @click="$router.go(-1)">
                <v-icon size="22px" class="mr-1" dark>arrow_back</v-icon>
                Zurück
              </v-btn>
            </v-flex>
            <v-flex xs6 text-xs-right>
              <v-menu bottom left offset-y>
                <v-btn slot="activator" flat small>
                  <v-icon size="22px" class="mr-1" dark>add</v-icon>
                  Fortschritt
                </v-btn>
                <v-list class="py-0">
                  <v-list-tile @click="addProgress()">
                    <v-icon color="primary" class="mr-2">timeline</v-icon>
                    <v-list-tile-title>Fortschritt hinzufügen</v-list-tile-title>
                  </v-list-tile>
                  <v-list-tile @click="addContactPoint()">
                    <v-icon color="light-green accent-2" class="mr-2">forum</v-icon>
                    <v-list-tile-title>Kontaktpunkt hinzufügen</v-list-tile-title>
                  </v-list-tile>
                </v-list>
              </v-menu>
              <v-btn flat small @click.stop="editOpportunity()">
                <v-icon size="22px" class="mr-1" dark>edit</v-icon>
                Opportunity bearbeiten
              </v-btn>
              <v-btn flat small @click="openConfirmDialog()">
                <v-icon size="22px" class="mr-1" dark>delete</v-icon>
                Opportunity löschen
              </v-btn>
            </v-flex>
            <opportunity-dialog v-model="opportunityDialogState" @input="opportunityDialogState = false" />
          </v-layout>
          <v-divider />
        </v-flex>
        <v-flex xs4 class="pt-4">
          <p :class="`display-1 ${getStateColor(opportunity.state)}--text mr-5`">
            {{ opportunity.title }}
          </p>
          <v-icon>business</v-icon>
          <chip class="ml-2 mb-2">
            {{ companyName }}
          </chip>
          <p class="mt-2 mr-5" v-html="markdownify(opportunity.description)"></p>
        </v-flex>
        <v-flex xs8>
          <v-timeline>
            <v-timeline-item
              v-for="item in timelineItems"
              :key="timelineItems.indexOf(item)"
              :color="`${getStateColor(item.opportunityState)}`"
              :icon="`${getTimelineIcon(item)}`"
              fill-dot
            >
              <template v-if="item.hasOwnProperty('contact')" v-slot:opposite>
                <span class="font-italic">
                  {{ dateFormatted(item.date) }}
                  <p class="mb-0">
                    mit
                    <span :class="`${getStateColor(item.opportunityState)}--text`">
                      {{ item.contact.name }}
                    </span>
                  </p>
                </span>
                <p class="font-italic ma-0">
                  via
                  <span
                    v-for="type in item.types"
                    :key="type.id"
                    :class="`${getStateColor(item.opportunityState)}--text`"
                  >
                    {{ type.title }}
                  </span>
                </p>
              </template>
              <template v-else v-slot:opposite>
                <span class="font-italic">
                  {{ dateFormatted(item.date) }}
                </span>
                <p class="font-italic mb-0">
                  <span v-if="stateChangedToPrevious(item)">
                    Status zu
                    <span :class="`${getStateColor(item.opportunityState)}--text`">
                      {{ item.opportunityState }}
                    </span>
                    geändert
                  </span>
                  <span v-else :class="`${getStateColor(item.opportunityState)}--text`">
                    Fortschritt hinzugefügt
                  </span>
                </p>
              </template>
              <v-hover v-if="item.hasOwnProperty('contact')">
                <v-card
                  slot-scope="{ hover }"
                  class="clickable"
                  :color="`${hover ? '#616161' : ''}`"
                  @click="viewContactPoint(item)"
                >
                  <v-card-title
                    style="background-color: #616161;"
                    :class="
                      `${getStateColor(item.opportunityState)}
                                        headline white--text font-weight-light`
                    "
                  >
                    {{ item.title }}
                  </v-card-title>
                  <v-avatar v-if="item.rating" :size="36" class="emoji-container">
                    <emoji :emoji="item.rating" :size="24" set="messenger" />
                  </v-avatar>
                  <v-tooltip v-show="item.comment" top max-width="750">
                    <v-container slot="activator">
                      <span class="marked" v-html="truncatedDescription(item.comment)"></span>
                    </v-container>
                    <span v-html="markdownify(item.comment)"></span>
                  </v-tooltip>
                </v-card>
              </v-hover>
              <v-hover v-else>
                <v-card>
                  <v-card-title v-if="item.progressText">
                    <v-tooltip v-if="markdownify(item.progressText).length > 300" top max-width="750">
                      <div slot="activator">
                        <span class="marked" v-html="truncatedDescription(item.progressText)"></span>
                      </div>
                      <span v-html="markdownify(item.progressText)"></span>
                    </v-tooltip>
                    <span v-else class="marked" v-html="markdownify(item.progressText)"></span>
                  </v-card-title>
                  <v-card-title
                    v-else
                    :class="`${getStateColor(item.opportunityState)}--text title font-italic font-weight-regular`"
                  >
                    Status geändert
                  </v-card-title>
                </v-card>
              </v-hover>
            </v-timeline-item>
          </v-timeline>
          <v-divider />
        </v-flex>
      </v-layout>
    </v-fade-transition>
    <v-layout v-if="loadingData">
      <v-flex xs12 class="text-xs-center">
        <v-progress-circular :size="128" color="primary" indeterminate />
      </v-flex>
    </v-layout>
    <opp-progress-dialog
      v-model="oppProgressDialogState"
      @refresh="refreshTable()"
      @input="oppProgressDialogState = false"
    />
    <contact-point-dialog
      v-model="contactPointDialogState"
      :contact-names="contactNames"
      :opportunity="opportunity"
      :companies="[Object.assign({}, { id: company.id, name: company.name })]"
      @refresh="refreshTable()"
      @input="contactPointDialogState = false"
    />
    <confirm-dialog
      v-model="confirmDialogState"
      :question-to-be-confirmed="deleteOpportunityMessage"
      @confirmed="deleteOpportunity()"
    />
  </v-container>
</template>

<script>
import api from "../../utils/http-common"
import { mapActions, mapGetters, mapMutations } from "vuex"
import marked from "marked"

import { Emoji } from "emoji-mart-vue-fast"

import Chip from "../../components/core/Chip.vue"
import OppProgressDialog from "../../components/dialogs/OppProgressDialog.vue"
import ContactPointDialog from "../../components/dialogs/ContactPointDialog.vue"
import ConfirmDialog from "../../components/dialogs/ConfirmDialog.vue"
import OpportunityDialog from "../../components/dialogs/OpportunityDialog"

const datefns = require("date-fns")
const de = require("date-fns")

export default {
  components: {
    Emoji,
    Chip,
    OppProgressDialog,
    ContactPointDialog,
    ConfirmDialog,
    OpportunityDialog,
  },
  data() {
    return {
      loadingData: true,
      opportunityId: parseInt(this.$route.params.opportunityId),
      confirmDialogState: false,
      opportunityDialogState: false,
      deleteOpportunityMessage: "Bist du dir sicher, dass du diese Opportunity endgültig löschen willst?",
      oppProgressDialogState: false,
      contactPointDialogState: false,
      contactPoints: [],
      contactNames: [],
      companyName: "",
      progress: [],
      timelineItems: [],
      company: "",
    }
  },
  computed: {
    ...mapGetters(["opportunities", "contacts"]),
    opportunity() {
      return this.opportunities.find((it) => it.id === this.opportunityId)
    },
  },
  beforeMount() {
    this.refreshTable()
  },
  methods: {
    ...mapActions({
      getOpportunities: "getOpportunities",
      getContacts: "getContacts",
    }),
    ...mapMutations({
      storeOpportunityDetails: "storeEditedOpportunityDetails",
      storeContactDetails: "storeEditedContactDetails",
    }),
    goPageBack() {
      this.$router.go(-1)
    },
    refreshTable() {
      this.loadingData = true
      this.getContacts()
      this.getOpportunities().then(() => {
        this.getContactPointsOfOpportunity()
      })
    },
    getContactPointsOfOpportunity() {
      api
        .get(`point/get/opportunity/${this.opportunityId}`)
        .then((res) => {
          this.contactPoints = res.data.sort(compareTimelineItems)
          this.company = this.contactPoints[0].contact.company
          this.companyName = this.contactPoints[0].contact.company?.name
          this.defineTimelineItems()
        })
        .catch((err) => alert(err))
    },
    defineTimelineItems() {
      this.timelineItems = this.contactPoints.concat(this.opportunity.progress).sort(compareTimelineItems)

      this.loadingData = false
    },
    dateFormatted(date) {
      return datefns.format(date, "DD.MM.YYYY", { locale: de })
    },
    addProgress() {
      this.storeOpportunityDetails({
        editedIndex: this.opportunity.id,
        editedOpportunity: Object.assign({}, this.opportunity),
      })

      this.oppProgressDialogState = true
    },
    addContactPoint() {
      this.storeOpportunityDetails({
        editedIndex: this.opportunity.id,
        editedOpportunity: Object.assign({}, this.opportunity),
      })

      this.contactNames = this.contacts
        .filter((it) => it.company && it.company.name.includes(this.companyName))
        .map((it) => it.name)
        .sort()
      this.contactPointDialogState = true
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
        case "Lose":
          return "#616161"
      }
    },
    viewContactPoint(contactPoint) {
      if (contactPoint.contact.company) {
        this.$router.push(`/contactpoints/${contactPoint.id}/${contactPoint.contact.company.id}`)
      } else {
        this.$router.push(`/contactpoints/${contactPoint.id}`)
      }
    },
    openConfirmDialog() {
      this.confirmDialogState = true
    },
    openOpportunityDialog() {
      this.opportunityDialogState = true
    },
    editOpportunity() {
      this.storeOpportunityDetails({
        editedIndex: this.opportunities.indexOf(this.opportunity),
        editedOpportunity: Object.assign({}, this.opportunity),
      })
      this.openOpportunityDialog()
    },
    deleteOpportunity() {
      api
        .delete(`opportunity/delete/${this.opportunityId}`)
        .then(() => this.$router.go(-1))
        .catch((error) => {
          console.log(error)
          alert(error)
        })
    },
    markdownify(value) {
      return marked(value, { sanitize: true }).trim()
    },
    truncatedDescription(value) {
      let markedValue = this.markdownify(value)
      if (markedValue.length < 300) return markedValue

      let lastIndexOfSpace = markedValue.substr(0, 300).lastIndexOf(" ")
      return markedValue.substr(0, lastIndexOfSpace) + "..."
    },
    getTimelineIcon(item) {
      // eslint-disable-next-line no-prototype-builtins
      if (item.hasOwnProperty("contact")) return "forum"

      if (this.stateChangedToPrevious(item)) {
        return "bubble_chart"
      } else {
        return "timeline"
      }
    },
    stateChangedToPrevious(item) {
      const index = this.timelineItems.indexOf(item)

      if (this.timelineItems.length > index + 1) {
        let previousItem = this.timelineItems[index + 1]

        return !(previousItem.opportunityState === item.opportunityState)
      }
    },
  },
}

function compareTimelineItems(a, b) {
  if (datefns.compareAsc(a.date, b.date) === 0) {
    if (a.id < b.id) return 1
    if (a.id > b.id) return -1
  } else {
    return datefns.compareAsc(b.date, a.date)
  }
}
</script>

<style scoped>
.emoji-container {
  background-color: #555;
  position: absolute;
  right: 16px;
  bottom: -12px;
}

.marked {
  white-space: pre-wrap;
}

>>> .marked p {
  margin-bottom: 0 !important;
}

>>> .marked ul {
  padding-top: 0 !important;
  padding-bottom: 0 !important;
}

>>> .marked ul li {
  padding: 0 !important;
}

.menuable__content__active {
  margin-bottom: 0 !important;
  padding-bottom: 0 !important;
}
</style>
