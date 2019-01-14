<template>
    <v-container grid-list-md fluid>
        <v-layout row wrap>
            <v-flex xs1>
                <v-btn block color="secondary" @click="goPageBack()">
                    <v-icon large dark>arrow_back</v-icon>
                </v-btn>
            </v-flex>
            <v-flex xs5>
                <h1 class="text-xs-left display-2 font-weight-thin">
                    {{ this.contactPoint.contact.company.name }}
                </h1>
            </v-flex>
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
                                {{ this.contactPoint.date }}
                            </v-card-text>
                        </v-card>
                    </v-flex>
                    <v-flex xs2>
                        <v-card dark color="info" height="100%">
                            <v-card-text class="headline text-xs-center">
                                <v-icon size="30px">{{ getPointTypeIconOf(this.contactPoint.type) }}</v-icon>
                            </v-card-text>
                        </v-card>
                    </v-flex>
                    <v-flex xs4>
                        <v-card height="100%">
                            <v-card-text class="headline text-xs-left font-weight-light">
                                {{ this.contactPoint.type }}
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
                        <v-card dark color="green">
                            <v-card-text class="headline text-xs-center">
                                <v-icon size="30px">comment</v-icon>
                            </v-card-text>
                        </v-card>
                    </v-flex>
                    <v-flex xs10>
                        <v-card dark>
                            <v-card-text class="headline text-xs-left font-weight-light">
                                {{ this.contactPoint.comment }}
                            </v-card-text>
                        </v-card>
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
                                :companyName="this.companyName"
                                :contactPointId="this.getContactPointId(this.contactPoint)"/>
                    </v-flex>
                    <v-flex xs12>
                        <v-layout row wrap>
                            <v-flex xs12 v-for="fileName in this.fileNames">
                                <v-card dark>
                                    <v-btn absolute top right fab small color="primary" class="mr-5"
                                           @click="downloadFile(fileName)">
                                        <v-icon style="transform: rotate(180deg)" size="24px" color="secondary">
                                            publish
                                        </v-icon>
                                    </v-btn>
                                    <v-btn absolute top right fab small color="error" class="ml-0"
                                           @click="deleteFile(fileName)">
                                        <v-icon size="24px" color="secondary">
                                            delete
                                        </v-icon>
                                    </v-btn>
                                    <v-card-text class="headline text-xs-left font-weight-light">
                                        {{ fileName }}
                                    </v-card-text>
                                </v-card>
                            </v-flex>
                        </v-layout>
                    </v-flex>
                </v-layout>
            </v-flex>
        </v-layout>
    </v-container>
</template>

<script>
    import {mapState} from 'vuex'
    import api from '@/utils/http-common'
    import pointStore from '@/stores/points.js'
    import FileUploadDialog from '@/components/point/FileUploadDialog.vue'

    export default {
        components: {
            FileUploadDialog
        },
        data() {
            return {
                contactPointId: this.$route.params.id,
                fileUploadDialogState: false,
                fileNames: []
            }
        },
        computed: {
            contactPoint() {
                return this.contactPoints[this.contactPointId]
            },
            ...mapState(['contactPoints']),
            contactPoints: {
                get() {
                    return pointStore.state.contactPoints
                },
                set(contactPoints) {
                    pointStore.commit('storeContactPoints', contactPoints)
                }
            },
            companyName() {
                return this.contactPoint.contact.company.name
            }
        },
        mounted() {
            this.getFileNames();
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
            goPageBack() {
                this.$router.go(-1)
            },
            getContactPointId() {
                return this.$route.params.id
            },
            downloadFile(filename) {
                return api.get(`point/download/${this.companyName}/${this.getContactPointId()}/${filename}`, {
                    responseType: 'arraybuffer'
                }).then(response => {
                    download(filename, response.data)
                }).catch(error => {
                    console.log(error)
                });
            },
            uploadFiles() {
                this.fileUploadDialogState = true
            },
            getFileNames() {
                api.get(`point/get/fileNames/${this.companyName}/${this.getContactPointId(this.contactPoint)}`).then(response => {
                    this.fileNames = response.data;
                    console.log(this.fileNames);
                }).catch(error => {
                    console.log(error);
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

<style>
    #drop-area:hover h2 {
        color: #4FC3F7;
    }
</style>