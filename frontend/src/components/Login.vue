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
                <v-snackbar v-model="wrongCredentials" color="error" bottom>
                    Die Anmeldedaten sind ungültig!
                    <v-btn dark flat @click="wrongCredentials = false">
                        Schließen
                    </v-btn>
                </v-snackbar>
            </v-flex>
        </v-layout>
    </v-container>
</template>

<script>
    import api from '../utils/http-common'

    export default {
        name: "login",
        data: () => ({
            username: null,
            password: null,
            usernameRules: [ v => !!v || "Ein Username wird benötigt" ],
            passwordRules: [ v => !!v || "Ein Passwort wird benötigt" ],
            wrongCredentials: false,
            kazookid: require('@/assets/rsz_kazoo-kid.png')
        }),
        methods: {
            doLogin: function (event) {
                api.post('security/login', {}, {
                    auth: {
                        username: this.username,
                        password: this.password
                    }
                }).then(response => {
                    this.$store.commit({
                        type: 'storeLogData',
                        authorized: true,
                        username: this.username,
                        password: this.password
                    })
                }).then(response => {
                    this.$router.replace('/search')
                }).catch(error => {
                    if (error.response.status === 401) {
                        this.wrongCredentials = true;
                    }
                    console.error(error)
                });
            }
        }
    }
</script>