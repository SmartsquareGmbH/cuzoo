<template>
    <v-card 
    class="secondary mt-3 clickable"
    @click.native="viewCompany(company)">
        <v-card-text class="headline text-xs-left">
            {{ company.name }}
            <v-spacer/>
            <v-chip
            :color="randomColor(contact.id)"
            class="mt-3"
            v-bind:key="contact.id"
            v-for="contact in this.$parent.getContactsOfCompany(company.name)">
                <span class="headline black--text">
                    {{ contact.name }}
                </span>
            </v-chip>
        </v-card-text>
    </v-card>
</template>

<script>
import {mapState} from 'vuex';

export default {
    props: ['company'],
    data() {
        return {
            colorCache: {}
        }
    },
    methods: {
        randomColor(id) {
            const random = () => Math.floor(256 * Math.random()) + 112;

            return this.colorCache[id] || (this.colorCache[id] = `rgb(${random()}, ${random()}, ${random()})`);
        }
    }
}
</script>
