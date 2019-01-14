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
                    <v-switch
                            disabled
                            class="dm-switch pt-1 mt-3"
                            label="Darkmode"
                            v-model="dark"/>
                    <v-btn flat icon disabled>
                        <v-icon style="transform: rotate(90deg)" dark>publish</v-icon>
                    </v-btn>
                </v-toolbar-items>
            </v-toolbar>
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
    import store from '@/store.js'
    import Login from "@/components/Login.vue"

    export default {
        components: {
            Login
        },
        data() {
            return {
                dark: store.getters.getDarkState,
                drawer: null,
            }
        },
        computed: {
            authorized() {
                return store.getters.getAuthorized
            }
        },
        methods: {
            goHome() {
                this.$router.push('/search');
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

    #sidebar {
        position: fixed;
        width: 12.5%;
        height: 100%;
        background: #151719;
    }

    .sidebar-header {
        padding: 1em;
        color: white;
        border-bottom: 1px solid rgba(100, 100, 100, 0.3);
    }

    #sidebar a {
        color: #ffffff;
        text-decoration: none;
        font-weight: bold;
    }

    #sidebar a.router-link-exact-active {
        color: skyblue;
    }

    #sidebar ul li {
        color: rgba(230, 230, 230, 0.9);
        list-style: none;
        padding: 15px 10px;
        text-align: left;
        border-bottom: 1px solid rgba(100, 100, 100, 0.3);
    }
</style>
