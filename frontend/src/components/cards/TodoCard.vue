<template>
    <v-fade-transition>
        <v-hover>
            <v-card slot-scope="{ hover }"
                    :class="`elevation-${hover ? 8 : 2}`">
                <v-card-title class="white--text subheading text-xs-left low-padding-bottom">

                    <v-layout row wrap>
                        <v-flex xs8 v-if="!hover">
                            <p class="text-truncate mb-0">
                                {{ todo.description }}
                            </p>
                        </v-flex>
                        <v-flex xs12 v-else>
                            <p class="mb-0">
                                {{ todo.description }}
                            </p>
                        </v-flex>
                        <v-flex xs4 class="text-xs-right expiration" v-if="!hover">
                            <v-icon :style="`transform: rotate(${hover ? 0 : 0}deg)`"
                                    class="mr-1">
                                timer
                            </v-icon>
                            <chip class="mb-2" :font-color="getUrgency(todo.expiration)">
                                {{ distanceInWords }}
                            </chip>
                        </v-flex>
                    </v-layout>
                </v-card-title>
                <v-expand-transition>
                    <div v-if="hover">
                        <v-card-title class="secondary title font-weight-light todo-footer">
                            <v-icon>person</v-icon>
                            <chip font-color="primary">
                                {{ todo.creator }}
                            </chip>
                            <v-icon class="mx-1">business</v-icon>
                            <v-tooltip top>
                                <chip slot="activator"
                                      font-color="primary">
                                    {{ todo.company.name | truncate(40) }}
                                </chip>
                                <span class="title font-weight-light">{{ todo.company.name }}</span>
                            </v-tooltip>
                            <v-spacer/>
                                <v-btn flat color="success"
                                       @click="taskIsDone(todo)">
                                    DONE
                                </v-btn>
                        </v-card-title>
                    </div>

                </v-expand-transition>


            </v-card>
        </v-hover>
    </v-fade-transition>
</template>

<script>
    import api from '../../utils/http-common'

    import Chip from "../core/Chip.vue";

    const datefns = require('date-fns');
    const de = require('date-fns/locale/de');

    export default {
        props: ['todo'],
        components: {Chip},
        computed: {
            distanceInWords() {
                return datefns.distanceInWords(
                    this.todo.expiration,
                    new Date(),
                    {locale: de}
                );
            },
            todoDescription() {
                return this.todo.description;
            }
        },
        mounted() {
            console.log(this.todo.id + ": ");
            console.log(this.todoDescription.slice(0, 50));
            console.log(this.todoDescription.slice(50));
        },
        methods: {
            taskIsDone(todo) {
                todo.done = true;
                api.put(`todo/done/${this.todo.id}`);
            },
            getUrgency(expiration) {
                let differenceInHours = datefns.differenceInHours(expiration, new Date());

                if (differenceInHours < 48) {
                    return 'error';
                } else if (differenceInHours < 168) {
                    return 'warning';
                } else {
                    return 'primary';
                }
            }
        }
    }
</script>

<style scoped>

    .low-padding-bottom {
        padding-bottom: 6px;
    }

    .todo-footer {
        padding-top: 0px;
        padding-bottom: 6px;
        padding-left: 10px;
    }
</style>