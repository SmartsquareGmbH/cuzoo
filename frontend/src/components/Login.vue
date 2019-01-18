<template>
    <v-container fluid fill-height>
        <v-layout align-center justify-center>
            <v-flex xs12 sm8 md4>
                <v-card class="elevation-12">
                    <v-toolbar dark color="primary">
                        <v-toolbar-title>Login</v-toolbar-title>
                        <v-spacer/>
                        <v-img
                                :src=kazookid
                                max-height="64px"
                                max-width="64px"
                        />
                    </v-toolbar>
                    <v-card-text>
                        <v-form>
                            <v-text-field
                                    @keyup.enter="doLogin" v-model="username" prepend-icon="person"
                                    name="username" label="User" type="text" :rules="usernameRules"/>
                            <v-text-field
                                    @keyup.enter="doLogin" v-model="password" prepend-icon="lock"
                                    name="password" label="Passwort" type="password" :rules="passwordRules"/>
                        </v-form>
                    </v-card-text>
                    <v-card-actions>
                        <v-spacer/>
                        <v-btn @click.native="doLogin" color="primary">Login</v-btn>
                    </v-card-actions>
                </v-card>
                <v-snackbar v-model="loginFailed" color="error" bottom>
                    {{ this.loginFailedMessage }}
                    <v-btn dark flat @click="wrongCredentials = false">
                        Schließen
                    </v-btn>
                </v-snackbar>
            </v-flex>
        </v-layout>
    </v-container>
</template>

<script>
    import {mapMutations} from 'vuex'
    import api from '../utils/http-common'

    export default {
        name: "login",
        data: () => ({
            username: null,
            password: null,
            usernameRules: [v => !!v || "Ein Username wird benötigt"],
            passwordRules: [v => !!v || "Ein Passwort wird benötigt"],
            loginFailed: false,
            loginFailedMessage: "Die Anmeldedaten sind ungültig!",
            kazookid: require('@/assets/rsz_kazoo-kid.png')
        }),
        methods: {
            ...mapMutations(['storeLogData']),
            doLogin() {
                api.post('security/login', {}, {
                    auth: {
                        username: this.username,
                        password: this.password
                    }
                }).then(() => {
                    this.storeLogData({
                        authorized: true,
                        username: this.username,
                        password: this.password
                    })
                }).then(() => {
                    this.$router.replace('/search')
                }).catch(error => {
                    if (error.message === "Request failed with status code 401") {
                        this.loginFailedMessage = "Die Anmeldedaten sind ungültig!"
                    } else {
                        this.loginFailedMessage = "Es konnte keine Verbindung zum Server hergestellt werden"
                    }

                    this.loginFailed = true
                });
            }
        }
    }
</script>