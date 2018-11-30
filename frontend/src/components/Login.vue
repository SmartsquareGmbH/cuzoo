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
                            <v-text-field v-on:keyup.enter="doLogin" v-model="logName" prepend-icon="person"
                                          name="login" label="User" type="text"></v-text-field>
                            <v-text-field v-on:keyup.enter="doLogin" v-model="logPass" id="password" prepend-icon="lock"
                                          name="password" label="Passwort" type="password"></v-text-field>
                        </v-form>
                    </v-card-text>
                    <v-card-actions>
                        <v-spacer></v-spacer>
                        <v-btn v-on:click.native="doLogin" color="primary">Login</v-btn>
                    </v-card-actions>
                </v-card>
            </v-flex>
        </v-layout>
    </v-container>
</template>

<script>
    import api from '../utils/http-common'

    export default {
        name: "login",
        data: () => ({
            logName: null,
            logPass: null,
            drawer: null,
            kazookid: require('@/assets/rsz_kazoo-kid.png')
        }),
        methods: {
            doLogin: function (event) {
                api.post('security/login', {}, {
                    auth: {
                        username: this.logName,
                        password: this.logPass
                    }
                }).then(response => {
                    this.$store.commit({
                        type: 'storeLogData',
                        authorized: true,
                        username: this.logName,
                        password: this.logPass
                    })
                }).then(response => {
                    this.$router.replace('/search')
                }).catch(error => {
                    if (error.response.status === 401) {
                        alert("Die Anmeldedaten sind ungültig!");
                    }
                    console.error(error)
                });
            }
        }
    }
</script>