<template>
    <div>
        <todo-card
                class="less-margin-bottom"
                v-bind:key="todo.id"
                v-for="todo in searchResults"
                :todo="todo"/>
    </div>
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
    .less-margin-bottom {
        margin-bottom: 8px !important;
    }
</style>