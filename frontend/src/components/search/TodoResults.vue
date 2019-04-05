<template>
    <v-flex xs12 v-if="searchResults.length > 0" class="pa-0 ma-0">
        <todo-card
                class="todo-results"
                v-bind:key="todo.id"
                v-for="todo in searchResults"
                :todo="todo"/>
    </v-flex>
    <v-flex v-else text-xs-center class="my-3">
        <span class="error--text font-italic subheading">Keine TODOs gefunden</span>
    </v-flex>
</template>

<script>
    import {mapGetters} from 'vuex';
    import TodoCard from "../cards/TodoCard.vue";

    export default {
        props: ['search'],
        components: {
            TodoCard
        },
        data: () => ({
            searchTerms: [],
        }),
        watch: {
            searchResults() {
                console.log(this.searchResults.map(it => it.description));
            }
        },
        computed: {
            ...mapGetters(['todos']),
            searchResults() {
                return this.todos.filter(todo => todo.done === false).filter(todo => {
                    this.defineSearchTerms(todo);

                    if (this.search) {
                        return this.searchTerms.some(term =>
                            term.includes(this.search.toLowerCase())
                        );
                    } else {
                        return this;
                    }
                }).splice(0, 10);
            }
        },
        methods: {
            defineSearchTerms(todo) {
                this.searchTerms = [];

                for (let key in todo) {
                    if (todo.hasOwnProperty(key) && todo[key]) {
                        if (key === 'company') {
                            this.searchTerms.push(todo[key].name.toLowerCase());
                        } else {
                            this.searchTerms.push(todo[key].toString().toLowerCase());
                        }
                    }
                }
            }
        }
    }
</script>

<style scoped>
    .todo-results {
        margin-bottom: 0px !important;
        margin-left: 0px !important;
        margin-right: 0px !important;
    }
</style>