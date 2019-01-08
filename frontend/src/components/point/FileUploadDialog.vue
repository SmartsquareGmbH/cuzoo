<template>
    <v-dialog :value="value" @input="$emit('input')" persistent max-width="950">
        <v-card color="transparent">
            <v-card-title class="headline primary" primary-title>
                {{ formTitle }}
            </v-card-title>
            <v-card-text>
                <vue-clip :options="options">
                    <template slot="clip-uploader-action">
                        <v-card class="bordered pa-1">
                            <div id="drop-area" class="dz-message clickable">
                                <h2 class="mt-4 mb-1">
                                    <v-icon class="mt-1" size="24px" absolute>attach_file</v-icon>
                                    Drag & Drop
                                </h2>
                                <p>Oder klicke zum Hochladen</p>
                            </div>
                        </v-card>
                    </template>

                    <template slot="clip-uploader-body" scope="props">
                        <div v-bind:key="file.name" v-for="file in props.files" class="text-xs-left">
                            <img v-bind:src="file.dataUrl"/>
                            <v-layout row wrap>
                                <v-flex xs10>
                                    <v-card>
                                        <v-card-text class="headline text-xs-left font-weight-light">
                                            {{ file.name }} {{ file.status }}
                                        </v-card-text>
                                    </v-card>
                                </v-flex>
                                <v-flex xs2>
                                    <v-card v-if="file.status == success" color="success">
                                        <v-card-text class="headline text-xs-center">
                                            <v-icon size="30px">done_outline</v-icon>
                                        </v-card-text>
                                    </v-card>
                                    <v-card v-else color="error">
                                        <v-card-text class="headline text-xs-center">
                                            <v-icon size="30px">error_outline</v-icon>
                                        </v-card-text>
                                    </v-card>
                                </v-flex>
                            </v-layout>
                        </div>
                    </template>
                </vue-clip>
            </v-card-text>
            <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn color="primary" flat @click.native="closeDialog()">Abbrechen</v-btn>
                <v-btn color="primary" flat v-on:click="submitCPoint()" :disabled="!valid">Speichern</v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>

<script>
    export default {
        props: ["value"],
        data() {
            return {
                formTitle: 'Dateien hochladen'
            }
        },
        computed: {

        },
        methods: {
            closeDialog() {
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
    .bordered {
        border-color: #fff;
        border-radius: 20px;
        border-style: solid;
    }
</style>