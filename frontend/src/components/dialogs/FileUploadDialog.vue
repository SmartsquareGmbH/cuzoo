<template>
    <v-dialog
            :value="value"
            :companyName="companyName"
            :contactPointId="contactPointId"
            @input="$emit('input')"
            persistent
            max-width="950">
        <v-card>
            <v-card-title class="headline primary" primary-title>
                <v-icon class="mt-0" size="28px" absolute>attach_file</v-icon>
                {{ formTitle }}
            </v-card-title>
            <v-card-text>
                <vue-clip :options="options">
                    <template slot="clip-uploader-action">
                        <v-card class="bordered pa-1" color="transparent elevation-12 mb-4">
                            <div id="drop-area" class="dz-message clickable text-xs-center">
                                <h2 class="mt-4 mb-1">
                                    Drag & Drop
                                </h2>
                                <p>Oder klicke zum Hochladen</p>
                            </div>
                        </v-card>
                    </template>

                    <template slot="clip-uploader-body" scope="props">
                        <div v-bind:key="file.name" v-for="file in props.files" class="text-xs-left">
                            <p class="title text-xs-left font-weight-light">
                                {{ file.name }}
                                <v-icon size="30px" v-if="file.status === 'success'" color="success">
                                    done_outline
                                </v-icon>
                                <v-icon v-else size="30px" color="error">error_outline</v-icon>
                            </p>
                        </div>
                    </template>
                </vue-clip>
            </v-card-text>
            <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn color="primary" flat @click.native="closeDialog()">OKAY</v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>

<script>
    import {mapGetters} from 'vuex'

    export default {
        props: ["value", "companyName", "contactPointId"],
        data() {
            return {
                formTitle: 'Dateien hochladen',
                uploaded: false
            }
        },
        computed: {
            ...mapGetters(['username', 'password']),
            options() {
                return {
                    url: `${process.env.VUE_APP_API_SCHEME}://${process.env.VUE_APP_API_HOSTNAME}:${process.env.VUE_APP_API_PORT}/api/file/upload/${this.contactPointId}`,
                    headers: {
                        "Authorization": "Basic " + btoa(this.username + ":" + this.password)
                    },
                    paramName: "file"
                }
            }
        },
        methods: {
            closeDialog() {
                this.$parent.getFileNames();
                this.$emit('input')
            }
        }
    }
</script>
<style>
    .clickable {
        cursor: pointer;
    }

    #drop-area {
        border: 2px dashed #ccc;
        border-radius: 20px;
        font-family: sans-serif;
        padding: 30px;
    }

    #drop-area:hover {
        border-color: #4FC3F7;
    }

    #drop-area:hover h2 {
        color: #4FC3F7;
    }

    #drop-area:hover p {
        font-size: 16px;
        color: #4FC3F7;
    }

    .bordered {
        border-color: #fff;
        border-radius: 20px;
        border-style: solid;
    }
</style>