<template>
    <v-dialog
            :value="value"
            :company="company"
            @input="$emit('input')"
            persistent
            max-width="950">
        <v-card>
            <v-card-title class="headline primary" primary-title>
                {{ formTitle }}
            </v-card-title>
            <v-card-text>
            </v-card-text>
            <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn color="primary" flat @click.native="closeDialog()">OKAY</v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>

<script>
    export default {
        props: ["value", "company"],
        data() {
            return {
                formTitle: 'TODO erstellen'
            }
        },
        computed: {},
        methods: {
            closeDialog() {
                this.$emit('input')
            }
        }
    }
</script>