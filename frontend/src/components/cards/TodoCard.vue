<template>
    <v-fade-transition>
        <v-hover>
            <v-card slot-scope="{ hover }"
                    :class="`clickable elevation-${hover ? 0 : 0}`"
                    @click="expandMenu = !expandMenu">
                <v-divider class="mx-2"
                           :style="`border-color: rgba(79, 195, 247, ${hover || expandMenu ? 1 : 0}) !important;`"/>
                <v-card-title class="white--text subheading text-xs-left low-padding-bottom pl-2">
                    <v-layout row wrap>
                        <v-flex xs8>
                            <p :class="`mb-0 ${fullDescription? `` : `text-truncate`}`">
                                {{ todo.description }}
                            </p>
                        </v-flex>
                        <v-flex xs4 class="text-xs-right">
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
                    <div v-if="expandMenu">
                        <v-card-title class="secondary title font-weight-light todo-footer">
                            <v-icon class="mr-1">business</v-icon>
                            <v-tooltip top>
                                <chip slot="activator"
                                      font-color="primary">
                                    {{ todo.company.name | truncate(30) }}
                                </chip>
                                <span class="title font-weight-light">{{ todo.company.name }}</span>
                            </v-tooltip>
                            <v-icon class="ml-1">person</v-icon>
                            <chip font-color="primary">
                                {{ todo.creator }}
                            </chip>
                            <v-spacer/>
                            <v-btn flat small color="success"
                                   @click="taskIsDone(todo)">
                                DONE
                                <v-icon size="20px" class="ml-1">done</v-icon>
                            </v-btn>
                        </v-card-title>
                    </div>
                </v-expand-transition>
                <v-divider class="mx-2"
                           :style="`border-color: rgba(79, 195, 247, ${hover || expandMenu ? 1 : 0}) !important;`"/>
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
        data: () => ({
            expandMenu: false,
            fullDescription: false
        }),
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
        watch: {
            expandMenu(value) {
                if (value) {
                    this.fullDescription = value;
                } else {
                    setTimeout(() => {
                        this.fullDescription = value;
                    }, 65);
                }
            }
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