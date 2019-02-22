<template>
    <v-fade-transition>
        <v-hover>
            <v-card slot-scope="{ hover }"
                    :class="`mt-${search ? 2 : 0} mb-${search ? 3 : 2} clickable elevation-${hover ? 6 : 2}`"
                    @click="viewContactPoint(contactPoint)">
                <div v-if="search === false">
                    <v-scroll-x-transition>
                        <v-btn
                                v-if="hover"
                                @click.stop="editContactPoint()"
                                class="elevation-12 mr-5"
                                color="secondary"
                                absolute right top fab small>
                            <v-icon size="24px" color="white">
                                edit
                            </v-icon>
                        </v-btn>
                    </v-scroll-x-transition>
                    <v-scroll-x-transition>
                        <v-btn v-if="hover"
                               @click.stop="openConfirmDialog()"
                               class="elevation-12"
                               color="secondary"
                               absolute right top fab small>
                            <v-icon size="24px" color="error">
                                delete
                            </v-icon>
                        </v-btn>
                    </v-scroll-x-transition>
                </div>
                <confirm-dialog
                        v-model="confirmDialogState"
                        :questionToBeConfirmed="deleteContactPointMessage"
                        @confirmed="deleteContactPoint()"/>
                <v-card-title class="secondary title font-weight-light">
                    <v-icon color="primary  " class="mr-1">forum</v-icon>
                    <span class="headline font-weight-light mr-2">{{ contactPoint.title }}</span> •
                    <span class="ml-2 primary--text mr-2">{{ dateFormatted }}</span> •
                    <v-icon class="ml-1">attach_file</v-icon>
                    <span class="ml-1 mr-1 primary--text">{{ fileNames.length }}</span>
                    <v-icon class="ml-1 mr-1">share</v-icon>
                    <v-chip
                            class="subheading mt-1 mb-1 primary--text"
                            v-bind:key="type"
                            v-for="type in contactPoint.types">
                        {{ type }}
                    </v-chip>
                    <v-spacer></v-spacer>
                    <v-chip
                            :class="`mt-${hover ? 4 : 0}`"
                            color="white">
                        <v-avatar class="info white--text" size="35px">
                            <v-icon>person</v-icon>
                        </v-avatar>
                        <span class="subheading secondary--text">
                            {{ contactPoint.contact.name }}
                        </span>
                    </v-chip>
                </v-card-title>
                <v-card-text class="text-xs-left text-truncate" width="20%">
                    {{ contactPoint.comment }}
                </v-card-text>
                <v-divider/>
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
        padding: 8px !important;
    }
</style>