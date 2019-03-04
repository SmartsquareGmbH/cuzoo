<template>
    <v-fade-transition>
        <v-flex xs12 v-if="todo.done === false">
            <v-hover>
                <v-card slot-scope="{ hover }"
                        height="100%">
                    <v-scroll-x-transition>
                        <v-btn v-if="hover"
                               absolute top right fab small
                               color="success"
                               @click="taskIsDone(todo)"
                               :class="`elevation-12 ${onDashboard ? 'more-margin-top' : ''}`">
                            <v-icon size="24px" color="secondary" class="ml-3">
                                done_outline
                            </v-icon>
                        </v-btn>
                    </v-scroll-x-transition>
                    <v-card-title class="secondary title font-weight-light low-padding-left no-padding-bottom">
                        <v-icon class="mx-1"
                                :style="`transform: rotate(${hover ? 360 : 0}deg)`">timer</v-icon>
                        <v-chip small
                                :class="`subheading my-1 ${getUrgency(todo.expiration)}--text`">
                            {{ distanceInWords }}
                        </v-chip>
                        <v-icon class="mx-1">business</v-icon>
                        <v-tooltip top>
                            <v-chip small slot="activator"
                                    class="subheading my-1 primary--text">
                                {{ todo.company.name | truncate(25) }}
                            </v-chip>
                            <span class="title font-weight-light">{{ todo.company.name }}</span>
                        </v-tooltip>
                    </v-card-title>
                    <v-card-title class="white--text headline font-weight-light text-xs-left">
                        {{ todo.description }}
                    </v-card-title>
                </v-card>
            </v-hover>
        </v-flex>
    </v-fade-transition>
</template>

<script>
    import api from '../../utils/http-common'

    const datefns = require('date-fns');
    const de = require('date-fns/locale/de');

    export default {
        props: ['todo', 'onDashboard'],
        data() {
            return {}
        },
        computed: {
            distanceInWords() {
                return datefns.distanceInWords(
                    this.todo.expiration,
                    new Date(),
                    {locale: de}
                );
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
                    return 'primary';
                }
            }
        }
    }
</script>

<style scoped>
    .no-padding-bottom {
        padding-bottom: 0px;
    }

    .low-padding-left {
        padding-left: 10px;
    }

    .more-margin-top {
        margin-top: 32px;
    }
</style>