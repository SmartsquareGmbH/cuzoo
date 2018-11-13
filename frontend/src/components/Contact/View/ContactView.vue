<template>
<v-container grid-list-md text-xs-center>
  <v-layout row wrap>
    <v-flex xs1>
      <v-btn block color="secondary" @click="goPageBack()">
        <v-icon large dark>arrow_back</v-icon>
      </v-btn>
    </v-flex>
    <v-flex xs10>
      <h1 class="display-1 text-xs-center">{{ this.contacts[contactId].name }}</h1>
    </v-flex>
    <v-flex xs1>
      <v-tooltip top>
      <v-btn disabled block color="secondary" href="#/contacts" slot="activator">
        <v-icon style="transform: rotate(180deg)" large dark>publish</v-icon>
      </v-btn>
      <span>Export Kontaktinfo</span>
      </v-tooltip>
    </v-flex>
    <v-flex xs1>
      <v-card dark color="green">
        <v-card-text class="headline text-xs-center">
          <v-icon>business_center</v-icon>
        </v-card-text>
      </v-card>
    </v-flex>
    <v-flex xs5>
      <v-card dark color="secondary" v-if="this.contacts[contactId].company.name != null && this.contacts[contactId].company.name != ''">
        <v-card-text class="headline text-xs-center">
          <span>{{ this.contacts[contactId].company.name }}</span>
        </v-card-text>
      </v-card>
      <v-card v-else dark color="error">
        <v-card-text class="headline text-xs-center">
          <span>N/A</span>
        </v-card-text>
      </v-card>
    </v-flex>
    <v-flex xs1>
      <v-card dark color="info">
        <v-card-text class="headline text-xs-center">
          <v-icon>mail</v-icon>
        </v-card-text>
      </v-card>
    </v-flex>
    <v-flex xs5>
      <v-card dark color="secondary" v-if="this.contacts[contactId].mail != null && this.contacts[contactId].mail != ''">
        <v-card-text class="headline text-xs-center">
          <span>{{ this.contacts[contactId].mail }}</span>
        </v-card-text>
      </v-card>
      <v-card v-else dark color="error">
        <v-card-text class="headline text-xs-center">
          <span>N/A</span>
        </v-card-text>
      </v-card>
    </v-flex>
    <v-flex xs1>
      <v-card dark color="green">
        <v-card-text class="headline text-xs-center">
          <v-icon>person</v-icon>
        </v-card-text>
      </v-card>
    </v-flex>
    <v-flex xs5>
      <v-card dark color="secondary">
        <v-card-text class="headline text-xs-center">
          <span>{{ this.contacts[contactId].role }}</span>
        </v-card-text>
      </v-card>
    </v-flex>
    <v-flex xs1>
      <v-card dark color="info">
        <v-card-text class="headline text-xs-center">
          <v-icon>phone</v-icon>
        </v-card-text>
      </v-card>
    </v-flex>
    <v-flex xs5>
      <v-card dark color="secondary" v-if="this.contacts[contactId].telephone != null && this.contacts[contactId].telephone != ''">
        <v-card-text class="headline text-xs-center">
          <span>{{ this.contacts[contactId].telephone }}</span>
        </v-card-text>
      </v-card>
      <v-card v-else dark color="error">
        <v-card-text class="headline text-xs-center">
          <span>N/A</span>
        </v-card-text>
      </v-card>
    </v-flex>
    <v-flex xs6>
      <v-card dark height="210%" color="secondary">
        <v-card-text class="headline text-xs-left pl-3">
           
        </v-card-text>
      </v-card>
    </v-flex>
    <v-flex xs3>
      <v-card dark color="secondary">
        <v-card-text class="headline text-xs-left pl-3">
          Kontaktaufnahme: 
        </v-card-text>
      </v-card>
    </v-flex>
    <v-flex xs3 v-if="this.contacts[contactId].lastContact != null && this.contacts[contactId].lastContact != ''">
      <v-card dark color="success">
        <v-card-text class="headline text-xs-center">
          <span>{{ this.contacts[contactId].lastContact }}</span>
        </v-card-text>
      </v-card>
    </v-flex>
    <v-flex xs3 v-else>
      <v-card dark color="error">
        <v-card-text class="headline text-xs-center">
          <span>N/A</span>
        </v-card-text>
      </v-card>
    </v-flex>
    <v-flex xs6>
    </v-flex>
    <v-flex xs3>
      <v-card dark color="secondary">
        <v-card-text class="headline text-xs-left pl-3">
          Antwort: 
        </v-card-text>
      </v-card>
    </v-flex>
    <v-flex xs3>
      <v-card dark v-if="this.contacts[contactId].lastAnswer != null && this.contacts[contactId].lastContact != ''" color="success">
        <v-card-text class="headline text-xs-center">
          <span>{{ this.contacts[contactId].lastAnswer }}</span>
        </v-card-text>
      </v-card>
      <v-card dark v-else color="error">
        <v-card-text class="headline text-xs-center">
          <span>N/A</span>
        </v-card-text>
      </v-card>
    </v-flex>
  </v-layout>
</v-container>
</template>

<script>
export default {
  computed: {
    contactId() {
      return this.$route.params.id;
    },
    contacts() {
      return this.$store.getters.getContacts;
    }
  },
  methods: {
    contactPointExists() {
      if (this.contacts[contactId].jug == 'x' || this.contacts[contactId].cloudLab == 'x' || 
          this.contacts[contactId].cioDay == 'x' || this.contacts[contactId].cloudFlyer == 'x') {
        return true;
      } else {
        return false;
      }
    },
    goPageBack() {
      this.$router.go(-1);
    }
  }
}
</script>