<template>
    <v-fade-transition>
        <v-hover>
            <v-card slot-scope="{ hover }"
                    :class="`elevation-${hover ? 8 : 2}`">
                <v-scroll-x-transition>
                    <v-btn v-if="hover"
                           absolute top right fab small
                           color="success"
                           @click="taskIsDone(todo)"
                           class="elevation-12 more-margin-top">
                        <v-icon size="24px" color="secondary" class="ml-3">
                            done_outline
                        </v-icon>
                    </v-btn>
                </v-scroll-x-transition>
                <v-card-title class="secondary title font-weight-light low-padding-left no-padding-bottom">
                    <v-icon :style="`transform: rotate(${hover ? 360 : 0}deg)`"
                            class="mx-1">
                        timer
                    </v-icon>
                    <chip :font-color="getUrgency(todo.expiration)">
                        {{ distanceInWords }}
                    </chip>
                    <v-icon class="mx-1">business</v-icon>
                    <v-tooltip top>
                        <chip slot="activator"
                              font-color="primary">
                            {{ todo.company.name | truncate(25) }}
                        </chip>
                        <span class="title font-weight-light">{{ todo.company.name }}</span>
                    </v-tooltip>
                </v-card-title>
                <v-tooltip top max-width="750">
                    <v-card-title slot="activator"
                                  class="white--text title font-weight-light text-xs-left">
                        {{ todo.description | truncate(85) }}
                    </v-card-title>
                    <span class="subheading font-weight-light">{{ todo.description }}</span>
                </v-tooltip>
            </v-card>
        </v-hover>
    </v-fade-transition>
</template>

<script>
    import api from '../../utils/http-common'

    import Chip from "../main/small/Chip.vue";

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