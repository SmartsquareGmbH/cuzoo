<template>
    <v-fade-transition>
        <v-hover>
            <v-card slot-scope="{ hover }"
                    style="border-radius: 10px"
                    :class="`secondary mt-2 mb-3 clickable elevation-${hover ? 2 : 2}`"
                    @click.native="viewContactPoints(company)"
                    hover>
                <v-card-text
                        class="headline text-xs-left">
                    {{ company.name }}
                    <v-divider
                            class="mt-2 mb-3"/>
                    <div
                            v-if="company.zipCode || company.place || company.street"
                            class="headline font-weight-light text-xs-left">
                        <v-icon
                                color="primary"
                                class="mr-1"
                                size="30px">
                            place
                        </v-icon>
                        {{ company.zipCode }} {{ company.place }}, {{ company.street }}
                    </div>
                </v-card-text>
                <v-card
                        color="#377D93"
                        v-if="(company.description || company.other)">
                    <v-scroll-y-transition>
                        <v-card-text
                                class="headline text-xs-left font-weight-light">
                            {{ company.description }}
                            <v-spacer/>
                            {{ company.other }}
                        </v-card-text>
                    </v-scroll-y-transition>
                </v-card>
                <v-card-text
                        class="headline text-xs-left"
                        v-if="contacts.length > 0">
                    <v-icon
                            color="primary"
                            class="mr-2"
                            size="30px">
                        people
                    </v-icon>
                    <v-chip
                            class="mb-2"
                            color="primary"
                            v-bind:key="contact.id"
                            v-for="contact in contacts">
                        <span
                                class="headline secondary--text">
                            {{ contact.name }}
                        </span>
                    </v-chip>
                </v-card-text>
            </v-card>
        </v-hover>
    </v-fade-transition>
</template>

<script>
    export default {
        props: ['company', 'contacts'],
        data() {
            return {
                colorCache: {},
                companies: this.$parent.companies
            }
        },
        methods: {
            randomColor(id) {
                const random = () => Math.floor(256 * Math.random()) + 112;

                return this.colorCache[id] || (this.colorCache[id] = `rgb(${random()}, ${random()}, ${random()})`);
            },
            viewContactPoints(item) {
                this.$router.push('/' + (item.id));
            }
        }
    }
</script>
