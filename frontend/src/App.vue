<template>
    <v-app :dark="dark">
        <v-content v-if="!authorized">
            <login></login>
        </v-content>
        <v-container v-if="authorized">
            <v-toolbar
                    class="elevation-0"
                    :light="dark"
                    :dark="!dark"
                    clipped-left
                    fixed
                    app>
                <v-toolbar-title class="mr-2">
                    <v-chip
                            @click.native="goHome"
                            outline
                            color="black">
                        <span class="headline font-weight-light">
                            CUZOO
                        </span>
                    </v-chip>
                </v-toolbar-title>
                <v-toolbar-items class="hidden-sm-and-down">
                    <v-btn flat href="#/dashboard" disabled>
                        <v-icon>dashboard</v-icon>
                        <span class="ml-2">Dashboard</span>
                    </v-btn>
                    <v-btn flat href="#/companies">
                        <v-icon>business_center</v-icon>
                        <span class="ml-2">Unternehmen</span>
                    </v-btn>
                    <v-btn flat href="#/contacts">
                        <v-icon>people</v-icon>
                        <span class="ml-2">Ansprechpartner</span>
                    </v-btn>
                    <v-btn flat href="#/search">
                        <v-icon>search</v-icon>
                        <span class="ml-2">Suche</span>
                    </v-btn>
                </v-toolbar-items>
                <v-spacer></v-spacer>
                <v-toolbar-items>
                    <v-menu offset-y>
                        <v-btn slot="activator" color="primary">
                            <v-icon color="black">person</v-icon>
                            <span class="ml-2 black--text">{{ username }}</span>
                        </v-btn>
                        <v-list light>
                            <v-list-tile @click="openSettings()">
                                <v-list-tile-title class="mr-2">Einstellungen</v-list-tile-title>
                                <v-icon color="black" light>settings</v-icon>
                            </v-list-tile>
                            <v-list-tile @click="logout()">
                                <v-list-tile-title class="mr-2">Logout</v-list-tile-title>
                                <v-icon style="transform: rotate(90deg)" color="black" light>publish</v-icon>
                            </v-list-tile>
                        </v-list>
                    </v-menu>
                </v-toolbar-items>
            </v-toolbar>
            <settings v-model="settingState"/>
            <v-content>
                <router-view></router-view>
            </v-content>
            <v-footer
                    class="justify-center"
                    :light="!dark"
                    :dark="dark"
                    app>
                &copy; 2018 Smartsquare GmbH
            </v-footer>
        </v-container>
    </v-app>
</template>

<script>
    import {mapGetters, mapMutations} from 'vuex';

    import Login from "@/components/main/Login.vue";
    import Settings from "@/components/main/Settings.vue";

    export default {
        components: {
            Login,
            Settings
        },
        data() {
            return {
                drawer: null,
                settingState: false
            }
        },
        computed: {
            ...mapGetters({
                dark: 'darkState',
                authorized: 'authorized',
                username: 'username'
            })
        },
        methods: {
            ...mapMutations(['storeLogData']),
            goHome() {
                this.$router.push('/search');
            },
            openSettings() {
                this.settingState = true;
            },
            logout() {
                this.storeLogData({authorized: false});
            }
        }
    }
</script>

<style>
    .dm-switch {
        padding: 1em;
    }

    .nav-header h1 {
        padding: 0.35em;
        text-align: center;
    }

    * {
        margin: 0px;
        padding: 0px;
        font-family: "Avenir", Helvetica, Arial, sans-serif;
    }

    #app {
        -webkit-font-smoothing: antialiased;
        -moz-osx-font-smoothing: grayscale;
        text-align: center;
        color: #ffffff;
    }
</style>
