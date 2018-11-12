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
        <v-flex xs10>
            <h1 class="display-1 text-xs-center">Unternehmensansicht</h1>
        </v-flex>
        <v-flex xs1>
        </v-flex>
        <v-flex xs1>
            <v-card dark color="green">
                <v-card-text class="headline text-xs-center">
                    <v-icon>business_center</v-icon>
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
            <v-card dark color="transparent" class="elevation-0">
                <v-card-text class="headline text-xs-left">
                    Unternehmenszweck
                </v-card-text>
            </v-card>
        </v-flex>
        <v-flex xs1>
            <v-card dark color="green" height="100%">
                <v-card-text class="headline text-xs-center">
                    <v-icon>home</v-icon>
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
            <v-card dark height="100%">
                <v-card-text 
                v-if="this.companies[this.companyId].purpose != null &&
                this.companies[this.companyId].purpose != ''" 
                class="title text-xs-left">
                    {{ this.companies[this.companyId].purpose }}
                </v-card-text>
                <v-card-text
                v-else
                class="headline text-xs-left font-weight-light font-italic">
                    N/A
                </v-card-text>
            </v-card>
        </v-flex>
        <v-flex xs1>
            <v-card dark color="green" height="212%" class="centered">
                <v-card-text class="headline text-xs-center">
                    <v-icon >place</v-icon>
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
                <v-card-text class="headline text-xs-left">
                    Sonstiges
                </v-card-text>
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
        <v-flex xs6 align-end>
            <v-card dark height="100%">
                <v-card-text 
                v-if="this.companies[this.companyId].other != null &&
                this.companies[this.companyId].other != ''" 
                class="title text-xs-left">
                    {{ this.companies[this.companyId].other }}
                </v-card-text>
                <v-card-text
                v-else
                class="headline text-xs-left font-weight-light font-italic">
                    N/A
                </v-card-text>
            </v-card>
        </v-flex>
    </v-layout>
</v-container>
</template>

<script>
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
