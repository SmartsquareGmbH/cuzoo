<template>
    <v-scroll-x-transition>
    <v-layout row wrap class="clickable">
        <v-flex xs1>
            <v-card color="info" height="100%">
                <v-card-text class="pt-4">
                    <v-icon large class="pt-4">{{ getPointTypeIconOf(contactPoint.type) }}</v-icon>
                </v-card-text>
            </v-card>
        </v-flex>
        <v-flex xs11>
            <v-hover>
            <v-card
                    slot-scope="{ hover }"
                    :class="`elevation-${hover ? 6 : 2}`"
                    v-on:click="viewContactPoint(contactPoint)"
                    height="100%">
                <v-scroll-x-transition>
                    <v-btn v-if="hover" absolute right top fab small color="secondary" @click=""
                           class="elevation-12 mr-5">
                        <v-icon size="24px" color="white">
                            edit
                        </v-icon>
                    </v-btn>
                </v-scroll-x-transition>
                <v-scroll-x-transition>
                    <v-btn v-if="hover" absolute right top fab small color="secondary" @click.native="deleteContactPoint(todo)"
                           class="elevation-12">
                        <v-icon size="24px" color="error">
                            delete
                        </v-icon>
                    </v-btn>
                </v-scroll-x-transition>
                <v-card-title class="secondary headline font-weight-light">
                    {{ contactPoint.title }} •
                    <span class="ml-2 primary--text mr-2">{{ dateFormatted }}</span> •
                    <v-icon class="ml-1">attach_file</v-icon>
                    <span class="ml-1 primary--text">{{ fileNames.length }}</span>
                    <v-spacer></v-spacer>
                    <v-chip
                            :class="`mt-${hover ? 4 : 0} mb-${hover ? 0 : 2}`"
                            color="white">
                        <v-avatar class="info white--text" size="35px">
                            <v-icon>person</v-icon>
                        </v-avatar>
                        <span class="headline secondary--text">
                            {{ contactPoint.contact.name }}
                        </span>
                    </v-chip>
                </v-card-title>
                <v-card-text class="subheading text-xs-left text-truncate" width="20%">
                    {{ contactPoint.comment }}
                </v-card-text>
            </v-card>
            </v-hover>
        </v-flex>
    </v-layout>
    </v-scroll-x-transition>
</template>

<script>
    import api from '../../utils/http-common'

    const datefns = require('date-fns');
    const de = require('date-fns/locale/de');

    export default {
        props: ['contactPoint'],
        data() {
            return {
                contactPoints: this.$parent.contactPoints,
                company: this.$parent.company,
                fileNames: []
            }
        },
        computed: {
            dateFormatted() {
                return datefns.format(this.contactPoint.date, 'DD.MM.YY', { locale: de });
            }
        },
        mounted() {
            this.refreshData();
        },
        methods: {
            getPointTypeIconOf: function (type) {
                switch (type) {
                    case 'Telefon':
                        return 'phone'
                    case 'E-Mail':
                        return 'mail'
                    case 'Social Media':
                        return 'share'
                    case 'Persönlich':
                        return 'people'
                }
            },
            viewContactPoint(contactPoint) {
                this.$router.push(this.$route.fullPath + '/' + (contactPoint.id));
            },
            refreshData() {
                api.get(`file/get/names/${this.contactPoint.id}`)
                    .then(response => {
                        if (response.data.length > 0) {
                            this.fileNames = response.data;
                        }
                    })
                    .catch(error => {
                        console.log(error);
                    });
            },
            deleteContactPoint() {
                api.delete(`point/delete/${this.contactPoint.id}`)
                    .then(() => this.$parent.refreshContactPoints);
            }
        }
    }
</script>

<style>
    .clickable {
        cursor: pointer;
    }
</style>