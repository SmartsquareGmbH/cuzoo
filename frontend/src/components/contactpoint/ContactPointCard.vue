<template>
    <v-fade-transition>
        <v-hover>
            <v-card slot-scope="{ hover }"
                    :class="`mt-2 mb-3 elevation-${hover ? 6 : 2}`"
                    @click="viewContactPoint(contactPoint)">
                <v-scroll-x-transition>
                    <v-btn v-if="hover && search === false" absolute right top fab small color="secondary" @click.stop="editContactPoint()"
                           class="elevation-12 mr-5">
                        <v-icon size="24px" color="white">
                            edit
                        </v-icon>
                    </v-btn>
                </v-scroll-x-transition>
                <v-scroll-x-transition>
                    <v-btn v-if="hover && search === false" absolute right top fab small color="secondary"
                           @click.stop="deleteContactPoint()"
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
                    <span class="ml-1 mr-2 primary--text">{{ fileNames.length }}</span>
                    <span v-if="search === true"
                          class="ml-1 font-weight-light font-italic headline">
                        {{ contactPoint.contact.company.name }}
                    </span>
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
                <v-divider/>
                <v-card-text class="subheading text-xs-left">
                    <v-chip
                            color="info"
                            class="title mt-1 mb-1"
                            v-bind:key="contactPoint.label"
                            v-for="label in contactPoint.labels">
                        {{ label }}
                    </v-chip>
                </v-card-text>
            </v-card>
        </v-hover>
    </v-fade-transition>
</template>

<script>
    import api from '../../utils/http-common'
    import {mapGetters, mapMutations} from 'vuex'

    const datefns = require('date-fns');
    const de = require('date-fns/locale/de');

    export default {
        props: ['contactPoint', 'search'],
        data() {
            return {
                contactPoints: this.$parent.contactPoints,
                contactNames: [],
                company: this.$parent.company,
                fileNames: []
            }
        },
        computed: {
            ...mapGetters(['companies']),
            dateFormatted() {
                return datefns.format(this.contactPoint.date, 'DD.MM.YY', {locale: de});
            }
        },
        mounted() {
            this.refreshData();
        },
        methods: {
            ...mapMutations({
                storeDetails: 'storeEditedContactPointDetails',
            }),
            viewContactPoint(contactPoint) {
                const index = this.companies.findIndex(company => company.id === contactPoint.contact.company.id);
                this.$router.push(`/${index}/${contactPoint.id}`);
            },
            refreshData() {
                api.get(`file/get/names/${this.contactPoint.id}`)
                    .then(response => {
                        if (response.data.length > 0) {
                            this.fileNames = response.data;
                        }
                    })
                    .catch(error => {
                        alert(error);
                    });
            },
            editContactPoint() {
                this.$parent.getContactsOfCompany().forEach(contact => {
                    this.$parent.contactNames.push(contact.name)
                });

                this.$parent.contactNames.sort();

                this.storeDetails({
                    editedIndex: this.contactPoints.indexOf(this.contactPoint),
                    editedContactPoint: Object.assign({}, this.contactPoint)
                });

                this.$parent.contactPointDialogState = true;
            },
            deleteContactPoint() {
                if (confirm("Bist du dir sicher, dass du diesen Kontaktpunkt löschen willst?")) {
                    api.delete(`point/delete/${this.contactPoint.id}`)
                        .then(() => this.$parent.refreshData())
                        .catch(error => {
                            alert(error);
                        });
                }
            }
        }
    }
</script>

<style>
    .clickable {
        cursor: pointer;
    }
</style>