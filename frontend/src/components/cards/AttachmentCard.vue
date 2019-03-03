<template>
    <v-flex xs12>
        <v-hover>
            <v-card dark
                    slot-scope="{ hover }"
                    class="mb-1">
                <v-scroll-x-transition>
                    <v-btn v-if="hover && fileIsImageOrPDF(fileName)"
                           absolute top right fab small color="primary" class="more-distance-to-right"
                           @click="previewFile(fileName)">
                        <v-icon size="24px" color="secondary">
                            search
                        </v-icon>
                    </v-btn>
                </v-scroll-x-transition>
                <v-scroll-x-transition>
                    <v-btn v-if="hover" absolute top right fab small color="primary" class="mr-5"
                           @click="downloadFile(fileName)">
                        <v-icon style="transform: rotate(180deg)" size="24px" color="secondary">
                            publish
                        </v-icon>
                    </v-btn>
                </v-scroll-x-transition>
                <v-scroll-x-transition>
                    <v-btn v-if="hover" absolute top right fab small color="error" class="ml-0"
                           @click="openConfirmDialog()">
                        <v-icon size="24px" color="secondary">
                            delete
                        </v-icon>
                    </v-btn>
                </v-scroll-x-transition>
                <v-card-text class="headline text-xs-left font-weight-light">
                    {{ fileName }}
                </v-card-text>
            </v-card>
        </v-hover>
        <confirm-dialog
                v-model="confirmDialogState"
                :questionToBeConfirmed="deleteFileMessage"
                @confirmed="deleteFile(fileName)"/>
    </v-flex>
</template>

<script>
    import api from '../../utils/http-common';

    import ConfirmDialog from "../../components/dialogs/ConfirmDialog.vue";

    export default {
        props: ['contactPoint', 'fileName'],
        components: {
            ConfirmDialog
        },
        data() {
            return {
                confirmDialogState: false,
                deleteFileMessage:
                    'Bist du dir sicher, dass du diese Datei löschen willst? ' +
                    'Danach kann sie nicht wieder hergestellt werden'
            }
        },
        methods: {
            previewFile(fileName) {
                this.$router.push({
                    name: 'filePreview',
                    params: {
                        fileName: fileName
                    }
                });
            },
            downloadFile(fileName) {
                return api.get(`file/download/${this.contactPoint.id}/${fileName}`, {
                    responseType: 'arraybuffer'
                }).then(response => {
                    download(fileName, response.data)
                }).catch(error => {
                    console.log(error)
                });
            },
            deleteFile(fileName) {
                api.delete(`file/delete/${this.contactPoint.id}/${fileName}`)
                    .then(() => {
                        this.getFileNames();
                    })
            },
            openConfirmDialog() {
                this.confirmDialogState = true;
            },
            fileIsImageOrPDF(fileName) {
                if (fileName.includes('pdf') ||
                    fileName.includes('png') ||
                    fileName.includes('jpg') ||
                    fileName.includes('jpeg')) {
                    return true
                }
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
    .more-distance-to-right {
        margin-right: 96px;
    }
</style>