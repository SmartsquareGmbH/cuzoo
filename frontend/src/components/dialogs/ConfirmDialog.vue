<template>
    <v-layout row wrap>
        <v-dialog
                :value="value"
                @input="$emit('input')"
                persistent
                max-width="420">
            <v-card>
                <v-card-text class="text-xs-left headline font-weight-light">
                    {{ questionToBeConfirmed }}
                </v-card-text>
                <v-card-actions>
                    <v-spacer></v-spacer>
                    <v-btn
                            color="error"
                            flat="flat"
                            @click="closeDialog()">
                        Abbrechen
                    </v-btn>
                    <v-btn
                            color="success"
                            flat="flat"
                            @click="confirm()">
                        Bestätigen
                    </v-btn>
                </v-card-actions>
            </v-card>
        </v-dialog>
    </v-layout>
</template>

<script>
    export default {
        props: ['value', 'questionToBeConfirmed'],
        data() {
            return {
                confirmDialog: false
            }
        },
        methods: {
            confirm() {
                this.$emit('confirmed');
                this.closeDialog();
            },
            closeDialog() {
                this.$emit('input');
            }
        }
    }
</script>