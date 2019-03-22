<template>
    <v-flex xs12>
        <v-card class="pl-3 mt-1"
                elevation="6"
                height="75">
            <v-layout row wrap text-xs-left>
                <v-flex xs2>
                    <v-sheet
                            elevation="12"
                            class="v-sheet--offset text-xs-center"
                            height="75"
                            width="100"
                            :color="color">
                        <v-icon color="primary" size="56px" class="pt-2">
                            done_all
                        </v-icon>
                    </v-sheet>
                </v-flex>
                <v-flex xs2 class="more-padding-top">
                <span class="headline font-weight-light">
                    TODOs
                </span>
                </v-flex>
                <v-flex xs2 style="padding-top: 12px;" class="text-xs-right">
                    <v-btn small flat fab
                           @click="addTODO()"
                           color="transparent">
                        <v-tooltip top>
                            <v-icon large
                                    color="light-green accent-2"
                                    slot="activator">
                                add
                            </v-icon>
                            <span>TODO hinzufügen</span>
                        </v-tooltip>
                    </v-btn>
                    <todo-dialog
                            v-model="todoDialogState"
                            :companyNames="this.companyNames"
                            @refresh="refreshTodos()"/>
                </v-flex>
                <v-flex xs6>
                    <v-text-field
                            class="pr-3"
                            ref="searchBarTodos"
                            v-model="searchTodos"
                            @keyup.enter="goToFirstResult()"
                            append-icon="search"
                            label="Suche nach TODOs"
                            color="primary"
                            hide-details/>
                </v-flex>
            </v-layout>
        </v-card>
        <v-layout row wrap>
            <div class="dash">
                <perfect-scrollbar :options="settings">
                    <div :style="`height: ${(this.windowHeight - 375) / 2}px`">
                        <v-flex xs12>
                            <todo-results :search="this.searchTodos"/>
                        </v-flex>
                    </div>
                </perfect-scrollbar>
            </div>
        </v-layout>
    </v-flex>
</template>

<script>
    import {mapGetters} from 'vuex';

    import TodoDialog from '../dialogs/TodoDialog.vue';
    import TodoCard from '../cards/TodoCard.vue';
    import TodoResults from '../search/TodoResults.vue'

    export default {
        components: {
            TodoDialog,
            TodoCard,
            TodoResults
        },
        data: () => ({
            windowHeight: 0,
            companyNames: [],
            todoDialogState: false,
            searchTodos: '',
            settings: {
                maxScrollbarLength: 120,
                wheelSpeed: 0.75,
                suppressScrollX: true
            },
        }),
        computed: {
            ...mapGetters(['companies', 'todos', 'selectedCompany'])
        },
        watch: {
            selectedCompany() {
                if (!this.selectedCompany || this.companies.map(it => it.name).includes(this.selectedCompany)) {
                    this.searchTodos = this.selectedCompany;
                }
            }
        },
        beforeMount() {
            this.onResize();
        },
        methods: {
            addTODO() {
                this.companyNames = this.companies.map(it => it.name).sort();
                this.todoDialogState = true;
            },
            onResize() {
                this.windowHeight = window.innerHeight;
            },
            refreshTodos() {
                this.$parent.refreshData();
            }
        }
    }
</script>

<style scoped>
    .dash {
        height: 100%;
        width: 100%;
        position: relative;
    }

    .v-sheet--offset {
        top: -24px;
        position: relative;
    }

    .more-padding-top {
        padding-top: 20px !important;
    }
</style>