<template>
    <v-dialog :value="value" @input="$emit('input')" persistent max-width="750">
        <v-card>
            <v-card-title class="headline primary" primary-title>
                Einstellungen
            </v-card-title>
            <v-card-text class="text-xs-cent    er rimary--text">
                <v-switch
                        disabled
                        color="primary"
                        class="dm-switch pt-1 mt-3"
                        label="Darkmode"
                        v-model="darkmodeState"
                        @change="storeState()"/>
            </v-card-text>
            <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn color="primary" flat v-on:click="closeSettings()">Schließen</v-btn>
                <v-btn color="primary" flat v-on:click="submitCompany()" :disabled="!valid">Speichern</v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>


<script>
    import {mapMutations} from 'vuex';

    export default {
        props: ["value"],
        data() {
            return {
                valid: false,
                darkmodeState: true
            }
        },
        methods: {
            ...mapMutations(["storeDarkState"]),
            storeState() {
                this.storeDarkState({darkState: this.darkmodeState});
            },
            closeSettings() {
                this.$emit("input");
            }
        }
    }
</script>

<style scoped>
    .dm-switch {
        padding: 1em;
    }
</style>