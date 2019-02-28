<template>
    <v-card dark>
        <v-btn absolute top right fab small color="primary" class="mr-5"
               @click="downloadFile(fileName)">
            <v-icon style="transform: rotate(180deg)" size="24px" color="secondary">
                publish
            </v-icon>
        </v-btn>
        <v-btn absolute top right fab small color="error" class="ml-0"
               @click="openConfirmDialog()">
            <v-icon size="24px" color="secondary">
                delete
            </v-icon>
        </v-btn>
        <confirm-dialog
                v-model="confirmDialogState"
                :questionToBeConfirmed="deleteFileMessage"
                @confirmed="deleteFile(fileName)"/>
        <v-card-text class="headline text-xs-left font-weight-light">
            {{ fileName }}
        </v-card-text>
    </v-card>
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
            downloadFile(filename) {
                return api.get(`file/download/${this.contactPoint.id}/${filename}`, {
                    responseType: 'arraybuffer'
                }).then(response => {
                    download(filename, response.data)
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