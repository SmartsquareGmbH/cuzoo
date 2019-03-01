<template>
    <v-container grid-list-md text-xs-left fluid>
        <v-fade-transition>
            <v-layout row wrap v-if="doneLoading">
                <v-flex xs1>
                    <v-btn flat small @click="goPageBack()">
                        <v-icon size="22px" class="mr-1" dark>arrow_back</v-icon>
                        Zurück
                    </v-btn>
                </v-flex>
                <v-flex xs5 class="text-xs-right">
                    <v-btn flat small @click="editContactPoint()">
                        <v-icon size="22px" class="mr-1" dark>edit</v-icon>
                        Kontaktpunkt editieren
                    </v-btn>
                </v-flex>
                <contact-point-dialog
                        v-model="contactPointDialogState"
                        :contactNames="this.contactNames"/>
                <v-flex xs6>
                    <v-btn id="upload-btn" @click="uploadFiles()" flat small>
                        <v-icon size="22px" class="mr-1" dark>publish</v-icon>
                        Dateien hochladen
                    </v-btn>
                </v-flex>
                <v-flex xs6>
                    <v-layout row wrap>
                        <v-flex xs2>
                            <v-card dark color="green">
                                <v-card-text class="headline text-xs-center">
                                    <v-icon size="30px">business</v-icon>
                                </v-card-text>
                            </v-card>
                        </v-flex>
                        <v-flex xs10>
                            <v-card dark>
                                <v-tooltip bottom>
                                    <v-card-text slot="activator" class="headline text-xs-left font-weight-light">
                                        {{ this.contactPoint.contact.company.name | truncate(60) }}
                                    </v-card-text>
                                    <span class="headline font-weight-light">{{ this.company.name }}</span>
                                </v-tooltip>
                            </v-card>
                        </v-flex>
                        <v-flex xs2>
                            <v-card dark color="green" height="100%" class="centered">
                                <v-card-text class="headline text-xs-center">
                                    <v-icon size="30px">person</v-icon>
                                </v-card-text>
                            </v-card>
                        </v-flex>
                        <v-flex xs10>
                            <v-card dark>
                                <v-card-text class="headline text-xs-left font-weight-light">
                                    {{ this.contactPoint.contact.name }}
                                </v-card-text>
                            </v-card>
                        </v-flex>
                        <v-flex xs2>
                            <v-card height="100%" dark color="info">
                                <v-card-text class="headline text-xs-center">
                                    <v-icon size="30px">title</v-icon>
                                </v-card-text>
                            </v-card>
                        </v-flex>
                        <v-flex xs10>
                            <v-card height="100%" dark>
                                <v-card-text class="headline text-xs-left font-weight-light">
                                    {{ this.contactPoint.title }}
                                </v-card-text>
                            </v-card>
                        </v-flex>
                        <v-flex xs2>
                            <v-card dark color="info" height="100%">
                                <v-card-text class="headline text-xs-center">
                                    <v-icon size="30px">event</v-icon>
                                </v-card-text>
                            </v-card>
                        </v-flex>
                        <v-flex xs4>
                            <v-card height="100%">
                                <v-card-text class="headline text-xs-left font-weight-light">
                                    {{ this.dateFormatted }}
                                </v-card-text>
                            </v-card>
                        </v-flex>
                        <v-flex xs2>
                            <v-card dark color="info" height="100%">
                                <v-card-text class="headline text-xs-center">
                                    <v-icon size="30px">share</v-icon>
                                </v-card-text>
                            </v-card>
                        </v-flex>
                        <v-flex xs4 class="text-xs-left">
                            <v-chip
                                    class="title mt-3"
                                    v-for="type in contactPoint.types"
                                    v-bind:key="contactPoint.type">
                                {{ type }}
                            </v-chip>
                        </v-flex>
                        <v-flex xs2>
                            <v-card dark color="info">
                                <v-card-text class="headline text-xs-center">
                                    <v-icon size="30px" class="pt-1">label</v-icon>
                                </v-card-text>
                            </v-card>
                        </v-flex>
                        <v-flex xs10 class="text-xs-left">
                            <v-chip
                                    class="title mt-3"
                                    v-for="label in contactPoint.labels"
                                    v-bind:key="label">
                                {{ label }}
                            </v-chip>
                        </v-flex>
                        <v-flex xs12>
                            <v-card-text class="headline text-xs-left no-padding-left"
                                         v-if="this.contactPoint.comment">
                                <v-tooltip top>
                                    <v-icon color="info" size="30px" slot="activator">comment</v-icon>
                                    Kommentar zum Kontakpunkt
                                </v-tooltip>
                                Kommentar
                            </v-card-text>
                        </v-flex>
                        <v-flex xs12>
                        <span v-if="this.contactPoint.comment"
                              style="white-space: pre-wrap;">{{ this.contactPoint.comment }}
                        </span>
                        </v-flex>
                    </v-layout>
                </v-flex>
                <v-flex xs6>
                    <v-layout row wrap>
                        <v-flex xs2>
                            <v-card dark color="info" height="100%" class="centered">
                                <v-card-text class="headline text-xs-center">
                                    <v-icon size="30px">attach_file</v-icon>
                                </v-card-text>
                            </v-card>
                        </v-flex>
                        <v-flex xs2>
                            <v-card dark height="100%">
                                <v-card-text class="headline text-xs-center font-weight-light">
                                    {{ this.fileNames.length }}
                                </v-card-text>
                            </v-card>
                        </v-flex>
                        <v-flex xs8>
                            <file-upload-dialog
                                    v-model="fileUploadDialogState"
                                    :companyName="this.company.name"
                                    :contactPointId="this.contactPointId"/>
                        </v-flex>
                        <v-flex xs12>
                            <v-layout row wrap>
                                <attachment-card
                                        v-for="fileName in this.fileNames"
                                        :fileName="fileName"
                                        :contactPoint="contactPoint"/>
                            </v-layout>
                        </v-flex>
                    </v-layout>
                </v-flex>
            </v-layout>
        </v-fade-transition>
        <v-layout v-if="!doneLoading">
            <v-flex xs12 class="text-xs-center">
                <v-progress-circular :size="128" color="primary" indeterminate/>
            </v-flex>
        </v-layout>
    </v-container>
