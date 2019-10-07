<template>
  <v-layout v-resize="onResize" row wrap>
    <v-flex xs4>
      <number-card class="opp-column-header" color="error" title="Leads" :number="leads.length" />
      <v-layout row wrap>
        <v-flex xs12 class="pt-0">
          <perfect-scrollbar :options="settings">
            <div :style="`height: ${columnHeight}px`">
              <opportunity-card
                v-for="(opportunity, index) in leads"
                :key="opportunity.id"
                :opportunity="opportunity"
                :class="`mb-${index === leads.length - 1 ? 0 : 2}`"
              />
            </div>
          </perfect-scrollbar>
        </v-flex>
      </v-layout>
    </v-flex>
    <v-flex xs4>
      <number-card class="opp-column-header" color="warning" title="Prospects" :number="prospects.length" />
      <v-layout row wrap>
        <v-flex xs12 class="pt-0">
          <perfect-scrollbar :options="settings">
            <div :style="`height: ${columnHeight}px`">
              <opportunity-card
                v-for="(opportunity, index) in prospects"
                :key="opportunity.id"
                :opportunity="opportunity"
                :class="`mb-${index === prospects.length - 1 ? 0 : 2} mr-0`"
              />
            </div>
          </perfect-scrollbar>
        </v-flex>
      </v-layout>
    </v-flex>
    <v-flex xs4>
      <number-card class="opp-column-header" color="success" title="Quotes" :number="quotes.length" />
      <v-layout row wrap>
        <v-flex xs12 class="pt-0">
          <perfect-scrollbar :options="settings">
            <div :style="`height: ${columnHeight}px`">
              <opportunity-card
                v-for="(opportunity, index) in quotes"
                :key="opportunity.id"
                :opportunity="opportunity"
                :class="`mb-${index === quotes.length - 1 ? 0 : 2}`"
              />
            </div>
          </perfect-scrollbar>
        </v-flex>
      </v-layout>
    </v-flex>
  </v-layout>
</template>

<script>
import { mapActions, mapGetters } from "vuex"

import NumberCard from "../cards/NumberCard.vue"
import OpportunityCard from "../cards/OpportunityCard.vue"

export default {
  components: {
    NumberCard,
    OpportunityCard,
  },
  data: () => ({
    windowHeight: 0,
    settings: {
      maxScrollbarLength: 120,
      wheelSpeed: 0.75,
      suppressScrollX: true,
    },
  }),
  computed: {
    ...mapGetters(["opportunities"]),
    leads() {
      return this.opportunities.filter((it) => it.state === "Lead")
    },
    prospects() {
      return this.opportunities.filter((it) => it.state === "Prospect")
    },
    quotes() {
      return this.opportunities.filter((it) => it.state === "Quote")
    },
    columnHeight() {
      const breakpoint = this.$vuetify.breakpoint

      if (breakpoint.lg || breakpoint.xl) return this.windowHeight * 0.70625
      else return 430
    },
  },
  beforeMount() {
    this.getOpportunities()
    this.onResize()
  },
  methods: {
    ...mapActions(["getOpportunities"]),
    onResize() {
      this.windowHeight = window.innerHeight
    },
  },
}
</script>

<style scoped>
.fade-out-gradient {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  text-align: center;
  margin: 0;
  padding: 6px 0;

  background-image: linear-gradient(to bottom, transparent, #333333);
}

.opp-column-header {
  margin-bottom: 12px !important;
}
</style>
