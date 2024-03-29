<template>
  <v-fade-transition>
    <v-hover>
      <v-card
        slot-scope="{ hover }"
        :class="`ma-${search ? 0 : 0} pa-0 clickable elevation-${hover ? 0 : 0} font-weight-light`"
        :color="`${hover ? '#616161' : ''}`"
        @click="viewContactPoint(contactPoint)"
      >
        <v-card-title class="no-padding-bottom subheading">
          {{ contactPoint.title }}
        </v-card-title>
        <v-card-title class="low-padding-left subheading pt-2">
          <v-icon>attach_file</v-icon>
          <span class="mx-1 primary--text">{{ fileNames.length }}</span>
          <v-icon :style="`transform: rotate(${hover ? 180 : 0}deg)`" class="mx-1">
            share
          </v-icon>
          <chip v-for="type in contactPoint.types" :key="type" font-color="primary">
            {{ type }}
          </chip>
          <v-icon class="mx-1">business</v-icon>
          <v-tooltip top>
            <chip v-if="contactPoint.contact.company" slot="activator" font-color="primary">
              {{ contactPoint.contact.company.name | truncate(25) }}
            </chip>
            <span v-if="contactPoint.contact.company" class="title font-weight-light">
              {{ contactPoint.contact.company.name }}
            </span>
          </v-tooltip>
          <chip v-if="contactPoint.rating">
            <emoji :emoji="contactPoint.rating" :size="20" set="messenger" class="pa-0" />
          </chip>
          <v-spacer></v-spacer>
          <v-icon>person</v-icon>
          <chip font-color="primary" class="mr-0">{{ contactPoint.contact.name }}</chip>
        </v-card-title>
        <slot />
      </v-card>
    </v-hover>
  </v-fade-transition>
</template>

<script>
import api from "../../utils/http-common"
import { mapGetters, mapMutations } from "vuex"

import Chip from "../core/Chip.vue"
import { Emoji } from "emoji-mart-vue-fast"

const datefns = require("date-fns")
const de = require("date-fns/locale/de")

export default {
  components: {
    Chip,
    Emoji,
  },
  props: ["contactPoint", "search"],
  data() {
    return {
      contactPoints: this.$parent.contactPoints,
      contactNames: [],
      company: this.$parent.company,
      fileNames: [],
      confirmDialogState: false,
      deleteContactPointMessage: "Bist du dir sicher, dass du diesen Kontaktpunkt löschen willst?",
    }
  },
  computed: {
    ...mapGetters(["companies"]),
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
    }),
    viewContactPoint(contactPoint) {
      if (contactPoint.contact.company) {
        this.$router.push(`/contactpoints/${contactPoint.id}/${contactPoint.contact.company.id}`)
      } else {
        this.$router.push(`/contactpoints/${contactPoint.id}`)
      }
    },
    refreshData() {
      api
        .get(`file/get/names/${this.contactPoint.id}`)
        .then((response) => {
          if (response.data.length > 0) {
            this.fileNames = response.data
          }
        })
        .catch((error) => {
          alert(error)
        })
    },
  },
}
</script>

<style scoped>
.no-padding-bottom {
  padding-bottom: 0;
}

.low-padding-left {
  padding-left: 10px;
}
</style>