</template>

<script>
    import {mapActions, mapGetters, mapMutations} from 'vuex';
    import api from '../../utils/http-common';

    import FileUploadDialog from '../../components/dialogs/FileUploadDialog.vue';
    import ContactPointDialog from "../../components/dialogs/ContactPointDialog.vue";
    import ConfirmDialog from "../../components/dialogs/ConfirmDialog.vue";
    import AttachmentCard from "../../components/cards/AttachmentCard.vue";

    const datefns = require('date-fns');
    const de = require('date-fns/locale/de');

    export default {
        components: {
            FileUploadDialog,
            ContactPointDialog,
            ConfirmDialog,
            AttachmentCard
        },
        data() {
            return {
                contactPointId: this.$route.params.contactPointId,
                companyId: this.$route.params.companyId,
                fileUploadDialogState: false,
                fileNames: [],
                contactPointDialogState: false,
                contactNames: [],
                doneLoading: false,
            }
        },
        computed: {
            ...mapGetters([
                'companies',
                'contacts',
                'contactPoints',
                'sortedContactPoints'
            ]),
            contactPoint() {
                return this.contactPoints.find(contactPoint => {
                    return contactPoint.id == this.contactPointId;
                })
            },
            company() {
                return this.companies.find(company => {
                    return company.id == this.companyId;
                })
            },
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
                storeContactPoints: 'storeContactPoints'
            }),
            ...mapActions([
                'getContactPoints',
                'getContacts',
                'getCompanies'
            ]),
            goPageBack() {
                this.$router.go(-1)
            },
            uploadFiles() {
                this.fileUploadDialogState = true
            },
            getFileNames() {
                api.get(`file/get/names/${this.contactPoint.id}`).then(response => {
                    this.fileNames = response.data;
                }).catch(error => {
                    console.log(error);
                });
            },
            getContactsOfCompany() {
                return this.contacts.filter((contact) => {
                    if (contact.company != null) {
                        return contact.company.name === this.company.name
                    } else {
                        return null
                    }
                })
            },
            editContactPoint() {
                this.getContactsOfCompany().forEach(contact => {
                    this.contactNames.push(contact.name)
                });

                this.contactNames.sort();

                this.storeDetails({
                    editedIndex: this.contactPoints.indexOf(this.contactPoint),
                    editedContactPoint: Object.assign({}, this.contactPoint)
                });

                this.contactPointDialogState = true;
            },
            refreshData() {
                this.getContactPoints();
                this.getContacts();
                this.getCompanies();

                api.get(`file/get/names/${this.contactPointId}`).then(response => {
                    this.fileNames = response.data;
                    this.doneLoading = true;
                });
            }
        }
    }

    function download(filename, content) {
        const url = window.URL.createObjectURL(new Blob([content]));
        const link = document.createElement('a');
        link.href = url;
        link.setAttribute('download', filename);
        document.body.appendChild(link);
        link.click();
    }
</script>