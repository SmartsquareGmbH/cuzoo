<template>
  <v-container fluid fill-height>
    <v-layout align-center justify-center row wrap>
      <v-flex xs12 sm8 md4>
        <v-card class="elevation-8 mb-4">
          <v-toolbar dark color="primary" class="login-header">
            <v-toolbar-title>Login</v-toolbar-title>
            <v-spacer />
            <v-img :src="logo" max-width="136px" />
          </v-toolbar>
          <v-card-text>
            <v-form>
              <v-text-field
                v-model="username"
                prepend-icon="person"
                name="username"
                label="User"
                type="text"
                :rules="usernameRules"
                @keyup.enter="doLogin"
              />
              <v-text-field
                v-model="password"
                prepend-icon="lock"
                name="password"
                label="Passwort"
                type="password"
                :rules="passwordRules"
                @keyup.enter="doLogin"
              />
            </v-form>
          </v-card-text>
          <v-card-actions>
            <v-spacer />
            <v-btn outline color="primary" @click.native="doDemoLogin">Demo</v-btn>
            <v-btn
              :disabled="isDemo"
              color="primary"
              class="secondary--text"
              @click.native="doLogin(username, password)"
              >Login</v-btn
            >
          </v-card-actions>
        </v-card>
        <v-snackbar v-model="loginFailed" color="error" bottom>
          {{ loginFailedMessage }}
          <v-btn dark flat @click="wrongCredentials = false">
            Schließen
          </v-btn>
        </v-snackbar>
      </v-flex>
    </v-layout>
  </v-container>
</template>

<script>
import { mapMutations } from "vuex"
import api from "../../../utils/http-common"

export default {
  data: () => ({
    username: undefined,
    password: undefined,
    usernameRules: [(v) => !!v || "Ein Username wird benötigt"],
    passwordRules: [(v) => !!v || "Ein Passwort wird benötigt"],
    loginFailed: false,
    loginFailedMessage: "Die Anmeldedaten sind ungültig!",
    logo: require("@/assets/sq_white.png"),
  }),
  computed: {
    isDemo() {
      return process.env.VUE_APP_ENV_MODE === "demo"
    },
  },
  methods: {
    ...mapMutations(["storeLogData"]),
    doLogin(username, password) {
      api
        .post(
          "security/login",
          {},
          {
            auth: {
              username: username,
              password: password,
            },
          }
        )
        .then(() => {
          this.storeLogData({
            authorized: true,
            username: username,
            password: password,
          })
        })
        .then(() => {
          this.$router.replace("/dashboard")
        })
        .catch((error) => {
          if (error.message === "Request failed with status code 401") {
            this.loginFailedMessage = "Die Anmeldedaten sind ungültig!"
          } else {
            this.loginFailedMessage = "Es konnte keine Verbindung zum Server hergestellt werden"
          }

          this.loginFailed = true
        })
    },
    doDemoLogin() {
      if (this.isDemo) this.doLogin("demo", "password")
      else window.open("https://cuzoo-demo.smartsquare.de/")
    },
  },
}
</script>

<style scoped>
.login-header {
  padding-left: 20px !important;
  padding-right: 20px !important;
}
</style>
