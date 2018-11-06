<template>
<v-container grid-list-md text-xs-center>
  <v-layout row wrap>
    <v-flex xs12>
      <v-text-field
      color="primary"
      v-model="search"
      append-icon="search"
      label="Suche..."
      hide-details
      outline>
      </v-text-field>
    </v-flex>
    <v-flex xs1>
      <v-btn class="mt-3" v-if="this.firstToDisplay > 0" block color="info" @click="previous"><v-icon>arrow_back</v-icon></v-btn>
    </v-flex>
    <v-flex xs4>
        <v-card-text class="headline text-xs-center">
          Unternehmen
        </v-card-text>
    </v-flex>
    <v-flex xs2>
      <v-card-text class="text-xs-center mt-2">
        Seite {{ this.page }} / {{ Math.ceil(this.filteredCompanies.length / 10) }}
      </v-card-text>
    </v-flex>
    <v-flex xs4>
        <v-card-text class="headline text-xs-center">
          Kontaktpersonen
        </v-card-text>
    </v-flex>
    <v-flex xs1>
      <v-btn class="mt-3" v-if="this.lastToDisplay < this.filteredCompanies.length" block color="info" @click="next"><v-icon>arrow_forward</v-icon></v-btn>
    </v-flex>
    <v-flex xs6>
      <v-card v-bind:key="company.id" v-for="company in filteredCompanies.slice(firstToDisplay ,lastToDisplay)" color="secondary" class="single-companies mb-2">
        <v-card-text class="title pa-3 text-xs-left info--text">
          {{ company.name }}
          <v-divider 
          v-if="(company.place != null && company.place != '') || (company.street != null && company.street != '') " 
          class="mt-2 mb-2"/>
          <span class="subheading pt-2 pb-2 text-xs-left white--text">
            {{ company.zipCode }} {{ company.place }}
            <v-spacer/> {{ company.street }}
          <v-divider 
          v-if="(company.purpose != null && company.purpose != '') || (company.other != null && company.other != '') " 
          class="mt-2 mb-2"/>
            <v-spacer/>
            {{ company.purpose }}
            <v-spacer/>
            {{ company.other }}
          <v-divider v-if="company.homepage != null && company.homepage != ''" class="mt-2 mb-2"/>
            <v-spacer/>
              {{ company.homepage }}
          </span>
        </v-card-text>
      </v-card>
    </v-flex>
    <v-flex xs6>
      <v-card v-bind:key="contact.id" v-for="contact in filteredContacts.slice(firstToDisplay ,lastToDisplay)" color="secondary" class="single-contacts mb-2">
        <v-card-text class="title pt-3 text-xs-left white--text">
          {{ contact.name }} 
          <v-divider class="mt-2 mb-2"/>
          <span class="font-italic white--text">{{ contact.role }}</span>
            <v-spacer/>
          <span class="info--text font-italic">
            <v-spacer/>
            {{ contact.company }}
          </span>
        </v-card-text>
        <v-card-actions>
          <v-btn block color="info" @click="viewContact(contact)">
            INFO
          </v-btn>
          <v-btn disabled block color="green">
            KONTAKT
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-flex>
    </v-layout>
  </v-container>
</template>

<script>
// @ is an alias to /src
import HelloWorld from "@/components/HelloWorld.vue";
export default {
  name: "home",
  components: {
    HelloWorld
  },
  data() {
    return {
      search: '',
      page: 1,
      firstToDisplay: 0,
      lastToDisplay: 10
    }
  },
  methods: {
    previous () {
      this.firstToDisplay = this.firstToDisplay - 10;
      this.lastToDisplay = this.lastToDisplay - 10;
      this.page = this.page - 1;
    },
    next () {
      this.firstToDisplay = this.firstToDisplay + 10;
      this.lastToDisplay = this.lastToDisplay + 10;
      this.page = this.page + 1;
    },
    viewContact: function(item) {
      const index = this.contacts.findIndex(contact => contact.id == item.id);
      this.$router.replace('/contacts/' + (index));
    }
  },
  computed: {
    pageReset () {
      if (this.search != '') {
        this.firstToDisplay = 0;
        this.lastToDisplay = 10;
        this.page = 1;
      }
      return 0;
    },
    companies() {
      return this.$store.getters.getCompanies;
    },
    contacts() {
      return this.$store.getters.getContacts;
    },
    filteredCompanies() {
      return this.companies.filter((company) => {
        return company.name.toLowerCase().includes((this.search.toLowerCase()));
      })
    },
    filteredContacts() {
      return this.contacts.filter((contact) => {
        if (contact.name.toLowerCase().includes((this.search.toLowerCase()))) {
          return contact.name.toLowerCase().includes((this.search.toLowerCase()));
        } else {
          if (contact.company != null) {
            return contact.company.toLowerCase().includes((this.search.toLowerCase()));
          }
        }
      })
    }
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
h1, h2 {
  font-weight: normal;
}
ul {
  list-style-type: none;
  padding: 0;
}
li {
  display: inline-block;
  margin: 0 10px;
}
a {
  color: #42b983;
}
</style>
