<template>
    <v-container grid-list-md fluid>
        <v-layout row wrap>
            <v-flex xs1>
                <v-btn block color="secondary" @click="goPageBack()">
                    <v-icon large dark>arrow_back</v-icon>
                </v-btn>
            </v-flex>
            <v-flex xs4>
            </v-flex>
            <v-flex xs1>
                <v-btn block color="secondary" @click="editContactPoint()">
                    <v-icon large dark>edit</v-icon>
                </v-btn>
            </v-flex>
            <contact-point-dialog
                    v-model="contactPointDialogState"
                    :contactNames="this.contactNames"/>
            <v-flex xs1>
                <v-btn id="upload-btn" @click="uploadFiles()" block color="secondary">
                    <v-icon large dark>
                        publish
                    </v-icon>
                </v-btn>
            </v-flex>
            <v-flex xs6>
                <v-layout row wrap>
                    <v-flex xs2>
                        <v-card dark color="green" height="100%" class="centered">
                            <v-card-text class="headline text-xs-center">
                                <v-icon size="30px">business_center</v-icon>
                            </v-card-text>
                        </v-card>
                    </v-flex>
                    <v-flex xs10>
                        <v-card dark>
                            <v-card-text class="headline text-xs-left font-weight-light">
                                {{ this.contactPoint.contact.company.name }}
                            </v-card-text>
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
                    <v-flex xs2 v-if="this.contactPoint.comment">
                        <v-card dark color="info">
                            <v-card-text class="headline text-xs-center">
                                <v-icon size="30px">comment</v-icon>
                            </v-card-text>
                        </v-card>
                    </v-flex>
                    <v-flex xs10 v-if="this.contactPoint.comment">
                        <v-card dark>
                            <v-card-text class="headline text-xs-left font-weight-light">
                                {{ this.contactPoint.comment }}
                            </v-card-text>
                        </v-card>
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
                                v-bind:key="contactPoint.label">
                            {{ label }}
                        </v-chip>
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
    </v-container>
</template>

<script>
    import {mapGetters, mapMutations} from 'vuex';
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
                contactNames: []
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
                return datefns.format(this.contactPoint.date, 'DD.MM.YY', { locale: de });
            }
        },
        mounted() {
            this.getFileNames();
        },
        methods: {
            ...mapMutations({
                storeDetails: 'storeEditedContactPointDetails',
                storeContactPoints: 'storeContactPoints'
            }),
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
                api.get('point/get').then(response => {
                    let contactPoints = response.data;

                    contactPoints.forEach(contactPoint => {
                        contactPoint.labels = contactPoint.labels.map(label => {
                            return label.title;
                        });
                    });

                    contactPoints.forEach(contactPoint => {
                        contactPoint.types = contactPoint.types.map(type => {
                            return type.title;
                        });
                    });

                    this.storeContactPoints({
                        contactPoints: contactPoints
                    })
                }).catch(error => {
                    console.log(error)
                })
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