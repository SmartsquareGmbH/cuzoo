<template>
  <v-dialog :value="value" persistent max-width="750" @input="$emit('input')">
    <v-card>
      <v-card-title class="headline primary" primary-title> Einstellungen für {{ username }} </v-card-title>
      <v-card-text class="text-xs-left primary--text">
        <v-container>
          <span class="subheading">Persönliche Informationen</span>
          <v-text-field
            v-model="user.fullname"
            :disabled="isDemo"
            class="mt-3"
            prepend-icon="person"
            name="username"
            label="Name"
          />
          <v-text-field
            v-model="user.mail"
            :disabled="isDemo"
            persistent-hint
            hint="An diese Adresse werden Erinnerungen für TODOs gesendet"
            prepend-icon="mail"
            name="password"
            label="E-Mail"
          />
        </v-container>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn color="primary" flat @click="closeSettings()">Schließen</v-btn>
        <v-btn color="primary" flat :disabled="!valid" @click="submitUserInformation()">Speichern</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script>
import api from "../../../utils/http-common"
import { mapActions, mapGetters } from "vuex"

export default {
  props: ["value"],
  data: () => ({
    valid: false,
  }),
  computed: {
    ...mapGetters(["username", "user"]),
    userInformation() {
      return [this.user.fullname, this.user.mail]
    },
    isDemo() {
      return this.username === "demo"
    },
  },
  watch: {
    userInformation() {
      if (this.value) this.valid = true
    },
  },
  beforeMount() {
    this.getUserInformation().then(() => {
      this.fullname = this.user.fullname
      this.mail = this.user.mail
    })
  },
  mounted() {
    this.valid = false
  },
  methods: {
    ...mapActions(["getUserInformation"]),
    closeSettings() {
      this.$emit("input")
      this.valid = false
    },
    submitUserInformation() {
      api
        .put(`user/submit/info/${this.username}`, {
          fullname: this.user.fullname,
          mail: this.user.mail,
        })
        .then(() => this.closeSettings())
        .catch((err) => alert(err))
    },
  },
}
</script>
