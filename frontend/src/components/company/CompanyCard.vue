<template>
    <v-fade-transition>
        <v-card
                style="border-radius: 10px"
                class="secondary mt-3 mb-4 clickable"
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
            v-if="company.description || company.other">
                <v-card-text
                class="headline text-xs-left font-weight-light">
                    {{ company.description }}
                    <v-spacer/>
                    {{ company.other }}
                </v-card-text>
            </v-card>
            <v-card-text
            class="headline text-xs-left"
            v-if="this.$parent.getContactsOfCompany(company.name).length > 0">
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
                v-for="contact in this.$parent.getContactsOfCompany(company.name)">
                    <span
                    class="headline secondary--text">
                        {{ contact.name }}
                    </span>
                </v-chip>
            </v-card-text>
        </v-card>
    </v-fade-transition>
</template>

<script>
export default {
    props: ['company'],
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
        viewContactPoints (item) {
            const index = this.companies.findIndex(company => company.id === item.id);
            this.$router.push('/' + (index));
        }
    }
}
</script>
