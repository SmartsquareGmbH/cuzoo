<template>
    <v-dialog :value="value" @input="$emit('input')" persistent max-width="750">
        <v-card>
            <v-card-title class="headline primary" primary-title>
                Einstellungen für {{ username }}
            </v-card-title>
            <v-card-text class="text-xs-center primary--text">
                <v-container>
                        <v-text-field
                                v-model="user.fullname" prepend-icon="person"
                                name="username" label="Name"/>
                        <v-text-field
                                v-model="user.mail" prepend-icon="mail"
                                name="password" label="E-Mail"/>
                </v-container>
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
    import {mapGetters, mapMutations, mapActions} from 'vuex';

    export default {
        props: ["value"],
        data: () => ({
            valid: false
        }),
        computed: {
            ...mapGetters(['username', 'user']),
        },
        beforeMount() {
            this.getUserInformation();
        },
        methods: {
            ...mapActions(['getUserInformation']),
            closeSettings() {
                this.$emit("input");
            }
        }
    }
</script>