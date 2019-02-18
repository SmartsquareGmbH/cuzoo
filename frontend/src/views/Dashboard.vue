<template>
    <v-container grid-list-md fluid>
        <v-layout row wrap>
            <v-flex xs12>

            </v-flex>
        </v-layout>
    </v-container>
</template>

<script>
    import {mapGetters} from 'vuex';

    import TodoDialog from '../components/dialogs/TodoDialog.vue';
    import TodoCard from '../components/cards/TodoCard.vue';
    import ContactPointDialog from "../components/dialogs/ContactPointDialog.vue";
    import ContactPointCard from "../components/cards/ContactPointCard.vue";

    export default {
        components: {
            TodoDialog,
            TodoCard,
            ContactPointDialog,
            ContactPointCard
        },
        data: () => ({

        }),
        computed: {
            ...mapGetters([
                'companies',
                'contacts',
                'contactPoints',
                'todos',
            ]),
        },
        mounted() {
            this.refreshData();
        },
        methods: {
            refreshData() {

            }
        }
    }
</script>