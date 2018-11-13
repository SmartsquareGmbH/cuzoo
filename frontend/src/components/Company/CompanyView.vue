<template>
<v-container grid-list-md text-xs-center fill-height fluid>
    <v-layout row wrap>
        <v-flex xs1>
            <v-card>
                <v-btn block color="secondary" @click="goPageBack()">
                    <v-icon large dark>arrow_back</v-icon>
                </v-btn>
            </v-card>
        </v-flex>
        <v-flex xs4>
            <h1 class="display-1 text-xs-center">Unternehmen</h1>
        </v-flex>
        <v-flex xs2>
        </v-flex>
        <v-flex xs4>
            <h1 class="display-1 text-xs-center">Ansprechpartner</h1>
        </v-flex>
        <v-flex xs1>
        </v-flex>
        <v-flex xs1>
            <v-card dark color="green">
                <v-card-text class="headline text-xs-center">
                    <v-icon size="30px">business_center</v-icon>
                </v-card-text>  
            </v-card>
        </v-flex>
        <v-flex xs5>
            <v-card dark>
                <v-card-text class="headline text-xs-left">
                    {{ this.companies[this.companyId].name }}
                </v-card-text>
            </v-card>
        </v-flex>
        <v-flex xs6>
            <v-card v-bind:key="contact.id" v-for="contact in contactsOfCompany" color="secondary" class="single-contacts mb-2">

            </v-card>
        </v-flex>
        <v-flex xs1>
            <v-card dark color="green" height="100%">
                <v-card-text class="headline text-xs-center">
                    <v-icon size="30px">home</v-icon>
                </v-card-text>  
            </v-card>
        </v-flex>
        <v-flex xs5>
            <v-card dark height="100%">
                <v-card-text 
                v-if="this.companies[this.companyId].homepage != null &&
                this.companies[this.companyId].homepage != ''" 
                class="headline text-xs-left">
                    <a :href=this.homepage target="_blank">{{ this.companies[this.companyId].homepage }}</a>
                </v-card-text>
                <v-card-text
                v-else
                class="headline text-xs-left font-weight-light font-italic">
                    N/A
                </v-card-text>
            </v-card>
        </v-flex>
        <v-flex xs6>
            <v-card dark color="transparent" class="elevation-0">

            </v-card>
        </v-flex>

        <v-flex xs1>
            <v-card dark color="green" height="212%" class="centered">
                <v-card-text class="headline text-xs-center">
                    <v-icon size="30px">place</v-icon>
                </v-card-text>  
            </v-card>
        </v-flex>
        <v-flex xs1>
            <v-card dark>
                <v-card-text 
                v-if="this.companies[this.companyId].zipCode != null &&
                this.companies[this.companyId].zipCode != ''" 
                class="headline text-xs-left">
                    {{ this.companies[this.companyId].zipCode }}
                </v-card-text>
                <v-card-text
                v-else
                class="headline text-xs-left font-weight-light font-italic">
                    N/A
                </v-card-text>
            </v-card>
        </v-flex>
        <v-flex xs4>
            <v-card dark>
                <v-card-text 
                v-if="this.companies[this.companyId].place != null &&
                this.companies[this.companyId].place != ''" 
                class="headline text-xs-left">
                    {{ this.companies[this.companyId].place }}
                </v-card-text>  
                <v-card-text
                v-else
                class="headline text-xs-left font-weight-light font-italic">
                    N/A
                </v-card-text>
            </v-card>
        </v-flex>
        <v-flex xs6>
            <v-card dark color="transparent" class="elevation-0">

            </v-card>
        </v-flex>
        <v-flex xs1>
            <v-card height="100%" color="transparent elevation-0">

            </v-card>
        </v-flex>
        <v-flex xs5>
            <v-card dark height="100%">
                <v-card-text 
                v-if="this.companies[this.companyId].street != null &&
                this.companies[this.companyId].street != ''"
                class="headline text-xs-left">
                    {{ this.companies[this.companyId].street }}
                </v-card-text> 
                <v-card-text
                v-else
                class="headline text-xs-left font-weight-light font-italic">
                    N/A
                </v-card-text>
            </v-card>
        </v-flex>
        <v-flex xs6>
            <v-card dark color="transparent">
            </v-card>
        </v-flex>
        <v-flex xs6>
            <v-card dark color="transparent" class="elevation-0">
                <v-card-text class="headline text-xs-left">
                    <v-tooltip top>
                    <v-icon color="info" size="36px" slot="activator">info</v-icon>
                        Hauptfunktionen des Unternehmens
                    </v-tooltip>
                    Unternehmenszweck
                </v-card-text>
            </v-card>
        </v-flex>
        <v-flex xs6>
            <v-card dark color="transparent" class="elevation-0">
            </v-card>
        </v-flex>
        <v-flex xs6>
            <v-card dark height="100%">
                <v-card-text 
                v-if="this.companies[this.companyId].purpose != null &&
                this.companies[this.companyId].purpose != ''" 
                class="headline text-xs-left">
                    {{ this.companies[this.companyId].purpose }}
                </v-card-text>
                <v-card-text
                v-else
                class="headline text-xs-left font-weight-light font-italic">
                    N/A
                </v-card-text>
            </v-card>
        </v-flex>
        <v-flex xs6>
            <v-card dark color="transparent" class="elevation-0">
            </v-card>
        </v-flex>
        <v-flex xs6>
            <v-card dark color="transparent" class="elevation-0">
                <v-card-text class="headline text-xs-left">
                    <v-tooltip top>
                    <v-icon color="info" size="36px" slot="activator">info</v-icon>
                        Sonstige Angaben zum Unternehmen
                    </v-tooltip>
                    Sonstiges
                </v-card-text>
            </v-card>
        </v-flex>
        <v-flex xs6>
            <v-card dark color="transparent" class="elevation-0">
            </v-card>
        </v-flex>
        <v-flex xs6>
            <v-card dark height="100%">
                <v-card-text 
                v-if="this.companies[this.companyId].other != null &&
                this.companies[this.companyId].other != ''" 
                class="headline text-xs-left">
                    {{ this.companies[this.companyId].other }}
                </v-card-text>
                <v-card-text
                v-else
                class="headline text-xs-left font-weight-light font-italic">
                    N/A
                </v-card-text>
            </v-card>
        </v-flex>
        <v-flex xs6>
            <v-card dark color="transparent" class="elevation-0">
            </v-card>
        </v-flex>
    </v-layout>
</v-container>
</template>

<script>
import { mapState } from 'vuex';

export default {
    computed: {
        companyId() {
            return this.$route.params.id;
        },
        companies() {
            return this.$store.getters.getCompanies;
        },
        homepage() {
            return "http://" + this.companies[this.companyId].homepage;
        },
        ...mapState(['contacts']),
        contacts: {
            get () {
                return this.$store.state.contacts
            },
            set (contacts) {
                this.$store.commit('storeContacts', contacts)
            }
        },
        contactsOfCompany() {
            return this.contacts.filter((contact) => {
                console.log(contact);
                if (contact.company.toLowerCase().includes((this.companies[this.$route.params.id].toLowerCase()))) {
                    return contact.company.toLowerCase().includes((this.companies[this.$route.params.id].toLowerCase()));
                } else {
                    if (contact.company != null) {
                        return contact.company.toLowerCase().includes((this.companies[this.$route.params.id].toLowerCase()));
                    }
                }
            })
        }
    },
    methods : {
        goPageBack() {
            this.$router.back();
        }
    }
}
</script>

<style>
.centered {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 100vh;
}
</style>
