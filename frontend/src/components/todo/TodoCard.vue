<template>
    <v-scroll-x-transition>
        <v-flex xs12 v-if="todo.done === false">
            <v-hover>
                <v-card slot-scope="{ hover }"
                        :color="getUrgency(todo.expiration)"
                        height="100%">
                    <v-scroll-x-transition>
                        <v-btn v-if="hover"
                               absolute top right fab small
                               color="secondary"
                               @click="taskIsDone(todo)"
                               class="elevation-12">
                            <v-icon size="24px" color="success" class="ml-3">
                                done_outline
                            </v-icon>
                        </v-btn>
                    </v-scroll-x-transition>
                    <v-card-text
                            v-if="getUrgency(todo.expiration) !== 'secondary'"
                            class="secondary--text headline text-xs-left">
                        <v-icon size="30px" class="mr-2" color="secondary">
                            {{ getUrgency(todo.expiration) }}
                        </v-icon>
                        {{ todo.description }}
                    </v-card-text>
                    <v-card-text v-else class="white--text headline text-xs-left">
                        <v-icon :size="`${hover ? 48 : 32}px`" class="mr-2" color="white">
                            info
                        </v-icon>
                        {{ todo.description }}
                    </v-card-text>
                </v-card>
            </v-hover>
        </v-flex>
    </v-scroll-x-transition>
</template>

<script>
    import api from '../../utils/http-common'

    const datefns = require('date-fns');
    const de = require('date-fns/locale/de');

    export default {
        props: ['todo'],
        data() {
            return {
            }
        },
        methods: {
            taskIsDone(todo) {
                todo.done = true;
                api.put(`todo/done/${this.todo.id}`);
            },
            getUrgency(expiration) {
                let differenceInHours = datefns.differenceInHours(expiration, new Date());

                if (differenceInHours < 24) {
                    return 'error';
                } else if (differenceInHours < 72) {
                    return 'warning';
                } else {
                    return 'secondary';
                }
            }
        }
    }
</script>