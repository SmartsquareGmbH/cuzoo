<template>
  <v-fade-transition>
    <v-hover>
      <v-card
        slot-scope="{ hover }"
        style="border-radius: 10px"
        :class="`secondary mt-2 mb-3 clickable elevation-${hover ? 2 : 2}`"
        hover
        @click.native="viewCompany(company)"
      >
        <v-card-text class="headline text-xs-left">
          {{ company.name }}
          <v-divider class="my-2" />
          <v-layout row wrap>
            <v-flex v-if="contacts.length > 0" xs8 class="headline text-xs-left">
              <v-icon color="primary" class="mr-2" size="30px">
                people
              </v-icon>
              <v-chip v-for="contact in contacts" :key="contact.id">
                <span class="title font-weight-regular white--text">
                  {{ contact.name }}
                </span>
              </v-chip>
            </v-flex>
            <v-flex
              v-if="company.zipCode || company.place || company.street"
              xs4
              class="headline font-weight-light text-xs-right low-padding"
            >
              <v-icon color="primary" class="mr-1" size="30px">
                place
              </v-icon>
              {{ company.zipCode }} {{ company.place }}, {{ company.street }}
            </v-flex>
          </v-layout>
        </v-card-text>
        <v-hover>
          <v-card v-if="company.description || company.other" slot-scope="{ innerHover }" color="#377D93">
            <v-card-text v-if="!innerHover" class="text-xs-left font-weight-light">
              {{ company.description | truncate(325) }}
            </v-card-text>
            <v-card-text v-else class="text-xs-left font-weight-light">
              {{ company.description }}
            </v-card-text>
          </v-card>
        </v-hover>
        <v-card-text v-if="company.labels.length > 0" class="headline text-xs-left low-padding">
          <v-icon color="primary" class="mr-2" size="30px">
            label
          </v-icon>
          <v-chip v-for="label in company.labels" :key="label" class="mb-2">
            <span class="title font-weight-regular white--text">
              {{ label }}
            </span>
          </v-chip>
        </v-card-text>
      </v-card>
    </v-hover>
  </v-fade-transition>
</template>

<script>
export default {
  props: ["company", "contacts"],
  data() {
    return {
      colorCache: {},
      companies: this.$parent.companies,
    }
  },
  methods: {
    randomColor(id) {
      const random = () => Math.floor(256 * Math.random()) + 112

      return this.colorCache[id] || (this.colorCache[id] = `rgb(${random()}, ${random()}, ${random()})`)
    },
    viewCompany(item) {
      this.$router.push("/companies/" + item.id)
    },
  },
}
</script>

<style scoped>
.low-padding {
  padding-top: 8px !important;
  padding-bottom: 8px !important;
}
</style>
