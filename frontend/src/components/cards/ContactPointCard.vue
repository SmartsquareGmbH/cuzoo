<template>
    <v-fade-transition>
        <v-hover>
            <v-card slot-scope="{ hover }"
                    :class="`mb-${search ? 3 : 2} clickable elevation-${hover ? 10 : 2}`"
                    @click="viewContactPoint(contactPoint)">
                <div v-if="search === false">
                    <v-scroll-x-transition>
                        <v-btn
                                v-if="hover"
                                @click.stop="editContactPoint()"
                                class="elevation-12 mr-5"
                                color="primary"
                                absolute right top fab small>
                            <v-icon size="24px" color="secondary">
                                edit
                            </v-icon>
                        </v-btn>
                    </v-scroll-x-transition>
                    <v-scroll-x-transition>
                        <v-btn v-if="hover"
                               @click.stop="openConfirmDialog()"
                               class="elevation-12"
                               color="error"
                               absolute right top fab small>
                            <v-icon size="24px" color="secondary">
                                delete
                            </v-icon>
                        </v-btn>
                    </v-scroll-x-transition>
                </div>
                <confirm-dialog
                        v-model="confirmDialogState"
                        :questionToBeConfirmed="deleteContactPointMessage"
                        @confirmed="deleteContactPoint()"/>
                <v-card-title class="secondary no-padding-bottom">
                    <span class="headline font-weight-light">{{ contactPoint.title }}</span>
                </v-card-title>
                <v-card-title class="secondary title font-weight-light low-padding-y low-padding-left">
                    <v-icon :size="24">attach_file</v-icon>
                    <span class="mx-1 primary--text">{{ fileNames.length }}</span>
                    <v-icon class="mx-1">share</v-icon>
                    <v-chip small
                            class="subheading my-1 primary--text"
                            v-bind:key="type"
                            v-for="type in contactPoint.types">
                        {{ type }}
                    </v-chip>
                    <v-icon class="mx-1">business</v-icon>
                    <v-tooltip top>
                        <v-chip small slot="activator"
                                class="subheading my-1 primary--text">
                            {{ contactPoint.contact.company.name | truncate(25) }}
                        </v-chip>
                        <span class="title font-weight-light">{{ contactPoint.contact.company.name }}</span>
                    </v-tooltip>
                    <v-spacer></v-spacer>
                    <v-icon>person</v-icon>
                    <v-chip small
                            class="subheading my-1 primary--text">
                        {{ contactPoint.contact.name }}
                    </v-chip>
                </v-card-title>
                <v-card-text
                        v-if="contactPoint.comment"
                        class="text-xs-left text-truncate" width="20%">
                    {{ contactPoint.comment }}
                </v-card-text>
                <v-divider v-if="contactPoint.labels.length > 0"/>
                <v-card-text class="subheading text-xs-left low-padding">
                    <v-chip
                            color="info"
                            class="subheading mt-1 mb-1"
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
    import api from '../../utils/http-common';
    import {mapGetters, mapMutations} from 'vuex';

    import ConfirmDialog from "../dialogs/ConfirmDialog.vue";

    const datefns = require('date-fns');
    const de = require('date-fns/locale/de');

    export default {
        props: ['contactPoint', 'search'],
        components: {
            ConfirmDialog
        },
        data() {
            return {
                contactPoints: this.$parent.contactPoints,
                contactNames: [],
                company: this.$parent.company,
                fileNames: [],
                confirmDialogState: false,
                deleteContactPointMessage: 'Bist du dir sicher, dass du diesen Kontaktpunkt löschen willst?'
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
                this.$router.push(`/${contactPoint.contact.company.id}/${contactPoint.id}`);
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
                api.delete(`point/delete/${this.contactPoint.id}`)
                    .then(() => this.$parent.refreshData())
                    .catch(error => {
                        alert(error);
                    });
            },
            openConfirmDialog() {
                this.confirmDialogState = true;
            }
        }
    }
</script>

<style scoped>
    .low-padding {
        padding: 8px;
    }

    .no-padding-bottom {
        padding-bottom: 0px;
    }

    .low-padding-y {
        padding-top: 6px;
        padding-bottom: 6px;
    }

    .low-padding-left {
        padding-left: 10px;
    }
</style>